package com.matthewkayin.winjam;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import com.matthewkayin.util.SoundManager;

public class Main extends JPanel{

    private boolean running;
    private long beforeTime;
    private long beforeSec;
    private final long SECOND = 1000000000;
    private final int TARGET_FPS = 60;
    private final long OPTIMAL_TIME = SECOND / TARGET_FPS;
    private int frames;
    private int fps;

    private final int SCREEN_WIDTH = 1280;
    private final int SCREEN_HEIGHT = 720;

    //input variables
    private int mousex = 0;
    private int mousey = 0;
    private boolean keydown[];
    private final int numberOfKeys = 4;
    private final int W = 0;
    private final int S = 1;
    private final int A = 2;
    private final int D = 3;

    private SoundManager s;
    private Level level;
    private int currentLevel = 0;
    private int playerstartx = (SCREEN_WIDTH / 2) - (20);
    private int playerstarty = SCREEN_HEIGHT - 40;
    private int levels[][][] = new int[][][]{
            { {0, playerstartx, playerstarty}, {1, playerstartx, 200} },
            { {0, playerstartx, playerstarty}, {1, playerstartx, 10}, {2, playerstartx, 300} },
            { {0, playerstartx, playerstarty}, {1, playerstartx - 600, playerstarty - 50, 10}, {5, playerstartx - 350, playerstarty - 100}, {2, 300, 200} },
            { {0, playerstartx, playerstarty}, {1, 1250, 10}, {3, 700, 500}, {2, 100, 200}, {4, 50, 50}, {4, 100, 900} }
    };

    private BufferedImage gate_bottom;
    private BufferedImage gate_top;
    private BufferedImage ship;
    private BufferedImage lasers;
    private BufferedImage black_hole;
    private BufferedImage background;

    private double gateangle;
    private final double LAUNCH_SPEED = 4.0;
    private long launchStart;
    private final long launchTime = 700 * 1000000; //700ms to nanoseconds
    private int launchdirx = 0;
    private int launchdiry = 0;

    private int state = 0;

    public Main(){

        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setFocusable(true);
        requestFocus();
        setBackground(Color.pink);

        addMouseListener(new MouseAdapter(){

            public void mousePressed(MouseEvent e){

                if(state == 0){

                    state = 1;
                    level = new Level(levels[0]);
                    return;
                }

                if(level.isFinished() == 1){

                    level.setState(3);
                    return;
                }

                if(level.isFinished() == 2){

                    level.setState(4);
                    return;
                }

                if(level.getPlayer().orbiting){

                    level.freeing = true;
                    int mod = 1;
                    if(level.playeranglespeed < 0){

                        mod = -1;
                    }
                    double speed = (2 * Math.PI * level.distfromplanet) / ((2 * Math.PI) / (level.playeranglespeed ));
                    level.getPlayer().setVx(0);
                    level.getPlayer().setVy(0);
                    // int x = (int)( level.getPlayer().getX() + (speed * Math.cos(level.playerangle + ((Math.PI / 2) * mod) )));
                    int x = (int)( level.getPlayer().getX() + (level.getPlayer().getWidth() / 2) + (1000 * Math.cos(level.getPlayerRenderAngle() - (mod*Math.PI / 2) )));

                    //             int y = (int) (level.getPlayer().getY() + (speed * Math.sin(level.playerangle + ((Math.PI / 2) * mod))) );
                    int y = (int) (level.getPlayer().getY() + (level.getPlayer().getHeight() / 2) + (1000 * Math.sin(level.getPlayerRenderAngle() - (mod*Math.PI / 2))) );
                    level.impulse(x, y, speed);

                }else{

                    if(!level.haslaunched && level.getPlayer().getVy() == 0 && level.getPlayer().getVx() == 0){

                        launchStart = System.nanoTime();
                        level.impulse(mousex, mousey, 1.3);
                        launchdirx = mousex;
                        launchdiry = mousey;
                    }
                }
            }

            public void mouseReleased(MouseEvent e){


            }
        });

        addMouseMotionListener(new MouseMotionAdapter(){

            public void mouseMoved(MouseEvent e){

                mousex = e.getX();
                mousey  = e.getY();
            }

            public void mouseDragged(MouseEvent e){

                mousex = e.getX();
                mousey = e.getY();
            }
        });

        addKeyListener(new KeyAdapter(){

            public void keyPressed(KeyEvent e){

                int keycode = e.getKeyCode();

                switch(keycode){

                    case KeyEvent.VK_ESCAPE:
                        if(state == 0){running = false;}
                        state = 0;
                        currentLevel = 0;
                        break;

                    case KeyEvent.VK_W:
                        keydown[W] = true;
                        break;

                    case KeyEvent.VK_S:
                        keydown[S] = true;
                        break;

                    case KeyEvent.VK_A:
                        keydown[A] = true;
                        break;

                    case KeyEvent.VK_D:
                        keydown[D] = true;
                        break;

                    case KeyEvent.VK_SPACE:

                        //doesn't do a damn thing

                        break;
                }
            }

            public void keyReleased(KeyEvent e){

                int keycode = e.getKeyCode();

                switch(keycode){

                    case KeyEvent.VK_W:
                        keydown[W] = true;
                        break;

                    case KeyEvent.VK_S:
                        keydown[S] = true;
                        break;

                    case KeyEvent.VK_A:
                        keydown[A] = true;
                        break;

                    case KeyEvent.VK_D:
                        keydown[D] = true;
                        break;
                }
            }
        });

        keydown = new boolean[numberOfKeys];
        for(int i = 0; i < keydown.length; i++){

            keydown[i] = false;
        }

        s = new SoundManager();

        try{

            ship = ImageIO.read(new File("res/gfx/slingship.png"));
            gate_bottom = ImageIO.read(new File("res/gfx/gate_bottom.png"));
            gate_top = ImageIO.read(new File("res/gfx/gate_top.png"));
            black_hole = ImageIO.read(new File("res/gfx/blackhole.png"));
            background = ImageIO.read(new File("res/gfx/space_background.png"));

        }catch(IOException e){

            e.printStackTrace();
        }

        level = new Level(levels[0]);

        //DELETE ME WHEN DONE
        currentLevel = 3;
        state = 1;
        level = new Level(levels[currentLevel]);

        running = false;
    }

    public void run(){

        running = true;
        beforeTime = System.nanoTime();
        beforeSec = 0;
        frames = 0;

        while(running){

            long currentTime = System.nanoTime();
            long elapsed = currentTime - beforeTime;
            beforeTime = currentTime;

            beforeSec += elapsed;
            frames++;

            if(beforeSec >= SECOND){

                fps = frames;
                frames = 0;
                beforeSec -= SECOND;
                System.out.println("FPS = " + fps);
            }

            update();
            repaint();

            try{

                Thread.sleep((beforeTime - System.nanoTime() + OPTIMAL_TIME) / 1000000);

            }catch(Exception e){

                e.printStackTrace();
            }
        }
    }

    public void update(){

        if(state == 0){

            return;
        }

        if(level.isFinished() == 4){

            level = new Level(levels[currentLevel]);

        }else if(level.isFinished() == 3){

            currentLevel++;
            level = new Level(levels[currentLevel]);
            //put some code to check if we're at last level pls congratulate them

        }else{

            level.update();

            if(level.getPlayer().getX() < 0 || level.getPlayer().getX() + level.getPlayer().getWidth() > SCREEN_WIDTH ||
                level.getPlayer().getY() < 0 || level.getPlayer().getY() + level.getPlayer().getHeight() > SCREEN_HEIGHT){

                level.getPlayer().setVx(0);
                level.getPlayer().setVy(0);
                level.setState(2);
            }
        }

        if(!level.haslaunched){

            if(level.getPlayer().getVy() == 0 && level.getPlayer().getVx() == 0){

                gateangle = getAngleMouse();

            }else{

                long theTime = System.nanoTime();
                if(theTime - launchStart >= launchTime){

                    level.getPlayer().setVx(0);
                    level.getPlayer().setVy(0);
                    level.impulse(launchdirx, launchdiry, LAUNCH_SPEED);
                    level.haslaunched = true;
                }
            }
        }

        String v = level.getSound();
        while(!v.equals("")){

            s.playSound(v);
            v = level.getSound();
        }
    }

    public void paint(Graphics g){

        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;

        //draw stuff here
        g2d.drawImage(background, 0, 0, null);


        if(state == 0){

            //draw menu
            g2d.setFont(new Font("Helvetica", Font.BOLD, 150));
            g2d.setColor(Color.yellow);
            g2d.drawString("SLING SHIPS", (SCREEN_WIDTH / 2) - 520, 170);
            g2d.setFont(new Font("Helvetica", Font.BOLD, 80));
            g2d.setColor(Color.green);
            g2d.drawString("Click to Start", (SCREEN_WIDTH / 2) - 300, 500);

        }else{

            g2d.setColor(Color.green);
            Ellipse2D.Double circle;

            for(int i = 0; i < level.noPlanets(); i++){

                circle = new Ellipse2D.Double(level.getPlanet(i).getX(), level.getPlanet(i).getY(), level.getPlanet(i).getWidth(), level.getPlanet(i).getHeight());
                g2d.fill(circle);
            }

            for(int i = 0; i < level.getBHSize(); i++){

                AffineTransform stationary = new AffineTransform();
                stationary.scale(1, 1);
                stationary.translate(level.getBlackHole(i).getX(), level.getBlackHole(i).getY());
                g2d.drawImage(black_hole, stationary, null);
            }

            g2d.setColor(Color.WHITE);
            circle = new Ellipse2D.Double(level.getEnd().getX(), level.getEnd().getY(), level.getEnd().getWidth(), level.getEnd().getHeight());
            g2d.fill(circle);

            g2d.setColor(Color.red);
            for(int i = 0; i < level.noBlocks(); i++){

                Rectangle2D.Double rect = new Rectangle2D.Double(level.getBlock(i).getX(), level.getBlock(i).getY(), level.getBlock(i).getWidth(), level.getBlock(i).getHeight());
                g2d.fill(rect);
            }

            //g2d.drawImage(gate_bottom, level.gatex, level.gatey, null);
            AffineTransform gate = new AffineTransform();
            gate.scale(1, 1);
            gate.rotate(gateangle, playerstartx + (level.getPlayer().getWidth() / 2), playerstarty + (level.getPlayer().getHeight() / 2));
            gate.translate(level.gatex, level.gatey);
            g2d.drawImage(gate_bottom, gate, null);

            AffineTransform t = new AffineTransform();
            t.scale(1, 1);
            double pangletouse = 0;
            if(!level.haslaunched){

                pangletouse = gateangle;

            }else{

                pangletouse = level.getPlayerRenderAngle();
            }
            t.rotate(pangletouse, level.getPlayer().getX() + (level.getPlayer().getWidth() / 2), level.getPlayer().getY() + (level.getPlayer().getHeight() / 2));
            t.translate(level.getPlayer().getX(), level.getPlayer().getY());

            g2d.drawImage(ship, t,null);

            //g2d.drawImage(gate_top, level.gatex, level.gatey, null);
            g2d.drawImage(gate_top, gate, null);

            if(!level.haslaunched){

                lasers = new BufferedImage(100, 120, BufferedImage.TYPE_INT_ARGB);
                Graphics2D lg = (Graphics2D)lasers.getGraphics();
                //rbg = 94, 198, 227
                lg.setColor(new Color(94, 198, 227));
                //left ship thing 4, 11 ; right ship thing 34, 11
                //left gate thing 11, 33 ; right gate thing 87, 33
                long now = System.nanoTime();
                double percentShot = 0.0;
                if(level.getPlayer().getVy() != 0 || level.getPlayer().getVx() != 0){

                    percentShot = (double)(now - launchStart) / (double)launchTime;
                }
                int nsx = 4;
                int nsy = (int)(11 - (58*percentShot));
                int ngx = 11;
                int ngy = 33;
                lg.setStroke(new BasicStroke(3));
                lg.drawLine(30 + nsx, 80 + nsy, ngx, ngy);
                nsx = 34;
                nsy = (int)(11 - (58*percentShot));
                ngx = 87;
                ngy = 33;
                lg.drawLine(30 + nsx, 80 + nsy, ngx, ngy);


//            AffineTransform lt = new AffineTransform();
//            lt.scale(1, 1);
//            lt.rotate(gateangle, playerstartx + (level.getPlayer().getWidth() / 2), playerstarty + (level.getPlayer().getHeight() / 2));
//            lt.translate(level.gatex, level.gatey);
                g2d.drawImage(lasers, gate, null);
            }

            if(level.getPlayer().orbiting){

                int x = (int)( level.getPlayer().getX() + (level.getPlayer().getWidth() / 2) + (40 * Math.cos(level.getPlayerRenderAngle() - (Math.PI / 2) )));

                //             int y = (int) (level.getPlayer().getY() + (speed * Math.sin(level.playerangle + ((Math.PI / 2) * mod))) );
                int y = (int) (level.getPlayer().getY() + (level.getPlayer().getHeight() / 2) + (40 * Math.sin(level.getPlayerRenderAngle() - (Math.PI / 2))) );
                g2d.setColor(Color.red);
                g2d.fillRect(x - 2, y - 2, 4, 4);
            }

            if(level.isFinished() == 1){

                g2d.setColor(Color.green);
                g2d.setFont(new Font("Helvetica", Font.BOLD, 200));
                g2d.drawString("VICTORY", (SCREEN_WIDTH / 2) - 450, (SCREEN_HEIGHT / 2) );

            }else if(level.isFinished() == 2){

                g2d.setColor(Color.red);
                g2d.setFont(new Font("Helvetica", Font.BOLD, 200));
                g2d.drawString("FAILURE", (SCREEN_WIDTH / 2) - 450, (SCREEN_HEIGHT / 2) );
            }
        }

        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }

    public double getAngleMouse(){

        double distx = mousex - level.getPlayer().getX();
        double disty = mousey - level.getPlayer().getY();
        if(distx == 0){

            return 0;
        }
        double ra = Math.atan(disty / distx);
        if(distx > 0){

            ra += (Math.PI / 2);

        }else{

            ra -= (Math.PI / 2);
        }

        return ra;
    }

    public static void main(String[] args){

        JFrame window = new JFrame("SLING SHIP");
        window.setSize(1980, 1020);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /*
        * BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
          Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
          window.getContentPane().setCursor(blankCursor);
        * */

        Main game = new Main();
        window.add(game);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        game.run();
        window.dispose();
    }
}
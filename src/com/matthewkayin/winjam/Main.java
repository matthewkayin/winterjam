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
            { {0, playerstartx, playerstarty}, {1, 500, 500} },
            { {0, playerstartx, playerstarty}, {1, 1000, 550}, {2, 500, 500} },
            { {0, playerstartx, playerstarty}, {1, 1000, 600}, {2, 500, 500}, {2, 200, 200} },
            { {0, playerstartx, playerstarty}, {1, 1250, 10}, {3, 700, 500}, {2, 100, 200} }
    };

    private BufferedImage gate_bottom;
    private BufferedImage gate_top;
    private BufferedImage ship;
    private BufferedImage lasers;

    private double gateangle;

    public Main(){

        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setFocusable(true);
        requestFocus();
        setBackground(Color.pink);

        addMouseListener(new MouseAdapter(){

            public void mousePressed(MouseEvent e){

                if(level.getPlayer().orbiting){

                    level.freeing = true;
                    int mod = 1;
                    if(level.playeranglespeed < 0){

                        mod = -1;
                    }
                    double speed = (2 * Math.PI * level.distfromplanet) / ((2 * Math.PI) / (level.playeranglespeed ));
                    level.impulse(level.getPlayer().getX() + (speed * Math.cos(level.playerangle + ((Math.PI / 2) * mod) )), level.getPlayer().getY() + (speed * Math.sin(level.playerangle + ((Math.PI / 2) * mod))), speed);

                }else{

                    level.impulse(mousex, mousey,  6.0);
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
                        running = false;
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

                        if(level.isFinished() == 1){

                            level.setState(3);
                        }

                        if(level.isFinished() == 2){

                            level.setState(4);
                        }

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

        }catch(IOException e){

            e.printStackTrace();
        }

        level = new Level(levels[0]);

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

        if(level.isFinished() == 4){

            level = new Level(levels[currentLevel]);

        }else if(level.isFinished() == 3){

            currentLevel++;
            level = new Level(levels[currentLevel]);
            //put some code to check if we're at last level pls congradulate them

        }else{

            level.update();
        }

        if(!level.haslaunched){

            gateangle = getAngleMouse();
        }
    }

    public void paint(Graphics g){

        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;

        //draw stuff here
        g2d.setBackground(Color.black);
        g2d.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        g2d.setColor(Color.green);
        Ellipse2D.Double circle;

        for(int i = 0; i < level.noPlanets(); i++){

            circle = new Ellipse2D.Double(level.getPlanet(i).getX(), level.getPlanet(i).getY(), level.getPlanet(i).getWidth(), level.getPlanet(i).getHeight());
            g2d.fill(circle);
        }

        g2d.setColor(Color.WHITE);
        circle = new Ellipse2D.Double(level.getEnd().getX(), level.getEnd().getY(), level.getEnd().getWidth(), level.getEnd().getHeight());
        g2d.fill(circle);

        //TODO Player and Gate angle is set to 180 degrees when looking straight forward

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

            level.getPlayerAngle();
        }
        t.rotate(pangletouse, level.getPlayer().getX() + (level.getPlayer().getWidth() / 2), level.getPlayer().getY() + (level.getPlayer().getHeight() / 2));
        t.translate(level.getPlayer().getX(), level.getPlayer().getY());

        g2d.drawImage(ship, t,null);

        //g2d.drawImage(gate_top, level.gatex, level.gatey, null);
        g2d.drawImage(gate_top, gate, null);

        if(!level.haslaunched){

            if(lasers == null){

                lasers = new BufferedImage(100, 120, BufferedImage.TYPE_INT_ARGB);
                Graphics2D lg = (Graphics2D)lasers.getGraphics();
                //rbg = 94, 198, 227
                lg.setColor(new Color(94, 198, 227));
                //left ship thing 4, 11 ; right ship thing 34, 11
                //left gate thing 11, 33 ; right gate thing 87, 33
                int nsx = 4;
                int nsy = 11;
                int ngx = 11;
                int ngy = 33;
                lg.setStroke(new BasicStroke(3));
                lg.drawLine(30 + nsx, 80 + nsy, ngx, ngy);
                nsx = 34;
                ngx = 87;
                ngy = 33;
                lg.drawLine(30 + nsx, 80 + nsy, ngx, ngy);
            }

//            AffineTransform lt = new AffineTransform();
//            lt.scale(1, 1);
//            lt.rotate(gateangle, playerstartx + (level.getPlayer().getWidth() / 2), playerstarty + (level.getPlayer().getHeight() / 2));
//            lt.translate(level.gatex, level.gatey);
            g2d.drawImage(lasers, gate, null);
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

        JFrame window = new JFrame("winter gam");
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
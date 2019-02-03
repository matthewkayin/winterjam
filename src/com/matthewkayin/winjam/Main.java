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
    private int levels[][][] = new int[][][]{
            { {0, 10, 10}, {1, 500, 500} , {4, 100, 100}, {4, 1000, 600} },
            { {0, 10, 10}, {1, 1000, 550}, {2, 500, 500} },
            { {0, 10, 10}, {1, 1000, 600}, {2, 500, 500}, {2, 200, 200} },
            { {0, 10, 10}, {1, 1250, 10}, {3, 700, 500}, {2, 100, 200} }
    };

    private BufferedImage gate_bottom;
    private BufferedImage gate_top;
    private BufferedImage ship;

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

        g2d.setColor(Color.cyan);

        for(int i = 0; i < level.getBHSize(); i++){

            circle = new Ellipse2D.Double(level.getBlackHole(i).getX(), level.getBlackHole(i).getY(), level.getBlackHole(i).getWidth(), level.getBlackHole(i).getHeight());
            g2d.fill(circle);
        }

        g2d.setColor(Color.WHITE);
        circle = new Ellipse2D.Double(level.getEnd().getX(), level.getEnd().getY(), level.getEnd().getWidth(), level.getEnd().getHeight());
        g2d.fill(circle);

        AffineTransform t = new AffineTransform();
        t.scale(1, 1);
        t.rotate(level.getPlayerAngle(), level.getPlayer().getX() + (level.getPlayer().getWidth() / 2), level.getPlayer().getY() + (level.getPlayer().getHeight() / 2));
        t.translate(level.getPlayer().getX(), level.getPlayer().getY());

        g2d.drawImage(ship, t,null);

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
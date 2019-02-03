package com.matthewkayin.winjam;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

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
                        s.playSound("beep");
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

        level = new Level();

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

        level.update();
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

        g2d.setColor(Color.RED);
        Rectangle2D.Double rect = new Rectangle2D.Double(level.getPlayer().getX(), level.getPlayer().getY(), level.getPlayer().getWidth(), level.getPlayer().getHeight());
        g2d.fill(rect);

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
        window.setSize(1280, 720);
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
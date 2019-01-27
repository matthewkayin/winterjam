package com.matthewkayin.winjam;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;

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

    public Main(){

        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setFocusable(true);
        requestFocus();
        setBackground(Color.pink);

        addMouseListener(new MouseAdapter(){

            public void mousePressed(MouseEvent e){


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


    }

    public void paint(Graphics g){

        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;

        //draw stuff here

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
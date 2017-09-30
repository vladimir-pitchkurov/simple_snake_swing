package com.vladimir;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements Runnable, KeyListener {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake");
        frame.add(new GamePanel());

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setPreferredSize(new Dimension(GamePanel.WIDTH, GamePanel.HEIGHT));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    public static final int WIDTH = 400;
    public static final int HEIGHT = 400;

    private Thread thread;
    private boolean running;
    private long targetTime;

    private Graphics2D graphics2D;
    private BufferedImage image;
    private Entity head;
    private ArrayList<Entity> snake;
    public static final int SIZE = 10;

    private int dx, dy;
    private boolean up, down, right, left, start, gameower;
    private Entity apple;
    private int score, level;


    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);
    }

    public void setFPS(int fps){
        targetTime = 1000/fps;
    }

    public void setApple(){
        int x = (int)(Math.random() *(WIDTH-SIZE));
        int y = (int)(Math.random() * (HEIGHT - SIZE));

        x = x -(x%SIZE);
        y = y - (y % SIZE);
        apple.setPosition(x, y);
    }
    private void setUpLevel(){
        snake = new ArrayList<Entity>();
        head = new Entity(SIZE);
        head.setPosition(WIDTH / 2, HEIGHT / 2);
        snake.add(head);
        for (int i = 1; i < 10; i++) {
            Entity e = new Entity(SIZE);
            e.setPosition(head.getX() + (i*SIZE), head.getY());
            snake.add(e);
        }
        apple = new Entity(SIZE);
        setApple();
        score = 0;
        gameower = false;
        level = 1;
        dx = dy = 0;
        setFPS(level * 10);
    }
    @Override
    public void addNotify() {
        super.addNotify();
        thread = new Thread(this);
        thread.start();
    }


    public void keyTyped(KeyEvent e) {


    }


    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();

        if(k == KeyEvent.VK_UP){up = true;}
        if(k == KeyEvent.VK_DOWN){down = true;}
        if(k == KeyEvent.VK_RIGHT){right = true;}
        if(k == KeyEvent.VK_LEFT){left = true;}
        if(k == KeyEvent.VK_ENTER){start = true;}
    }


    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();

        if(k == KeyEvent.VK_UP){up = false;}
        if(k == KeyEvent.VK_DOWN){down = false;}
        if(k == KeyEvent.VK_RIGHT){right = false;}
        if(k == KeyEvent.VK_LEFT){left = false;}
        if(k == KeyEvent.VK_ENTER){start = false;}
    }


    public void run() {
        if(running){
            return;
        }
        init();
        long startTime;
        long elapsed;
        long wait;
        while (running){
            startTime = System.nanoTime();

            update();
            requestRender();


            elapsed = System.nanoTime() - startTime;
            wait = targetTime - elapsed/1000000;
            if(wait >0){
                try {
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void init() {
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        graphics2D = image.createGraphics();
        running = true;
        setUpLevel();

    }

    private void update() {

        if(gameower){
            if(start){
                setUpLevel();
            }
            return;
        }
        if(up && dy == 0){
            dy = -SIZE;
            dx = 0;
        }
        if(down && dy == 0){
            dy = SIZE;
            dx = 0;
        }
        if(left && dx == 0){
            dy = 0;
            dx = -SIZE;
        }
        if(right && dx == 0){
            dy = 0;
            dx = SIZE;
        }
        if(dx != 0 || dy != 0) {
            for (int i = snake.size() - 1; i > 0; i--) {
                snake.get(i).setPosition(snake.get(i - 1).getX(),
                        snake.get(i - 1).getY());
            }
            head.move(dx, dy);
        }
        for (Entity e : snake){
            if(e.isCollsion(head)){
                gameower = true;
                break;
            }
        }
        if(apple.isCollsion(head)){
            score++;
            setApple();
            Entity e = new Entity(SIZE);
            e.setPosition(-100, -100);
            snake.add(e);
            if(score % 10 == 0){
                level ++;
                if(level > 10){level = 10;}
                setFPS(level * 10);
            }

        }
        if(head.getX() < 0)head.setX(WIDTH);
        if(head.getX() > WIDTH)head.setX(0);
        if(head.getY() < 0)head.setY(HEIGHT);
        if(head.getY() > HEIGHT)head.setY(0);
    }

    private void requestRender() {
        render(graphics2D);
        Graphics g = getGraphics();
        g.drawImage(image, 0,0, null);
        g.dispose();

    }

    public void render(Graphics2D graphics2D){
        graphics2D.clearRect(0, 0, WIDTH, HEIGHT);
        graphics2D.setColor(Color.GREEN);
        for (Entity e : snake){
            e.render(graphics2D);
        }
        graphics2D.setColor(Color.YELLOW);
        apple.render(graphics2D);
        if(gameower){
            graphics2D.drawString("Game Over!", 150, 200);
        }
        graphics2D.setColor(Color.WHITE);
        graphics2D.drawString("Score : " + score + " Level : " + level,10, 10);
    }

}

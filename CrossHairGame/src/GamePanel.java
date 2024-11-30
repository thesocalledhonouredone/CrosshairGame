import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable {

    final int screenWidth = 1280;
    final int screenHeight = 720;
    final int FPS = 60;

    Thread gameThread;
    KeyHandler keyHandler = new KeyHandler();

    // corsshair position
    int crossX = screenWidth / 2;
    int crossY = screenHeight / 2;
    int crossSpeed = 7;

    // rectangle generation
    final int rectangleWidth = 50;
    final int rectangleHeight = 50;
    ArrayList<Rectangle> rectangles = new ArrayList<>();
    Random random = new Random();
    final int numberOfRectangles = 8;

    int score = 0;

    long lastTime = System.nanoTime();

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);

        for (int i = 0; i < numberOfRectangles; i++) {
            addRandomRectangle();
        }
    }

    private void addRandomRectangle() {
        int x = random.nextInt(screenWidth - rectangleWidth);
        int y = random.nextInt(screenHeight - rectangleHeight);
        rectangles.add(new Rectangle(x, y, rectangleWidth, rectangleHeight));
    }

    public void update() {
        if (keyHandler.up) {
            crossY -= crossSpeed;
        }
        if (keyHandler.down) {
            crossY += crossSpeed;
        }
        if (keyHandler.left) {
            crossX -= crossSpeed;
        }
        if (keyHandler.right) {
            crossX += crossSpeed;
        }

        // wrap screen logic
        if (crossX >= screenWidth) crossX = 0;
        if (crossX < 0) crossX = screenWidth - 1;
        if (crossY >= screenHeight) crossY = 0;
        if (crossY < 0) crossY = screenHeight - 1;

        // shoot logic
        if (keyHandler.shoot) {
            for (int i = 0; i < rectangles.size(); i++) {
                Rectangle rect = rectangles.get(i);
                if (rect.contains(crossX, crossY)) {
                    score++;
                    rectangles.remove(i);
                    addRandomRectangle();
                    break;
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.RED);
        for (Rectangle rect : rectangles) {
            g2.fillRect(rect.x, rect.y, rect.width, rect.height);
        }

        g2.setColor(Color.GREEN);
        g2.setStroke(new BasicStroke(2));
        int lineLength = 10;
        g2.drawLine(crossX - lineLength, crossY, crossX + lineLength, crossY);
        g2.drawLine(crossX, crossY - lineLength, crossX, crossY + lineLength);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 30));
        g2.drawString("Score: " + score, 20, 40);

        g2.dispose();
    }

    @Override
    public void run() {
        double drawInterval = (double) 1000000000 / FPS;
        double delta = 0;
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;

            lastTime = currentTime;

            if (delta > 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }
}

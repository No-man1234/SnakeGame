import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private class Tile {
        int x;
        int y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    int boardWidth;
    int boardHeight;
    int tileSize = 25;

    // Snake
    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    // Food
    Tile food;
    Random random;

    // Game logic
    Timer gameLoop;
    int velocityX;
    int velocityY;
    boolean gameOver = false;

    SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);

        initializeGame();
    }

    private void initializeGame() {
        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<Tile>();

        food = new Tile(10, 10);
        random = new Random();
        placeFood();

        velocityX = 0;
        velocityY = 0;
        gameOver = false;

        if (gameLoop != null) {
            gameLoop.stop();
        }
        gameLoop = new Timer(100, this);
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (gameOver) {
            // Display game over message
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            String gameOverText = "Game Over";
            int textWidth = g.getFontMetrics().stringWidth(gameOverText);
            g.drawString(gameOverText, (boardWidth - textWidth) / 2, boardHeight / 2 - 20);

            g.setFont(new Font("Arial", Font.PLAIN, 24));
            String scoreText = "Score: " + String.valueOf(snakeBody.size());
            textWidth = g.getFontMetrics().stringWidth(scoreText);
            g.drawString(scoreText, (boardWidth - textWidth) / 2, boardHeight / 2 + 10);

            String restartText = "Press SPACE to restart or '0' to exit";
            textWidth = g.getFontMetrics().stringWidth(restartText);
            g.drawString(restartText, (boardWidth - textWidth) / 2, boardHeight / 2 + 40);
        } else {
            // Food
            g.setColor(Color.red);
            g.fillOval(food.x * tileSize, food.y * tileSize, tileSize, tileSize);

            // Snake Head
            g.setColor(Color.green);
            g.fillRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize);

            // Snake Body
            for (int i = 0; i < snakeBody.size(); i++) {
                Tile snakePart = snakeBody.get(i);
                g.fillRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize);
            }

            // Score
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.drawString("Score: " + String.valueOf(snakeBody.size()), 10, 20);
        }
    }

    public void placeFood() {
        boolean validPosition = false;

        while (!validPosition) {
            food.x = random.nextInt(boardWidth / tileSize);
            food.y = random.nextInt(boardHeight / tileSize);

            // Check if the food position overlaps with the snake
            validPosition = true;
            if (collision(snakeHead, food)) {
                validPosition = false;
            } else {
                for (Tile snakePart : snakeBody) {
                    if (collision(snakePart, food)) {
                        validPosition = false;
                        break;
                    }
                }
            }
        }
    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public void move() {
        if (collision(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }

        // Snake Body
        for (int i = snakeBody.size() - 1; i >= 0; i--) {
            Tile snakePart = snakeBody.get(i);
            if (i == 0) {
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            } else {
                Tile prevSnakePart = snakeBody.get(i - 1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        // Snake Head
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        // Wrap around screen
        snakeHead.x = (snakeHead.x + boardWidth / tileSize) % (boardWidth / tileSize);
        snakeHead.y = (snakeHead.y + boardHeight / tileSize) % (boardHeight / tileSize);

        // Check for collision with snake body
        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);
            if (collision(snakeHead, snakePart)) {
                gameOver = true;
                break;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                initializeGame();
            } else if (e.getKeyChar() == '0') {
                System.exit(0);
            }
        } else {
            if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
                velocityX = 0;
                velocityY = -1;
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
                velocityX = 0;
                velocityY = 1;
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
                velocityX = -1;
                velocityY = 0;
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
                velocityX = 1;
                velocityY = 0;
            }else if (e.getKeyChar() == '0') {
                System.exit(0);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake");
        SnakeGame snakeGame = new SnakeGame(600, 600);

        frame.add(snakeGame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

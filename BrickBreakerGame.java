import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class BrickBreakerGame extends Application {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int PADDLE_WIDTH = 100;
    private static final int PADDLE_HEIGHT = 10;
    private static final int BALL_RADIUS = 10;
    private static final int BRICK_WIDTH = 50;
    private static final int BRICK_HEIGHT = 20;
    private static final int NUM_BRICKS = 30;
    private static final int NUM_ROWS = 5;

    private double paddleX = WIDTH / 2 - PADDLE_WIDTH / 2;
    private double ballX = WIDTH / 2;
    private double ballY = HEIGHT / 2;
    private double ballSpeedX = 5;
    private double ballSpeedY = 5;
    private int score = 0;
    private int level = 1;
    private int bricksRemaining = NUM_BRICKS;

    private List<Brick> bricks = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        StackPane root = new StackPane();
        root.getChildren().add(canvas);

        generateBricks();

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT && paddleX > 0)
                paddleX -= 10;
            else if (e.getCode() == KeyCode.RIGHT && paddleX < WIDTH - PADDLE_WIDTH)
                paddleX += 10;
        });

        primaryStage.setTitle("Brick Breaker Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render(gc);
            }
        }.start();
    }

    private void update() {
        ballX += ballSpeedX;
        ballY += ballSpeedY;

        if (ballX <= 0 || ballX >= WIDTH)
            ballSpeedX *= -1;

        if (ballY <= 0 || (ballY >= HEIGHT - PADDLE_HEIGHT - BALL_RADIUS && ballX >= paddleX && ballX <= paddleX + PADDLE_WIDTH))
            ballSpeedY *= -1;

        if (ballY > HEIGHT) {
            resetBall();
            decrementScore();
        }

        for (Brick brick : bricks) {
            if (brick.isVisible() && brick.intersects(ballX, ballY, BALL_RADIUS)) {
                brick.setVisible(false);
                ballSpeedY *= -1;
                incrementScore();
                bricksRemaining--;
            }
        }

        if (bricksRemaining == 0) {
            level++;
            bricksRemaining = NUM_BRICKS;
            generateBricks();
            resetBall();
            increaseDifficulty();
        }
    }

    private void render(GraphicsContext gc) {
        gc.clearRect(0, 0, WIDTH, HEIGHT);

        gc.setFill(Color.BLUE);
        gc.fillRect(paddleX, HEIGHT - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);

        gc.setFill(Color.RED);
        gc.fillOval(ballX - BALL_RADIUS, ballY - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);

        for (Brick brick : bricks) {
            if (brick.isVisible()) {
                gc.setFill(brick.getColor());
                gc.fillRect(brick.getX(), brick.getY(), BRICK_WIDTH, BRICK_HEIGHT);
            }
        }

        gc.setFill(Color.BLACK);
        gc.fillText("Score: " + score + "   Level: " + level, 10, 20);
    }

    private void resetBall() {
        ballX = WIDTH / 2;
        ballY = HEIGHT / 2;
        ballSpeedX = 5;
        ballSpeedY = 5;
    }

    private void generateBricks() {
        bricks.clear();
        double offsetX = (WIDTH - NUM_ROWS * BRICK_WIDTH) / 2;
        double offsetY = 50;
        Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE};

        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_BRICKS / NUM_ROWS; col++) {
                double x = offsetX + col * BRICK_WIDTH;
                double y = offsetY + row * BRICK_HEIGHT;
                Brick brick = new Brick(x, y, BRICK_WIDTH, BRICK_HEIGHT, colors[row]);
                bricks.add(brick);
            }
        }
    }

    private void incrementScore() {
        score += 10;
    }

    private void decrementScore() {
        if (score >= 10)
            score -= 10;
    }

    private void increaseDifficulty() {
        // Increase ball speed or add more bricks, etc.
        ballSpeedX += 1;
        ballSpeedY += 1;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static class Brick {
        private double x, y, width, height;
        private Color color;
        private boolean visible = true;

        public Brick(double x, double y, double width, double height, Color color) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = color;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public Color getColor() {
            return color;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        public boolean intersects(double px, double py, double radius) {
            return px + radius > x && px - radius < x + width && py + radius > y && py - radius < y + height;
        }
    }
}

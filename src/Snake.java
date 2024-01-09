import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Snake extends Application {
    public static void main(String[] args) {
        launch();
    }

    private final int WIDTH = 800;
    private final int HEIGHT = 800;
    private final int COUNT_CELLS = 20;
    private final int SIZE_CELL = WIDTH/COUNT_CELLS;
    private Point[] SNAKE_BODY = new Point[5];
    private Point SNAKE_HEAD;
    private String direction;
    private Timeline timeline;
    private Point FOOD;
    private final String[] IMAGE_FOODS = {"banana.png", "hamburger.png", "apple.png", "pizza.png", "pie.png"};
    private Image IMAGE_FOOD;
    private Label label;

    private int score = 0;

    Canvas canvas;
    GraphicsContext context;


    private void drawField(GraphicsContext context){
        for (int i = 0; i < COUNT_CELLS; i++) {
            for (int j = 0; j < COUNT_CELLS; j++) {
                if (((i + j) % 2 == 0)) {
                    context.setFill(Color.OLIVE);
                } else {
                    context.setFill(Color.GRAY);
                }
                context.fillRect(
                        i*SIZE_CELL,
                        j*SIZE_CELL,
                        SIZE_CELL,
                        SIZE_CELL
                );
            }
        }
    }

    private void generateInitialSnake(){
        SNAKE_BODY[0] = new Point(5, 2);
        SNAKE_BODY[1] = new Point(4, 2);
        SNAKE_BODY[2] = new Point(3, 2);
        SNAKE_BODY[3] = new Point(2, 2);
        SNAKE_BODY[4] = new Point(1, 2);

        SNAKE_HEAD = SNAKE_BODY[0];
    }

    private void generateInitialDirection(){
        this.direction = "DOWN";
    }

    private void drawSnake(GraphicsContext context){

        // draw head
        context.setFill(Color.RED);
        context.fillRoundRect(
                SNAKE_HEAD.getX()*SIZE_CELL,
                SNAKE_HEAD.getY()*SIZE_CELL,
                SIZE_CELL,
                SIZE_CELL,
                30,
                30
        );

        for (int i = 1; i < SNAKE_BODY.length; i++) {
            context.setFill(Color.GREEN);
            context.fillRoundRect(
                    SNAKE_BODY[i].getX()*SIZE_CELL,
                    SNAKE_BODY[i].getY()*SIZE_CELL,
                    SIZE_CELL,
                    SIZE_CELL,
                    20,
                    20
            );
        }
    }

    private void move() {

        // after this operation
        // current el = previous
        // (in period)

        Point oldHead = new Point(SNAKE_HEAD.getX(), SNAKE_HEAD.getY());

        Point[] bufferMas = new Point[SNAKE_BODY.length];
        System.arraycopy(SNAKE_BODY, 0, bufferMas, 1, SNAKE_BODY.length-1);
        bufferMas[0] = new Point(oldHead.getX(), oldHead.getY());

        SNAKE_BODY = bufferMas;
        SNAKE_HEAD = SNAKE_BODY[0];


        switch (direction) {
            case "LEFT":
                SNAKE_HEAD.minusX();
                break;
            case "RIGHT":
                SNAKE_HEAD.plusX();
                break;
            case "UP":
                SNAKE_HEAD.minusY();
                break;
            case "DOWN":
                SNAKE_HEAD.plusY();
                break;
        }

    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Snake");
        FlowPane pane = new FlowPane(10, 10);
        pane.setAlignment(Pos.CENTER);
        pane.setOrientation(Orientation.VERTICAL);
        Scene scene = new Scene(pane, 900, 900);
        label = new Label("Score: 0");
        label.setFont(new Font(30));


        ///////Main logic///////

        canvas = new Canvas(WIDTH, HEIGHT);
        context = canvas.getGraphicsContext2D();

        // generate snake body
        generateInitialSnake();

        // generate FOOD Point
        // draw food
        generateFoodPoint();
        drawFood(context);

        // generate direction
        generateInitialDirection();

        KeyFrame frame = new KeyFrame(new Duration(300), businessLogic);

        timeline = new Timeline(frame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        scene.setOnKeyPressed(trackKeyPress);

        ///////Main logic///////

        pane.getChildren().addAll(label, canvas);
        primaryStage.setScene(scene);
        primaryStage.show();
    }



    private void showBody(){
        for (Point point: SNAKE_BODY) {
            System.out.println(point);
        }
        System.out.println("--------");
    }



    EventHandler<ActionEvent> businessLogic = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            // check game over
            if (!isGameOver()) {
                // draw field
                drawField(context);

                // draw food
                drawFood(context);

                // change position snake of body
                move();

                // draw snake
                drawSnake(context);

                if (isEating()){
                    score++;
                    generateFoodPoint();
                    drawField(context);
                    drawFood(context);
                    drawSnake(context);
                    label.setText("Score: "+score);
                }

            } else {
                gameOver();
            }
        }
    };


    EventHandler<KeyEvent> trackKeyPress = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent e) {
            String justDirection = e.getCode().toString();
            switch (justDirection){
                case "LEFT":
                    if (direction!="RIGHT") direction = justDirection;
                    break;
                case "RIGHT":
                    if (direction!="LEFT") direction = justDirection;
                    break;
                case "UP":
                    if (direction!="DOWN") direction = justDirection;
                    break;
                case "DOWN":
                    if (direction!="UP") direction = justDirection;
                    break;
            }
        }
    };

    private boolean isGameOver(){
        boolean isContainHeadInBody = false;
        for (int i = 1; i < SNAKE_BODY.length; i++) {
            isContainHeadInBody = (SNAKE_BODY[i].equals(SNAKE_HEAD));
        }

        return (SNAKE_HEAD.getX()>COUNT_CELLS
                || SNAKE_HEAD.getX()<0
                || SNAKE_HEAD.getY()>COUNT_CELLS
                || SNAKE_HEAD.getY()<0
                || isContainHeadInBody
                );
    }

    private void gameOver(){
        timeline.stop();
    }

    private void generateFoodPoint(){
        FOOD = new Point(
                 ((int) (Math.random()*COUNT_CELLS)),
                 ((int) (Math.random()*COUNT_CELLS))
        );
        IMAGE_FOOD = new Image("file:pictures/"+ IMAGE_FOODS[((int) (Math.random()*IMAGE_FOODS.length))]);
        System.out.println(FOOD);
    }

    private void drawFood(GraphicsContext context){
        context.drawImage(IMAGE_FOOD, FOOD.getX()*SIZE_CELL, FOOD.getY()*SIZE_CELL, SIZE_CELL, SIZE_CELL);
    }

    private boolean isEating(){
        if (FOOD.equals(SNAKE_HEAD)){
            Point addedPart = new Point(-1,-1);
            Point[] newBody = new Point[SNAKE_BODY.length+1];
            System.arraycopy(SNAKE_BODY, 0, newBody, 0, SNAKE_BODY.length);
            SNAKE_BODY = newBody;
            SNAKE_BODY[SNAKE_BODY.length-1] = addedPart;
            return true;
        }
        return false;
    }
}

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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
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
    private Button restartButton;
    private Label status;
    private int speed = 250;
    private KeyFrame frame;
    private boolean doubleSpeed;

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

        SNAKE_BODY = new Point[5];

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

        for (int i = 1; i < SNAKE_BODY.length-1; i++) {
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
        restartButton = new Button("Restart");
        restartButton.setFont(new Font(30));
        status = new Label("IN GAME");
        status.setFont(new Font(30));
        ///////Main logic///////

        canvas = new Canvas(WIDTH, HEIGHT);
        context = canvas.getGraphicsContext2D();

        // generate snake body
        generateInitialSnake();

        // generate direction
        generateInitialDirection();

        // generate FOOD Point
        // draw food
        generateFoodPoint();
        drawFood(context);

        frame = new KeyFrame(new Duration(speed), businessLogic);
        timeline = new Timeline(frame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();


        scene.setOnKeyPressed(trackKeyPress);

        scene.setOnKeyReleased(trackDoubleSpeed);


        restartButton.setOnAction(event->{
            score = 0;
            label.setText("Score: 0");
            generateInitialSnake();
            generateInitialDirection();
            generateFoodPoint();
            timeline.playFromStart();
        });

        ///////Main logic///////

        BorderPane box = new BorderPane();
        box.setLeft(label);
        box.setCenter(status);
        box.setRight(restartButton);

        restartButton.setFocusTraversable(false);

        pane.getChildren().addAll(box, canvas);
        primaryStage.setScene(scene);
        primaryStage.show();
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

            if (justDirection.equals("SPACE") && !doubleSpeed){
                doubleSpeed = true;
                speed = 100;
                timeline.stop();

                timeline.getKeyFrames().setAll(new KeyFrame(new Duration(speed), businessLogic));
                timeline.play();
            }

            switch (justDirection){
                case "LEFT":
                    if (!direction.equals("RIGHT")) direction = justDirection;
                    break;
                case "RIGHT":
                    if (!direction.equals("LEFT")) direction = justDirection;
                    break;
                case "UP":
                    if (!direction.equals("DOWN")) direction = justDirection;
                    break;
                case "DOWN":
                    if (!direction.equals("UP")) direction = justDirection;
                    break;
            }
        }
    };

    EventHandler<KeyEvent> trackDoubleSpeed = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            if (event.getCode().toString().equals("SPACE")){
                speed = 250;
                timeline.stop();

                timeline.getKeyFrames().setAll(new KeyFrame(new Duration(speed), businessLogic));
                timeline.play();
                doubleSpeed = false;
            }
        }
    };

    private boolean isGameOver(){
        for (int i = 1; i < SNAKE_BODY.length; i++) {
            if (SNAKE_BODY[i].equals(SNAKE_HEAD)){
                return true;
            }
        }

        return (SNAKE_HEAD.getX()>=COUNT_CELLS
                || SNAKE_HEAD.getX()<0
                || SNAKE_HEAD.getY()>=COUNT_CELLS
                || SNAKE_HEAD.getY()<0
                );
    }

    private void gameOver(){
        status.setText("GAME OVER");
        timeline.stop();
    }

    private void generateFoodPoint(){
        FOOD = new Point(
                 ((int) (Math.random()*COUNT_CELLS)),
                 ((int) (Math.random()*COUNT_CELLS))
        );
        
        
        int random_int = ((int) (Math.random()*IMAGE_FOODS.length));

        // Toggle 1 - If you want to run a program using a Java utility - uncomment this
        // Toggle 1 - (the preferred)
        IMAGE_FOOD = new Image("file:pictures/"+ IMAGE_FOODS[random_int]);

        // Toggle 2 - If you want to create jar file from this program, and booting recourses from jar file - uncomment this
        //IMAGE_FOOD = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
        //String path = "/pictures/" + IMAGE_FOODS[random_int];
        
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

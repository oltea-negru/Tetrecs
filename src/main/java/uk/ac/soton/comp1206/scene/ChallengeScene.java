package uk.ac.soton.comp1206.scene;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.network.Multimedia;
import uk.ac.soton.comp1206.network.Scores;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.Calendar;
import java.util.Set;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * The game played on this challenge scene
     */
    protected Game game;

    /**
     * The UI labels for the score, lives and level
     */
    public Text scoreLabel;
    public Text scoreText = new Text("Score: ");
    public Text livesLabel;
    public Text livesText = new Text("Lives: ");
    public Text levelLabel;
    public Text levelText = new Text("Level: ");
    public Text highScoreText=new Text("High Score: ");
    public Text highScoreLabel;

    /**
     * The main board for all the pieces
     */
    private GameBoard board;

    /**
     * The big piece board for the current piece
     */
    private PieceBoard pieceBoard;

    /**
     * The little piece board for the incoming piece
     */
    private PieceBoard littleBoard;

    /**
     * The x coordinate in the main board of the keyboard
     */
    private static int x;

    /**
     * The y coordinate in the main board of the keyboard
     */
    private static int y;

    /**
     * Create a new Single Player challenge scene
     *
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        //the keyboard hovering starts at [0,0]
        x = 0;
        y = 0;
        logger.info("Creating Challenge Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());
        setupGame();
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        //the big pane setup
        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        //the main pane setup
        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        //creating the timer bar
        var timer = new Rectangle(0, gameWindow.getWidth() + 2, gameWindow.getWidth() / 1.2, (float) gameWindow.getHeight() / 25);
        timer.setFill(Color.GREEN);
        mainPane.setBottom(timer);

        //creating the main board with the current game status
        board = new GameBoard(game.getGrid(), (float) gameWindow.getWidth() / 2, (float) gameWindow.getWidth() / 2);
        mainPane.setCenter(board);

        //creating a small pane for the two little piece boards
        var paneForNextPieces = new BorderPane();
        mainPane.setRight(paneForNextPieces);

        var pane1=new BorderPane();
        paneForNextPieces.setCenter(pane1);

        //the big piece board for the current piece
        pieceBoard = new PieceBoard(game.currentPiece, 180, 180);
        pieceBoard.setTranslateX(-30);
        pane1.setCenter(pieceBoard);

        var overLittlePieceBoard= new Text("Next Piece");
        overLittlePieceBoard.getStyleClass().add("level");
        pane1.setBottom(overLittlePieceBoard);

        //the little piece board for the incoming piece
        littleBoard = new PieceBoard(game.incomingPiece, 100, 100);
        littleBoard.setTranslateX(20);
        paneForNextPieces.setBottom(littleBoard);

        //creating labels for properties
        scoreLabel = new Text();
        livesLabel = new Text();
        levelLabel = new Text();
        highScoreLabel=new Text(Integer.toString(Scores.getHighScore()));

        //binding the text labels to the integer property values
        scoreLabel.textProperty().bind(game.scoreProperty().asString());
        livesLabel.textProperty().bind(game.livesProperty().asString());
        levelLabel.textProperty().bind(game.levelProperty().asString());


        //styling the text labels
        scoreLabel.getStyleClass().add("level");
        scoreText.getStyleClass().add("level");
        levelLabel.getStyleClass().add("lives");
        levelText.getStyleClass().add("lives");
        livesLabel.getStyleClass().add("lives");
        livesText.getStyleClass().add("lives");
        highScoreLabel.getStyleClass().add("lives");
        highScoreText.getStyleClass().add("lives");

        //creating HBoxes to organize the UI components
        HBox scoreBox = new HBox(scoreText, scoreLabel);
        HBox livesBox = new HBox(livesText, livesLabel);
        HBox levelBox = new HBox(levelText, levelLabel);
        HBox highScoreBox= new HBox(highScoreText, highScoreLabel);

        pane1.setTop(scoreBox);
        scoreBox.setTranslateY(70);

        HBox bigHBox = new HBox(50, livesBox, levelBox,highScoreBox);
        bigHBox.setAlignment(Pos.CENTER);
        mainPane.setTop(bigHBox);

        //rotating the current piece when the right mouse button is clicked on the main board
        board.setOnRightClicked((e) -> {
            Multimedia.playAudio("rotate.wav");
            game.rotateCurrentPiece();
        });

        //rotating the current piece when the left mouse button is clicked on the big piece board
        pieceBoard.setOnBlockClick((e) -> {
            Multimedia.playAudio("rotate.wav");
            game.rotateCurrentPiece();
        });

        //swapping the pieces when the left mouse button is clicked on the little piece board
        littleBoard.setOnBlockClick((e) -> {
            Multimedia.playAudio("transition.wav");
            game.swapCurrentPiece();
        });

        //Handle block on game board grid being clicked
        board.setOnBlockClick(this::blockClicked);

        //updating the listener when the game pieces change
        game.setNextPieceListener((one, two) -> {
            receivePiece(one);
            receiveNextPiece(two);
        });

        //updating the listener when lines are cleared
        game.setLineClearedListener((e)->{
            Multimedia.playAudio("clear.wav");
            clearLines(e);
        });

        //updating the listener when the time changes
        game.setGameLoopListener((e) -> {
            Platform.runLater(() ->
                    timerAnimation(timer, game.getTimerDelay()));
        });

        //updating the listener when the lives count goes to zero
        game.setLivesListener(() -> {
            game.timer.cancel();
                Platform.runLater(()-> gameWindow.startScores(game));
        });
    }

    /**
     * Handle when a block is clicked
     *
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
    }

    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        keyPressed();
        game.start();
    }

    /**
     * Outputs the piece on the big piece board
     *
     * @param piece
     */
    public void receivePiece(GamePiece piece) {
        pieceBoard.showPiece(piece);
    }

    /**
     * Outputs the piece on the little piece board
     *
     * @param piece
     */
    public void receiveNextPiece(GamePiece piece) {
        littleBoard.showPiece(piece);
    }

    /**
     * Clears line using the set of blocks coordinates
     *
     * @param set
     */
    public void clearLines(Set<int[]> set) {
        board.clearLine(set);
    }

    /**
     * Handles moving upwards using the keyboard
     */
    private void moveUp() {
        if (y > 0)
            y--;
        logger.info("The current coordinates are {} and {}", getY(), getX());
    }

    /**
     * Handles moving downwards using the keyboard
     */
    private void moveDown() {
        if (y < game.getRows() - 1)
            y++;
        logger.info("The current coordinates are {} and {}", getY(), getX());
    }

    /**
     * Handles moving towards left using the keyboard
     */
    private void moveLeft() {
        if (x > 0)
            x--;
        logger.info("The current coordinates are {} and {}", getY(), getX());
    }

    /**
     * Handles moving towards right using the keyboard
     */
    private void moveRight() {
        if (x < game.getCols() - 1)
            x++;
        logger.info("The current coordinates are {} and {}", getY(), getX());
    }

    /**
     * Rotates the current piece right
     */
    private void rotateRight() {
        game.rotateCurrentPiece();
        logger.info("The current piece {} has been rotated RIGHT", game.currentPiece.toString());
    }

    /**
     * Rotates the current piece left
     */
    private void rotateLeft() {
        game.rotateCurrentPiece();
        game.rotateCurrentPiece();
        game.rotateCurrentPiece();
        logger.info("The current piece {} has been rotated LEFT", game.currentPiece.toString());
    }

    /**
     * Get the x of the current keyboard position
     *
     * @return
     */
    private int getX() {
        return x;
    }

    /**
     * Get the y of the current keyboard position
     *
     * @return
     */
    private int getY() {
        return y;
    }

    /**
     * Handles what should happen when different keys are pressed on the keyboard
     */
    private void keyPressed() {
        scene.setOnKeyPressed((e) -> {
            //the current block should have no hover effect unless it has been entered
            board.getBlock(x, y).paint();
            if (e.getCode() == KeyCode.ESCAPE){
                Multimedia.stop();
                game.timer.cancel();
                gameWindow.startMenu();}
            else if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.X)
                game.blockClicked(board.getBlock(x, y));
            else if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.R)
                game.swapCurrentPiece();
            else if (e.getCode() == KeyCode.Q || e.getCode() == KeyCode.Z || e.getCode() == KeyCode.OPEN_BRACKET)
                rotateLeft();
            else if (e.getCode() == KeyCode.E || e.getCode() == KeyCode.C || e.getCode() == KeyCode.CLOSE_BRACKET)
                rotateRight();
            else if (e.getCode() == KeyCode.W || e.getCode() == KeyCode.UP)
                moveUp();
            else if (e.getCode() == KeyCode.S || e.getCode() == KeyCode.DOWN)
                moveDown();
            else if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.LEFT)
                moveLeft();
            else if (e.getCode() == KeyCode.D || e.getCode() == KeyCode.RIGHT)
                moveRight();

            //the block has been entered so now has a hover effect
            board.getBlock(x, y).hoverPaint();
        });
    }

    /**
     * Creates the animation responsible for the timer
     *
     * @param timer
     * @param duration
     */
    public void timerAnimation(Rectangle timer, int duration) {
        double calendar = Calendar.getInstance().getTimeInMillis();

        //the initial ratio is 1
        double ratio = 1;

        //setter for the dimensions
        timer.setWidth(ratio * gameWindow.getWidth());

        //creating a new timer
        AnimationTimer animationTimerRectangle = new AnimationTimer() {
            double timeLeft = duration;
            int redColor;
            int greenColor;

            //handles the colors considering the ratio
            @Override
            public void handle(long l) {
                if (timeLeft > 0) {
                    Calendar currentCalendar = Calendar.getInstance();
                    timeLeft = duration - (currentCalendar.getTimeInMillis() - calendar);
                    double ratio = timeLeft / duration;
                    timer.setWidth(ratio * gameWindow.getWidth());

                    if (ratio > 0.9) {
                        greenColor = 128;
                    } else if (ratio <= 0.9 && ratio > 0.5) {
                        greenColor = (int) Math.floor(-317.5 * ratio + 413.75);
                        redColor = (int) Math.floor(-637.5 * ratio + 573.75);
                    } else if (ratio > 0.1 && ratio < 0.5) {
                        greenColor = (int) Math.floor(637.5 * ratio - 63.75);
                    } else if (ratio < 0.1) {
                        greenColor = 0;
                    }
                    timer.setFill(Color.rgb(redColor, greenColor, 0));
                } else {
                    stop();
                }
            }
        };
        animationTimerRectangle.start();
    }
}

package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.LivesListener;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.network.Multimedia;

import java.util.*;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     *
     * @param cols number of columns
     * @param rows number of rows
     */

    /**
     * The current piece to be played
     */
    public GamePiece currentPiece;

    /**
     * The next piece that should be played after the current one
     */
    public GamePiece incomingPiece;

    /**
     * The listener to call when dealing with the game pieces
     */
    private NextPieceListener nextPieceListener;

    /**
     * The listener to call when a line is cleared
     */
    private LineClearedListener lineClearedListener;

    /**
     * The listener to call when the time ran out
     */
    private GameLoopListener gameLoopListener;

    /**
     * The listener to call when the lives property is zero
     */
    private LivesListener livesListener;

    /**
     * Integer properties for the score, lives count, multiplier and level
     */
    protected IntegerProperty score = new SimpleIntegerProperty(0);
    protected IntegerProperty lives = new SimpleIntegerProperty(3);
    protected IntegerProperty multiplier = new SimpleIntegerProperty(1);
    protected IntegerProperty level = new SimpleIntegerProperty(0);

    /**
     * The timer used to fire gameLoop method with initial value of 12 seconds
     */
    public Timer timer;
    private TimerTask timerTask;
    private int time = 12000;

    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols, rows);

        //The current piece is spawned once the game is created
        currentPiece = spawnPiece();

        //The next piece is spawned as soon as the current piece is
        incomingPiece = spawnPiece();
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
        startTimer();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
        logger.info("The first piece is {}", currentPiece.toString());
    }

    /**
     * Handle what should happen when a particular block is clicked
     *
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();
        logger.info("Block [{},{}] was clicked",x,y);

        // If a piece cannot be played it should be remembered until the timer runs out
        if (getGrid().canPlayPiece(currentPiece, x, y)) {
            getGrid().playPiece(currentPiece, x, y);
            afterPiece();
            nextPiece();
            Multimedia.playAudio("place.wav");
        } else
            Multimedia.playAudio("fail.wav");
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     *
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     *
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     *
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Get the score value
     * @return
     */
    public int getScore() {
        return score.get();
    }

    /**
     * Set a score value
     * @param value
     */
    protected void setScore(int value) {
        score.set(value);
    }

    /**
     * Get the score property
     * @return
     */
    public IntegerProperty scoreProperty() {
        return score;
    }

    /**
     * Get the lives amount
     * @return
     */
    public int getLives() {
        return lives.get();
    }

    /**
     * Set a lives value
     * @param value
     */
    protected void setLives(int value) {
        lives.set(value);
    }

    /**
     * Get the lives property
     * @return
     */
    public IntegerProperty livesProperty() {
        return lives;
    }

    /**
     * Get the multiplier value
     * @return
     */
    protected int getMultiplier() {
        return multiplier.get();
    }

    /**
     * Set a multiplier value
     * @param value
     */
    protected void setMultiplier(int value) {
        multiplier.set(value);
    }

    /**
     * Get the multiplier property
     * @return
     */
    public IntegerProperty multiplierProperty() {
        return multiplier;
    }

    /**
     * Get the level value
     * @return
     */
    protected int getLevel() {
        return level.get();
    }

    /**
     * Set a level value
     * @param value
     */
    protected void setLevel(int value) {
        level.set(value);
    }

    /**
     * Get the level property
     * @return
     */
    public IntegerProperty levelProperty() {
        return level;
    }

    /**
     * Return a random game piece from all 15 possibilities
     * @return
     */
    public GamePiece spawnPiece() {
        return GamePiece.createPiece(new Random().nextInt(14));
    }

    /**
     * Return the value that should be added to the score value after each piece played
     * @param lines
     * @param blocks
     * @return
     */
    private int updateScore(int lines, int blocks) {
        if (lines != 0)
            return lines * 10 * blocks * getMultiplier();

        //In case of no lines being cleared
        return 0;
    }

    /**
     * Adds to the current score value
     * @param value
     */
    private void addToScore(int value){
        if(getScore()/1000!= (getScore()+value)/1000)
            Multimedia.playAudio("levelUp.wav");
        setScore(getScore()+value);
    }

    /**
     * Changes the level regarding the score value
     */
    private void checkLevel(){
        setLevel(getScore() / 1000);
    }

    /**
     * Handles the incoming piece being transformed in the current one while a new incoming piece is created
     * and start the timer countdown from the previous piece played
     */
    public void nextPiece() {
        currentPiece = incomingPiece;
        incomingPiece = spawnPiece();
        nextPieceListener.nextPiece(currentPiece, incomingPiece);
        startTimer();
        logger.info("Next piece is {}", currentPiece.toString());
    }

    /**
     * Handles the lines being full of game blocks either if they are a row or a column, updates the score and cancels the
     * timer
     */
    public void afterPiece() {
        //Array for all rows to be cleared
        ArrayList<Integer> rows = isAnyRowFull();

        //Array for all the columns to be cleared
        ArrayList<Integer> columns = isAnyColumnFull();

        //Set for all block coordinates to be cleared
        Set<int[]> set = new HashSet<>();

        //Initially no blocks have been cleared
        int blocksCleared = 0;

        for (Integer row : rows)
            for (int j = 0; j < grid.getCols(); j++) {
                //If the block value is 0 it means it has already been cleared so it should not be counted anymore
                if (grid.get(row, j) == 0) blocksCleared--;

                //Resetting the block value to 0
                grid.set(row, j, 0);

                //Storing the block's coordinates for the fade out clearing of lines
                int[] coordinates = new int[2];
                coordinates[0] = row;
                coordinates[1] = j;
                set.add(coordinates);

                blocksCleared++;
            }

        for (Integer column : columns)
            for (int i = 0; i < grid.getRows(); i++) {
                //If the block value is 0 it means it has already been cleared so it should not be counted anymore
                if (grid.get(i, column) == 0) blocksCleared--;

                //Resetting the block value to 0
                grid.set(i, column, 0);

                //Storing the block's coordinates for the fade out clearing of lines
                int[] coordinates = new int[2];
                coordinates[0] = i;
                coordinates[1] = column;
                set.add(coordinates);

                blocksCleared++;
            }

        //Updating the score considering the number of lines and blocks that have been cleared
        int newScore = updateScore(rows.size() + columns.size(), blocksCleared);
        addToScore(newScore);

        //If no lines were cleared this round the multiplier is set back to 1, otherwise it increases by 1
        if (rows.isEmpty() && columns.isEmpty())
            setMultiplier(1);
        else
            setMultiplier(getMultiplier() + 1);

        //In case the score increased the level should change accordingly
        checkLevel();

        lineClearedListener.clearLine(set);
        timer.cancel();
    }

    /**
     * Returns an array list of full row indexes
     * @return
     */
    private ArrayList<Integer> isAnyRowFull() {
        var fullRowArray = new ArrayList<Integer>();
        for (int i = 0; i < grid.getRows(); i++) {
            int fullRow = 0;
            for (int j = 0; j < grid.getCols(); j++)
                if (grid.get(i, j) != 0)
                    fullRow++;
            if (fullRow == grid.getCols())
                fullRowArray.add(i);
        }
        return fullRowArray;
    }

    /**
     * Returns an array list of full column indexes
     * @return
     */
    private ArrayList<Integer> isAnyColumnFull() {
        var fullColumnArray = new ArrayList<Integer>();
        for (int j = 0; j < grid.getCols(); j++) {
            int fullCol = 0;
            for (int i = 0; i < grid.getRows(); i++)
                if (grid.get(i, j) != 0)
                    fullCol++;
            if (fullCol == grid.getRows())
                fullColumnArray.add(j);
        }
        return fullColumnArray;
    }

    /**
     * Sets the class Next Piece Listener to the listener passed as a parameter
     * @param listener
     */
    public void setNextPieceListener(NextPieceListener listener) {
        this.nextPieceListener = listener;
    }

    /**
     * Sets the class Line Cleared Listener to the listener passed as a parameter
     * @param listener
     */
    public void setLineClearedListener(LineClearedListener listener) {
        this.lineClearedListener = listener;
    }

    /**
     * Sets the class Game Loop Listener to the listener passed as a parameter
     * @param listener
     */
    public void setGameLoopListener(GameLoopListener listener) {
        this.gameLoopListener = listener;
    }

    /**
     * Sets the class Lives Listener to the listener passed as a parameter
     * @param listener
     */
    public void setLivesListener(LivesListener listener) {
        this.livesListener = listener;
    }

    /**
     * Rotates to the right the current piece and updates the listener
     */
    public void rotateCurrentPiece() {
        logger.info("The current piece {} has been rotated", currentPiece.toString());
        currentPiece.rotate();
        nextPieceListener.nextPiece(currentPiece, incomingPiece);
    }

    /**
     * Swaps the current piece with the incoming one and updates the listener
     */
    public void swapCurrentPiece() {
        logger.info("The pieces {} and {} has been swapped",currentPiece.toString(),incomingPiece.toString());
        GamePiece gamePiece= currentPiece;
        currentPiece = incomingPiece;
        incomingPiece = gamePiece;
        nextPieceListener.nextPiece(currentPiece, incomingPiece);
    }

    /**
     * Updates the time value for every regarding the level value
     */
    public void setTimeDelay() {
        time = 12000 - 500 * getLevel();
        if (time < 2500)
            time = 2500;

        logger.info("Time allocated: {} milliseconds", time);

        //Notifies the listener about the time change
        gameLoopListener.setOnGameLoop(time);
    }

    /**
     * Returns the time value
     * @return
     */
    public int getTimerDelay() {
        return time;
    }

    /**
     * Handles what should hapen when the time runs out
     */
    public void gameLoop() {
        //In case the lives value is zero the game stops
        if (getLives() == 0) {
            logger.info("Life limit has been reached!");
            livesListener.zeroLivesYet();
            Multimedia.playAudio("gameOver.wav");
            timer.cancel();
        }
        setMultiplier(1);
        setLives(getLives() - 1);
        nextPiece();
        Multimedia.playAudio("losingLife.wav");
    }

    /**
     * Creates a timer task that runs gameLoop method and schedules it to be executed in the specified time limit
     */
    public void startTimer() {
        logger.info("New timer has started!");
        timerTask = new TimerTask() {
            @Override
            public void run() {
                gameLoop();
            }
        };
        timer = new Timer("Timer");
        setTimeDelay();
        timer.schedule(timerTask, getTimerDelay());
    }
}



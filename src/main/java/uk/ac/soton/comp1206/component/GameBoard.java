package uk.ac.soton.comp1206.component;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.event.RightClicked;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.network.Multimedia;

import java.util.Set;

/**
 * A GameBoard is a visual component to represent the visual GameBoard.
 * It extends a GridPane to hold a grid of GameBlocks.
 * <p>
 * The GameBoard can hold an internal grid of it's own, for example, for displaying an upcoming block. It also be
 * linked to an external grid, for the main game board.
 * <p>
 * The GameBoard is only a visual representation and should not contain game logic or model logic in it, which should
 * take place in the Grid.
 */
public class GameBoard extends GridPane {

    private static final Logger logger = LogManager.getLogger(GameBoard.class);

    /**
     * Number of columns in the board
     */
    private final int cols;

    /**
     * Number of rows in the board
     */
    private final int rows;

    /**
     * The visual width of the board - has to be specified due to being a Canvas
     */
    private final double width;

    /**
     * The visual height of the board - has to be specified due to being a Canvas
     */
    private final double height;

    /**
     * The grid this GameBoard represents
     */
    final Grid grid;

    /**
     * The blocks inside the grid
     */
    GameBlock[][] blocks;

    /**
     * The listener to call when a specific block is clicked
     */
    private BlockClickedListener blockClickedListener;

    /**
     * The listener to call when riht click is pressed
     */
    private RightClicked setOnRightClick;


    /**
     * Create a new GameBoard, based off a given grid, with a visual width and height.
     *
     * @param grid   linked grid
     * @param width  the visual width
     * @param height the visual height
     */
    public GameBoard(Grid grid, double width, double height) {
        this.cols = grid.getCols();
        this.rows = grid.getRows();
        this.width = width;
        this.height = height;
        this.grid = grid;
        //Build the GameBoard
        build();
    }

    /**
     * Get a specific block from the GameBoard, specified by it's row and column
     *
     * @param x column
     * @param y row
     * @return game block at the given column and row
     */
    public GameBlock getBlock(int x, int y) {
        return blocks[x][y];
    }

    /**
     * Build the GameBoard by creating a block at every x and y column and row
     */
    protected void build() {
        logger.info("Building grid: {} x {}", cols, rows);

        //setting grid's dimensions
        setMaxWidth(width);
        setMaxHeight(height);

        setGridLinesVisible(true);

        //creating an array of GameBlocks
        blocks = new GameBlock[cols][rows];

        for (var y = 0; y < rows; y++) {
            for (var x = 0; x < cols; x++) {
                createBlock(x, y);
            }
            //creating listeners for every block
            setOnMouseClicked(this::rightClicked);
        }
    }

    /**
     * Create a block at the given x and y position in the GameBoard
     *
     * @param x column
     * @param y row
     */
    protected GameBlock createBlock(int x, int y) {
        var blockWidth = width / cols;
        var blockHeight = height / rows;

        //Create a new GameBlock UI component
        GameBlock block = new GameBlock(this, x, y, blockWidth, blockHeight);

        //Add to the GridPane
        add(block, x, y);

        //Add to our block directory
        blocks[x][y] = block;

        //Link the GameBlock component to the corresponding value in the Grid
        block.bind(grid.getGridProperty(x, y));

        //Add a mouse click handler to the block to trigger GameBoard blockClicked method
        block.setOnMouseClicked((e) -> blockClicked(e, block));

        return block;
    }

    /**
     * Set the listener to handle an event when a block is clicked
     *
     * @param listener listener to add
     */
    public void setOnBlockClick(BlockClickedListener listener) {
        this.blockClickedListener = listener;
    }

    /**
     * Triggered when a block is clicked. Call the attached listener.
     *
     * @param event mouse event
     * @param block block clicked on
     */
    private void blockClicked(MouseEvent event, GameBlock block) {
        if (blockClickedListener != null && event.getButton() == MouseButton.PRIMARY) {
            blockClickedListener.blockClicked(block);
        }
    }

    /**
     * Setter for the RightClicked listener
     * @param listener
     */
    public void setOnRightClicked(RightClicked listener) {
        this.setOnRightClick = listener;
    }

    /**
     * Verifies if the MouseButton used is the right one
     * @param event
     */
    public void rightClicked(MouseEvent event) {
        if (setOnRightClick != null && event.getButton() == MouseButton.SECONDARY) {
            setOnRightClick.setOnRightClick(this);
        }
    }

    /**
     * Clears the blocks when a line is full
     * @param coordinates
     */
    public void clearLine(Set<int[]> coordinates) {
        for (int[] pair : coordinates) {
            getBlock(pair[0], pair[1]).fadeOut();
        }
    }
}

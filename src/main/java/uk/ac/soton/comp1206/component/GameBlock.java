package uk.ac.soton.comp1206.component;

import javafx.animation.AnimationTimer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.paint.*;

/**
 * The Visual User Interface component representing a single block in the grid.
 * <p>
 * Extends Canvas and is responsible for drawing itself.
 * <p>
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 * <p>
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {

    /**
     * The set of colours for different pieces
     */
    public static final Color[] COLOURS = {
            Color.TRANSPARENT,
            Color.DEEPPINK,
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.YELLOWGREEN,
            Color.LIME,
            Color.GREEN,
            Color.DARKGREEN,
            Color.DARKTURQUOISE,
            Color.DEEPSKYBLUE,
            Color.AQUA,
            Color.AQUAMARINE,
            Color.BLUE,
            Color.MEDIUMPURPLE,
            Color.PURPLE
    };

    /**
     * The board to whom this block belongs
     */
    private final GameBoard gameBoard;

    /**
     * The block's dimensions
     */
    private final double width;
    private final double height;

    /**
     * The column this block exists as in the grid
     */
    private final int x;

    /**
     * The row this block exists as in the grid
     */
    private final int y;

    /**
     * The value of this block (0 = empty, otherwise specifies the colour to render as)
     */
    private final IntegerProperty value = new SimpleIntegerProperty(0);

    /**
     * Create a new single Game Block
     *
     * @param gameBoard the board this block belongs to
     * @param x         the column the block exists in
     * @param y         the row the block exists in
     * @param width     the width of the canvas to render
     * @param height    the height of the canvas to render
     */
    public GameBlock(GameBoard gameBoard, int x, int y, double width, double height) {
        this.gameBoard = gameBoard;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;

        //A canvas needs a fixed width and height
        setWidth(width);
        setHeight(height);

        //Do an initial paint
        paint();

        //When the value property is updated, call the internal updateValue method
        value.addListener(this::updateValue);

        //When the mouse hovers over the block
        hover();
    }

    /**
     * When the value of this block is updated,
     *
     * @param observable what was updated
     * @param oldValue   the old value
     * @param newValue   the new value
     */
    private void updateValue(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        paint();
    }

    /**
     * Handle painting of the block canvas
     */
    public void paint() {
        //If the block is empty, paint as empty
        if (value.get() == 0) {
            paintEmpty();
        } else {
            //If the block is not empty, paint with the colour represented by the value
            paintColor(COLOURS[value.get()]);
        }
    }

    /**
     * Paint this canvas empty
     */
    private void paintEmpty() {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0, 0, width, height);

        //Fill
        gc.setFill(Color.web("blue", 0.1));
        gc.fillRect(0, 0, width, height);

        //Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0, 0, width, height);
    }

    /**
     * Paint this canvas with the given colour
     *
     * @param colour the colour to paint
     */
    private void paintColor(Paint colour) {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0, 0, width, height);

        //Image filler
        Image image = new Image(this.getClass().getResource("/images/paw.png").toExternalForm());
        gc.drawImage(image, this.x, this.y, this.getWidth()-5, this.getHeight()-5);
        gc.setFill(Color.color(0, 0, 0, 0));
        gc.fillRect(0, 0, width, height);

        //Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0, 0, width, height);
    }

    /**
     * Get the column of this block
     *
     * @return column number
     */
    public int getX() {
        return x;
    }

    /**
     * Get the row of this block
     *
     * @return row number
     */
    public int getY() {
        return y;
    }

    /**
     * Get the current value held by this block, representing it's colour
     *
     * @return value
     */
    private int getValue() {
        return this.value.get();
    }

    /**
     * Bind the value of this block to another property. Used to link the visual block to a corresponding block in the Grid.
     *
     * @param input property to bind the value to
     */
    public void bind(ObservableValue<? extends Number> input) {
        value.bind(input);
    }

    /**
     * Colors the block when mouse entered and paints it to the initial state when mouse exited
     */
    private void hover() {
        //Only if it is the main board so that the other classes that extend GameBoard won't be affected by hovering
        if (this.gameBoard.getClass() == GameBoard.class) {
            this.setOnMouseEntered((e) ->
            {
                this.hoverPaint();
            });
            this.setOnMouseExited((e) -> {
                this.paint();
            });
        }
    }

    /**
     * Paints the block a different color when hovered over
     */
    public void hoverPaint() {
            var gc = getGraphicsContext2D();
            gc.setFill(Color.color(1, 1, 1, 0.7));
            gc.fillRect(0, 0, width, height);
    }

    /**
     * Flashes when lines cleared
     */
    protected void fadeOut() {

        var gc = getGraphicsContext2D();

        AnimationTimer animationTimer = new AnimationTimer() {

            double opacity = 1;

            @Override
            public void handle(long l) {
                doHandle();
            }

            private void doHandle() {
                opacity -= 0.02f;

                if (opacity > 0) {
                    //clear before painting
                    gc.clearRect(0, 0, width, height);

                    gc.fillRect(0, 0, width, height);

                    gc.setFill(Color.color(1, 1, 1, opacity));
                    gc.fillRect(0, 0, width, height);

                    gc.setFill(Color.web("green", 0.5));
                    gc.fillRect(0, 0, width, height);

                    //Border
                    gc.setStroke(Color.BLACK);
                    gc.strokeRect(0, 0, width, height);

                    //fill triangle
                    double[] xPoints = {0, getWidth(), getWidth()};
                    double[] yPoints = {0, 0, getHeight()};

                    //fill triangle
                    gc.setFill(Color.web("grey", 0.3));
                    gc.fillPolygon(xPoints, yPoints, 3);

                } else {
                    //lowest opacity is reached, make the block empty
                    paintEmpty();
                    stop();
                }
            }
        };
        animationTimer.start();
    }
}

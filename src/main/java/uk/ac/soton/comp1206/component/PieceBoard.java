package uk.ac.soton.comp1206.component;

import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

public class PieceBoard extends GameBoard {

    /**
     * The GamePiece that will appear on this board
     */
    private GamePiece piece;

    /**
     * Create a PieceBoard using a given GamePiece, width and height
     * @param gamePiece
     * @param width
     * @param height
     */
    public PieceBoard(GamePiece gamePiece, int width, int height) {
        super(new Grid(3, 3), width, height);
        //outputting the piece
        showPiece(gamePiece);
    }

    /**
     * Takes in and returns a GamePiece for which it will create the grid regarding its centre
     * @param gamePiece
     * @return
     */
    public PieceBoard showPiece(GamePiece gamePiece) {
        piece = gamePiece;
        doGrid();
        return this;
    }

    /**
     * Creates a grid for a specified piece everytime it is called
     */
    private void doGrid() {
        int[][] blocks = piece.getBlocks();
        for (int column = 0; column < blocks.length; column++) {
            for (int row = 0; row < blocks[column].length; row++) {
                grid.set(column, row, 0);
                if (blocks[column][row] != 0) {
                    grid.set(column, row, piece.getValue());
                }
            }
        }
    }
}

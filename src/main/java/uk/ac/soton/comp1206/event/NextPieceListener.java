package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * The Next Piece Listener is used to handle the event when a new piece is generated to be placed next after the current
 * one. It passes the current piece to be played and the next randomly generated one as parameters
 */

public interface NextPieceListener {

    /**
     * Handle the current piece and the next one
     * @param current
     * @param next
     */
    public void nextPiece(GamePiece current, GamePiece next);
}

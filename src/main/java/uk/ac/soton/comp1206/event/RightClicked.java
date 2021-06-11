package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBoard;

/**
 * The Right Clicked Listener is used to handle the event when the right mouse button is clicked. It passes the
 * GameBoard which received the click
 */

public interface RightClicked {

    /**
     * Handle a Mouse Clicked event
     * @param gameBoard
     */
    public void setOnRightClick(GameBoard gameBoard);

}

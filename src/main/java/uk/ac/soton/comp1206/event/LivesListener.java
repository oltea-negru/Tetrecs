package uk.ac.soton.comp1206.event;

/**
 * The Lives Listener is used to handle the event when the life is zero. It does not pass anything as a parameter since
 * the life can only be zero once during the whole duration of the game
 */
public interface LivesListener {

    /**
     * Handle the zero lives left
     */
    public void zeroLivesYet();
}

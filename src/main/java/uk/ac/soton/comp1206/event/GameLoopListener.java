package uk.ac.soton.comp1206.event;

/**
 * The Game Loop listener is used to handle the event when the time runs out. It passes the
 * time limit as a parameter
 */
public interface GameLoopListener {

    /**
     * Handle the actions that have to be executed before time runs out
     *
     * @param time
     */
    public void setOnGameLoop(int time);
}

package uk.ac.soton.comp1206.event;

import java.util.Set;

/**
 * The Line Cleared Listener is used to handle the event when a line full of blocks is cleared. It passes the
 * set of coordinates of all the blocks that have to be cleared
 */
public interface LineClearedListener {

    /**
     * Handle the block's coordinates in each line cleared
     * @param set
     */
    public void clearLine(Set<int[]> set);
}

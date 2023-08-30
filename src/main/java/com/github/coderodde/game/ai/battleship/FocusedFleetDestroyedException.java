package com.github.coderodde.game.ai.battleship;

/**
 * Instances of this class are thrown when a focused Battleship AI bot destroys
 * a focused fleet.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 30, 2023)
 * @since 1.6 (Aug 30, 2023)
 */
public final class FocusedFleetDestroyedException extends RuntimeException {
    
    public FocusedFleetDestroyedException(String exceptionMessage) {
        super(exceptionMessage);
    }
}

package com.github.coderodde.game.ai.battleship;

/**
 * This class defines the interface for the Battleship game AI bots.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 21, 2023)
 * @since 1.6 (Aug 21, 2023)
 */
public interface BattleshipAIBot {
    
    /**
     * Computes the most probable shot location.
     * 
     * @return the matrix coordinate.
     */
    public MatrixCoordinates computeNextShotLocation();
    
    /**
     * Performs a shot.
     * 
     * @param matrixCoordinates the shooting spot coordinates.
     */
    public void shoot(MatrixCoordinates matrixCoordinates);
}

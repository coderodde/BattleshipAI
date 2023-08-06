package com.github.coderodde.game.ai.battleship;

/**
 * This class defines the interface for the Battleship game AI bots.
 * @author rodio
 */
public interface BattleshipAIBot {
    
    /**
     * Computes the most probable shot location.
     * 
     * @return the matrix coordinate.
     */
    public MatrixCoordinate computeNextShotLocation(GameField gameField);
    
    /**
     * Performs a shot.
     * 
     * @param matrixCoordinate the spot coordinates.
     */
    public void shoot(MatrixCoordinate matrixCoordinate);
}

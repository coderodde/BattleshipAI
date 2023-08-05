package com.github.coderodde.game.ai.battleship;

/**
 * This class defines the interface for the Battleship game AI bots.
 * @author rodio
 */
public interface BattleshipAIBot {
    
    /**
     * Performs a shoot attempt.
     * 
     * @return the matrix coordinate.
     */
    public MatrixCoordinate shoot(GameField gameField);
}

package com.github.coderodde.game.ai.battleship;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 30, 2023)
 * @since 1.6 (Aug 30, 2023)
 */
public final class FocusedBattleshipAIBot implements BattleshipAIBot {

    private final GameField gameField;
    
    private MatrixCoordinates nextShotMatrixCoordinate =
            new MatrixCoordinates(-1, -1);
    
    private final Set<MatrixCoordinates> clearedNeighbourhood = new HashSet<>();
    
    public FocusedBattleshipAIBot(GameField gameField,
                                  MatrixCoordinates matrixCoordinates) {
        
        Ship initialFocusedShip = gameField.getShipAt(matrixCoordinates);
        
        if (initialFocusedShip.getLength() < 1) {
            throw new IllegalArgumentException(
                    "The input ship has length " 
                            + initialFocusedShip.getLength() 
                            + ", must be at least 1.");
        }
        
        if (initialFocusedShip.getLength() == 1) {
            throw new FocusedFleetDestroyedException(
                    "Ship "
                            + initialFocusedShip
                            + " is destroyed.");
        }
        
        this.gameField = gameField;
        clearedNeighbourhood.add(new MatrixCoordinates(matrixCoordinates));
    }
    
    @Override
    public MatrixCoordinates computeNextShotLocation() {
        return new MatrixCoordinates(nextShotMatrixCoordinate);
    }

    @Override
    public void shoot(MatrixCoordinates matrixCoordinates) {
        clearedNeighbourhood.add(matrixCoordinates);
        
        gameField.shoot(matrixCoordinates.x, 
                        matrixCoordinates.y);
        
        List<Ship> standingOpponentFleet = gameField.getStandingOpponentFleet();
        
        if (standingOpponentFleet.isEmpty()) {
            throw new OpponentFleetDestroyedException();
        }
        
        computeNextShotLocationImpl(standingOpponentFleet);
    }
    
    private void computeNextShotLocationImpl(List<Ship> standingOpponentFleet) {
        
    }
}

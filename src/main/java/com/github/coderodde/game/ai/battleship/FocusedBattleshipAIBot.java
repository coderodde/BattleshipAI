package com.github.coderodde.game.ai.battleship;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class implements the focused Battleship AI bot.
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
            // Trivially cleared.
            throw new FocusedFleetDestroyedException(
                    "Ship "
                            + initialFocusedShip
                            + " is destroyed.");
        }
        
        this.gameField = gameField;
        clearedNeighbourhood.add(new MatrixCoordinates(matrixCoordinates));
        computeNextShotLocationImpl(gameField.getStandingOpponentFleet());
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
        // TODO: Find whether we need to sort the fleet by length DESC:
        standingOpponentFleet.sort((s1, s2) -> {
            return s2.getLength() - s1.getLength();
        });
        
        if (clearedNeighbourhood.size() == 1) {
            bruteForceSearchOnInitialShot(standingOpponentFleet);
        } else {
            
        }
    }
    
    private void bruteForceSearchOnInitialShot(
            List<Ship> standingOpponentFleet) {
        
        MatrixCoordinates initialShotCoordinates =
                clearedNeighbourhood.iterator().next();
        
        FrequencyCounterMatrix frequencyCounterMatrix = 
                new FrequencyCounterMatrix(gameField.getWidth(),
                                           gameField.getHeight());
        
        List<Ship> searchFleet = new ArrayList<>(standingOpponentFleet.size());
        
        for (Ship standingOpponentShip : standingOpponentFleet) {
            searchFleet.add(new Ship(standingOpponentShip));
        }
        
        for (Ship searchShip : searchFleet) {
            bruteForceSearchHorizontalOnInitialShot(searchShip, 
                                                    frequencyCounterMatrix,
                                                    initialShotCoordinates);
            
            bruteForceSearchVerticalOnInitialShot(searchShip,
                                                  frequencyCounterMatrix,
                                                  initialShotCoordinates);
        }
        
        MatrixCoordinates mc = frequencyCounterMatrix.getMaximumMatrixCounter();
        nextShotMatrixCoordinate.x = mc.x;
        nextShotMatrixCoordinate.y = mc.y;
    }
    
    private void bruteForceSearchHorizontalOnInitialShot(
            Ship searchShip,
            FrequencyCounterMatrix frequencyCounterMatrix,
            MatrixCoordinates initialShotCoordinates) {
        
        searchShip.setOrientation(Ship.Orientation.HORIZONTAL);
        
        int shipY = initialShotCoordinates.y;
        
        for (int i = 0; i < searchShip.getLength(); i++) {
            int shipX = initialShotCoordinates.x - i;
            
            if (shipX < 0) {
                System.out.println("shipX < 0");
                break;
            } else if (shipX + searchShip.getLength() > gameField.getWidth()) {
                continue;
            }
            
            searchShip.setLocation(shipX, shipY);
            frequencyCounterMatrix.incrementWithExclusion(
                    searchShip, 
                    clearedNeighbourhood);
        }
    }
    
    private void bruteForceSearchVerticalOnInitialShot(
            Ship searchShip,
            FrequencyCounterMatrix frequencyCounterMatrix,
            MatrixCoordinates initialShotLocation) {
        
        searchShip.setOrientation(Ship.Orientation.VERTICAL);
        
        int shipX = initialShotLocation.x;
        
        for (int i = 0; i < searchShip.getLength(); i++) {
            int shipY = initialShotLocation.y - i;
            
            if (shipY < 0) {
                System.out.println("shipY < 0");
                break;
            } else if (shipY + searchShip.getLength() > gameField.getHeight()) {
                continue;
            }
            
            searchShip.setLocation(shipX, shipY);
            frequencyCounterMatrix.incrementWithExclusion(
                    searchShip, 
                    clearedNeighbourhood);
        }
    }
}

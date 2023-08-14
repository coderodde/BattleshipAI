package com.github.coderodde.game.ai.battleship;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the Battleship game AI bot that is focused to a 
 * particular ship. 
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 14, 2023)
 * @since 1.6 (Aug 14, 2023)
 */
public final class FocusedBattleshipAIBot implements BattleshipAIBot {

    public static final class FocusedShipDestroyedException 
            extends RuntimeException {
        
        public FocusedShipDestroyedException(String exceptionMessage) {
            super(exceptionMessage);
        }
    }
    
    /**
     * Maintains the stack of focused ships.
     */
    private final List<Ship> focusedShipList = new ArrayList<>();
    
    /**
     * The game field being operated on.
     */
    private final GameField gameField;
    
    /**
     * The coordinates of the next shot.
     */
    private MatrixCoordinate nextShotMatrixCoordinate = 
            new MatrixCoordinate(0, 0);
    
    /**
     * Constructs a focused AI bot.
     * 
     * @param initialFocusedShip the initial focused ship.
     * @param matrixCoordinate the matrix coordinate of the shot that revealed
     *                         the initial focused ship.
     * @param gameField the game field storing the fleet.
     */
    public FocusedBattleshipAIBot(Ship initialFocusedShip,
                                  MatrixCoordinate matrixCoordinate,
                                  GameField gameField) {
        
        if (initialFocusedShip.getLength() == 1) {
            throw new FocusedShipDestroyedException(
                    "Ship " + initialFocusedShip + " is destroyed.");
        }
        
        focusedShipList.add(initialFocusedShip);
        this.gameField = gameField;
        computeNextShotLocationImpl(matrixCoordinate);
    }
    
    /**
     * Returns the matrix coordinate of the next shot.
     * 
     * @param gameField the game field.
     * @return the coordinates of the next favourable shot.
     */
    @Override
    public MatrixCoordinate computeNextShotLocation(GameField gameField) {
        return new MatrixCoordinate(this.nextShotMatrixCoordinate);
    }

    /**
     * Shoots and updates the game field statistics.
     * 
     * @param matrixCoordinate the coordinates at which to shoot.
     */
    @Override
    public void shoot(MatrixCoordinate matrixCoordinate) {
        gameField.shoot(matrixCoordinate.x, 
                        matrixCoordinate.y);
        
        Ship ship = gameField.getShipAt(matrixCoordinate.x,
                                        matrixCoordinate.y);
        
        if (ship == null) {
            computeNextShotLocationImpl(matrixCoordinate);
            return;
        }
        
        if (gameField.shipIsDestroyed(ship)) {
            removeLastFocusedShip();
            
            if (focusedShipList.isEmpty()) {
                throw new FocusedShipDestroyedException("Ship is destroyed.");
            }
        }
        
        Ship lastFocusedShip = getLastFocusedShip();
        
        if (!ship.equals(lastFocusedShip)) {
            focusedShipList.add(ship);
        }
        
        computeNextShotLocationImpl(matrixCoordinate);
    }
    
    private Ship getLastFocusedShip() {
        return focusedShipList.get(focusedShipList.size() - 1);
    }

    private void removeLastFocusedShip() {
        focusedShipList.remove(focusedShipList.size() - 1);
    }
    
    private void computeNextShotLocationImpl(MatrixCoordinate shotCoordinates) {
        Ship ship = getLastFocusedShip();
        Ship searchShip = new Ship(ship);
        
        FrequencyCounterMatrix frequencyCounterMatrix = 
                new FrequencyCounterMatrix(gameField.getWidth(),
                                           gameField.getHeight());
        
        searchHorizontally(frequencyCounterMatrix, shotCoordinates, searchShip);
        searchVertically(frequencyCounterMatrix, shotCoordinates, searchShip);
        
        MatrixCoordinate mc = frequencyCounterMatrix.getMaximumMatrixCounter();
        
        this.nextShotMatrixCoordinate = new MatrixCoordinate(mc.x, mc.y);
    }
    
    private void searchHorizontally(
            FrequencyCounterMatrix frequencyCounterMatrix,
            MatrixCoordinate shotCoordinates,
            Ship searchShip) {
        
        searchShip = new Ship(searchShip);
        searchShip.setOrientation(Ship.Orientation.HORIZONTAL);
        
        for (int i = 0; i < searchShip.getLength(); i++) {
            int shipX = shotCoordinates.x - i;
            
            if (shipX < 0) {
                break;
            }
            
            searchShip.setX(shipX);
            
            if (!gameField.shipOccupiesClosedCell(searchShip) && 
                !overlapsFocusedShip(searchShip)) {
                
                frequencyCounterMatrix.incrementShip(searchShip);
            }
        }
    }
    
    private void searchVertically(
            FrequencyCounterMatrix frequencyCounterMatrix,
            MatrixCoordinate shotCoordinates,
            Ship searchShip) {
        
        searchShip = new Ship(searchShip);
        searchShip.setOrientation(Ship.Orientation.VERTICAL);
        
        for (int i = 0; i < searchShip.getLength(); i++) {
            int shipY = shotCoordinates.y - i;
            
            if (shipY < 0) {
                break;
            }
            
            searchShip.setY(shipY);
            
            if (!gameField.shipOccupiesClosedCell(searchShip) &&
                !overlapsFocusedShip(searchShip)) {
                
                frequencyCounterMatrix.incrementShip(searchShip);
            }
        }
    }
    
    private boolean overlapsFocusedShip(Ship searchShip) {
        for (Ship focusedShip : focusedShipList) {
            if (!focusedShip.equals(searchShip) &&
                 focusedShip.overlap(searchShip)) {
                return true;
            }
        }
        
        return false;
    }
}

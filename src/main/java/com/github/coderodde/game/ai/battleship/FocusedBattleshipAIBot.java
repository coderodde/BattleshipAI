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

    public static final class FocusedFleetDestroyedException 
            extends RuntimeException {
        
        public FocusedFleetDestroyedException(String exceptionMessage) {
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
    
    private Ship.Orientation detectedShipOrientation = null;
    
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
        
        if (initialFocusedShip.getLength() < 1) {
            throw new IllegalArgumentException(
                    "The input ship has length " 
                            + initialFocusedShip.getLength() 
                            + ", must be at least 1.");
        }
        
        if (initialFocusedShip.getLength() == 1) {
            throw new FocusedFleetDestroyedException(
                    "Ship " + initialFocusedShip + " is destroyed.");
        }
        
        this.gameField = gameField;
        focusedShipList.add(initialFocusedShip);
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
            focusedShipList.remove(ship);
            
            if (focusedShipList.isEmpty()) {
                throw new FocusedFleetDestroyedException(
                        "Focused fleet is destroyed.");
            }
        }
        
        if (!focusedShipList.contains(ship)) {
            focusedShipList.add(ship);
        }
        
        computeNextShotLocationImpl(matrixCoordinate);
    }
    
    private Ship getLastFocusedShip() {
        return focusedShipList.get(focusedShipList.size() - 1);
    }
    
    private void computeNextShotLocationImpl(MatrixCoordinate shotCoordinates) {
        Ship ship = getLastFocusedShip();
        Ship searchShip = new Ship(ship);
        
        FrequencyCounterMatrix frequencyCounterMatrix = 
                new FrequencyCounterMatrix(gameField.getWidth(),
                                           gameField.getHeight());
        
        if (null == detectedShipOrientation) {
            searchHorizontally(frequencyCounterMatrix, 
                               shotCoordinates, 
                               searchShip);
            
            searchVertically(frequencyCounterMatrix, 
                             shotCoordinates, 
                             searchShip);
            
            MatrixCoordinate mc = 
                    frequencyCounterMatrix.getMaximumMatrixCounter();
            
            Ship probeShip = gameField.getShipAt(mc.x, mc.y);
            
            if (probeShip.equals(searchShip)) {
                detectedShipOrientation = 
                        detectOrientation(shotCoordinates, mc);
            }
            
            return;
            
        } else switch (detectedShipOrientation) {
            
            case HORIZONTAL -> {
                searchHorizontally(frequencyCounterMatrix,
                                   shotCoordinates,
                                   searchShip);
            }
            
            default -> {
                searchVertically(frequencyCounterMatrix,
                                 shotCoordinates,
                                 searchShip);
            }
        }
        
        MatrixCoordinate mc = frequencyCounterMatrix.getMaximumMatrixCounter();
        this.nextShotMatrixCoordinate = new MatrixCoordinate(mc.x, mc.y);
    }
    
    private Ship.Orientation detectOrientation(
            MatrixCoordinate shipCompartmentCoordinate1,
            MatrixCoordinate shipCompartmentCoordinate2) {
        int dx = shipCompartmentCoordinate1.x - shipCompartmentCoordinate2.x;
        
        return dx == 0 ? Ship.Orientation.VERTICAL : 
                         Ship.Orientation.HORIZONTAL;
    }
    
    private void searchHorizontally(
            FrequencyCounterMatrix frequencyCounterMatrix,
            MatrixCoordinate shotCoordinates,
            Ship searchShip) {
        
        searchShip = new Ship(searchShip);
        searchShip.setOrientation(Ship.Orientation.HORIZONTAL);
        Ship opponentShip = gameField.getShipAt(shotCoordinates.x,
                                                shotCoordinates.y);
        
        for (int i = 0; i < searchShip.getLength(); i++) {
            int shipX = shotCoordinates.x - i;
            int shipY = shotCoordinates.y;
            
            if (shipX < 0) {
                break;
            }
            
            searchShip.setLocation(shipX, shipY);
            
            if (!overlapsFocusedShip(searchShip, opponentShip)) {
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
        Ship opponentShip = gameField.getShipAt(shotCoordinates.x,
                                                shotCoordinates.y);
        
        for (int i = 0; i < searchShip.getLength(); i++) {
            int shipX = shotCoordinates.x;
            int shipY = shotCoordinates.y - i;
            
            if (shipY < 0) {
                break;
            }
            
            searchShip.setLocation(shipX, shipY);
            
            if (!overlapsFocusedShip(searchShip, opponentShip)) {
                frequencyCounterMatrix.incrementShip(searchShip);
            }
        }
    }
    
    private boolean overlapsFocusedShip(Ship searchShip, Ship opponentShip) {
        gameField.removeShip(opponentShip);
        
        for (Ship focusedShip : focusedShipList) {
            if (!focusedShip.equals(searchShip) &&
                gameField.shipOccupiesAnotherShip(focusedShip)) {
                
                gameField.addShip(opponentShip);
                return true;
            }
        }
        
        gameField.addShip(opponentShip);
        return false;
    }
}

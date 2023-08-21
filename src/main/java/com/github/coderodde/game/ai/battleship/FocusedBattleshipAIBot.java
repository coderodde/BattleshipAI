package com.github.coderodde.game.ai.battleship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * Maps each ship to its orientation. If orientation for the ship {@code s}
     * is not detected, {@code orientationMap.get(s)} equals {@code null}.
     */
    private final Map<Ship, Ship.Orientation> orientationMap = new HashMap<>();
    
    /**
     * The game field being operated on.
     */
    private final GameField gameField;
    
    /**
     * The coordinates of the next shot.
     */
    private MatrixCoordinates nextShotMatrixCoordinate = 
            new MatrixCoordinates(0, 0);
    
    /**
     * Constructs a focused AI bot.
     * 
     * @param initialFocusedShip the initial focused ship.
     * @param matrixCoordinate the matrix coordinate of the shot that revealed
     *                         the initial focused ship.
     * @param gameField the game field storing the fleet.
     */
    public FocusedBattleshipAIBot(Ship initialFocusedShip,
                                  MatrixCoordinates matrixCoordinate,
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
        gameField.shoot(matrixCoordinate.x, 
                        matrixCoordinate.y);
        
        focusedShipList.add(initialFocusedShip);
        orientationMap.put(initialFocusedShip, null);
        computeNextShotLocationImpl(matrixCoordinate);
    }
    
    /**
     * Returns the matrix coordinate of the next shot.
     * 
     * @param gameField the game field.
     * @return the coordinates of the next favourable shot.
     */
    @Override
    public MatrixCoordinates computeNextShotLocation() {
        return new MatrixCoordinates(this.nextShotMatrixCoordinate);
    }

    /**
     * Shoots and updates the game field statistics.
     * 
     * @param matrixCoordinate the coordinates at which to shoot.
     */
    @Override
    public void shoot(MatrixCoordinates matrixCoordinate) {
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
    
    private void computeNextShotLocationImpl(MatrixCoordinates shotCoordinates) {
        Ship ship = getLastFocusedShip();
        Ship searchShip = new Ship(ship);
        
        FrequencyCounterMatrix frequencyCounterMatrix = 
                new FrequencyCounterMatrix(gameField.getWidth(),
                                           gameField.getHeight());
        
        Ship.Orientation detectedShipOrientation = orientationMap.get(ship);
        
        if (null == detectedShipOrientation) {
            searchHorizontally(frequencyCounterMatrix, 
                               shotCoordinates, 
                               searchShip);
            
            searchVertically(frequencyCounterMatrix, 
                             shotCoordinates, 
                             searchShip);
            
            MatrixCoordinates mc = 
                    frequencyCounterMatrix.getMaximumMatrixCounter();
            
            Ship probeShip = gameField.getShipAt(mc.x, mc.y);
            
            if (probeShip != null) {
                if (!focusedShipList.contains(probeShip)) {
                    focusedShipList.add(probeShip);
                    orientationMap.put(probeShip, null);
                } else {
                    Ship.Orientation orientation = 
                            tryInferOrientation(probeShip, 
                                                shotCoordinates);
                    
                    if (orientation != null) {
                        orientationMap.put(probeShip, orientation);
                    }
                }
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
        
        MatrixCoordinates mc = frequencyCounterMatrix.getMaximumMatrixCounter();
        this.nextShotMatrixCoordinate = new MatrixCoordinates(mc.x, mc.y);
    }
    
    private Ship.Orientation detectOrientation(
            MatrixCoordinates shipCompartmentCoordinate1,
            MatrixCoordinates shipCompartmentCoordinate2) {
        
        int dx = shipCompartmentCoordinate1.x - 
                 shipCompartmentCoordinate2.x;
        
        return dx == 0 ? Ship.Orientation.VERTICAL : 
                         Ship.Orientation.HORIZONTAL;
    }
    
    private void searchHorizontally(
            FrequencyCounterMatrix frequencyCounterMatrix,
            MatrixCoordinates shotCoordinates,
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
                frequencyCounterMatrix.incrementShipExcept(searchShip, 
                                                           shotCoordinates);
            }
        }
    }
    
    private void searchVertically(
            FrequencyCounterMatrix frequencyCounterMatrix,
            MatrixCoordinates shotCoordinates,
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
                frequencyCounterMatrix.incrementShipExcept(searchShip,
                                                           shotCoordinates);
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
    
    private Ship.Orientation 
        tryInferOrientation(Ship probeShip, 
                            MatrixCoordinates shotCoordinates) {
        int x = shotCoordinates.x;
        int y = shotCoordinates.y;
        
        if (gameField.getShipAt(x, y - 1) == probeShip) {
            return Ship.Orientation.VERTICAL;
        }
        
        if (gameField.getShipAt(x, y + 1) == probeShip) {
            return Ship.Orientation.VERTICAL;
        }
        
        if (gameField.getShipAt(x - 1, y) == probeShip) {
            return Ship.Orientation.HORIZONTAL;
        }
        
        if (gameField.getShipAt(x + 1, y) == probeShip) {
            return Ship.Orientation.HORIZONTAL;
        }
        
        return null;
    }
        
}

package com.github.coderodde.game.ai.battleship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class defines the game field for the Battleship game.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 5, 2023)
 */
public final class GameField {
    
    public static final class ShipOverlapsWithExistingFleetException 
            extends RuntimeException {
    }
    
    public enum GameFieldCellState {
        CLEAR,
        SHOT;
    }
    
    private static final int DEFAULT_WIDTH = 10;
    private static final int DEFAULT_HEIGHT = 10;
    
    private final int width;
    private final int height;
    private final GameFieldCellState[][] gameFieldCellStateMatrix;
    private final Ship[][] shipMatrix;
    private final List<Ship> searchFleet = new ArrayList<>();
    private final List<Ship> opponentFleet = new ArrayList<>();
    
    // The key ships of the following map must be effictely immutable:
    private final Map<Ship, Ship> mapOpponentShipToSearchShip = new HashMap<>();
    
    public GameField(int width, int height) {
        this.width = width;
        this.height = height;
        this.gameFieldCellStateMatrix = new GameFieldCellState[height][width];
        this.shipMatrix = new Ship[height][width];
        initializeGameFieldCellStateMatrix();
    }
    
    public GameField(GameField other) {
        this.width = other.width;
        this.height = other.height;
        this.gameFieldCellStateMatrix = new GameFieldCellState[height][width];
        this.shipMatrix = new Ship[height][width];
        this.copyGameData(other);
    }
    
    public GameField() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
    
    public void addShip(Ship ship) {
        checkShipFitsIn(ship);
        checkDoesNotOverlapExistingShip(ship);
        
        Ship opponentShip = new Ship(ship);
        Ship searchShip = new Ship(ship);
        
        searchFleet.add(searchShip);
        opponentFleet.add(opponentShip);
        mapOpponentShipToSearchShip.put(opponentShip, searchShip);
        
        printShipToShipMatrix(opponentShip);
    }
    
    public Ship getShip(int index) {
        return searchFleet.get(index);
    }
    
    public Ship getShipAt(int x, int y) {
        return shipMatrix[y][x];
    }
    
    public Ship getShipAt(MatrixCoordinates matrixCoordinates) {
        return shipMatrix[matrixCoordinates.y]
                         [matrixCoordinates.x];
    }
    
    public void removeShip(Ship ship) {
        searchFleet.remove(mapOpponentShipToSearchShip.get(ship));
        opponentFleet.remove(ship);
        unprintShipFromShipMatrix(ship);
    }
    
    public boolean shipOccupiesClosedCell(Ship ship) {
        switch (ship.getOrientation()) {
            case HORIZONTAL:
                return horizontalShipOccupiesClosedCell(ship);
                
            case VERTICAL:
                return verticalShipOccupiesClosedCell(ship);
                
            default:
                throw new IllegalStateException("Should not get here.");
        }
    }
    
    public boolean shipOccupiesAnotherShip(Ship ship) {
        switch (ship.getOrientation()) {
            case HORIZONTAL:
                return horizonalShipOccupiesAnotherShip(ship);
                
            case VERTICAL:
                return verticalShipOccupiesAnotherShip(ship);
                
            default:
                throw new IllegalStateException("Should not get here.");
        }
    }
    
    public List<Ship> getSearchFleet() {
        return searchFleet;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void shoot(int x, int y) {
        gameFieldCellStateMatrix[y][x] = GameFieldCellState.SHOT;
    }
    
    public boolean shipIsDestroyed(Ship ship) {
        switch (ship.getOrientation()) {
            case HORIZONTAL:
                return horizontalShipIsDestroyed(ship);
                
            case VERTICAL:
                return verticalShipIsDestroyed(ship);
                
            default:
                throw new IllegalStateException("Should not get here.");
        }
    }
    
    public boolean gameOver() {
        return opponentFleet.isEmpty();
    }
    
    private void initializeGameFieldCellStateMatrix() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                gameFieldCellStateMatrix[y][x] = GameFieldCellState.CLEAR;
            }
        }
    }
    
    /**
     * Checks whether an input ship with the 
     * <b><i>horizontal orientation</i></b> occupies any cell that is already
     * cleared/shot.
     * 
     * @param ship the target ship to check.
     * 
     * @return {@code true} only if the input ship occupies a cleared cell.
     */
    private boolean horizontalShipOccupiesClosedCell(Ship ship) {
        int y = ship.getY();
        
        for (int i = 0; i < ship.getLength(); i++) {
            int x = ship.getX() + i;
            
            if (gameFieldCellStateMatrix[y][x] == GameFieldCellState.SHOT) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Checks whether an input ship with the <b><i>vertical orientation</i></b>
     * occupies any cell that is already cleared/shot.
     * 
     * @param ship the target ship to check.
     * 
     * @return {@code true} only if the input ship occupies a cleared cell.
     */
    private boolean verticalShipOccupiesClosedCell(Ship ship) {
        int x = ship.getX();
        
        for (int i = 0; i < ship.getLength(); i++) {
            int y = ship.getY() + i;
            
            if (gameFieldCellStateMatrix[y][x] == GameFieldCellState.SHOT) {
                return true;
            }
        }
        
        return false;
    }
        
    private void printShipToShipMatrix(Ship ship) {
        switch (ship.getOrientation()) {
            case HORIZONTAL -> {
                printHorizontalShipToShipMatrix(ship);
                return;
            }
                
            case VERTICAL -> {
                printVerticalShipToShipMatrix(ship);
                return;
            }
                
            default -> throw new IllegalStateException("Should not get here.");
        }
    }
    
    private void printHorizontalShipToShipMatrix(Ship ship) {
        int y = ship.getY();
        
        for (int i = 0; i < ship.getLength(); i++) {
            int x = ship.getX() + i;
            
            if (shipMatrix[y][x] != null) {
                throw new IllegalStateException(
                        "Trying to locate a ship to an occupied cell.");
            }
            
            shipMatrix[y][x] = ship;
        }
    }
    
    private void printVerticalShipToShipMatrix(Ship ship) {
        int x = ship.getX();
        
        for (int i = 0; i < ship.getLength(); i++) {
            int y = ship.getY() + i;
            
            if (shipMatrix[y][x] != null) {
                throw new IllegalStateException(
                        "Trying to locate a ship to an occupied cell.");
            }
            
            shipMatrix[y][x] = ship;
        }
    }
    
    private boolean horizontalShipIsDestroyed(Ship ship) {
        int x = ship.getX();
        int y = ship.getY();
        
        for (int i = 0; i < ship.getLength(); i++) {
            if (gameFieldCellStateMatrix[y][x + i] ==
                    GameFieldCellState.CLEAR) {
                
                return false;
            }
        }
        
        return true;
    }
    
    private boolean verticalShipIsDestroyed(Ship ship) {
        int x = ship.getX();
        int y = ship.getY();
        
        for (int i = 0; i < ship.getLength(); i++) {
            if (gameFieldCellStateMatrix[y + i][x] == 
                    GameFieldCellState.CLEAR) {
                
                return false;
            }
        }
        
        return true;
    }
    
    private void unprintShipFromShipMatrix(Ship ship) {
        switch (ship.getOrientation()) {
            case HORIZONTAL:
                unprintHorizontalShipFromShipMatrix(ship);
                return;
                
            case VERTICAL:
                unprintVerticalShipFromShipMatrix(ship);
                return;
                
            default:
                throw new IllegalStateException("Should not get here.");
        }
    }
    
    private void unprintHorizontalShipFromShipMatrix(Ship ship) {
        int x = ship.getX();
        int y = ship.getY();
        
        for (int i = 0; i < ship.getLength(); i++) {
            shipMatrix[y][x + i] = null;
        }
    }
    
    private void unprintVerticalShipFromShipMatrix(Ship ship) {
        int x = ship.getX();
        int y = ship.getY();
        
        for (int i = 0; i < ship.getLength(); i++) {
            shipMatrix[y + i][x] = null;
        }
    }
    
    private void checkDoesNotOverlapExistingShip(Ship ship) {
        for (Ship existingShip : opponentFleet) {
            if (ship.overlap(existingShip)) {
                throw new ShipOverlapsWithExistingFleetException();
            }
        }
    }
    
    /**
     * 
     * @param copySourceGameField 
     */
    private void copyGameData(GameField copySourceGameField) {
        for (Ship ship : copySourceGameField.getSearchFleet()) {
            this.addShip(ship);
        }
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                this.gameFieldCellStateMatrix[y][x] =
//                        copySourceGameField.gameFieldCellStateMatrix[y][x];
//                
//                this.shipMatrix[]
//                copySourceGameField.shipMatrix[y][x] = this.shipMatrix[y][x];
//            }
//        }
        
//        for (Map.Entry<Ship, Ship> entry : 
//                mapOpponentShipToSearchShip.entrySet()) {
//            
//            Ship opponentShip = new Ship(entry.getKey());
//            Ship searchShip = new Ship(entry.getValue());
//            
//            copySourceGameField.opponentFleet.add(opponentShip);
//            copySourceGameField.searchFleet.add(searchShip);
//            
//            copySourceGameField.mapOpponentShipToSearchShip
//                               .put(opponentShip, searchShip);
//        }
    }
    
    private void checkShipFitsIn(Ship ship) {
        switch (ship.getOrientation()) {
            case HORIZONTAL:
                if (ship.getLength() > width) {
                    throw new IllegalArgumentException(
                            "Horizontal ship is longer than the width of the " 
                            + "game field. Ship length = " 
                            + ship.getLength() 
                            + ", game field width = " 
                            + width);
                }
                
                break;
                
            case VERTICAL:
                if (ship.getLength() > height) {
                    throw new IllegalArgumentException(
                            "Vertical ship is longer than the height of the "
                            + "gmae field. Ship length = "
                            + ship.getLength() 
                            + ", game field width = " 
                            + height);
                }
                
                break;
                
            default:
                throw new IllegalStateException("Should not get here.");
        }
    }
    
    private boolean horizonalShipOccupiesAnotherShip(Ship ship) {
        for (int i = 0; i < ship.getLength(); i++) {
            Ship s = shipMatrix[ship.getY()][ship.getX() + i];
            
            if (s != null) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean verticalShipOccupiesAnotherShip(Ship ship) {
        for (int i = 0; i < ship.getLength(); i++) {
            Ship s = shipMatrix[ship.getY() + i][ship.getX()];
            
            if (s != null) {
                return true;
            }
        }
        
        return false;
    }
}

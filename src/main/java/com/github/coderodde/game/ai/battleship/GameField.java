package com.github.coderodde.game.ai.battleship;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class defines the game field for the Battleship game.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 5, 2023)
 */
public final class GameField {
    
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
    
    public GameField(int width, int height) {
        this.width = width;
        this.height = height;
        this.gameFieldCellStateMatrix = new GameFieldCellState[height][width];
        this.shipMatrix = new Ship[height][width];
        initializeGameFieldCellStateMatrix();
    }
    
    public GameField() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
    
    public void addShip(Ship ship) {
        Ship opponentShip = new Ship(ship);
        searchFleet.add(ship);
        opponentFleet.add(opponentShip);
        printShipToShipMatrix(opponentShip);
    }
    
    public Ship getShip(int index) {
        return searchFleet.get(index);
    }
    
    public Ship getShipAt(int x, int y) {
        return shipMatrix[y][x];
    }
    
    public void removeShip(Ship ship) {
        
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
    
    public List<Ship> getFleet() {
        return Collections.<Ship>unmodifiableList(searchFleet);
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
}

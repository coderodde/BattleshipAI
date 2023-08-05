package com.github.coderodde.game.ai.battleship;

import com.github.coderodde.game.ai.battleship.Ship.ShipCompartment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
    private final List<Ship> fleet = new ArrayList<>();
    
    public GameField(int width, int height) {
        this.width = width;
        this.height = height;
        this.gameFieldCellStateMatrix = new GameFieldCellState[height][width];
        initializeGameFieldCellStateMatrix();
    }
    
    public GameField() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
    
    public void addShip(Ship ship) {
        fleet.add(ship);
    }
    
    public List<Ship> getFleet() {
        return Collections.<Ship>unmodifiableList(fleet);
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
        Set<ShipCompartment> shipCompartments = 
                ship.convertToShipCompartments();
        
        for (ShipCompartment shipCompartment : shipCompartments) {
            int x = shipCompartment.x;
            int y = shipCompartment.y;
            
            if (gameFieldCellStateMatrix[y][x] == GameFieldCellState.CLEAR) {
                return false;
            }
        }
        
        return true;
    }
    
    private void initializeGameFieldCellStateMatrix() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                gameFieldCellStateMatrix[y][x] = GameFieldCellState.CLEAR;
            }
        }
    }
}

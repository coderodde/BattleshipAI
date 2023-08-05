package com.github.coderodde.game.ai.battleship;

import java.util.ArrayList;
import java.util.List;

/**
 * This class defines the game field for the Battleship game.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 5, 2023)
 */
public final class GameField {
    
    private static final int DEFAULT_WIDTH = 10;
    private static final int DEFAULT_HEIGHT = 10;
    
    private final int width;
    private final int height;
    private final List<Ship> fleet = new ArrayList<>();
    
    public GameField(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public GameField() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
}

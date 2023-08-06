package com.github.coderodde.game.ai.battleship;

import java.util.Arrays;

/**
 * This class represents the frequency counter matrix.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 5, 2023)
 * @since 1.6 (Aug 5, 2023)
 */
public final class FrequencyCounterMatrix {
    
    private final int[][] frequencyCounterMatrix;
    private int bestX;
    private int bestY;
    private int bestCount = -1;
    
    public FrequencyCounterMatrix(int width, int height) {
        this.frequencyCounterMatrix = new int[height][width];
    }
    
    public void increment(int x, int y) {
        frequencyCounterMatrix[y][x]++;
        
        if (frequencyCounterMatrix[y][x] > bestCount) {
            bestCount = frequencyCounterMatrix[y][x];
            bestX = x;
            bestY = y;
        }
    }
    
    public void increment(Ship ship) {
        switch (ship.getOrientation()) {
            case HORIZONTAL:
                incrementHorizontal(ship);
                return;
                
            case VERTICAL:
                incrementVertical(ship);
                return;
                
            default:
                throw new IllegalStateException("Should not get here.");
        }
    }
    
    public void clear() {
        for (int[] row : frequencyCounterMatrix) {
            Arrays.setAll(row, (op) -> 0);
        }
        
        bestCount = -1;
    }
    
    public MatrixCoordinate getMaximumMatrixCounter() {
        return new MatrixCoordinate(bestX, bestY);
    }
    
    private void incrementHorizontal(Ship ship) {
        for (int i = 0; i < ship.getLength(); i++) {
            increment(ship.getX() + i, ship.getY());
        }
    }
    
    private void incrementVertical(Ship ship) {
        for (int i = 0; i < ship.getLength(); i++) {
            increment(ship.getX(), ship.getY() + i);
        }
    }
}

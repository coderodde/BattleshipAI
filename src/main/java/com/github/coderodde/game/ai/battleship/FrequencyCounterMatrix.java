package com.github.coderodde.game.ai.battleship;

import java.util.Arrays;
import java.util.List;

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
    
    public void incrementShip(Ship ship) {
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
    
    public void incrementFleet(List<Ship> fleet) {
        for (Ship ship : fleet) {
            incrementShip(ship);
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
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int maximumCounterLength = computeMaximumCounterLength();
        int lineNumber = 1;
        int totalLines = frequencyCounterMatrix.length;
        
        for (int[] row : frequencyCounterMatrix) {
            load(sb,
                 row,
                 maximumCounterLength, 
                 lineNumber++, 
                 totalLines);
        }
        
        return sb.toString();
    }
    
    public int getCounter(int x, int y) {
        return frequencyCounterMatrix[y][x];
    }
    
    private int computeMaximumCounterLength() {
        int tentativeMaximumLength = 0;
        
        for (int[] row : frequencyCounterMatrix) {
            for (int entry : row) {
                tentativeMaximumLength = 
                        Math.max(
                                tentativeMaximumLength,
                                Integer.toString(entry).length());
            }
        }
        
        return tentativeMaximumLength;
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
    
    private static void load(StringBuilder sb,
                             int[] row, 
                             int maximumEntryLength, 
                             int lineNumber, 
                             int totalNumbers) {
        int rowLength = row.length;
        int entryIndex = 0;
        String format = "%" + maximumEntryLength + "d";
        
        for (int entry : row) {
            sb.append(String.format(format, entry));
            
            if (++entryIndex < rowLength) {
                sb.append(" ");
            }
        }
        
        if (lineNumber < totalNumbers) {
            sb.append("\n");
        }
    }
}

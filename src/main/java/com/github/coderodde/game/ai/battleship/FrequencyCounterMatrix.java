package com.github.coderodde.game.ai.battleship;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * This class represents the frequency counter matrix.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 5, 2023)
 * @since 1.6 (Aug 5, 2023)
 */
public final class FrequencyCounterMatrix {
    
    /**
     * The actual frequency counter matrix.
     */
    private final int[][] frequencyCounterMatrix;
    
    /**
     * The pseudo-random number generator. Used for resolving ties.
     */
    private final Random random = new Random();
    
    /**
     * List of best coordinate pairs.
     */
    private final List<MatrixCoordinates> bestCoordinates = new ArrayList<>();
    
    /**
     * The best count so far.
     */
    private int bestCount = -1;
    
    /**
     * Constructs this frequency counter matrix.
     * 
     * @param width  the width of the matrix.
     * @param height the height of the matrix.
     */
    public FrequencyCounterMatrix(int width, int height) {
        this.frequencyCounterMatrix = new int[height][width];
    }
    
    /**
     * Increments the matrix cell by one at coordinates {@code (x, y)}.
     * 
     * @param x the X-coordinate of the cell.
     * @param y the Y-coordinate of the cell.
     */
    public void increment(int x, int y) {
        frequencyCounterMatrix[y][x]++;
        
        if (frequencyCounterMatrix[y][x] > bestCount) {
            // Found new best spot:
            bestCount = frequencyCounterMatrix[y][x];
            bestCoordinates.clear();
            bestCoordinates.add(new MatrixCoordinates(x, y));
        } else if (frequencyCounterMatrix[y][x] == bestCount) {
            // Adding a tie:
            bestCoordinates.add(new MatrixCoordinates(x, y));
        }
    }
    
    /**
     * Increments all the cells occupied by {@code ship}.
     * 
     * @param ship the ship to increment.
     */
    public void incrementShip(Ship ship) {
        switch (ship.getOrientation()) {
            case HORIZONTAL -> {
                incrementHorizontal(ship);
                return;
            }
                
            case VERTICAL -> {
                incrementVertical(ship);
                return;
            }
                
            default -> throw new IllegalStateException("Should not get here.");
        }
    }
    
    /**
     * Increments the ship except the cell with coordinates {@code mc}.
     * 
     * @param ship the ship to increment.
     * @param mc   the coordinate to exclude.
     */
    public void incrementShipExcept(Ship ship, MatrixCoordinates mc) {
        switch (ship.getOrientation()) {
            case HORIZONTAL -> {
                incrementHorizontalExcept(ship, mc);
                return;
            }
                
            case VERTICAL -> {
                incrementVerticalExcept(ship, mc);
                return;
            }
                
            default -> throw new IllegalStateException("Should not get here.");
        }
    }
    
    /**
     * Increments the entire ship.
     * 
     * @param fleet the fleet to increment.
     */
    public void incrementFleet(List<Ship> fleet) {
        for (Ship ship : fleet) {
            incrementShip(ship);
        }
    }
    
    /**
     * Clears the entire matrix.
     */
    public void clear() {
        for (int[] row : frequencyCounterMatrix) {
            Arrays.setAll(row, (op) -> 0);
        }
        
        bestCount = -1;
    }
    
    /**
     * Returns one of the most favourable cell coordinates randomly.
     * 
     * @return one of the most favourable cell coordinates.
     */
    public MatrixCoordinates getMaximumMatrixCounter() {
        return bestCoordinates.get(random.nextInt(bestCoordinates.size()));
    }
    
    /**
     * Returns the textual representation of this frequency counter matrix.
     * 
     * @return the textual representation of this matrix.
     */
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
    
    /**
     * Gets the count at coordinates {@code (x, y)}.
     * 
     * @param x the {@code X}-coordinate of the cell.
     * @param y the {@code Y}-coordinate of the cell.
     * 
     * @return the count at the specified cell.
     */
    public int getCounter(int x, int y) {
        return frequencyCounterMatrix[y][x];
    }
    
    /**
     * Computes and returns the maximum length of a counter in characters.
     * 
     * @return the maximum length of a counter in characters.
     */
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
    
    /**
     * Increments a <b>horizontal</b> ship.
     * 
     * @param ship the ship to increment.
     */
    private void incrementHorizontal(Ship ship) {
        for (int i = 0; i < ship.getLength(); i++) {
            increment(ship.getX() + i, ship.getY());
        }
    }
    
    void incrementWithExclusion(
            Ship ship,
            Set<MatrixCoordinates> excludedPoints) {
        
        switch (ship.getOrientation()) {
            case HORIZONTAL -> {
                incrementHorizontalWithExclusion(ship, excludedPoints);
                return;
            }
                
            case VERTICAL -> {
                incrementVerticalWithExclusion(ship, excludedPoints);
                return;
            }
                
            default -> throw new IllegalStateException("Should not get here.");
        }
    }
    
    private void incrementHorizontalWithExclusion(
            Ship ship,
            Set<MatrixCoordinates> excludedPoints) {
        
        MatrixCoordinates mc = new MatrixCoordinates(-1, ship.getY());
        
        for (int i = 0; i < ship.getLength(); i++) {
            mc.x = ship.getX() + i;
            
            if (!excludedPoints.contains(mc)) {
                increment(ship.getX() + i, ship.getY());
            }
        }
    }
    
    private void incrementVerticalWithExclusion(
            Ship ship,
            Set<MatrixCoordinates> excludedPoints) {
        
        MatrixCoordinates mc = new MatrixCoordinates(ship.getX(), -1);
        
        for (int i = 0; i < ship.getLength(); i++) {
            mc.y = ship.getY() + i;
            
            if (!excludedPoints.contains(mc)) {
                increment(ship.getX(), ship.getY() + i);
            }
        }
    }
    
    /**
     * Increments a <b>vertical</b> ship.
     * 
     * @param ship the ship to increment.
     */
    private void incrementVertical(Ship ship) {
        for (int i = 0; i < ship.getLength(); i++) {
            increment(ship.getX(), ship.getY() + i);
        }
    }
    
    /**
     * Increments a <b>horizontal</b> ship except the cell at coordinates 
     * {@code mc}.
     * 
     * @param ship the ship to increment.
     * @param mc   the coordinates of a cell to exclude.
     */
    private void incrementHorizontalExcept(Ship ship, MatrixCoordinates mc) {
        for (int i = 0; i < ship.getLength(); i++) {
            int x = ship.getX() + i;
            int y = ship.getY();
            
            if (x != mc.x || y != mc.y) {
                increment(ship.getX() + i, ship.getY());
            }
        }
    }
    
    /**
     * Increments a <b>vertical</b> ship except the cell at coordinates 
     * {@code mc}.
     * 
     * @param ship the ship to increment.
     * @param mc   the coordinates of a cell to exclude.
     */
    private void incrementVerticalExcept(Ship ship, MatrixCoordinates mc) {
        for (int i = 0; i < ship.getLength(); i++) {
            int x = ship.getX();
            int y = ship.getY() + i;
            
            if (x != mc.x || y != mc.y) {
                increment(ship.getX(), ship.getY() + i);
            }
        }
    }
    
    /**
     * Loads a matrix row into a string builder.
     * 
     * @param sb                 the target string builder.
     * @param row                the row to load.
     * @param maximumEntryLength the maximum length of an entry in characters.
     * @param lineNumber         the line number of the {@code row}.
     * @param totalRows          the total number of rows in the matrix. 
     */
    private static void load(StringBuilder sb,
                             int[] row, 
                             int maximumEntryLength, 
                             int lineNumber, 
                             int totalRows) {
        int rowLength = row.length;
        int entryIndex = 0;
        String format = "%" + maximumEntryLength + "d";
        
        for (int entry : row) {
            sb.append(String.format(format, entry));
            
            if (++entryIndex < rowLength) {
                sb.append(" ");
            }
        }
        
        if (lineNumber < totalRows) {
            sb.append("\n");
        }
    }
}

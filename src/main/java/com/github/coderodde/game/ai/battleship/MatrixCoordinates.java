package com.github.coderodde.game.ai.battleship;

public final class MatrixCoordinates {
    public int x;
    public int y;

    public MatrixCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public MatrixCoordinates(MatrixCoordinates copy) {
        this.x = copy.x;
        this.y = copy.y;
    }
    
    @Override
    public String toString() {
        return "[x = " + x + ", y = " + y + "]";
    }
    
    @Override
    public boolean equals(Object o) {
        MatrixCoordinates other = (MatrixCoordinates) o;
        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + this.x;
        hash = 61 * hash + this.y;
        return hash;
    }
}

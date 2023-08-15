package com.github.coderodde.game.ai.battleship;

public final class MatrixCoordinates {
    public final int x;
    public final int y;

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
}

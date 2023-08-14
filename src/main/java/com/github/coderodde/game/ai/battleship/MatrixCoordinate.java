package com.github.coderodde.game.ai.battleship;

public final class MatrixCoordinate {
    public final int x;
    public final int y;

    public MatrixCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public MatrixCoordinate(MatrixCoordinate copy) {
        this.x = copy.x;
        this.y = copy.y;
    }
    
    @Override
    public String toString() {
        return "[x = " + x + ", y = " + y + "]";
    }
}

package com.github.coderodde.game.ai.battleship;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This class implements the random battleship AI bot.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 8, 2023)
 * @since 1.6 (Aug 8, 2023)
 */
public final class RandomBattleshipAIBot implements BattleshipAIBot {

    private final List<MatrixCoordinates> shotCoordinates;
    private final GameField gameField;
    
    public RandomBattleshipAIBot(GameField gameField) {
        this.gameField = gameField;
        int width = gameField.getWidth();
        int height = gameField.getHeight();
        this.shotCoordinates = new ArrayList<>(width * height);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                shotCoordinates.add(new MatrixCoordinates(x, y));
            }
        }
        
        Collections.shuffle(shotCoordinates, new Random());
    }
    
    @Override
    public MatrixCoordinates computeNextShotLocation(GameField gameField) {
        if (shotCoordinates.isEmpty()) {
            return null;
        }
        
        return shotCoordinates.remove(shotCoordinates.size() - 1);
    }

    @Override
    public void shoot(MatrixCoordinates matrixCoordinate) {
        gameField.shoot(matrixCoordinate.x,
                        matrixCoordinate.y);
        
        
    }
}

package com.github.coderodde.game.ai.battleship;

import com.github.coderodde.game.ai.battleship.GameField.ShipOverlapsWithExistingFleetException;
import java.util.Random;

/**
 * This class runs the demonstration of the two Battleship AI bots.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 8, 2023)
 * @since 1.6 (Aug 8, 2023)
 */
public final class Demo {
    
    private static final int GAME_FIELD_WIDTH = 10;
    private static final int GAME_FIELD_HEIGHT = 10;
    
    public static void main(String[] args) {
        
    }
    
    private static GameField createGameField() {
        GameField gameField = new GameField(GAME_FIELD_WIDTH,
                                            GAME_FIELD_HEIGHT);
        
        addRandomFleet(gameField);
        return gameField;
    }
    
    private static void addRandomFleet(GameField gameField) {
        Random random = new Random();
        
        Ship ship1 = new Ship(2, getRandomOrientation(random));
        Ship ship2 = new Ship(3, getRandomOrientation(random));
        Ship ship3 = new Ship(3, getRandomOrientation(random));
        Ship ship4 = new Ship(4, getRandomOrientation(random));
        Ship ship5 = new Ship(5, getRandomOrientation(random));
        
        Ship[] fleet = { ship1,
                         ship2,
                         ship3,
                         ship4,
                         ship5, };
        
        for (int i = 0; i < 5; i++) {
            Ship ship = fleet[i];
            int x;
            int y;
            
            innerLoop:
            while (true) {
                if (ship.getOrientation() == Ship.Orientation.HORIZONTAL) {
                    x = random.nextInt(
                            gameField.getWidth() - ship.getLength() + 1);
                    
                    y = random.nextInt(gameField.getHeight());
                } else {
                    x = random.nextInt(gameField.getWidth());
                    y = random.nextInt(
                            gameField.getHeight() - ship.getLength() + 1);
                }
                
                ship.setLocation(x, y);
                
                try {
                    gameField.addShip(ship);
                    break innerLoop;
                } catch (ShipOverlapsWithExistingFleetException ex) {
                    
                }
            }
        }
    }
    
    private static Ship.Orientation getRandomOrientation(Random random) {
        return random.nextBoolean() ? 
                Ship.Orientation.HORIZONTAL :
                Ship.Orientation.VERTICAL;
    }
}

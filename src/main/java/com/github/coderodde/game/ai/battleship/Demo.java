package com.github.coderodde.game.ai.battleship;

import com.github.coderodde.game.ai.battleship.GameField.ShipOverlapsWithExistingFleetException;
import com.github.coderodde.game.ai.battleship.Ship.Orientation;
import java.util.Random;

/**
 * This class runs the demonstration of the two Battleship AI bots.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 8, 2023)
 * @since 1.6 (Aug 8, 2023)
 */
public final class Demo {
    
    private static final int GAME_FIELD_WIDTH = 3;
    private static final int GAME_FIELD_HEIGHT = 3;
    
    public static void main(String[] args) {
        benchmarkInitialShot();
        System.exit(0);
        GameField bruteforceAIGameField = createGameField();
        GameField randomAIGameField = new GameField(bruteforceAIGameField);
        
        profile(new RandomBattleshipAIBot(randomAIGameField), 
                randomAIGameField);
        
//        profile(new BruteforceBattleshipAIBot(bruteforceAIGameField), 
//                bruteforceAIGameField);
    }
    
    private static void profile(BattleshipAIBot bot, GameField gameField) {
        System.out.println("Bot class: " + bot.getClass().getSimpleName());
        long totalDuration = System.currentTimeMillis();
        int shots = 0;
        
        while (!gameField.gameOver()) {
            long computationDuration = System.currentTimeMillis();
            
            MatrixCoordinates matrixCoordinate = bot.computeNextShotLocation();
            
            boolean exit = matrixCoordinate == null;
            
            if (exit) {
                break;
            } else {
                System.out.print(matrixCoordinate + " ");
                shots++;
                bot.shoot(matrixCoordinate);

                computationDuration = System.currentTimeMillis() 
                                    - computationDuration;

                System.out.printf(
                        "Computation of the shot %2d took %d milliseconds\n", 
                        shots, 
                        computationDuration);
            }
        }
        
        totalDuration = System.currentTimeMillis() - totalDuration;
        
        System.out.println(
                "Opponent destroyed in " 
                        + shots 
                        + " shots in " 
                        + totalDuration 
                        + " milliseconds.");
    }
    
    private static GameField createGameField() {
        GameField gameField = new GameField(GAME_FIELD_WIDTH,
                                            GAME_FIELD_HEIGHT);
        
        addRandomFleet(gameField);
        return gameField;
    }
    
    private static void addRandomFleet(GameField gameField) {
        Random random = new Random(1L);
        
        Ship ship1 = new Ship(2, getRandomOrientation(random));
        Ship ship2 = new Ship(3, getRandomOrientation(random));
        Ship ship3 = new Ship(3, getRandomOrientation(random));
//        Ship ship4 = new Ship(4, getRandomOrientation(random));
//        Ship ship5 = new Ship(5, getRandomOrientation(random));
        
        Ship[] fleet = { ship1,
//                         ship2,
//                         ship3,
//                         ship4,
//                         ship5,
        };
        
        for (int i = 0; i < fleet.length; i++) {
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
    
    private static void benchmarkInitialShot() {
        GameField gameField = new GameField(10, 10);
        
        Ship[] fleet = {
            new Ship(5, Orientation.HORIZONTAL),
            new Ship(4, Orientation.VERTICAL),
            new Ship(3, Orientation.HORIZONTAL),
            new Ship(3, Orientation.HORIZONTAL),
        };
        
        fleet[0].setLocation(0, 0);
        fleet[1].setLocation(0, 1);
        fleet[2].setLocation(2, 3);
        fleet[3].setLocation(2, 4);
        
        for (Ship ship : fleet) {
            gameField.addShip(ship);
        }
        
        Random random = new Random();
        
        for (int i = 0; i < 10; i++) {
            gameField.shoot(random.nextInt(gameField.getWidth()),
                            random.nextInt(gameField.getHeight()));
        }
        
        BattleshipAIBot bot = new BruteforceBattleshipAIBot(gameField, 0);
        
        long t = System.currentTimeMillis();
        MatrixCoordinates mc = bot.computeNextShotLocation();
        
        System.out.println(
                "First shot duration: " 
                        + (System.currentTimeMillis() - t) 
                        + " milliseconds.");
    }
}

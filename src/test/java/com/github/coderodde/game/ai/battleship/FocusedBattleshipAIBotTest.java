package com.github.coderodde.game.ai.battleship;

import com.github.coderodde.game.ai.battleship.Ship.Orientation;
import org.junit.Test;

public class FocusedBattleshipAIBotTest {

    @Test 
    public void test1() {
        GameField gameField = new GameField(3, 3);
        
        Ship ship1 = new Ship(2, Orientation.HORIZONTAL);
        Ship ship2 = new Ship(3, Orientation.VERTICAL);
        
        ship1.setLocation(0, 1);
        ship2.setLocation(2, 0);
        
        gameField.addShip(ship1);
        gameField.addShip(ship2);
        
        FocusedBattleshipAIBotOld bot = 
                new FocusedBattleshipAIBotOld(
                        ship2, 
                        new MatrixCoordinates(2, 1),
                        gameField);
        
        MatrixCoordinates mc = null;
        
        System.out.println(mc = bot.computeNextShotLocation());

        bot.shoot(mc);
        
        System.out.println(mc = bot.computeNextShotLocation());
        
        bot.shoot(mc);
        
        System.out.println(mc = bot.computeNextShotLocation());
    }
    
//    @Test
    public void test2() {
        GameField gameField = new GameField(5, 5);
        Ship ship1 = new Ship(3, Orientation.VERTICAL);
        Ship ship2 = new Ship(2, Orientation.HORIZONTAL);
        
        ship1.setLocation(2, 1);
        ship2.setLocation(1, 4);
        
        gameField.addShip(ship1);
        gameField.addShip(ship2);
        
        gameField.shoot(2, 2);
        
        FocusedBattleshipAIBotOld bot =
                new FocusedBattleshipAIBotOld(
                        ship1, 
                        new MatrixCoordinates(2, 2), 
                        gameField);
        
        MatrixCoordinates mc = null;
        
        System.out.println(mc = bot.computeNextShotLocation());
        
        bot.shoot(mc);
        
        System.out.println(mc = bot.computeNextShotLocation());
    }
}

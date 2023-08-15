package com.github.coderodde.game.ai.battleship;

import org.junit.Test;

public class FocusedBattleshipAIBotTest {
    
    @Test
    public void test1() {
        GameField gameField = new GameField(5, 5);
        Ship ship = new Ship(3, Ship.Orientation.VERTICAL);
        ship.setLocation(2, 1);
        gameField.addShip(ship);
        
        MatrixCoordinates mc = new MatrixCoordinates(2, 2);
        
        FocusedBattleshipAIBot bot = 
                new FocusedBattleshipAIBot(
                        ship, 
                        mc, 
                        gameField);
        
        bot.shoot(mc);
        MatrixCoordinates next = bot.computeNextShotLocation(gameField);
        
        System.out.println(next);
    }
}

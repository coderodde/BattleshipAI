package com.github.coderodde.game.ai.battleship;

import org.junit.Test;
import static org.junit.Assert.*;

public class BruteforceBattleshipAIBotTest {
    
    @Test
    public void test1() {
        GameField gameField = new GameField(2, 2);
        Ship ship1 = new Ship(2, Ship.Orientation.HORIZONTAL);
        Ship ship2 = new Ship(2, Ship.Orientation.HORIZONTAL);
        
        ship2.setY(1);
        
        gameField.addShip(ship1);
        gameField.addShip(ship2);
        
        BruteforceBattleshipAIBot bot =
                new BruteforceBattleshipAIBot(gameField);
        
        bot.computeNextShotLocation(gameField);
        
        FrequencyCounterMatrix frequencyCounterMatrix = 
                bot.getFrequencyCounterMatrix();
        
        System.out.println(frequencyCounterMatrix);
    }
}

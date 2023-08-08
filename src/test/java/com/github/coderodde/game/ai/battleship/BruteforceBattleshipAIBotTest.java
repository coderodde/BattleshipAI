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
        
        assertEquals(8, frequencyCounterMatrix.getCounter(0, 0));
        assertEquals(8, frequencyCounterMatrix.getCounter(0, 1));
        assertEquals(8, frequencyCounterMatrix.getCounter(1, 0));
        assertEquals(8, frequencyCounterMatrix.getCounter(1, 1));
    }
    
    @Test
    public void test2() {
        GameField gameField = new GameField(3, 2);
        Ship ship1 = new Ship(2, Ship.Orientation.HORIZONTAL);
        Ship ship2 = new Ship(3, Ship.Orientation.HORIZONTAL);
        
        ship2.setY(1);
        
        gameField.addShip(ship1);
        gameField.addShip(ship2);
        
        BruteforceBattleshipAIBot bot =
                new BruteforceBattleshipAIBot(gameField);
        
        bot.computeNextShotLocation(gameField);
        
        FrequencyCounterMatrix frequencyCounterMatrix = 
                bot.getFrequencyCounterMatrix();
        
        System.out.println(frequencyCounterMatrix);
        
        assertEquals(6, frequencyCounterMatrix.getCounter(0, 0));
        assertEquals(6, frequencyCounterMatrix.getCounter(0, 1));
        assertEquals(8, frequencyCounterMatrix.getCounter(1, 0));
        assertEquals(8, frequencyCounterMatrix.getCounter(1, 1));
        assertEquals(6, frequencyCounterMatrix.getCounter(2, 0));
        assertEquals(6, frequencyCounterMatrix.getCounter(2, 1));
    }
    
    @Test
    public void test3() {
        GameField gameField = new GameField(2, 2);
        Ship ship1 = new Ship(2, Ship.Orientation.HORIZONTAL);
        
        gameField.addShip(ship1);
        
        BruteforceBattleshipAIBot bot =
                new BruteforceBattleshipAIBot(gameField);
        
        bot.computeNextShotLocation(gameField);
        
        FrequencyCounterMatrix frequencyCounterMatrix = 
                bot.getFrequencyCounterMatrix();
        
        System.out.println(frequencyCounterMatrix);
        
        assertEquals(4, frequencyCounterMatrix.getCounter(0, 0));
        assertEquals(4, frequencyCounterMatrix.getCounter(0, 1));
        assertEquals(4, frequencyCounterMatrix.getCounter(1, 0));
        assertEquals(4, frequencyCounterMatrix.getCounter(1, 1));
    }
    
    @Test
    public void test4() {
        GameField gameField = new GameField(2, 2);
        Ship ship1 = new Ship(2, Ship.Orientation.HORIZONTAL);
        
        gameField.addShip(ship1);
        gameField.shoot(1, 0);
        
        BruteforceBattleshipAIBot bot =
                new BruteforceBattleshipAIBot(gameField);
        
        bot.computeNextShotLocation(gameField);
        
        FrequencyCounterMatrix frequencyCounterMatrix = 
                bot.getFrequencyCounterMatrix();
        
        System.out.println(frequencyCounterMatrix);
        
        assertEquals(2, frequencyCounterMatrix.getCounter(0, 0));
        assertEquals(4, frequencyCounterMatrix.getCounter(0, 1));
        assertEquals(0, frequencyCounterMatrix.getCounter(1, 0));
        assertEquals(2, frequencyCounterMatrix.getCounter(1, 1));
    }
}

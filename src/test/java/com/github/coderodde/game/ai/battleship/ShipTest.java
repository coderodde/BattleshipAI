package com.github.coderodde.game.ai.battleship;

import org.junit.Test;
import static org.junit.Assert.*;

public class ShipTest {
    
    @Test
    public void testOverlap() {
        for (int length1 = 1; length1 <= 5; length1++) {
            for (int length2 = 1; length2 <= 5; length2++) {
                overlapHH(length1, length2);
                overlapHV(length1, length2);
                overlapVH(length1, length2);
                overlapVV(length1, length2);
            }
        }
    }
    
    private static void overlapHH(int length1, int length2) {
        Ship ship1= new Ship(length1, Ship.Orientation.HORIZONTAL);
        Ship ship2 = new Ship(length2, Ship.Orientation.HORIZONTAL);
        runTest(ship1, ship2);
    }
    
    private static void overlapHV(int length1, int length2) {
        Ship ship1= new Ship(length1, Ship.Orientation.HORIZONTAL);
        Ship ship2 = new Ship(length2, Ship.Orientation.VERTICAL);
        runTest(ship1, ship2);
    }
    
    private static void overlapVH(int length1, int length2) {
        Ship ship1= new Ship(length1, Ship.Orientation.VERTICAL);
        Ship ship2 = new Ship(length2, Ship.Orientation.HORIZONTAL);
        runTest(ship1, ship2);
    }
    
    private static void overlapVV(int length1, int length2) {
        Ship ship1= new Ship(length1, Ship.Orientation.VERTICAL);
        Ship ship2 = new Ship(length2, Ship.Orientation.VERTICAL);
        runTest(ship1, ship2);
    }
    
    private static void runTest(Ship ship1, Ship ship2) {
        for (int x1 = 0; x1 < 10; x1++) {
            ship1.setX(x1);
            
            for (int y1 = 0; y1 < 10; y1++) {
                ship1.setY(y1);
                
                for (int x2 = 0; x2 < 10; x2++) {
                    ship2.setX(x2);
                    
                    for (int y2 = 0; y2 < 10; y2++) {
                        ship2.setY(y2);
                        
                        assertEquals(
                                Ship.compartmentsOverlap(ship1, ship2), 
                                ship1.overlap(ship2));
                    }
                }
            }
        }
    }
}

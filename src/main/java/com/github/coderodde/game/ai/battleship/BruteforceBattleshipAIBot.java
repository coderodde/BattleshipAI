package com.github.coderodde.game.ai.battleship;

/**
 * This class implements the brute-force Battleship
 */
public class BruteforceBattleshipAIBot implements BattleshipAIBot {

    private final GameField gameField;
    private final FrequencyCounterMatrix frequencyCounterMatrix;
    
    public BruteforceBattleshipAIBot(GameField gameField) {
        this.gameField = gameField;
        this.frequencyCounterMatrix =
                new FrequencyCounterMatrix(
                        gameField.getWidth(),
                        gameField.getHeight());
    }
    
    @Override
    public MatrixCoordinate shoot(GameField gameField) {
        frequencyCounterMatrix.clear();
        
        gameField.getFleet().sort(Ship::compareTo);
        
        for (Ship s : gameField.getFleet()) {
            System.out.println(s);
        }
        
        putShipHorizontal(0);
        putShipVertical(0);
        return frequencyCounterMatrix.getMaximumMatrixCounter();
    }
    
    private void putShipHorizontal(int shipIndex) {
        if (shipIndex == gameField.getFleet().size()) {
            // Once here, all the ships in the fleet are positioned.
            return;
        }
        
        Ship targetShip = gameField.getFleet().get(shipIndex);
        targetShip.setOrientation(Ship.Orientation.HORIZONTAL);
        
        putShipHorizontal(shipIndex + 1);
        putShipVertical(shipIndex + 1);
    }
    
    private void putShipVertical(int shipIndex) {
        
    }
}

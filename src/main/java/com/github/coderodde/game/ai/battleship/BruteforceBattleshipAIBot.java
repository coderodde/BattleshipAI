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
    public void shoot(MatrixCoordinate matrixCoordinate) {
        gameField.shoot(matrixCoordinate.x,
                        matrixCoordinate.y);
        
        Ship ship = gameField.getShipAt(matrixCoordinate.x,
                                        matrixCoordinate.y);
        
        if (ship == null) {
            return;
        }
        
        if (gameField.shipIsDestroyed(ship)) {
            gameField.removeShip(ship);
        }
    }
    
    @Override
    public MatrixCoordinate computeNextShotLocation(GameField gameField) {
        if (gameField.getSearchFleet().isEmpty()) {
            // Once here, the AI has found and destroyed all the ships:
            return null;
        }
        
        frequencyCounterMatrix.clear();
        
        gameField.getSearchFleet().sort(Ship::compareTo);
        
        for (Ship s : gameField.getSearchFleet()) {
            s.setLocation(0, 0);
        }
        
        putShipHorizontal(0);
        putShipVertical(0);
        
        return frequencyCounterMatrix.getMaximumMatrixCounter();
    }
    
    public FrequencyCounterMatrix getFrequencyCounterMatrix() {
        return frequencyCounterMatrix;
    }
    
    private void putShipHorizontal(int shipIndex) {
        if (shipIndex == gameField.getSearchFleet().size()) {
            // Once here, all the ships in the fleet are positioned. Print them
            // to the frequency counter matrix:
            frequencyCounterMatrix.incrementFleet(gameField.getSearchFleet());
            return;
        }
        
        Ship targetShip = gameField.getSearchFleet().get(shipIndex);
        targetShip.setOrientation(Ship.Orientation.HORIZONTAL);
        
        for (int x = 0; 
                x <= gameField.getWidth() - targetShip.getLength();
                x++) {
            
            for (int y = 0; y < gameField.getHeight(); y++) {
                targetShip.setLocation(x, y);
                
                if (gameField.shipOccupiesClosedCell(targetShip) ||
                    targetShip.overlapsAny(
                            gameField.getSearchFleet().subList(0, shipIndex))) {
                    
                    // The current target ship occupies a closed cell or 
                    // overlaps some other ship in the fleet. Just omit placing
                    // it:
                    continue;
                }
                
                putShipHorizontal(shipIndex + 1);
                putShipVertical(shipIndex + 1);
            }
        }
    }
    
    private void putShipVertical(int shipIndex) {
        if (shipIndex == gameField.getSearchFleet().size()) {
            // Once here, all the ships in the fleet are positioned. Print them
            // to the frequency counter matrix:
            frequencyCounterMatrix.incrementFleet(gameField.getSearchFleet());
            return;
        }
        
        Ship targetShip = gameField.getSearchFleet().get(shipIndex);
        targetShip.setOrientation(Ship.Orientation.VERTICAL);
        
        for (int x = 0; x < gameField.getWidth(); x++) {
            for (int y = 0; 
                    y <= gameField.getHeight() - targetShip.getLength();
                    y++) {
                
                targetShip.setLocation(x, y);
                
                if (gameField.shipOccupiesClosedCell(targetShip) ||
                    targetShip.overlapsAny(
                            gameField.getSearchFleet().subList(0, shipIndex))) {
                    
                    // The current target ship occupies a closed cell or 
                    // overlaps some other ship in the fleet. Just omit placing
                    // it:
                    continue;
                }
                
                putShipHorizontal(shipIndex + 1);
                putShipVertical(shipIndex + 1);
            }
        }
    }
}

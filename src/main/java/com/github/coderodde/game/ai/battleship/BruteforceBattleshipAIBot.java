package com.github.coderodde.game.ai.battleship;

import com.github.coderodde.game.ai.battleship.FocusedBattleshipAIBot.FocusedFleetDestroyedException;

/**
 * This class implements the brute-force Battleship. When requested for a shot
 * position, it arranges all the remaining ships into all possible arrangements
 * and returns the point that is the most covered.
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 21, 2023)
 * @since 1.6 (Aug 21, 2023)
 */
public class BruteforceBattleshipAIBot implements BattleshipAIBot {

    /**
     * The game field containing the opponents fleet.
     */
    private final GameField gameField;
    
    /**
     * The frequency matrix.
     */
    private final FrequencyCounterMatrix frequencyCounterMatrix;
    
    /**
     * If this bot is focused on an ongoing ship, this field refers to the bot
     * that is localized to shoot in a neighbourhood.
     */
    private FocusedBattleshipAIBot focusedBot;
    
    /**
     * Constructs this AI bot.
     * 
     * @param gameField the game field.
     */
    public BruteforceBattleshipAIBot(GameField gameField) {
        this.gameField = gameField;
        this.frequencyCounterMatrix =
                new FrequencyCounterMatrix(
                        gameField.getWidth(),
                        gameField.getHeight());
    }

    /**
     * {@inheritDoc } 
     */
    @Override
    public void shoot(MatrixCoordinates matrixCoordinate) {
        // Are we focused on a particular ship?
        if (focusedBot != null) {
            // ... yes, we are.
            try {
                // Let the focused bot shoot.
                focusedBot.shoot(matrixCoordinate);
            } catch (FocusedFleetDestroyedException ex) {
                // Focused ship/fleet is destroyed.
                focusedBot = null;
                
                if (gameField.fleetDestroyed()) {
                    // Once here, there is no ships left, throw:
                    throw new OpponentFleetDestroyedException();
                }
            }
                
            return;
        }
        
        gameField.shoot(matrixCoordinate.x,
                        matrixCoordinate.y);
        
        Ship ship = gameField.getShipAt(matrixCoordinate.x,
                                        matrixCoordinate.y);
        
        if (ship == null) {
            // The previous shot was a miss, do nothing:
            return;
        }
        
        // Once here, we have a hit:
        if (gameField.shipIsDestroyed(ship)) {
            // Once here, we destoroyed an entire ship:
            gameField.removeShip(ship);
            
            if (gameField.fleetDestroyed()) {
                // 'ship' was the last ship standing, game is over:
                throw new OpponentFleetDestroyedException();
            }
        } else {
            // Once here, we have a hit, yet the ship was not fully destroyed,
            // focus on it:
            focusedBot = new FocusedBattleshipAIBot(ship,
                                                    matrixCoordinate, 
                                                    gameField);
        }
    }
    
    /**
     * Computes the next shot position.
     * 
     * @return the next shot position.
     */
    @Override
    public MatrixCoordinates computeNextShotLocation() {
        if (gameField.getSearchFleet().isEmpty()) {
            throw new IllegalStateException(
                    "The client programmer should have caught the " + 
                            "OpponentFleetDestroyedException by now.");
        }
        
        // Reset all the entries of the frequency counter matrix to zero:
        frequencyCounterMatrix.clear();
        
        // Sort the ships. Longest ship comes first.
        gameField.getSearchFleet().sort(Ship::compareTo);
        
        // Set each search ship to its starting location (0, 0):
        for (Ship s : gameField.getSearchFleet()) {
            s.setLocation(0, 0);
        }
        
        // Search over longest ship oriented in horizontal direction:
        putShipHorizontal(0);
        
        // Search over longest ship oriented in vertical direction:
        putShipVertical(0);
        
        return frequencyCounterMatrix.getMaximumMatrixCounter();
    }
    
    /**
     * Returns the frequency counter matrix.
     * 
     * @return the frequency counter matrix.
     */
    public FrequencyCounterMatrix getFrequencyCounterMatrix() {
        return frequencyCounterMatrix;
    }
    
    /**
     * Attempts to set a ship at index {@code shipIndex} in horizontal 
     * orientation in all possible locations avoiding the cells that are already
     * shot.
     * 
     * @param shipIndex the index of the ship.
     */
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
    
    /**
     * Attempts to set a ship at index {@code shipIndex} in vertical orientation 
     * in all possible locations avoiding the cells that are already shot.
     * 
     * @param shipIndex the index of the ship.
     */
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

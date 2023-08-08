package com.github.coderodde.game.ai.battleship;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * This class defines a ship in the Battleship game.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 8, 2023)
 * @since 1.6 (Aug 8, 2023)
 */
public final class Ship implements Comparable<Ship> {
    
    public enum Orientation {
        
        HORIZONTAL ("Horizontal"),
        VERTICAL   ("Vertical");
        
        private final String enumName;
        
        private Orientation(String enumName) {
            this.enumName = enumName;
        }
        
        public String toString() {
            return enumName;
        }
    }
    
    public static final class ShipCompartment {
        public final int x;
        public final int y;
        
        public ShipCompartment(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        @Override
        public boolean equals(Object o) {
            ShipCompartment other = (ShipCompartment) o;
            return x == other.x && y == other.y;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
    
    private int x;
    private int y;
    private final int length;
    private Orientation orientation;
    
    public Ship(int length, Orientation orientation) {
        this.length = length;
        this.orientation = orientation;
    }
    
    public Ship(Ship ship) {
        this.x = ship.x;
        this.y = ship.y;
        this.length = ship.length;
        this.orientation = ship.orientation;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getLength() {
        return length;
    }
    
    public Orientation getOrientation() {
        return orientation;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public void setLocation(int x, int y) {
        setX(x);
        setY(y);
    }
    
    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }
    
    @Override
    public boolean equals(Object o) {
        Ship ship = (Ship) o;
        return x == ship.x && 
               y == ship.y && 
               length == ship.length && 
               orientation == ship.orientation;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y, length, orientation);
    }
    
    @Override
    public int compareTo(Ship other) {
        return length - other.length;
    }
    
    @Override
    public String toString() {
        return "[Ship; x = " 
                + x 
                + ", y = " 
                + y 
                + ", length = " 
                + length
                + ", orientation = "
                + orientation 
                + "]";
    }
    
    public Set<ShipCompartment> convertToShipCompartments() {
        Set<ShipCompartment> compartments = new HashSet<>(length, 2.0f);
        
        switch (orientation) {
            case HORIZONTAL:
                loadCompartmentsHorizontal(compartments);
                break;
                
            case VERTICAL:
                loadCompartmentsVertical(compartments);
                break;
                
            default:
                throw new IllegalStateException("Should not get here.");
        }
        
        return compartments;
    }
    
    public boolean overlap(Ship ship) {
        switch (getOrientation()) {
            case HORIZONTAL:
                switch (ship.getOrientation()) {
                    case HORIZONTAL:
                        return overlapHorzHorz(this, ship);
                        
                    case VERTICAL:
                        return overlapHorzVert(this, ship);
                }
            case VERTICAL:
                switch (ship.getOrientation()) {
                    case HORIZONTAL:
                        return overlapVertHorz(this, ship);
                        
                    case VERTICAL:
                        return overlapVertVert(this, ship);
                }
        }
        
        throw new IllegalStateException("Should not get here.");
    }
    
    public boolean overlapsAny(List<Ship> fleet) {
        for (Ship ship : fleet) {
            if (ship != this && ship.overlap(this)) {
                return true;
            }
        }
        
        return false;
    }
    
    public static boolean compartmentsOverlap(Ship ship1, Ship ship2) {
        Set<ShipCompartment> shipCompartments1 = 
                ship1.convertToShipCompartments();
        
        Set<ShipCompartment> shipCompartments2 = 
                ship2.convertToShipCompartments();
        
        Set<ShipCompartment> shipCompartmentsSmall;
        Set<ShipCompartment> shipCompartmentsLarge;
        
        if (shipCompartments1.size() < shipCompartments2.size()) {
            shipCompartmentsSmall = shipCompartments1;
            shipCompartmentsLarge = shipCompartments2;
        } else {
            shipCompartmentsSmall = shipCompartments2;
            shipCompartmentsLarge = shipCompartments1;            
        }
        
        for (ShipCompartment shipCompartment : shipCompartmentsSmall) {
            if (shipCompartmentsLarge.contains(shipCompartment)) {
                return true;
            }
        }
        
        return false;
    }
    
    private static boolean overlapHorzHorz(Ship ship1, Ship ship2) {
        if (ship1.getY() != ship2.getY()) {
            return false;
        }
        
        if (ship1.getX() + ship1.getLength() <= ship2.getX()) {
            return false;
        }
        
        if (ship2.getX() + ship2.getLength() <= ship1.getX()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Checks whether the two input ships overlap. 
     * 
     * @param shipHorz the first ship; horizontal orientation.
     * @param shipVert the second ship; vertical orientation.
     * 
     * @return {@code true} only if the two ships overlap.
     */
    private static boolean overlapHorzVert(Ship shipHorz, Ship shipVert) {
        if (shipHorz.getY() < shipVert.getY()) {
            return false;
        }
        
        if (shipHorz.getY() >= shipVert.getY() + shipVert.getLength()) {
            return false;
        }
        
        if (shipHorz.getX() > shipVert.getX()) {
            return false;
        }
        
        if (shipHorz.getX() + shipHorz.getLength() <= shipVert.getX()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Checks whether the two input ships overlap.
     * 
     * @param shipVert the ship with the vertical orientation.
     * @param shipHorz the ship with the horizontal orientation.
     * 
     * @return {@code true} only if the two input ships overlap.
     */
    private static boolean overlapVertHorz(Ship shipVert, Ship shipHorz) {
        return overlapHorzVert(shipHorz, shipVert);
    }
    
    private static boolean overlapVertVert(Ship ship1, Ship ship2) {
        if (ship1.getX() != ship2.getX()) {
            return false;
        }
        
        if (ship1.getY() + ship1.getLength() <= ship2.getY()) {
            return false;
        }
        
        if (ship2.getY() + ship2.getLength() <= ship1.getY()) {
            return false;
        }
        
        return true;
    }
    
    private void loadCompartmentsVertical(Set<ShipCompartment> compartments) {
        for (int i = 0; i < length; i++) {
            compartments.add(new ShipCompartment(x, y + i));
        }
    }
    
    private void loadCompartmentsHorizontal(Set<ShipCompartment> compartments) {
        for (int i = 0; i < length; i++) {
            compartments.add(new ShipCompartment(x + i, y));
        }
    }
}

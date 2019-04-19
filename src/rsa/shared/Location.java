package rsa.shared;

import rsa.quad.HasPoint;
import rsa.quad.Trie;

/**
 * A location given by a pair of coordinates (doubles).
 *
 * Locations can be compared and must redefine the equals() method.
 */
public class Location implements HasPoint {

    private double x, y;

    public Location(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * The X coordinate of this location
     * @return X
     */
    @Override
    public double getX() {
        return x;
    }

    /**
     * The Y coordinate of this location
     * @return Y
     */
    @Override
    public double getY() {
        return y;
    }

    public double distance(Location l) {
        return Trie.getDistance(this.getX(), this.getY(), l.getX(), l.getY());
    }

    public Location clone() {
    	return new Location(x, y);
    }

    @Override
    public int hashCode() {
        return (Double.hashCode(getX()) + Double.hashCode(getY())) * 7;
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof HasPoint) {
            return x == ((HasPoint) o).getX() && y == ((HasPoint) o).getY();
        }

        return false;
    }
}

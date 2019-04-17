package rsa.shared;

import rsa.quad.HasPoint;
import rsa.quad.Trie;

public class Location implements HasPoint {

    private double x, y;

    public Location(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    public double distance(Location l) {
        return Trie.getDistance(this.getX(), this.getY(), l.getX(), l.getY());
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof HasPoint) {
            return x == ((HasPoint) o).getX() && y == ((HasPoint) o).getY();
        }

        return false;
    }
}

package rsa.quad;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Set;

public abstract class Trie<T extends HasPoint> {

    protected double bottomRightX, bottomRightY;

    static int capacity;

    protected double topLeftX, topLeftY;

    protected Trie(double topLeftX, double topLeftY, double bottomRightX, double bottomRightY) {

        this.topLeftX = topLeftX;
        this.topLeftY = topLeftY;

        this.bottomRightX = bottomRightX;
        this.bottomRightY = bottomRightY;

    }

    /**
     * Collects all points in this node and its descendants in given set
     */
    abstract void collectAll(Set<T> points);

    /**
     * Collect points at a distance smaller or equal to radius from (x,y) and place them in given list
     */
    abstract void collectNear(double x, double y, double radius, Set<T> points);

    abstract void delete(T point);

    abstract T find(T point);

    abstract Trie<T> insert(T point);

    abstract Trie<T> insertReplace(T point);

    protected Rectangle buildRectangle() {
    	
        return new Rectangle(topLeftX, topLeftY, bottomRightX, bottomRightY);
    }

    /**
     * Check if the rectangle from this point overlaps with a circle centered in (x,y) with radius radius
     */
    protected boolean overlaps(double x, double y, double radius) {

        Point2D center = new Point2D.Double(x, y);

        return (buildRectangle().contains(center) ||
                intersectsEdges(center, radius));
    }

    private boolean intersectsEdges(Point2D center, double radius) {

        double x1 = topLeftX, x2 = bottomRightX;

        double y1 = topLeftY, y2 = bottomRightY;

        double xc = center.getX(), yc = center.getY();

        double radiusSqrd = radius * radius;
        
        double testX = xc, testY = yc;

        if (xc < x1) testX = x1;
        else if (xc > x2) testX = x2;
        
        if (yc < y2) testY = y2;
        else if (yc > y1) testY = y1;
        
        double dist1 = xc - testX, dist2 = yc - testY;
        
        double distance = Math.pow(dist1, 2) + Math.pow(dist2, 2);
        
        return distance <= radiusSqrd;
    }

    public static double getDistance(double x1, double y1, double x2, double y2) {
        return new Point2D.Double(x1, y1).distance(new Point2D.Double(x2, y2));
    }

    public static int getCapacity() {
        return capacity;
    }

    public static void setCapacity(int cap) {
        capacity = cap;
    }

    protected enum Quadrant {
        NE,
        NW,
        SE,
        SW;
    }
    
    protected class Rectangle {
    	
    	double topLeftX, topLeftY, bottomRightX, bottomRightY;

		public Rectangle(double topLeftX, double topLeftY, double bottomRightX, double bottomRightY) {
			this.topLeftX = topLeftX;
			this.topLeftY = topLeftY;
			this.bottomRightX = bottomRightX;
			this.bottomRightY = bottomRightY;
		}
		
		public boolean contains(Point2D center) {
			return contains(center.getX(), center.getY());
		}

		public boolean contains(double x, double y) {
			
			 return this.topLeftX <= x && this.bottomRightX >= x && this.topLeftY >= y && this.bottomRightY <= y;
			
		}
    	
    }
}

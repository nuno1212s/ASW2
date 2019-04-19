package rsa.quad;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Set;

/**
 * Abstract class common to all classes implementing the trie structure.
 * Defines methods required by those classes and provides general methods for checking overlaps and computing distances.
 * This class corresponds to the Component in the Composite design pattern.
 */
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

    /**
     * Delete given point
     */
    abstract void delete(T point);

    /**
     * Find a recorded point with the same coordinates of given point
     */
    abstract T find(T point);

    /**
     * Insert given point
     */
    abstract Trie<T> insert(T point);

    /**
     * Insert given point, replacing existing points in same location
     */
    abstract Trie<T> insertReplace(T point);

    /**
     * Check if the rectangle from this point overlaps with a circle centered in (x,y) with radius radius
     */
    boolean overlaps(double x, double y, double radius) {

        Point2D center = new Point2D.Double(x, y);

        return (buildRectangle().contains(center) ||
                intersectsEdges(center, radius));
    }

    /**
     * Builds a rectangle with the dimensions of the quad tree
     */
    Rectangle buildRectangle() {

        return new Rectangle(topLeftX, topLeftY, bottomRightX, bottomRightY);
    }

    /**
     * Checks if a circle intersects with this nodes area
     */
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

    /**
     * Get's the euclidian distance between two points
     */
    public static double getDistance(double x1, double y1, double x2, double y2) {
        return new Point2D.Double(x1, y1).distance(new Point2D.Double(x2, y2));
    }

    /**
     * The capacity of each leaf node
     */
    public static int getCapacity() {
        return capacity;
    }

    /**
     * Set the capacity of each leaf node
     */
    public static void setCapacity(int cap) {
        capacity = cap;
    }

    protected enum Quadrant {
        NE,
        NW,
        SE,
        SW;
    }

    /**
     * Rectangle utility class
     */
    protected class Rectangle {
    	
    	double topLeftX, topLeftY, bottomRightX, bottomRightY;

		public Rectangle(double topLeftX, double topLeftY, double bottomRightX, double bottomRightY) {
			this.topLeftX = topLeftX;
			this.topLeftY = topLeftY;
			this.bottomRightX = bottomRightX;
			this.bottomRightY = bottomRightY;
		}

        /**
         * Check if a point is contained within the rectangle
         */
		public boolean contains(Point2D center) {
			return contains(center.getX(), center.getY());
		}

        /**
         * Check if a point is contained within the rectangle
         */
		public boolean contains(double x, double y) {
			 return this.topLeftX <= x && this.bottomRightX >= x
                     && this.topLeftY >= y && this.bottomRightY <= y;
		}
    	
    }
}

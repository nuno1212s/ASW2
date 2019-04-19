package rsa.quad;

import java.util.HashSet;
import java.util.Set;

public class PointQuadtree<T extends HasPoint> {

    private Trie<T> rootNode;

    /**
     * Creates a quadTree with the given bounds
     */
    public PointQuadtree(double topLeftX, double topLeftY, double bottomRightX, double bottomRightY) {

        this.rootNode = new NodeTrie<>(topLeftX, topLeftY, bottomRightX, bottomRightY);

    }

    /**
     * Find a recorded point with the same coordinates of given point
     */
    public T find(T point) {
    	return rootNode.find(point);
    }

    /**
     * Delete given point from QuadTree, if it exists there
     */
    public void delete(T point) {

        rootNode.delete(point);

    }

    /**
     * Insert given point in the QuadTree
     */
    public void insert(T point) {
    	
        if (rootNode.insert(point) == null) {
        	
        	throw new PointOutOfBoundException();
        	
        }

    }

    /**
     * Insert point, replacing existing point in the same position
     */
    public void insertReplace(T point) {

        rootNode.insertReplace(point);

    }

    /**
     * A set with all points in the QuadTree
     */
    public Set<T> getAll() {

        Set<T> points = new HashSet<>();

        rootNode.collectAll(points);

        return points;

    }

    /**
     * Returns a set of points at a distance smaller or equal to radius from point with given coordinates.
     */
    public Set<T> findNear(double x, double y, double radius) {

        Set<T> points = new HashSet<>();

        rootNode.collectNear(x, y, radius, points);

        return points;
    }

}

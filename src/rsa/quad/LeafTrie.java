package rsa.quad;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import rsa.service.Ride;

public class LeafTrie<T extends HasPoint> extends Trie<T> {

    private Quadrant quadrant;

    private NodeTrie<T> parent;

    protected List<T> points;
    
    protected LeafTrie(double topLeftX, double topLeftY, double bottomRightX, double bottomRightY) {
    	super(topLeftX, topLeftY, bottomRightX, bottomRightY);
    	
    
    	this.points = new ArrayList<>(getCapacity());
    	
    }

    protected LeafTrie(Quadrant q, NodeTrie<T> parent, double topLeftX, double topLeftY, double bottomRightX, double bottomRightY) {
        super(topLeftX, topLeftY, bottomRightX, bottomRightY);
        this.quadrant = q;
        this.parent = parent;

        points = new ArrayList<>(getCapacity());
    }

    @Override
    void collectAll(Set<T> points) {

        points.addAll(this.points);

    }

    @Override
    void collectNear(double x, double y, double radius, Set<T> points) {

        double sqrdRadius = radius * radius;

        Point2D center = new Point2D.Double(x, y);

        for (T point : this.points) {

            Point2D ponto = new Point2D.Double(point.getX(), point.getY());
            
            if (ponto.distanceSq(center) <= sqrdRadius) {

                points.add(point);

            }

        }

    }

    @Override
    void delete(T point) {

        if (!buildRectangle().contains(point.getX(), point.getY())) {
            return;
        }

        findAndRemove(point);

    }

    @Override
    T find(T point) {

        if (!buildRectangle().contains(point.getX(), point.getY())) {
            return null;
        }

        for (T t : this.points) {
            if (t.equals(point)) {
            	return t;
            }
        }

        return null;
    }

    @Override
    Trie<T> insert(T point) {

        if (!buildRectangle().contains(point.getX(), point.getY())) {
        	
            return null;
        }

        if (this.points.size() >= getCapacity()) {
            return this.divide().insert(point);
        }

        points.add(point);

        return this;
    }

    @Override
    Trie<T> insertReplace(T point) {

        if (!buildRectangle().contains(point.getX(), point.getY())) {
            return null;
        }

        findAndRemove(point);

        this.points.add(point);

        return this;
    }

    private void findAndRemove(T point) {
        Iterator<T> iterator = this.points.iterator();

        while (iterator.hasNext()) {

            T p = iterator.next();

            if (p.equals(point)) {
            	iterator.remove();
            	break;
            }
        }
    }

    private Trie<T> divide() {

        NodeTrie<T> newNode = new NodeTrie<>(topLeftX, topLeftY, bottomRightX, bottomRightY);

        this.parent.updateSubChild(this.quadrant, newNode);

        newNode.divide(this.points);

        return newNode;
    }
}

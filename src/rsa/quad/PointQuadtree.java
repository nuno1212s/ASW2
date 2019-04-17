package rsa.quad;

import java.util.HashSet;
import java.util.Set;

public class PointQuadtree<T extends HasPoint> {

    private Trie<T> rootNode;

    public PointQuadtree(double topLeftX, double topLeftY, double bottomRightX, double bottomRightY) {

        this.rootNode = new NodeTrie<>(topLeftX, topLeftY, bottomRightX, bottomRightY);

    }

    public void delete(T point) {

        rootNode.delete(point);

    }

    public void insert(T point) {

        rootNode.insert(point);

    }

    public void insertReplace(T point) {

        rootNode.insert(point);

    }

    public Set<T> getAll() {

        Set<T> points = new HashSet<>();

        rootNode.collectAll(points);

        return points;

    }

    public Set<T> findNear(double x, double y, double radius) {

        Set<T> points = new HashSet<>();

        rootNode.collectNear(x, y, radius, points);

        return points;
    }

}
package rsa.quad;

import java.util.*;

public class NodeTrie<T extends HasPoint> extends Trie<T> {

    private Map<Quadrant, Trie<T>> childs = new TreeMap<>();

    protected NodeTrie(double topLeftX, double topLeftY, double bottomRightX, double bottomRightY) {
        super(topLeftX, topLeftY, bottomRightX, bottomRightY);
    }

    @Override
    void collectAll(Set<T> points) {
        childs.values().forEach((point) -> {

            point.collectAll(points);

        });
    }

    @Override
    void collectNear(double x, double y, double radius, Set<T> points) {

        if (overlaps(x, y, radius)) {
            for (Trie<T> value : this.childs.values()) {
                value.collectNear(x, y, radius, points);
            }
        }

    }

    @Override
    void delete(T point) {

        if (!buildRectangle().contains(point.getX(), point.getY())) {
            return;
        }

        this.childs.values().forEach(child -> child.delete(point));

    }

    @Override
    T find(T point) {
        return null;
    }

    @Override
    Trie<T> insert(T point) {

        if (!buildRectangle().contains(point.getX(), point.getY())) {
            return null;
        }

        if (!childs.isEmpty()) {
            divide(new ArrayList<>(0));
        }

        for (Trie<T> subNode : this.childs.values()) {

            Trie<T> insert = subNode.insert(point);

            if (insert != null) {

                return insert;

            }

        }

        return null;
    }

    @Override
    Trie<T> insertReplace(T point) {
        if (!buildRectangle().contains(point.getX(), point.getY())) {
            return null;
        }

        for (Trie<T> subNode : this.childs.values()) {

            Trie<T> insert = subNode.insertReplace(point);

            if (insert != null) {

                return insert;

            }

        }

        return null;
    }

    protected void updateSubChild(Quadrant q, Trie<T> newChild) {

        this.childs.put(q, newChild);

    }

    protected void divide(List<T> points) {

        double x1 = topLeftX, x2 = bottomRightX;

        double y1 = topLeftY, y2 = bottomRightY;

        this.childs.put(Quadrant.NW, new LeafTrie<>(Quadrant.NW, this, x1, y1, x2 + ((x1 - x2) / 2), y2 + ((y1 - y2) / 2)));

        this.childs.put(Quadrant.NE, new LeafTrie<>(Quadrant.NE, this, x2 + ((x1 - x2) / 2), y1, x2, y2 + ((y1 - y2) / 2)));

        this.childs.put(Quadrant.SW, new LeafTrie<>(Quadrant.SW, this, x1, y2 + ((y1 - y2) / 2), x2 + ((x1 - x2) / 2), y2 + ((y1 - y2) / 2)));

        this.childs.put(Quadrant.SE, new LeafTrie<>(Quadrant.SE, this, x2 + ((x1 - x2) / 2), y2 + ((y1 - y2) / 2), x2, y2));

        forx:
        for (T point : points) {
            for (Trie<T> value : this.childs.values()) {

                if (value.insert(point) != null) {
                    continue forx;
                }

            }
        }

    }
}

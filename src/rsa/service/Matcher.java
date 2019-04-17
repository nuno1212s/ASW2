package rsa.service;

import rsa.quad.PointQuadtree;
import rsa.shared.Location;
import rsa.shared.RideMatchInfo;
import rsa.shared.RideRole;
import rsa.shared.UserStars;

import java.util.*;
import java.util.stream.Collectors;

public class Matcher {

    private static Location topLeft, bottomRight;

    private static double radius;

    private List<Ride> rides;

    private PointQuadtree<Ride> quadTree;

    public Matcher() {

        rides = new ArrayList<>();
        quadTree = new PointQuadtree<>(topLeft.getX(), topLeft.getY(), bottomRight.getX(), bottomRight.getY());

    }

    public Ride getRideWithId(long rideId) {

        for (Ride ride : rides) {

            if (ride.getId() == rideId) {
                return ride;
            }

        }

        return null;
    }

    public void acceptMatch(long rideId, long matchId) {

    }

    public long addRide(User user, Location from, Location to, String plate, float cost) {

        Ride r = new Ride(user, from, to, plate, cost);

        quadTree.insert(r);

        return r.getId();

    }

    public void concludeRide(long rideId, UserStars stars) {

    }

    public static Location getBottomRight() {
        return bottomRight;
    }

    public static double getRadius() {
        return radius;
    }

    public static void setRadius(double radius) {
        Matcher.radius = radius;
    }

    public static Location getTopLeft() {
        return topLeft;
    }

    public static void setTopLeft(Location topLeft) {
        Matcher.topLeft = topLeft;
    }

    public static void setBottomRight(Location bottomRight) {
        Matcher.bottomRight = bottomRight;
    }

    SortedSet<RideMatchInfo> updateRide(long rideId, Location current) {

        Ride r = getRideWithId(rideId);

        quadTree.delete(r);

        r.setFrom(current);

        quadTree.insert(r);

        if (!r.isMatched()) {

            TreeSet<RideMatchInfo> rides = new TreeSet<>(r.getComparator());

            Set<Ride> near = quadTree.findNear(r.getFrom().getX(), r.getFrom().getY(), radius);

            near = near.stream()
                    .filter(r1 -> r1.getCurrentRideRole().other().equals(r.getCurrentRideRole())
                            && r1.getTo().distance(r.getTo()) < radius)
                    .collect(Collectors.toSet());

            for (Ride ride : near) {
                RideMatch match = new RideMatch(r, ride);

                rides.add(new RideMatchInfo(match));
            }

            return rides;

        }

        return null;
    }

    public class RideMatch {

        long id;

        Map<RideRole, Ride> rides;

        RideMatch(Ride left, Ride right) {

            id = new Random().nextLong();

            rides = new HashMap<>();

            rides.put(left.getRole(), left);
            rides.put(right.getRole(), right);

        }

        public long getId() {
            return id;
        }

        public Ride getRide(RideRole role) {
            return rides.get(role);
        }

        public boolean matchable() {

            boolean hasDriver = false;

            for (Map.Entry<RideRole, Ride> people : this.rides.entrySet()) {

                if (people.getKey() == RideRole.DRIVER) {

                    if (hasDriver) {
                        return false;
                    }

                    hasDriver = true;

                }

            }

            return hasDriver;
        }


    }

}

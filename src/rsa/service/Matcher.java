package rsa.service;

import rsa.quad.PointQuadtree;
import rsa.shared.Location;
import rsa.shared.RideMatchInfo;
import rsa.shared.RideRole;
import rsa.shared.UserStars;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Matcher implements Serializable {

    private static Location topLeft, bottomRight;

    private static double radius;

    private Map<Long, Ride> rides;

    private PointQuadtree<Ride> quadTree;

    public Matcher() {

        rides = new HashMap<>();
        quadTree = new PointQuadtree<>(topLeft.getX(), topLeft.getY(), bottomRight.getX(), bottomRight.getY());

    }

    private Ride getRideWithId(long rideId) {
        return rides.get(rideId);
    }

    public void acceptMatch(long rideId, long matchId) {


    }

    public long addRide(User user, Location from, Location to, String plate, float cost) {

        Ride r = new Ride(user, from, to, plate, cost);

        quadTree.insert(r);

        rides.put(r.getId(), r);

        return r.getId();

    }

    public void concludeRide(long rideId, UserStars stars) {

        Ride rideWithId = getRideWithId(rideId);

        rideWithId.getUser().addStars(rideWithId.getRole(), stars);

        rides.remove(rideId);
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

            Set<Ride> near = quadTree.findNear(r.getFrom().getX(), r.getFrom().getY(), Matcher.getRadius());

            near = near.stream()
                    .filter(r1 -> r1.getCurrentRideRole().other().equals(r.getCurrentRideRole())
                            && r1.getTo().distance(r.getTo()) < Matcher.getRadius()
                            && r1.getMatch() == null)
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

            if (!(getRide(RideRole.DRIVER).getMatch() == null && getRide(RideRole.PASSENGER).getMatch() == null)) {
                return false;
            }

            if (!(getRide(RideRole.DRIVER).getFrom().distance(getRide(RideRole.PASSENGER).getFrom()) < Matcher.getRadius())) {
                return false;
            }

            if (!(getRide(RideRole.DRIVER).getTo().distance(getRide(RideRole.PASSENGER).getTo()) < Matcher.getRadius())) {
                return false;
            }

            return hasDriver;
        }


    }

}

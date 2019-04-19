package rsa.service;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import rsa.quad.PointQuadtree;
import rsa.quad.Trie;
import rsa.shared.Location;
import rsa.shared.RideMatchInfo;
import rsa.shared.RideRole;
import rsa.shared.UserStars;

public class Matcher implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 365838490212732987L;

    /**
     * The top left and bottom right corners of the match area
     */
    private static Location topLeft = new Location(10, 20), bottomRight = new Location(20, 10);

    /**
     * The radius to search for matches
     */
    private static double radius = 10;

    private static final Random random = new Random();

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

    /**
     * Current active rides
     */
    private Map<Long, Ride> rides;

    /**
     * Current active possible matches
     */
    private Map<Long, RideMatch> matches;

    /**
     * The quadtree to store all the active users and their location
     */
    private PointQuadtree<Ride> quadTree;

    public Matcher() {

        Trie.setCapacity(10);

        rides = new HashMap<>();
        matches = new HashMap<>();

        makeQuad();
    }

    /**
     * Creates the quad tree with the given dimensions
     */
    private void makeQuad() {

        quadTree = new PointQuadtree<>(topLeft.getX(), topLeft.getY(), bottomRight.getX(), bottomRight.getY());

    }

    /**
     * Gets the ride with the corresponding ride id
     */
    private Ride getRideWithId(long rideId) {
        return rides.get(rideId);
    }

    /**
     * Get the ride match with the corresponding id
     */
    private RideMatch getRideMatchWithId(long matchId) {
        return matches.get(matchId);
    }

    /**
     * Accept the proposed match (identified by matchId) for given ride (identified by rideId)
     * @param rideId id of ride
     * @param matchId of match of accept
     */
    public void acceptMatch(long rideId, long matchId) {

        RideMatch match = getRideMatchWithId(matchId);

        match.setAccepted(rideId, true);

        if (match.allAccepted()) {

            for (RideRole role : RideRole.values()) {

                match.getRide(role).setMatch(match);

            }

            matches.remove(match.getId());

            removeMatchesEnvolving(match.getRides());
        }

    }

    /**
     * Remove all the matches that involve the given rides
     *
     * @param rides the rides
     */
    private void removeMatchesEnvolving(Collection<Ride> rides) {

        forx: for (Map.Entry<Long, RideMatch> matchEntry : this.matches.entrySet()) {

            RideMatch match = matchEntry.getValue();

            for (RideRole rideRole : RideRole.values()) {

                if (rides.contains(match.getRide(rideRole))) {
                    this.matches.remove(matchEntry.getKey());

                    continue forx;
                }

            }

        }

    }

    /**
     * Add a ride to the matcher
     *
     * @param user providing or requiring a ride
     * @param from origin location
     * @param to destination location
     * @param plate of then car (if null then it is a passenger)
     * @param cost of the ride (how must you charge, if you are the driver)
     * @return ride identifier
     */
    public long addRide(User user, Location from, Location to, String plate, float cost) {

        Ride r = new Ride(user, from, to, plate, cost);

        if (getRideWithId(r.getId()) != null) {
            return addRide(user, from, to, plate, cost);
        }

        quadTree.insert(r);

        rides.put(r.getId(), r);

        return r.getId();

    }

    /**
     * Mark ride as concluded and classify other using stars
     *
     * @param rideId of the ride to conclude
     * @param stars to assign to other user
     */
    public void concludeRide(long rideId, UserStars stars) {

        Ride rideWithId = getRideWithId(rideId);

        rideWithId.getUser().addStars(stars, rideWithId.getRideRole());

        rides.remove(rideId);

        removeMatchesEnvolving(Collections.singleton(rideWithId));
    }

    /**
     * Update current location of ride with given id. If ride is not yet matched, returns a set RideMatchInfo.
     * Proposed ride matches are currently near (use PointQuadtree) have different roles (one is a driver, the other a passenger) and go almost to the same destination (differ by radius).
     *
     * @param rideId of the ride to update
     * @param current location
     * @return set of RideMatchInfo
     */
    SortedSet<RideMatchInfo> updateRide(long rideId, Location current) {

        Ride r = getRideWithId(rideId);

        quadTree.delete(r);

        r.setCurrent(current);

        quadTree.insert(r);

        if (!r.isMatched()) {

            SortedSet<RideMatchInfo> rides = new TreeSet<>(r.getComparator());

            Set<Ride> near = quadTree.findNear(r.getCurrent().getX(), r.getCurrent().getY(), Matcher.getRadius());


            near = near.stream()
                    .filter(r1 ->
                            r1.getRideRole().other().equals(r.getRideRole())
                                    && r1.getTo().distance(r.getTo()) <= Matcher.getRadius()
                                    && r1.getMatch() == null)
                    .collect(Collectors.toSet());

            forx:
            for (Ride ride : near) {

                //Search through the current existing matches to see if there is already a match between the 2
                for (RideMatch match : this.matches.values()) {

                    if (match.getRide(r.getRideRole()).getId() == r.getId()
                            && match.getRide(r.getRideRole().other()).getId() == ride.getId()) {

                        rides.add(new RideMatchInfo(match));

                        continue forx;
                    }

                }

                RideMatch newMatch = new RideMatch(r, ride);

                matches.put(newMatch.getId(), newMatch);

                RideMatchInfo f = new RideMatchInfo(newMatch);

                rides.add(f);
            }

            return rides;
        }

        return null;
    }

    /**
     * A match between 2 rides. Each has specific role, either as driver or as passenger and they must be different.
     * It is assumed that both rides have the same destination, although not checked in this class.
     */
    public class RideMatch {

        long id;

        Map<RideRole, Ride> rides;

        Map<Long, Boolean> accepted;

        public RideMatch(Ride left, Ride right) {

            id = random.nextLong();

            rides = new HashMap<>();

            rides.put(left.getRideRole(), left);
            rides.put(right.getRideRole(), right);

            this.accepted = new HashMap<>();

            this.accepted.put(left.getId(), false);
            this.accepted.put(right.getId(), false);

        }

        /**
         * Set a ride as accepted
         * @param rideId of the ride
         * @param accepted whether it is accepted
         */
        void setAccepted(long rideId, boolean accepted) {
            this.accepted.put(rideId, accepted);
        }

        /**
         * Check if all members have accepted the match
         *
         * @return true if all have accepted, false if otherwise
         */
        boolean allAccepted() {

            boolean valid = true;

            for (boolean cur : this.accepted.values()) {
                valid &= cur;
            }

            return valid;

        }

        /**
         * Get all the rides involved in this match
         * @return the rides involved in this match
         */
        Collection<Ride> getRides() {
            return this.rides.values();
        }

        /**
         * Generated unique identifier of this ride match.
         * @return this ride match identifier
         */
        public long getId() {
            return id;
        }

        /**
         * Ride of user with given role
         * @param role of user
         * @return the ride of the role
         */
        public Ride getRide(RideRole role) {
            return rides.get(role);
        }

        /**
         * Are these rides matchable?
         * Do they fill both roles (user and passenger)?
         * Are they both unmatched?
         * Are they currently in (roughly) the same place?
         * Are they both going to (roughly) the same destination?
         * Locations are considered different if their distance exceeds radius Matcher.getRadius()
         *
         * @return true if its a match, false otherwise.
         */
        public boolean matchable() {

            boolean hasDriver = rides.get(RideRole.DRIVER) != null, hasPassenger = rides.get(RideRole.PASSENGER) != null;

            if (hasDriver && hasPassenger) {

                if (!(getRide(RideRole.DRIVER).getMatch() == null && getRide(RideRole.PASSENGER).getMatch() == null)) {
                    return false;
                }

                if (!(getRide(RideRole.DRIVER).getFrom().distance(getRide(RideRole.PASSENGER).getFrom()) <= Matcher.getRadius())) {
                    return false;
                }

                if (!(getRide(RideRole.DRIVER).getTo().distance(getRide(RideRole.PASSENGER).getTo()) <= Matcher.getRadius())) {
                    return false;
                }

                return true;
            }

            return false;
        }

    }

}

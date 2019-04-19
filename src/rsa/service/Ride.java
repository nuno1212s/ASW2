package rsa.service;

import rsa.quad.HasPoint;
import rsa.shared.Location;
import rsa.shared.RideMatchInfo;
import rsa.shared.RideRole;

import java.util.Comparator;
import java.util.Random;
import java.util.SortedSet;

/**
 * A user's (intention to) ride between two locations.
 * The user can be either the driver of the passenger.
 * There will be an attempt to match this ride with another of complementary type.
 * That is driver's rides will be matched with passenger's rides and vice versa.
 *
 * This class provides a comparator of ride matches adjusted to this ride.
 * Ride matches are sent to clients as RideMatchInfo instances.
 * If more than one is available then they are sorted.
 * The order depends on ride that is being matched.
 */
public class Ride implements HasPoint, RideMatchInfoSorter {

    private static final Random random = new Random();

    private long id;

    private float cost;

    private User user;

    private Location from, to, current;

    private String plate;

    private Matcher.RideMatch match;

    public Ride(User user, Location from, Location to, String plate, float cost) {

        this.id = random.nextLong();
        
        this.user = user;
        this.from = from.clone();
        this.current = from.clone();
        this.to = to.clone();

        this.plate = plate;

        this.cost = cost;

    }

    /**
     * Generated unique identifier of this ride.
     * @return this ride identifier
     */
    public long getId() {
        return this.id;
    }

    /**
     * Cost of this ride (only meaningful for for driver)
     * @return cost
     */
    public float getCost() {
        return this.cost;
    }

    /**
     * Get the origin of this ride
     * @return the from
     */
    public Location getFrom() {
        return this.from;
    }

    /**
     * Get destination of this ride
     * @return the to
     */
    public Location getTo() {
        return this.to;
    }

    /**
     * Current location
     * @return the current
     */
    public Location getCurrent() {
    	return this.current;
    }

    /**
     * Current match of this ride
     * @return the match
     */
    public Matcher.RideMatch getMatch() {
        return match;
    }

    /**
     * Assign a match to this ride
     * @param match the match to set
     */
    public void setMatch(Matcher.RideMatch match) {
        this.match = match;
    }

    /**
     * Car's registration plate for this ride
     * @return the plate (null if passenger)
     */
    public String getPlate() {
        return this.plate;
    }

    /**
     * User of this ride
     * @return the user
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Is the user the driver in this ride?
     * @return true if user is the driver; false otherwise
     */
    public boolean isDriver() {
        return getRideRole() == RideRole.DRIVER;
    }

    /**
     * This ride was match with another
     * @return true is this ride is matched
     */
    public boolean isMatched() {
        return getMatch() != null;
    }

    /**
     * Is the user the passenger in this ride?
     * @return true if user is the passenger; false otherwise
     */
    public boolean isPassenger() {
        return getRideRole() == RideRole.PASSENGER;
    }

    /**
     * Change cost of this ride (only meaningful for for driver)
     * @param cost the cost to set
     */
    public void setCost(float cost) {
        this.cost = cost;
    }

    /**
     * Change the origin of this ride
     * @param from the from to set
     */
    public void setFrom(Location from) {
        this.from = from;
    }

    /**
     * Change user of this ride
     * @param user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Change destination of this ride
     * @param to the to to set
     */
    public void setTo(Location to) {
        this.to = to;
    }

    /**
     * Change car registration plate for this ride
     * @param plate of car to set (null if passenger)
     */
    public void setPlate(String plate) {
    	this.plate = plate;
    }

    /**
     * Change current location
     * @param current the current to set
     */
    public void setCurrent(Location current) {
    	this.current = current;
    }

    /**
     * Role of user in ride, depending on a car's license plate being registered
     * @return {@link RideRole} depending on plate
     */
    public RideRole getRideRole() {
        return this.plate == null ? RideRole.PASSENGER : RideRole.DRIVER;
    }

    @Override
    public double getX() {
        return this.getCurrent().getX();
    }

    @Override
    public double getY() {
        return this.getCurrent().getY();
    }

    /**
     * Gets the suitable comparator for this ride
     *
     * Returns a random order if the rides have equal value because in the TreeSet implementation
     * If the comparator returns 0, the elements are considered as equal and are not added to the set
     *
     * @return the comparing order
     */
    @Override
    public Comparator<RideMatchInfo> getComparator() {
        return (ride1, ride2) -> {

            switch (getUser().getPreferredMatch()) {
                case BETTER:
                    int i = -Double.compare(ride1.getStars(getRideRole().other()),
                            ride2.getStars(getRideRole().other()));
                    
                    
                    if (i == 0) {
                    	break;
                    }
                    
                    return i;
                case CLOSER:
                	
                    i = Double.compare(ride1.getWhere(getRideRole().other()).distance(getCurrent()),
                            ride2.getWhere(getRideRole().other()).distance(getCurrent()));
                    if (i == 0) {
                    	break;
                    }
                    
                    return i;
                    
                case CHEAPER:
                    i = Float.compare(ride1.getCost(), ride2.getCost());
                    
                    if (i == 0) {
                    	break;
                    }
                    
                    return i;
            }

            return random.nextBoolean() ? 1 : -1;
        };
    }
    
    public String toString() {
    	return user.getNick();
    }
    
    public int hashCode() {
    	return ((Long) getId()).hashCode();
    }
    
    public boolean equals(Object o) {
    	if (o instanceof Ride) {
    		return ((Ride) o).getId() == getId();
    	}
    	
    	return false;
    }
}

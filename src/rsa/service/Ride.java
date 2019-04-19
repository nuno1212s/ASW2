package rsa.service;

import rsa.quad.HasPoint;
import rsa.shared.Location;
import rsa.shared.RideMatchInfo;
import rsa.shared.RideRole;

import java.util.Comparator;
import java.util.Random;

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

    public long getId() {
        return this.id;
    }

    public float getCost() {
        return this.cost;
    }

    public Location getFrom() {
        return this.from;
    }

    public Location getTo() {
        return this.to;
    }
    
    public Location getCurrent() {
    	return this.current;
    }

    public Matcher.RideMatch getMatch() {
        return match;
    }

    public void setMatch(Matcher.RideMatch match) {
        this.match = match;
    }

    public String getPlate() {
        return this.plate;
    }

    public User getUser() {
        return this.user;
    }

    public boolean isDriver() {
        return getRideRole() == RideRole.DRIVER;
    }

    public boolean isMatched() {
        return getMatch() != null;
    }

    public boolean isPassenger() {
        return getRideRole() == RideRole.PASSENGER;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public void setFrom(Location from) {
        this.from = from;
    }

    public void setTo(Location to) {
        this.to = to;
    }
    
    public void setPlate(String plate) {
    	this.plate = plate;
    }
    
    public void setCurrent(Location current) {
    	this.current = current;
    }

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

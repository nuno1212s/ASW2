package rsa.shared;

import rsa.service.Matcher;
import rsa.service.Ride;

/**
 * The info of a match between 2 rides (driver and passenger).
 * This is a DAO (Data Access Object) that provides information on current ride matches to external components.
 */
public class RideMatchInfo {

    private Matcher.RideMatch match;

    public RideMatchInfo(Matcher.RideMatch match) {
    	this.match = match;
    }

    /**
     * Car used in ride
     * @return car
     */
    public Car getCar() {
        Ride driver = match.getRide(RideRole.DRIVER);
        
        return driver.getUser().getCar(driver.getPlate());
        
    }

    /**
     * Cost of this ride, payed by the passenger to the driver
     * @return cost
     */
    public float getCost() {
        return match.getRide(RideRole.DRIVER).getCost();
    }

    /**
     * Id of match
     * @return id
     */
    public long getMatchId() {
        return match.getId();
    }

    /**
     * Get name of user with given role
     * @param role of user in match
     * @return name of user with given role
     */
    public String getName(RideRole role) {
        Ride r = match.getRide(role);
        
        return r.getUser().getName();
    }

    /**
     * Get average number of stars of user with given role
     * @param role of user in match
     * @return stars average of user with given role
     */
    public float getStars(RideRole role) {
        Ride r = match.getRide(role);
        
        return r.getUser().getAverage(role);
    }

    /**
     * The location of a user with given role
     * @param role of user in match
     * @return location of user with given role
     */
    public Location getWhere(RideRole role) {
        Ride r = match.getRide(role);
        
        return r.getCurrent();
    }

    public boolean equals(Object o) {
    	
    	if (o instanceof RideMatchInfo) {
    		return ((RideMatchInfo) o).getMatchId() == getMatchId();
    	}
    	
    	return false;
    	
    }
    
}

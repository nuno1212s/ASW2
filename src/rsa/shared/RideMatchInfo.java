package rsa.shared;

import rsa.service.Matcher;
import rsa.service.Ride;

public class RideMatchInfo {

    private Matcher.RideMatch match;

    public RideMatchInfo(Matcher.RideMatch match) {
    	this.match = match;
    }

    public Car getCar() {
        Ride driver = match.getRide(RideRole.DRIVER);
        
        return driver.getUser().getCar(driver.getPlate());
        
    }

    public float getCost() {
        return match.getRide(RideRole.DRIVER).getCost();
    }

    public long getMatchId() {
        return match.getId();
    }

    public String getName(RideRole role) {
        Ride r = match.getRide(role);
        
        return r.getUser().getName();
    }

    public float getStars(RideRole role) {
        Ride r = match.getRide(role);
        
        return r.getUser().getAverage(role);
    }

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

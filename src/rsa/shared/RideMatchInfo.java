package rsa.shared;

import rsa.service.Matcher;
import rsa.service.Ride;

public class RideMatchInfo {

    private Matcher.RideMatch match;

    public RideMatchInfo(Matcher.RideMatch match) {
        this.match = match;
    }

    public Car getCar() {
        Ride driverRide = match.getRide(RideRole.DRIVER);

        return driverRide.getUser().getCarWithPlate(driverRide.getPlate());
    }

    public float getCost() {
        return this.match.getRide(RideRole.DRIVER).getCost();
    }

    public long getMatchId() {
        return match.getId();
    }

    public String getName(RideRole role) {
        return match.getRide(role).getUser().getName();
    }

    public float getStars(RideRole role) {
        return match.getRide(role).getUser().getAverage(RideRole.DRIVER);
    }

    public Location getWhere(RideRole role) {
        return match.getRide(role).getFrom();
    }

}

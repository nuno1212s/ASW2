package rsa.shared;

import rsa.service.Matcher;
import rsa.service.Ride;

import java.util.HashMap;
import java.util.Map;

public class RideMatchInfo {

    private Car car;

    private float cost;

    private long matchId;

    private Map<RideRole, String> names;

    private Map<RideRole, Float> userStars;

    private Map<RideRole, Location> locations;

    public RideMatchInfo(Matcher.RideMatch match) {
        Ride driverRide = match.getRide(RideRole.DRIVER);

        this.car = driverRide.getUser().getCarWithPlate(driverRide.getPlate());

        this.cost = match.getRide(RideRole.DRIVER).getCost();

        this.matchId = match.getId();

        int rideRoles = RideRole.values().length;

        this.names = new HashMap<>(rideRoles);
        this.userStars = new HashMap<>(rideRoles);
        this.locations = new HashMap<>(rideRoles);

        for (RideRole value : RideRole.values()) {

            this.names.put(value, match.getRide(value).getUser().getName());
            this.userStars.put(value, match.getRide(value).getUser().getAverage(value));
            this.locations.put(value, match.getRide(value).getFrom());

        }

    }

    public Car getCar() {
        return car;
    }

    public float getCost() {
        return cost;
    }

    public long getMatchId() {
        return matchId;
    }

    public String getName(RideRole role) {
        return this.names.get(role);
    }

    public float getStars(RideRole role) {
        return this.userStars.get(role);
    }

    public Location getWhere(RideRole role) {
        return this.locations.get(role);
    }

}

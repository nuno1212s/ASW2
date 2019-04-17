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

    private Location from, to;

    private String plate;

    private Matcher.RideMatch match;

    public Ride(User user, Location from, Location to, String plate, float cost) {

        this.id = random.nextLong();
        this.user = user;
        this.from = from;
        this.to = to;

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

    public Matcher.RideMatch getMatch() {
        return match;
    }

    public void setMatch(Matcher.RideMatch match) {
        this.match = match;
    }

    public String getPlate() {
        return this.plate;
    }

    public RideRole getRole() {
        return null;
    }

    public User getUser() {
        return this.user;
    }

    public boolean isDriver() {
        return getRole() == RideRole.DRIVER;
    }

    public boolean isMatched() {
        return getMatch() != null;
    }

    public boolean isPassenger() {
        return getRole() == RideRole.PASSENGER;
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

    public RideRole getCurrentRideRole() {
        return this.plate == null ? RideRole.PASSENGER : RideRole.DRIVER;
    }

    @Override
    public double getX() {
        return this.getFrom().getX();
    }

    @Override
    public double getY() {
        return this.getFrom().getY();
    }

    @Override
    public Comparator<RideMatchInfo> getComparator() {
        return (ride1, ride2) -> {

            switch (getUser().getPreferedMatch()) {
                case BETTER:
                    return Double.compare(ride1.getStars(getCurrentRideRole().other()),
                            ride2.getStars(getCurrentRideRole().other()));
                case CLOSER:
                    return Double.compare(ride1.getWhere(getCurrentRideRole().other()).distance(getFrom()),
                            ride2.getWhere(getCurrentRideRole().other()).distance(getFrom()));
                case CHEAPER:
                    return Float.compare(ride1.getCost(), ride2.getCost());
            }

            return 0;
        };
    }
}

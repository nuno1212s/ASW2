package rsa.shared;

public enum RideRole {

    DRIVER,
    PASSENGER;

    public RideRole other() {
        if (this == DRIVER) {
            return PASSENGER;
        } else {
            return DRIVER;
        }
    }

}

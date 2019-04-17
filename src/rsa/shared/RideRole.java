package rsa.shared;

import java.io.Serializable;

public enum RideRole implements Serializable {

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

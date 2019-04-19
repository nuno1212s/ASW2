package rsa.shared;

import java.io.Serializable;

/**
 * The role of the user in a ride.
 */
public enum RideRole implements Serializable {

    /**
     * This user is driving the car
     */
    DRIVER,
    /**
     * This user is the passenger
     */
    PASSENGER;

    /**
     * Get the other possible RideRole
     */
    public RideRole other() {
        if (this == DRIVER) {
            return PASSENGER;
        } else {
            return DRIVER;
        }
    }

}

package rsa.shared;

import java.io.Serializable;

/**
 * Classification to a ride provided by the other user.
 */
public enum UserStars implements Serializable {

    /**
     * Great Ride
     */
    FIVE_STARS {
        @Override
        public int getStars() {
            return 5;
        }
    },
    /**
     * Good Ride
     */
    FOUR_STARS {
        @Override
        public int getStars() {
            return 4;
        }
    },
    /**
     * Lousy ride
     */
    ONE_STAR {
        @Override
        public int getStars() {
            return 1;
        }
    },
    /**
     * Average Ride
     */
    THREE_STARS {
        @Override
        public int getStars() {
            return 3;
        }
    },
    /**
     * Bad ride
     */
    TWO_STARS {
        @Override
        public int getStars() {
            return 2;
        }
    };

    public abstract int getStars();

}

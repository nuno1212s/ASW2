package rsa.shared;

import java.io.Serializable;

/**
 * Preferred way to sort matches. Users will set their preferences using this values.
 */
public enum PreferredMatch implements Serializable {

    /**
     * Prefer to ride with better users (higher average stars; this is the default).
     */
    BETTER,
    /**
     * Prefer cheaper rides (if you are a passenger)
     */
    CHEAPER,
    /**
     * Prefer to ride with nearby users
     */
    CLOSER

}

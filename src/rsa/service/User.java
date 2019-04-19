package rsa.service;

import rsa.shared.Car;
import rsa.shared.PreferredMatch;
import rsa.shared.RideRole;
import rsa.shared.UserStars;

import java.io.Serializable;
import java.util.*;

/**
 * An user of the Ride Sharing App.
 * An instance of this class records the user's authentication and other relevant data.
 */
public class User implements Serializable {

    private static final long serialVersionUID = -8605666932017737115L;

    private final String nick;
    
    private String name, password;

    private Map<String, Car> cars;

    private Map<RideRole, List<UserStars>> userStars;

    private PreferredMatch preferedMatch;

    public User(String nick, String name, String password) {

        this.nick = nick;
        this.name = name;
        this.password = password;

        this.cars = new HashMap<>();
        this.userStars = new HashMap<>();
        this.preferedMatch = PreferredMatch.BETTER;
    }

    /**
     * Bind a car to this user. Can be used to change car features.
     */
    public void addCar(Car car) {
        this.cars.put(car.getPlate(), car);
    }

    /**
     * Add stars to user according to a role. The registered values are used to compute an average.
     * @param stars to add to this user
     * @param role in which stars are added
     */
    public void addStars(UserStars stars, RideRole role) {
        List<UserStars> userStars = this.userStars.getOrDefault(role, new ArrayList<>());

        userStars.add(stars);

        this.userStars.put(role, userStars);
    }

    /**
     * Car with given license plate
     * @param plate of car
     * @return car
     */
    public Car getCar(String plate) {

        return this.cars.get(plate);
    }

    /**
     * Remove binding between use and car
     * @param plate of car to remove from this user
     */
    public void deleteCar(String plate) {
    	this.cars.remove(plate);
    }

    /**
     * Returns the average number of stars in given role
     * @param role of user
     * @return average number of stars
     */
    public float getAverage(RideRole role) {

        float sum = 0;

        List<UserStars> userStars = this.userStars.get(role);

        if (userStars == null) {
        	return 0;
        }
        
        for (UserStars userStar : userStars) {

            sum += userStar.getStars();

        }

        return sum / userStars.size();
    }

    /**
     * Name of user
     * @return user's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * The nick of this user: Cannot be changed as it a key.
     * @return nick
     */
    public String getNick() {
        return nick;
    }

    /**
     * Current password of this user
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Change password of this user
     * @param password to change
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Change user's name
     * @param name to change
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Current preference for sorting matches. Defaults to BETTER
     * @return preferred match by this user
     */
    public PreferredMatch getPreferredMatch() {
        return this.preferedMatch == null ? PreferredMatch.BETTER : this.preferedMatch;
    }

    /**
     * Change preference for sorting matches
     * @param match to set for this user
     */
    public void setPreferredMatch(PreferredMatch match) {
        this.preferedMatch = match;
    }

    /**
     * Check the authentication of this player
     * @param password for checking
     * @return true password is the expected, false otherwise
     */
    boolean authenticate(String password) {
    	return this.password.equals(password);
    }

}

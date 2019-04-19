package rsa.service;

import rsa.shared.*;

import java.util.SortedSet;

/**
 * An instance of this class is responsible for managing the ride sharing service, handling user requests and matching their rides.
 * The methods of this class are those needed by web client thus it follows the Facade design pattern.
 * It also follows the Singleton design pattern to provide a single instance of this class to the application
 */
public class Manager {

    /**
     * The instance of the manager class
     */
    private static Manager ins;

    /**
     * Returns the single instance of this class as proposed in the singleton design pattern.
     * @return instance of this class
     * @throws RideSharingAppException if I/O error occurs reading users serialization
     */
    public synchronized static Manager getInstance() throws RideSharingAppException {

        if (ins == null) {
            ins = new Manager();
        }

        return ins;
    }

    /**
     * User management
     */
    Users allUsers;

    Matcher matcher;

    private Manager() throws RideSharingAppException {

        allUsers = Users.getInstance();

        matcher = new Matcher();

    }

    /**
     * Register a player with given nick and password. Changes are stored in serialization file
     *
     * @param nick - of user
     * @param name - of user
     * @param password - of user
     * @return true if registered and false otherwise
     * @throws RideSharingAppException - if I/O error occurs when serializing data
     */
    public boolean register(String nick, String name, String password) throws RideSharingAppException {

        return allUsers.register(nick, name, password);

    }

    /**
     * Change password if old password matches the current one
     *
     * @param nick of player
     * @param oldPassword for authentication before update
     * @param newPassword after update
     * @return true if password changed and false otherwise
     * @throws RideSharingAppException if I/O error occurs when serializing data
     */
    public boolean updatePassword(String nick, String oldPassword, String newPassword) throws RideSharingAppException {

        return allUsers.updatePassword(nick, oldPassword, newPassword);

    }

    /**
     * Authenticates user given id and password
     * @param nick of user to authenticate
     * @param password of user to authenticate
     * @return true if authenticated and false otherwise
     */
    public boolean authenticate(String nick, String password) {

        return allUsers.authenticate(nick, password);

    }

    /**
     * Accept a match
     * @param rideId id of of ride to match
     * @param matchId id of match to consider
     */
    public void acceptMatch(long rideId, long matchId) {

        matcher.acceptMatch(rideId, matchId);

    }

    /**
     * Add a ride for user with given nick, from and to the given locations.
     * A car license plate must be given if user is the driver, or null if passenger.
     *
     * @param nick of user
     * @param password of user
     * @param from origin's location
     * @param to destination's location
     * @param plate of car (null if passenger)
     * @param cost of the ride (how much you charge, if you are the driver)
     * @return id of created ride
     * @throws RideSharingAppException if authentication fails
     */
    public long addRide(String nick, String password, Location from,
                        Location to, String plate, float cost)
            throws RideSharingAppException {

        if (!authenticate(nick, password)) {
            throw new RideSharingAppException("Authentication failed");
        }

        User user = allUsers.getUser(nick);

        return matcher.addRide(user, from, to, plate, cost);
    }

    /**
     * Conclude a ride and provide feedback on the other partner
     * @param rideId of the ride to conclude
     * @param classification of the ride partner (in starts)
     */
    public void concludeRide(long rideId, UserStars classification) {

        matcher.concludeRide(rideId, classification);

    }

    /**
     * Update current location of user and receive a set of proposed ride matches
     * @param rideId of ride to update
     * @param current location of user
     * @return A Set of RideMatchInfo
     */
    public SortedSet<RideMatchInfo> updateRide(long rideId, Location current) {

        return matcher.updateRide(rideId, current);

    }

    /**
     * Current preferred match for given authenticated user
     *
     * @param nick of user
     * @param password of user
     * @return the current preferred match for this user
     * @throws RideSharingAppException if authentication fails
     */
    public PreferredMatch getPreferredMatch(String nick, String password) throws RideSharingAppException {

        if (!authenticate(nick, password)) {
            throw new RideSharingAppException("Authentication failed");
        }

        return allUsers.getUser(nick).getPreferredMatch();

    }

    /**
     * Set preferred match for given authenticated user
     *
     * @param nick of user
     * @param password of user
     * @param match kind of match
     * @throws RideSharingAppException if authentication fails
     */
    public void setPreferredMatch(String nick, String password, PreferredMatch match) throws RideSharingAppException{

        if (!authenticate(nick, password)) {
            throw new RideSharingAppException("Authentication failed");
        }

        allUsers.getUser(nick).setPreferredMatch(match);
    }

    /**
     * Resets singleton for unit testing purposes.
     */
    void reset() {
    	allUsers.reset();

    	matcher = new Matcher();
    }

    /**
     * The average number of stars of given user in given role
     *
     * @param nick of user
     * @param role of interest
     * @return average stars on user in role
     *
     * @throws RideSharingAppException in user's nick is not found
     */
    public double getAverage(String nick, RideRole role) throws RideSharingAppException {

        User user = allUsers.getUser(nick);

        if (user != null) {
            return user.getAverage(role);
        }

        throw new RideSharingAppException("User is null");
    }

}

package rsa.service;

import rsa.shared.*;

public class Manager {

    private static Manager ins;

    public synchronized static Manager getInstance() throws RideSharingAppException {

        if (ins == null) {
            ins = new Manager();
        }

        return ins;
    }

    Users allUsers;

    Matcher matcher;

    private Manager() throws RideSharingAppException {

        allUsers = Users.getInstance();

        matcher = new Matcher();

    }

    public boolean register(String nick, String name, String password) throws RideSharingAppException {

        return allUsers.register(nick, name, password);

    }

    public boolean updatePassword(String nick, String oldPassword, String newPassword) throws RideSharingAppException {

        return allUsers.updatePassword(nick, oldPassword, newPassword);

    }

    public boolean authenticate(String nick, String password) {

        return allUsers.authenticate(nick, password);

    }

    public void acceptMatch(long rideId, long matchId) {

        matcher.acceptMatch(rideId, matchId);

    }

    public long addRide(String nick, String password, Location from, Location to, String plate, float cost)
            throws RideSharingAppException {

        if (!authenticate(nick, password)) {
            throw new RideSharingAppException("Authentication failed");
        }

        User user = allUsers.getUser(nick);

        return matcher.addRide(user, from, to, plate, cost);
    }

    public void concludeRide(long rideId, UserStars classification) {

        matcher.concludeRide(rideId, classification);

    }

    public PreferredMatch getPreferredMatch(String nick, String password) throws RideSharingAppException {

        if (!authenticate(nick, password)) {
            throw new RideSharingAppException("Authentication failed");
        }

        return allUsers.getUser(nick).getPreferredMatch();

    }

    public void setPreferredMatch(String nick, String password, PreferredMatch match) throws RideSharingAppException{

        if (!authenticate(nick, password)) {
            throw new RideSharingAppException("Authentication failed");
        }

        allUsers.getUser(nick).setPreferredMatch(match);
    }
    
    void reset() {
    	//TODO
    }

    public double getAverage(String nick, RideRole role) {

        User user = allUsers.getUser(nick);

        if (user != null) {
            return user.getAverage(role);
        }

        return -1;
    }

}

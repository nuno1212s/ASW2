package rsa.service;

import rsa.shared.RideSharingAppException;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Users implements Serializable {

    private static final long serialVersionUID = -7604766932017737115L;

    private static Users ins;

    private static File playersFile;

    public static File getPlayersFile() {
        return playersFile;
    }

    public static void setPlayersFile(File playersFile) {
        Users.playersFile = playersFile;
    }

    public synchronized static Users getInstance() throws RideSharingAppException {

        if (ins == null) {

            if (getPlayersFile().exists()) {

                restore();

            }

        }

        return ins;
    }

    private Map<String, User> users;

    private Users() {

        users = new ConcurrentHashMap<>();

    }

    public User getUser(String nick) {

        return users.getOrDefault(nick, null);

    }

    boolean register(String nick, String name, String password) throws RideSharingAppException {

        boolean valid = true;

        for (int i = 0; i < nick.length(); i++) {
            valid &= (Character.isLetterOrDigit(nick.charAt(i)) || nick.charAt(i) == '_');
        }

        if (valid) {
            User user = new User(nick, name, password);

            users.put(nick, user);

            backup();
        }

        return valid;
    }

    boolean authenticate(String nick, String password) {

        User user = getUser(nick);

        if (user != null) {

            return user.getPassword().equals(password);

        }

        return false;
    }

    boolean updatePassword(String nick, String oldPassword, String newPassword) throws RideSharingAppException {

        if (authenticate(nick, oldPassword)) {

            User user = getUser(nick);

            user.setPassword(newPassword);

            backup();

            return true;
        }

        return false;
    }

    private static void backup() throws RideSharingAppException {

        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(getPlayersFile()))) {

            outputStream.writeObject(getInstance());

        } catch (IOException e) {
            throw new RideSharingAppException("Error while backing up", e);
        }

    }

    private static void restore() throws RideSharingAppException {

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(getPlayersFile()))) {

            ins = (Users) inputStream.readObject();

        } catch (IOException e) {

            throw new RideSharingAppException("Error while restoring", e);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    void reset() {
        ins = new Users();

        getPlayersFile().delete();
    }

    protected Object readResolve() {
        try {
            return getInstance();
        } catch (RideSharingAppException e) {
            e.printStackTrace();
        }

        return null;
    }
}

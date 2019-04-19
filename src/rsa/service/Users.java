package rsa.service;

import rsa.shared.RideSharingAppException;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Users implements Serializable {

    private static final long serialVersionUID = -7604766932017737115L;

    private static Users ins;

    private static File playersFile;

    /**
     * Name of file containing managers's data
     * @return file containing serialization
     */
    public static File getPlayersFile() {
        return playersFile;
    }

    /**
     * Change pathname of file containing mnanager's data
     * @param playersFile contain serialization
     */
    public static void setPlayersFile(File playersFile) {
        Users.playersFile = playersFile;
    }

    /**
     * Returns the single instance of this class as proposed in the singleton design pattern.
     * If a backup of this class is available then the users instance is recreated from that data
     * @return instance of this class
     * @throws RideSharingAppException if I/O error occurs reading serialization
     */
    public synchronized static Users getInstance() throws RideSharingAppException {

        if (ins == null) {

            if (getPlayersFile() != null && getPlayersFile().exists()) {

                restore();

            }
            
            ins = new Users();

        }

        return ins;
    }

    /**
     * A map that contains all of the users
     */
    private Map<String, User> users;

    private Users() {
    	
    	try {
			String path = getProgramPath2();
			
			String finalPath = path + File.separator + "Users" + File.separator + "users.txt";
			
			setPlayersFile(new File(finalPath));
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        users = new ConcurrentHashMap<>();

    }

    /**
     * Get the path to the .jar
     * @return
     * @throws UnsupportedEncodingException
     */
    private String getProgramPath2() throws UnsupportedEncodingException {
        URL url = Users.class.getProtectionDomain().getCodeSource().getLocation();
        
        String jarPath = URLDecoder.decode(url.getFile(), "UTF-8");

        return new File(jarPath).getParentFile().getPath();
     }

    /**
     * Get the user with given nick
     * @param nick of player
     * @return player instance
     */
    public User getUser(String nick) {

        return users.getOrDefault(nick, null);

    }

    /**
     * Register a player with given nick and password.
     * Changes are immediately serialized.
     *
     * @param nick of user
     * @param name of user
     * @param password of user
     * @return true if registered and false otherwise
     * @throws RideSharingAppException on I/O error in serialization
     */
    boolean register(String nick, String name, String password) throws RideSharingAppException {

    	if (users.containsKey(nick)) {
    		System.out.println(users);
    		return false;
    	}
    	
        boolean valid = true;

        for (int i = 0; i < nick.length(); i++) {
            valid &= (Character.isLetterOrDigit(nick.charAt(i)) || nick.charAt(i) == '_');
            
            if (!valid) {
            	System.out.println(nick.charAt(i));
            }
        }

        if (valid) {
            User user = new User(nick, name, password);

            users.put(nick, user);

            //backup();
            return true;
        }

        return false;
    }

    /**
     * Authenticate user given id and password
     * @param nick of user to authenticate
     * @param password of user to authenticate
     * @return true if authenticated and false otherwise
     */
    boolean authenticate(String nick, String password) {

        User user = getUser(nick);

        if (user != null) {

            return user.authenticate(password);

        }

        return false;
    }

    /**
     * Change password if old password matches current one.
     * Changes are immediately serialized.
     * @param nick of player
     * @param oldPassword for authentication before update
     * @param newPassword after update
     * @return true if password changed and false otherwise
     * @throws RideSharingAppException on I/O error in serialization.
     */
    boolean updatePassword(String nick, String oldPassword, String newPassword) throws RideSharingAppException {

        if (authenticate(nick, oldPassword)) {

            User user = getUser(nick);

            user.setPassword(newPassword);

            //backup();

            return true;
        }

        return false;
    }

    /**
     * Make a backup of the current Users instance
     * @throws RideSharingAppException If there is an error on I/O
     */
    private static void backup() throws RideSharingAppException {
    	
    	if (!getPlayersFile().exists()) {
    		
    		try {
    			
    			getPlayersFile().createNewFile();
    		
    		} catch (IOException e) {
    			
    			throw new RideSharingAppException("Error while backing up", e);
    			
    		}
    	}

        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(getPlayersFile()))) {

            outputStream.writeObject(getInstance());

        } catch (IOException e) {
            throw new RideSharingAppException("Error while backing up", e);
        }

    }

    /**
     * Restores the instance from the manager serialization file, if it exists
     * @throws RideSharingAppException if there's an error in I/O serialization
     */
    private static void restore() throws RideSharingAppException {

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(getPlayersFile()))) {

            ins = (Users) inputStream.readObject();

        } catch (IOException e) {

            throw new RideSharingAppException("Error while restoring", e);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
    
    private static void deleteFile() {
    	
    	getPlayersFile().delete();
    	
    }

    /**
     * Resets the current instance of users
     */
    void reset() {
        ins.users = new ConcurrentHashMap<>();
        
        deleteFile();
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

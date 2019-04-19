package rsa.service;

import rsa.shared.Car;
import rsa.shared.PreferredMatch;
import rsa.shared.RideRole;
import rsa.shared.UserStars;

import java.io.Serializable;
import java.util.*;

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

    public void addCar(Car car) {
        this.cars.put(car.getPlate(), car);
    }

    public void addStars(UserStars stars, RideRole role) {
        List<UserStars> userStars = this.userStars.getOrDefault(role, new ArrayList<>());

        userStars.add(stars);

        this.userStars.put(role, userStars);
    }

    public Car getCar(String plate) {

        return this.cars.get(plate);
    }

    public void deleteCar(String plate) {
    	this.cars.remove(plate);
    }

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

    public String getName() {
        return this.name;
    }

    public String getNick() {
        return nick;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PreferredMatch getPreferredMatch() {
        return this.preferedMatch == null ? PreferredMatch.BETTER : this.preferedMatch;
    }

    public void setPreferredMatch(PreferredMatch match) {
        this.preferedMatch = match;
    }
    
    boolean authenticate(String password) {
    	return this.password.equals(password);
    }

}

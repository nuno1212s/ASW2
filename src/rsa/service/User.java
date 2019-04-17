package rsa.service;

import rsa.shared.Car;
import rsa.shared.PreferedMatch;
import rsa.shared.RideRole;
import rsa.shared.UserStars;

import java.io.Serializable;
import java.util.*;

public class User implements Serializable {

    private static final long serialVersionUID = -8605666932017737115L;

    private String nick, name, password;

    private List<Car> cars;

    private Map<RideRole, List<UserStars>> userStars;

    private PreferedMatch preferedMatch;

    public User(String nick, String name, String password) {

        this.nick = nick;
        this.name = name;
        this.password = password;

        this.cars = new ArrayList<>();
        this.userStars = new HashMap<>();
        this.preferedMatch = PreferedMatch.BETTER;
    }

    public void addCar(Car car) {
        this.cars.add(car);
    }

    public void addStars(RideRole role, UserStars stars) {
        List<UserStars> userStars = this.userStars.getOrDefault(role, new ArrayList<>());

        userStars.add(stars);

        this.userStars.put(role, userStars);
    }

    public Car getCarWithPlate(String plate) {

        for (Car car : this.cars) {
            if (car.getPlate().equalsIgnoreCase(plate)) {
                return car;
            }
        }

        return null;
    }

    public void deleteCar(String plate) {

        Iterator<Car> it = this.cars.iterator();

        while (it.hasNext()) {
            Car car = it.next();

            if (car.getPlate().equals(plate)) {
                it.remove();
                break;
            }

        }

    }

    public float getAverage(RideRole role) {

        float sum = 0;

        List<UserStars> userStars = this.userStars.get(role);

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

    public PreferedMatch getPreferedMatch() {
        return this.preferedMatch;
    }

    public void setPreferedMatch(PreferedMatch match) {
        this.preferedMatch = match;
    }

}

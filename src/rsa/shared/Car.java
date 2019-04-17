package rsa.shared;

import java.io.Serializable;

public class Car implements Serializable {

    private static final long serialVersionUID = -1245687592017737115L;

    private String plate, make, model, color;

    public Car(String plate, String make, String model, String color) {
        this.plate = plate;
        this.make = make;
        this.model = model;
        this.color = color;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}

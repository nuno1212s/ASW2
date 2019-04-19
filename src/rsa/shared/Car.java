package rsa.shared;

import java.io.Serializable;

/**
 * A car used in rides, with a license plate (that can be used as key) a make, model and color.
 */
public class Car implements Serializable {

    private static final long serialVersionUID = -1245687592017737115L;

    private String plate, make, model, color;

    public Car(String plate, String make, String model, String color) {
        this.plate = plate;
        this.make = make;
        this.model = model;
        this.color = color;
    }

    /**
     * License plate of this car
     * @return the plate
     */
    public String getPlate() {
        return plate;
    }

    /**
     * Set license plate of this car
     * @param plate the license to set
     */
    public void setPlate(String plate) {
        this.plate = plate;
    }

    /**
     * Make of this car
     * @return the make
     */
    public String getMake() {
        return make;
    }

    /**
     * Set make of this car
     * @param make the make to set
     */
    public void setMake(String make) {
        this.make = make;
    }

    /**
     * Model of this car
     * @return the model
     */
    public String getModel() {
        return model;
    }

    /**
     * Set model of this car
     * @param model the model to set
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Color of this car
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * Set color of this car
     * @param color the color to set
     */
    public void setColor(String color) {
        this.color = color;
    }
}

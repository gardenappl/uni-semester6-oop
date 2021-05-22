package ua.yuriih.carrental.lab1.model;

import java.math.BigDecimal;
import java.util.Objects;

public final class Car {
    private final int id;
    private final String model;
    private final String manufacturer;
    private final BigDecimal hrnPerDay;
    private final Integer currentUserId;

    public Car(int id, String model, String manufacturer, BigDecimal hrnPerDay, Integer currentUserId) {
        this.id = id;
        this.model = model;
        this.manufacturer = manufacturer;
        this.hrnPerDay = hrnPerDay;
        this.currentUserId = currentUserId;
    }

    public String getModel() {
        return model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public BigDecimal getHrnPerDay() {
        return hrnPerDay;
    }

    public Integer getCurrentUserId() {
        return currentUserId;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", model='" + model + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", hrnPerDay=" + hrnPerDay +
                ", currentUserId=" + currentUserId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return id == car.id && model.equals(car.model) && manufacturer.equals(car.manufacturer) && hrnPerDay.equals(car.hrnPerDay) && Objects.equals(currentUserId, car.currentUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, model, manufacturer, hrnPerDay, currentUserId);
    }
}

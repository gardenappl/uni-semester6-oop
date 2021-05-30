package ua.yuriih.carrental.lab1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.Objects;

public final class Car {
    private final int id;
    private final String model;
    private final String manufacturer;
    private final BigDecimal hrnPerDay;
    @JsonIgnore
    private final Long currentUserId;
    private final String thumbnailUrl;

    public Car(int id, String model, String manufacturer, BigDecimal hrnPerDay, Long currentUserId, String thumbnailUrl) {
        this.id = id;
        this.model = model;
        this.manufacturer = manufacturer;
        this.hrnPerDay = hrnPerDay;
        this.currentUserId = currentUserId;
        this.thumbnailUrl = thumbnailUrl;
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

    public Long getCurrentUserId() {
        return currentUserId;
    }

    public int getId() {
        return id;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", model='" + model + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", hrnPerDay=" + hrnPerDay +
                ", currentUserId=" + currentUserId +
                ", thumnbailUrl=" + thumbnailUrl +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return id == car.id && model.equals(car.model) && manufacturer.equals(car.manufacturer) && hrnPerDay.equals(car.hrnPerDay) && Objects.equals(currentUserId, car.currentUserId) && Objects.equals(thumbnailUrl, car.thumbnailUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, model, manufacturer, hrnPerDay, currentUserId, thumbnailUrl);
    }
}

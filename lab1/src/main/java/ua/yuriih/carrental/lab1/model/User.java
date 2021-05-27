package ua.yuriih.carrental.lab1.model;

import java.util.Objects;

public final class User {
    private final int id;
    private final String name;
    private final String password;
    private final Integer currentCar;
    private final boolean isAdmin;

    public User(int id, String name, String password, Integer currentCar, boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.currentCar = currentCar;
        this.isAdmin = isAdmin;
    }

    public int getId() {
        return id;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public int getCurrentCar() {
        return currentCar;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", currentCar=" + currentCar +
                ", isAdmin=" + isAdmin +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && currentCar == user.currentCar && isAdmin == user.isAdmin && name.equals(user.name) && password.equals(user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, password, currentCar, isAdmin);
    }
}

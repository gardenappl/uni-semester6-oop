package ua.yuriih.carrental.lab1.controller;

import ua.yuriih.carrental.lab1.model.Car;
import ua.yuriih.carrental.lab1.repository.CarDao;
import ua.yuriih.carrental.lab1.repository.connection.ConnectionPool;
import ua.yuriih.carrental.lab1.repository.connection.ConnectionWrapper;

import java.math.BigDecimal;
import java.util.List;

public class CarController {
    public static final CarController INSTANCE = new CarController();

    private CarController() {}

    public List<Car> getAvailableCars() {
        try (ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection()) {
            return CarDao.INSTANCE.getAllFreeCars(connection);
        }
    }

    public Car getCar(int id) {
        try (ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection()) {
            return CarDao.INSTANCE.getCar(connection, id);
        }
    }

    public List<Car> getCarForUser(long userId) {
        try (ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection()) {
            return CarDao.INSTANCE.getCarsForUser(connection, userId);
        }
    }
}

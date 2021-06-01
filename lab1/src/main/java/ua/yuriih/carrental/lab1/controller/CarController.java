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
        ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection();
        return CarDao.INSTANCE.getAllFreeCars(connection);
    }

    public Car getCar(int id) {
        ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection();
        return CarDao.INSTANCE.getCar(connection, id);
    }

    public BigDecimal getCost(int carId, int days) {
        ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection();
        return CarDao.INSTANCE.getCar(connection, carId).getHrnPerDay().multiply(BigDecimal.valueOf(days));
    }

    public List<Car> getCarForUser(long userId) {
        ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection();
        return CarDao.INSTANCE.getCarsForUser(connection, userId);
    }
}

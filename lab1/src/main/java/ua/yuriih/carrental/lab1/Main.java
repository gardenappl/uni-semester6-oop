package ua.yuriih.carrental.lab1;

import ua.yuriih.carrental.lab1.model.Car;
import ua.yuriih.carrental.lab1.repository.CarDao;
import ua.yuriih.carrental.lab1.repository.connection.ConnectionPool;
import ua.yuriih.carrental.lab1.repository.connection.ConnectionWrapper;

import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection();

        List<Car> cars = CarDao.INSTANCE.getAllFreeCars(connection);

        for (Car car : cars) {
            System.out.println(car);
        }
    }
}

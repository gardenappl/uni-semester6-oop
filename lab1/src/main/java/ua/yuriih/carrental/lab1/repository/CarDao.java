package ua.yuriih.carrental.lab1.repository;

import ua.yuriih.carrental.lab1.model.Car;
import ua.yuriih.carrental.lab1.repository.connection.ConnectionWrapper;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class CarDao {
    public static final CarDao INSTANCE = new CarDao();

    private CarDao() {}

    private static Car resultToCar(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String model = resultSet.getString("model");
        String manufacturer = resultSet.getString("manufacturer");
        BigDecimal hrnPerDay = resultSet.getBigDecimal("hrn_per_day");
        Integer currentUserId = resultSet.getInt("current_user_id");
        if (resultSet.wasNull())
            currentUserId = null;

        return new Car(id, model, manufacturer, hrnPerDay, currentUserId);
    }

    public List<Car> getAllFreeCars(ConnectionWrapper connection) {
        ArrayList<Car> cars = new ArrayList<>();

        connection.doTransaction(() -> {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM cars WHERE current_user_id IS NULL"
            );
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                cars.add(resultToCar(resultSet));
            }
        });

        return cars;
    }
}

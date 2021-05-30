package ua.yuriih.carrental.lab1.repository;

import ua.yuriih.carrental.lab1.model.RentRequest;
import ua.yuriih.carrental.lab1.repository.connection.ConnectionWrapper;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class RentRequestDao {
    public static final RentRequestDao INSTANCE = new RentRequestDao();

    private RentRequestDao() {}

    private static RentRequest resultToRentRequest(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int status = resultSet.getInt("status");
        String statusMessage = resultSet.getString("message");
        long userId = resultSet.getLong("user_id");
        int carId = resultSet.getInt("car_id");
        int days = resultSet.getInt("days");
        LocalDate startDate = resultSet.getObject("start_date", LocalDate.class);
        BigDecimal paymentCost = resultSet.getBigDecimal("payment_cost");

        return new RentRequest(id, status, statusMessage, userId, days, carId, startDate, paymentCost);
    }

    public RentRequest getRequest(ConnectionWrapper connection, int id) {
        String sql = "SELECT * FROM requests WHERE id = ? LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                return resultToRentRequest(resultSet);
            else
                return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void update(ConnectionWrapper connection, RentRequest request) {
        String sql = "UPDATE requests" +
                " SET status = ?," +
                "     message = ?," +
                "     user_id = ?," +
                "     car_id = ?," +
                "     days = ?," +
                "     start_date = ?," +
                "     payment_cost = ?," +
                " WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, request.getStatus());
            statement.setString(2, request.getStatusMessage());
            statement.setLong(3, request.getUserId());
            statement.setInt(4, request.getCarId());
            statement.setInt(5, request.getDays());
            statement.setObject(6, request.getStartDate());
            statement.setInt(7, request.getId());
            statement.setBigDecimal(8, request.getPaymentCost());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public RentRequest insert(ConnectionWrapper connection, int status, String message, int userId,
                              int carId, int days, LocalDate startDate, BigDecimal paymentCost) {
        String sql = "INSERT INTO requests" +
                " VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?)" +
                " RETURNING id";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, status);
            statement.setString(2, message);
            statement.setInt(3, userId);
            statement.setInt(4, carId);
            statement.setInt(5, days);
            statement.setObject(6, startDate);
            statement.setBigDecimal(7, paymentCost);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                return new RentRequest(
                        id,
                        status,
                        message,
                        userId,
                        days,
                        carId,
                        startDate,
                        paymentCost
                );
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<RentRequest> getAllRequestsWithStatus(ConnectionWrapper connection, int status) {
        String sql = "SELECT * FROM requests WHERE status = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, status);

            ArrayList<RentRequest> requests = new ArrayList<>();

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                requests.add(resultToRentRequest(resultSet));
            }

            return requests;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

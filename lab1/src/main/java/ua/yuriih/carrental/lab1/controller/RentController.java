package ua.yuriih.carrental.lab1.controller;

import ua.yuriih.carrental.lab1.model.Car;
import ua.yuriih.carrental.lab1.model.RentRequest;
import ua.yuriih.carrental.lab1.model.User;
import ua.yuriih.carrental.lab1.repository.CarDao;
import ua.yuriih.carrental.lab1.repository.PaymentDao;
import ua.yuriih.carrental.lab1.repository.RentRequestDao;
import ua.yuriih.carrental.lab1.repository.UserDao;
import ua.yuriih.carrental.lab1.repository.connection.ConnectionPool;
import ua.yuriih.carrental.lab1.repository.connection.ConnectionWrapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class RentController {
    public static final RentController INSTANCE = new RentController();

    private RentController() {}

    public List<RentRequest> getRequestsWithStatus(int status) {
        ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection();
        return RentRequestDao.INSTANCE.getAllRequestsWithStatus(connection, status);
    }

    public RentRequest addNewPending(int userId, int carId, int days, LocalDate startDate) {
        ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection();
        return RentRequestDao.INSTANCE.insert(
                connection,
                RentRequest.STATUS_PENDING,
                null,
                userId,
                carId,
                days,
                startDate,
                null
        );
    }

    private RentRequest approve(int id) {
        ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection();
        RentRequestDao rentRequestDao = RentRequestDao.INSTANCE;
        UserDao userDao = UserDao.INSTANCE;
        CarDao carDao = CarDao.INSTANCE;

        return connection.doTransaction(() -> {
            RentRequest request = rentRequestDao.getRequest(connection, id);

            if (request == null || request.getStatus() != RentRequest.STATUS_PENDING) {
                throw new IllegalArgumentException("Bad rent request ID");
            }

            RentRequest newRequest = new RentRequest(
                    id,
                    RentRequest.STATUS_NEEDS_PAYMENT,
                    "",
                    request.getUserId(),
                    request.getDays(),
                    request.getCarId(),
                    request.getStartDate(),
                    request.getPaymentCost()
            );

            rentRequestDao.update(connection, newRequest);

            User user = userDao.getUser(connection, request.getUserId());
            userDao.update(connection, new User(
                    user.getPassportId(),
                    user.getName(),
                    user.getPassword(),
                    request.getCarId(),
                    user.isAdmin()
            ));

            Car car = carDao.getCar(connection, request.getCarId());
            carDao.update(connection, new Car(
                    car.getId(),
                    car.getModel(),
                    car.getManufacturer(),
                    car.getHrnPerDay(),
                    user.getPassportId(),
                    car.getThumbnailUrl()
            ));

            return newRequest;
        });
    }

    public RentRequest deny(int id, String message) {
        ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection();
        RentRequestDao dao = RentRequestDao.INSTANCE;

        return connection.doTransaction(() -> {
            RentRequest request = dao.getRequest(connection, id);

            if (request == null || request.getStatus() != RentRequest.STATUS_PENDING) {
                throw new IllegalArgumentException("Bad rent request ID");
            }

            RentRequest newRequest = new RentRequest(
                    id,
                    RentRequest.STATUS_DENIED,
                    message,
                    request.getUserId(),
                    request.getDays(),
                    request.getCarId(),
                    request.getStartDate(),
                    request.getPaymentCost()
            );

            dao.update(connection, newRequest);

            return newRequest;
        });
    }

    public RentRequest pay(int id, BigDecimal amount) {
        ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection();
        RentRequestDao rentRequestDao = RentRequestDao.INSTANCE;
        CarDao carDao = CarDao.INSTANCE;
        PaymentDao paymentDao = PaymentDao.INSTANCE;


        return connection.doTransaction(() -> {
            RentRequest request = rentRequestDao.getRequest(connection, id);
            if (request == null || request.getStatus() != RentRequest.STATUS_NEEDS_PAYMENT)
                throw new IllegalArgumentException("Bad rent request ID");

            Car car = carDao.getCar(connection, request.getCarId());

            if (amount.compareTo(car.getHrnPerDay().multiply(BigDecimal.valueOf(request.getDays()))) < 0)
                throw new IllegalArgumentException("Amount is not high enough");

            RentRequest newRequest = new RentRequest(
                    id,
                    RentRequest.STATUS_ACTIVE,
                    "",
                    request.getUserId(),
                    request.getDays(),
                    request.getCarId(),
                    request.getStartDate(),
                    request.getPaymentCost()
            );

            rentRequestDao.update(connection, newRequest);
            paymentDao.insertPayment(connection, amount, id, false);

            return newRequest;
        });
    }

    private RentRequest endSuccessfully(int id) {
        ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection();
        RentRequestDao rentRequestDao = RentRequestDao.INSTANCE;
        UserDao userDao = UserDao.INSTANCE;
        CarDao carDao = CarDao.INSTANCE;

        return connection.doTransaction(() -> {
            RentRequest request = rentRequestDao.getRequest(connection, id);

            if (request == null || request.getStatus() != RentRequest.STATUS_ACTIVE) {
                throw new IllegalArgumentException("Bad rent request ID");
            }

            RentRequest newRequest = new RentRequest(
                    id,
                    RentRequest.STATUS_ENDED,
                    "",
                    request.getUserId(),
                    request.getDays(),
                    request.getCarId(),
                    request.getStartDate(),
                    request.getPaymentCost()
            );

            rentRequestDao.update(connection, newRequest);

            User user = userDao.getUser(connection, request.getUserId());
            userDao.update(connection, new User(
                    user.getPassportId(),
                    user.getName(),
                    user.getPassword(),
                    null,
                    user.isAdmin()
            ));

            Car car = carDao.getCar(connection, request.getCarId());
            carDao.update(connection, new Car(
                    car.getId(),
                    car.getModel(),
                    car.getManufacturer(),
                    car.getHrnPerDay(),
                    null,
                    car.getThumbnailUrl()
            ));

            return newRequest;
        });
    }

    private RentRequest setNeedsRepair(int id, String message, BigDecimal paymentCost) {
        ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection();
        RentRequestDao rentRequestDao = RentRequestDao.INSTANCE;
        UserDao userDao = UserDao.INSTANCE;

        return connection.doTransaction(() -> {
            RentRequest request = rentRequestDao.getRequest(connection, id);

            if (request == null || request.getStatus() != RentRequest.STATUS_ACTIVE) {
                throw new IllegalArgumentException("Bad rent request ID");
            }

            RentRequest newRequest = new RentRequest(
                    id,
                    RentRequest.STATUS_REPAIR_NEEDED,
                    message,
                    request.getUserId(),
                    request.getDays(),
                    request.getCarId(),
                    request.getStartDate(),
                    paymentCost
            );

            rentRequestDao.update(connection, newRequest);

            User user = userDao.getUser(connection, request.getUserId());
            userDao.update(connection, new User(
                    user.getPassportId(),
                    user.getName(),
                    user.getPassword(),
                    null,
                    user.isAdmin()
            ));

            return newRequest;
        });
    }

    private RentRequest payForRepair(int id, BigDecimal hrnAmount) {
        ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection();
        RentRequestDao rentRequestDao = RentRequestDao.INSTANCE;
        CarDao carDao = CarDao.INSTANCE;
        PaymentDao paymentDao = PaymentDao.INSTANCE;


        return connection.doTransaction(() -> {
            RentRequest request = rentRequestDao.getRequest(connection, id);
            if (request == null || request.getStatus() != RentRequest.STATUS_REPAIR_NEEDED)
                throw new IllegalArgumentException("Bad rent request ID");

            if (hrnAmount.compareTo(request.getPaymentCost()) < 0)
                throw new IllegalArgumentException("Amount is not high enough");

            RentRequest newRequest = new RentRequest(
                    id,
                    RentRequest.STATUS_ENDED,
                    "",
                    request.getUserId(),
                    request.getDays(),
                    request.getCarId(),
                    request.getStartDate(),
                    null
            );

            rentRequestDao.update(connection, newRequest);
            paymentDao.insertPayment(connection, hrnAmount, id, true);

            Car car = carDao.getCar(connection, request.getCarId());
            carDao.update(connection, new Car(
                    car.getId(),
                    car.getModel(),
                    car.getManufacturer(),
                    car.getHrnPerDay(),
                    null,
                    car.getThumbnailUrl()
            ));

            return newRequest;
        });
    }
}

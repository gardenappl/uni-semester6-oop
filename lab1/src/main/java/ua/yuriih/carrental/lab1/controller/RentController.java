package ua.yuriih.carrental.lab1.controller;

import ua.yuriih.carrental.lab1.model.Car;
import ua.yuriih.carrental.lab1.model.Payment;
import ua.yuriih.carrental.lab1.model.RentRequest;
import ua.yuriih.carrental.lab1.model.RequestInfo;
import ua.yuriih.carrental.lab1.repository.CarDao;
import ua.yuriih.carrental.lab1.repository.PaymentDao;
import ua.yuriih.carrental.lab1.repository.RentRequestDao;
import ua.yuriih.carrental.lab1.repository.connection.ConnectionPool;
import ua.yuriih.carrental.lab1.repository.connection.ConnectionWrapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class RentController {
    public static final RentController INSTANCE = new RentController();

    private RentController() {}

    public List<RequestInfo> getRequestsWithStatus(int status) {
        try (ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection()) {
            return RentRequestDao.INSTANCE.getAllRequestsWithStatus(connection, status);
        }
    }

    public List<RequestInfo> getRequestsWithStatusForUser(int status, long userId) {
        try (ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection()) {
            return RentRequestDao.INSTANCE.getUserRequestsWithStatus(connection, status, userId);
        }
    }

    public List<RequestInfo> getActiveOutdatedRequests() {
        try (ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection()) {
            return RentRequestDao.INSTANCE.getOutdatedActiveRequests(connection);
        }
    }

    public List<RequestInfo> getActiveOutdatedRequests(long userId) {
        try (ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection()) {
            return RentRequestDao.INSTANCE.getOutdatedActiveRequests(connection, userId);
        }
    }

    public RentRequest addNewPending(long userId, int carId, int days, LocalDate startDate, BigDecimal hrnAmount) {
        try (ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection()) {
            RentRequestDao rentRequestDao = RentRequestDao.INSTANCE;
            CarDao carDao = CarDao.INSTANCE;
            PaymentDao paymentDao = PaymentDao.INSTANCE;


            return connection.doTransaction(() -> {
                Car car = carDao.getCar(connection, carId);

                if (hrnAmount.compareTo(car.getHrnPerDay().multiply(BigDecimal.valueOf(days))) < 0)
                    throw new IllegalArgumentException("Payment amount is not high enough");

                RentRequest request = rentRequestDao.insert(connection, RentRequest.STATUS_PENDING, "", userId, carId, days, startDate, null);
                paymentDao.insertPayment(connection, hrnAmount, request.getId(), Payment.TYPE_REVENUE);

                return request;
            });
        }
    }

    public RentRequest approve(int id) {
        try (ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection()) {
            RentRequestDao rentRequestDao = RentRequestDao.INSTANCE;
            CarDao carDao = CarDao.INSTANCE;

            return connection.doTransaction(() -> {
                RentRequest request = rentRequestDao.getRequest(connection, id);

                if (request == null || request.getStatus() != RentRequest.STATUS_PENDING) {
                    throw new IllegalArgumentException("Bad rent request ID");
                }

                RentRequest newRequest = new RentRequest(
                        id,
                        RentRequest.STATUS_ACTIVE,
                        "",
                        request.getUserId(),
                        request.getDays(),
                        request.getCarId(),
                        request.getStartDate(),
                        request.getRepairCost()
                );

                rentRequestDao.update(connection, newRequest);
                rentRequestDao.deleteALlPendingForCarId(connection, request.getCarId());

                Car car = carDao.getCar(connection, request.getCarId());
                carDao.update(connection, new Car(
                        car.getId(),
                        car.getModel(),
                        car.getManufacturer(),
                        car.getHrnPerDay(),
                        request.getUserId(),
                        car.getThumbnailUrl()
                ));

                return newRequest;
            });
        }
    }

    public RentRequest deny(int id, String message) {
        try (ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection()) {
            RentRequestDao requestDao = RentRequestDao.INSTANCE;
            PaymentDao paymentDao = PaymentDao.INSTANCE;

            return connection.doTransaction(() -> {
                RentRequest request = requestDao.getRequest(connection, id);

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
                        request.getRepairCost()
                );

                requestDao.update(connection, newRequest);

                //Do refund

                Payment payment = paymentDao.getInitialPaymentForRequest(connection, id);
                paymentDao.insertPayment(connection,
                        payment.getHrnAmount().negate(),
                        id,
                        Payment.TYPE_REFUND
                );

                return newRequest;
            });
        }
    }

    public RentRequest endSuccessfully(int id, BigDecimal maintenanceCostHrn) {
        try (ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection()) {
            RentRequestDao rentRequestDao = RentRequestDao.INSTANCE;
            CarDao carDao = CarDao.INSTANCE;
            PaymentDao paymentDao = PaymentDao.INSTANCE;

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
                        request.getRepairCost()
                );

                rentRequestDao.update(connection, newRequest);

                Car car = carDao.getCar(connection, request.getCarId());
                carDao.update(connection, new Car(
                        car.getId(),
                        car.getModel(),
                        car.getManufacturer(),
                        car.getHrnPerDay(),
                        null,
                        car.getThumbnailUrl()
                ));

                paymentDao.insertPayment(connection,
                        maintenanceCostHrn.negate(),
                        id,
                        Payment.TYPE_MAINTENANCE
                );

                return newRequest;
            });
        }
    }

    public RentRequest setNeedsRepair(int id, String message, BigDecimal paymentCost) {
        try (ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection()) {
            RentRequestDao rentRequestDao = RentRequestDao.INSTANCE;
            PaymentDao paymentDao = PaymentDao.INSTANCE;

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

                paymentDao.insertPayment(connection, paymentCost.negate(), id, Payment.TYPE_REPAIR_COST);

                return newRequest;
            });
        }
    }

    public RentRequest payForRepair(int id, BigDecimal hrnAmount) {
        try (ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection()) {
            RentRequestDao rentRequestDao = RentRequestDao.INSTANCE;
            CarDao carDao = CarDao.INSTANCE;
            PaymentDao paymentDao = PaymentDao.INSTANCE;


            return connection.doTransaction(() -> {
                RentRequest request = rentRequestDao.getRequest(connection, id);
                if (request == null || request.getStatus() != RentRequest.STATUS_REPAIR_NEEDED)
                    throw new IllegalArgumentException("Bad rent request ID");

                if (hrnAmount.compareTo(request.getRepairCost()) < 0)
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
                paymentDao.insertPayment(connection, hrnAmount, id, Payment.TYPE_REPAIR_PAID_BY_CUSTOMER);

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
}

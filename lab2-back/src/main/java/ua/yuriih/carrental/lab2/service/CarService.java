package ua.yuriih.carrental.lab2.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.yuriih.carrental.lab2.model.Car;
import ua.yuriih.carrental.lab2.model.Payment;
import ua.yuriih.carrental.lab2.model.User;
import ua.yuriih.carrental.lab2.repository.CarRepository;
import ua.yuriih.carrental.lab2.repository.PaymentRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final PaymentRepository paymentRepository;

    public List<Car> getAvailableCars() {
        return carRepository.findByUserIsNull();
    }

    public List<Car> getAvailableCars(String manufacturer) {
        return carRepository.findByUserIsNullAndManufacturer(manufacturer);
    }

    public Car getCar(int id) {
        return carRepository.getById(id);
    }

    public List<String> getAllCarManufacturers() {
        return carRepository.findAllCarManufacturers();
    }

    @Transactional
    public Car addCar(String model, String manufacturer, BigDecimal uahPerDay, User user, String thumbnailUrl, String description, BigDecimal uahPurchase) {
        Car car = new Car(
                model,
                manufacturer,
                uahPerDay,
                thumbnailUrl,
                description,
                uahPurchase
        );
        car.setUser(user);
        car = carRepository.save(car);
        paymentRepository.save(new Payment(
                uahPurchase.negate(),
                null,
                Payment.TYPE_PURCHASE_NEW_CAR,
                car,
                Instant.now()
        ));
        return car;
    }
}

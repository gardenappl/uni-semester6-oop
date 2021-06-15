package ua.yuriih.carrental.lab2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.yuriih.carrental.lab2.model.Car;
import ua.yuriih.carrental.lab2.model.RentRequest;
import ua.yuriih.carrental.lab2.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByName(String name);

    User findByPassportId(long passportId);
}

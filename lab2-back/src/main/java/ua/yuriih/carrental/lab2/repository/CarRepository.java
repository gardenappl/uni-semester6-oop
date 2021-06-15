package ua.yuriih.carrental.lab2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.yuriih.carrental.lab2.model.Car;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {
    List<Car> findByUserIsNull();

    List<Car> findByUserIsNullAndManufacturer(String manufacturer);

    @Query("SELECT DISTINCT manufacturer FROM Car")
    List<String> findAllCarManufacturers();
}

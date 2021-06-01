package ua.yuriih.carrental.lab1.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.yuriih.carrental.lab1.controller.CarController;
import ua.yuriih.carrental.lab1.controller.UserController;
import ua.yuriih.carrental.lab1.model.Car;
import ua.yuriih.carrental.lab1.util.ServletJsonUtils;

import java.util.List;

@WebServlet(value = "/cars", name = "carsServlet")
public class ListCarsServlet extends HttpServlet {
    private static class Request {
        public boolean getAllAvailableCars;
        public Integer carId;
    }

    private static class Response {
        public final Car[] cars;

        Response(Car[] cars) {
            this.cars = cars;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        Request request = ServletJsonUtils.objectFromJsonRequest(req, Request.class);

        Car[] cars;
        if (request.getAllAvailableCars) {
            List<Car> carList = CarController.INSTANCE.getAvailableCars();
            cars = new Car[carList.size()];
            carList.toArray(cars);
        } else {
            cars = new Car[] { CarController.INSTANCE.getCar(request.carId) };
        }

        ServletJsonUtils.objectToJsonResponse(new Response(cars), resp);
    }
}

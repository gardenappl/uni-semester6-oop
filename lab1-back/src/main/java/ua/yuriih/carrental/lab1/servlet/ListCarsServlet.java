package ua.yuriih.carrental.lab1.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.yuriih.carrental.lab1.controller.CarController;
import ua.yuriih.carrental.lab1.model.Car;
import ua.yuriih.carrental.lab1.util.ServletJsonUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@WebServlet(value = "/cars", name = "carsServlet")
public class ListCarsServlet extends HttpServlet {
    private static class Request {
        public boolean getAllAvailableCars;
        public Integer carId;
        public String manufacturer;
    }

    private static class Response {
        public final List<Car> cars;

        Response(List<Car> cars) {
            this.cars = cars;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        Request request = ServletJsonUtils.objectFromJsonRequest(req, Request.class);

        CarController controller = CarController.INSTANCE;

        List<Car> cars;
        if (request.getAllAvailableCars) {
            if (request.manufacturer != null && request.manufacturer.length() > 0)
                cars = controller.getAvailableCars(request.manufacturer);
            else
                cars = controller.getAvailableCars();
        } else {
            cars = new ArrayList<>(1);
            cars.add(CarController.INSTANCE.getCar(request.carId));
        }

        ServletJsonUtils.objectToJsonResponse(new Response(cars), resp);
    }
}

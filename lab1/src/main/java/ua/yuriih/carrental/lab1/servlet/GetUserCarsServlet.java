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

@WebServlet(value = "/my-cars", name = "getUserCarsServlet")
public class GetUserCarsServlet extends HttpServlet {
    private static class Request {
        long token;
    }

    private static class Response {
        Car[] cars;

        Response(Car[] cars) {
            this.cars = cars;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        Request request = ServletJsonUtils.objectFromJsonRequest(req, Request.class);

        long userId = UserController.INSTANCE.getUserIdFromToken(request.token);

        List<Car> carList = CarController.INSTANCE.getCarForUser(userId);
        Car[] cars = new Car[carList.size()];
        carList.toArray(cars);

        ServletJsonUtils.objectToJsonResponse(new Response(cars), resp);
    }
}


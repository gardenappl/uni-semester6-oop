package ua.yuriih.carrental.lab1.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.yuriih.carrental.lab1.controller.CarController;
import ua.yuriih.carrental.lab1.model.Car;
import ua.yuriih.carrental.lab1.util.ServletJsonUtils;

import java.util.List;

@WebServlet(value = "/car-models")
public class ListCarManufacturersServlet extends HttpServlet {
    private static class Response {
        public final List<String> manufacturers;

        Response(List<String> manufacturers) {
            this.manufacturers = manufacturers;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        ServletJsonUtils.objectToJsonResponse(new Response(CarController.INSTANCE.getAllCarManufacturers()), resp);
    }
}

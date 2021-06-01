package ua.yuriih.carrental.lab1.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.yuriih.carrental.lab1.controller.CarController;
import ua.yuriih.carrental.lab1.util.ServletJsonUtils;

import java.math.BigDecimal;

@WebServlet(value = "/get-cost")
public class GetCostServlet extends HttpServlet {
    private static class Request {
        public int carId;
        public int days;
    }

    private static class Response {
        public final String hrnCost;
        Response(String hrnCost) {
            this.hrnCost = hrnCost;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        Request request = ServletJsonUtils.objectFromJsonRequest(req, Request.class);

        BigDecimal hrnCost = CarController.INSTANCE.getCost(request.carId, request.days);

        ServletJsonUtils.objectToJsonResponse(new Response(hrnCost.toString()), resp);
    }
}

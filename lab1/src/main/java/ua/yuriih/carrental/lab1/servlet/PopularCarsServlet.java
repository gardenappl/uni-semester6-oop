package ua.yuriih.carrental.lab1.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.yuriih.carrental.lab1.controller.CarController;
import ua.yuriih.carrental.lab1.controller.UserController;
import ua.yuriih.carrental.lab1.model.CarStatistic;
import ua.yuriih.carrental.lab1.util.ServletJsonUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@WebServlet(value = "/popular-cars")
public class PopularCarsServlet extends HttpServlet {
    private static class Request {
        public long token;
        public String since;
    }

    private static class Response {
        public final List<CarStatistic.RequestCount> carStatistics;

        Response(List<CarStatistic.RequestCount> carStatistics) {
            this.carStatistics = carStatistics;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        Request request = ServletJsonUtils.objectFromJsonRequest(req, Request.class);

        if (!UserController.INSTANCE.isAdminToken(request.token)) {
            ServletJsonUtils.objectToJsonResponse(new Response(Collections.emptyList()), resp);
            return;
        }

        LocalDate since = null;
        if (request.since != null && request.since.length() > 0)
            since = LocalDate.parse(request.since);

        ServletJsonUtils.objectToJsonResponse(new Response(CarController.INSTANCE.getMostPopularCars(since)), resp);
    }
}

package ua.yuriih.carrental.lab1.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.yuriih.carrental.lab1.controller.CarController;
import ua.yuriih.carrental.lab1.controller.RentController;
import ua.yuriih.carrental.lab1.controller.UserController;
import ua.yuriih.carrental.lab1.model.*;
import ua.yuriih.carrental.lab1.repository.CarDao;
import ua.yuriih.carrental.lab1.util.ServletJsonUtils;

import java.util.List;

@WebServlet(value = "/top-cars")
public class TopCarsServlet extends HttpServlet {
    private static class Request {
        public long token;
    }

    private static class Response {
        public final CarStatistic[] carStatistics;

        Response(CarStatistic[] carStatistics) {
            this.carStatistics = carStatistics;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        Request request = ServletJsonUtils.objectFromJsonRequest(req, Request.class);

        if (!UserController.INSTANCE.isAdminToken(request.token)) {
            ServletJsonUtils.objectToJsonResponse(new Response(new CarStatistic[0]), resp);
            return;
        }

        List<CarStatistic> carStatisticsList = CarController.INSTANCE.getMostProfitableCars();
        CarStatistic[] carStatistics = new CarStatistic[carStatisticsList.size()];
        carStatisticsList.toArray(carStatistics);

        ServletJsonUtils.objectToJsonResponse(new Response(carStatistics), resp);
    }
}

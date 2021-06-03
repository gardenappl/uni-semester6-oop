package ua.yuriih.carrental.lab1.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.yuriih.carrental.lab1.controller.CarController;
import ua.yuriih.carrental.lab1.controller.PaymentController;
import ua.yuriih.carrental.lab1.controller.UserController;
import ua.yuriih.carrental.lab1.model.CarStatistic;
import ua.yuriih.carrental.lab1.model.Payment;
import ua.yuriih.carrental.lab1.model.PaymentInfo;
import ua.yuriih.carrental.lab1.util.ServletJsonUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@WebServlet(value = "/all-payments")
public class GetAllPaymentsServlet extends HttpServlet {
    private static class Request {
        public long token;
        public String since;
        public String carId;
    }

    private static class Response {
        public final List<PaymentInfo> payments;

        Response(List<PaymentInfo> payments) {
            this.payments = payments;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        Request request = ServletJsonUtils.objectFromJsonRequest(req, Request.class);

        if (!UserController.INSTANCE.isAdminToken(request.token)) {
            ServletJsonUtils.objectToJsonResponse(new Response(Collections.emptyList()), resp);
            return;
        }

        if (request.carId != null && request.carId.length() > 0) {
            int carId = Integer.parseInt(request.carId);
            ServletJsonUtils.objectToJsonResponse(new Response(PaymentController.INSTANCE.getAllPaymentInfo(carId)), resp);
        } else {
            LocalDate since = null;
            if (request.since != null && request.since.length() > 0)
                since = LocalDate.parse(request.since);

            ServletJsonUtils.objectToJsonResponse(new Response(PaymentController.INSTANCE.getAllPaymentInfo(since)), resp);
        }
    }
}

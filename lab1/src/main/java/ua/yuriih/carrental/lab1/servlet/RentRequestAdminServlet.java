package ua.yuriih.carrental.lab1.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.yuriih.carrental.lab1.controller.RentController;
import ua.yuriih.carrental.lab1.controller.UserController;
import ua.yuriih.carrental.lab1.model.RentRequest;
import ua.yuriih.carrental.lab1.util.ServletJsonUtils;

import java.math.BigDecimal;
import java.util.List;

@WebServlet(value = "/admin-request", name = "adminRequest")
public class RentRequestAdminServlet extends HttpServlet {
    private static final String ACTION_APPROVE = "approve";
    private static final String ACTION_DENY = "deny";
    private static final String ACTION_SET_NEEDS_REPAIR = "needs-repair";
    private static final String ACTION_END_SUCCESSFULLY = "end";

    private static class Request {
        long token;
        int requestId;
        String action;
        String actionMessage;
        String repairCostHrn;
    }

    private static class Response {
        boolean success;

        Response(boolean success) {
            this.success = success;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        Request request = ServletJsonUtils.objectFromJsonRequest(req, Request.class);

        if (!UserController.INSTANCE.isAdminToken(request.token)) {
            ServletJsonUtils.objectToJsonResponse(new Response(false), resp);
            return;
        }

        RentController rentController = RentController.INSTANCE;

        switch (request.action) {
            case ACTION_APPROVE -> rentController.approve(request.requestId);
            case ACTION_DENY -> rentController.deny(request.requestId, request.actionMessage);
            case ACTION_END_SUCCESSFULLY -> rentController.endSuccessfully(request.requestId);
            case ACTION_SET_NEEDS_REPAIR -> {
                BigDecimal repairCostHrn = new BigDecimal(request.repairCostHrn);
                rentController.setNeedsRepair(request.requestId, request.actionMessage, repairCostHrn);
            }
        }
        ServletJsonUtils.objectToJsonResponse(new Response(true), resp);
    }
}

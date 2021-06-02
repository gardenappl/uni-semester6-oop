package ua.yuriih.carrental.lab1.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.yuriih.carrental.lab1.controller.CarController;
import ua.yuriih.carrental.lab1.controller.RentController;
import ua.yuriih.carrental.lab1.controller.UserController;
import ua.yuriih.carrental.lab1.model.Car;
import ua.yuriih.carrental.lab1.model.RequestInfo;
import ua.yuriih.carrental.lab1.util.ServletJsonUtils;

import java.util.List;

@WebServlet(value = "/my-requests")
public class GetUserRequestsServlet extends HttpServlet {
    private static class Request {
        public long token;
        public int status;
        public boolean getOutdatedActive;
    }

    private static class Response {
        public final RequestInfo[] requests;

        Response(RequestInfo[] requestInfos) {
            this.requests = requestInfos;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        Request request = ServletJsonUtils.objectFromJsonRequest(req, Request.class);

        long userId = UserController.INSTANCE.getUserIdFromToken(request.token);

        List<RequestInfo> requestInfoList = request.getOutdatedActive ?
                RentController.INSTANCE.getActiveOutdatedRequests(userId)
                : RentController.INSTANCE.getRequestsWithStatusForUser(request.status, userId);
        RequestInfo[] requestInfos = new RequestInfo[requestInfoList.size()];
        requestInfoList.toArray(requestInfos);

        ServletJsonUtils.objectToJsonResponse(new Response(requestInfos), resp);
    }
}


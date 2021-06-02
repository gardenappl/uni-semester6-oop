package ua.yuriih.carrental.lab1.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.yuriih.carrental.lab1.controller.RentController;
import ua.yuriih.carrental.lab1.controller.UserController;
import ua.yuriih.carrental.lab1.model.Car;
import ua.yuriih.carrental.lab1.model.RentRequest;
import ua.yuriih.carrental.lab1.model.RequestInfo;
import ua.yuriih.carrental.lab1.model.User;
import ua.yuriih.carrental.lab1.util.ServletJsonUtils;

import java.util.List;

@WebServlet(value = "/list-requests", name = "listRequests")
public class RentRequestListServlet extends HttpServlet {
    private static class Request {
        public long token;
        public int status;
        public boolean getOutdatedActive;
    }

    private static class Response {
        public final RequestInfo[] requests;

        Response(RequestInfo[] requests) {
            this.requests = requests;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        Request request = ServletJsonUtils.objectFromJsonRequest(req, Request.class);

        if (!UserController.INSTANCE.isAdminToken(request.token)) {
            ServletJsonUtils.objectToJsonResponse(new Response(new RequestInfo[0]), resp);
            return;
        }

        List<RequestInfo> rentRequestList = request.getOutdatedActive ?
                RentController.INSTANCE.getActiveOutdatedRequests()
                : RentController.INSTANCE.getRequestsWithStatus(request.status);
        RequestInfo[] rentRequests = new RequestInfo[rentRequestList.size()];
        rentRequestList.toArray(rentRequests);

        ServletJsonUtils.objectToJsonResponse(new Response(rentRequests), resp);
    }
}

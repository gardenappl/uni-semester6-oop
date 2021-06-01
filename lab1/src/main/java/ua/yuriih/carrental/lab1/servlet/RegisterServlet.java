package ua.yuriih.carrental.lab1.servlet;

import ua.yuriih.carrental.lab1.controller.UserController;
import ua.yuriih.carrental.lab1.util.ServletJsonUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(value = "/register", name = "registerServlet")
public class RegisterServlet extends HttpServlet {
    private static class Request {
        long passportId;
        String name;
        String password;
    }

    private static class Response {
        Long token;
        Response(Long token) {
            this.token = token;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        Request request = ServletJsonUtils.objectFromJsonRequest(req, Request.class);

        Long token = UserController.INSTANCE.registerAndLogIn(request.passportId, request.name, request.password);

        ServletJsonUtils.objectToJsonResponse(new Response(token), resp);
    }
}

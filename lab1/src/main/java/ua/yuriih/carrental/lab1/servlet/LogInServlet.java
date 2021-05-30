package ua.yuriih.carrental.lab1.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.yuriih.carrental.lab1.controller.UserController;
import ua.yuriih.carrental.lab1.util.ServletJsonUtils;

@WebServlet(value = "/login", name = "loginServlet")
public class LogInServlet extends HttpServlet {
    private static class Request {
        String name;
        String password;
    }

    private static class Response {
        Integer token;
        Response(Integer token) {
            this.token = token;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        Request request = ServletJsonUtils.objectFromJsonRequest(req, Request.class);

        Integer token = UserController.INSTANCE.logIn(request.name, request.password);

        ServletJsonUtils.objectToJsonResponse(new Response(token), resp);
    }
}

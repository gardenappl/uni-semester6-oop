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

        Integer token = UserController.INSTANCE.registerAndLogIn(request.name, request.password);

        ServletJsonUtils.objectToJsonResponse(new Response(token), resp);
    }
}

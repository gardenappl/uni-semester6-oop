package ua.yuriih.carrental.lab1.servlet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.yuriih.carrental.lab1.controller.UserController;
import ua.yuriih.carrental.lab1.util.ServletJsonUtils;

@WebServlet(value = "/login", name = "loginServlet")
public class LogInServlet extends HttpServlet {
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Request {
        public String username;
        public String password;
    }

    private static class Response {
        public final Long token;

        Response(Long token) {
            this.token = token;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        Request request = ServletJsonUtils.objectFromJsonRequest(req, Request.class);

        Long token = UserController.INSTANCE.logIn(request.username, request.password);

        ServletJsonUtils.objectToJsonResponse(new Response(token), resp);
    }
}

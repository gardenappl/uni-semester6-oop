package ua.yuriih.carrental.lab1.servlet;

import ua.yuriih.carrental.lab1.model.User;
import ua.yuriih.carrental.lab1.util.ServletJsonUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(value = "/register/*", name = "registerServlet")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletJsonUtils.objectToJsonResponse(new User(0, "Hi", "pass", 9, false), resp);
    }
}

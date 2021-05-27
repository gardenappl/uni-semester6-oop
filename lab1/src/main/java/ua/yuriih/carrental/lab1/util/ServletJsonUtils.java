package ua.yuriih.carrental.lab1.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public final class ServletJsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static void objectToJsonResponse(Object object, HttpServletResponse response) {
        response.setContentType("application/json");
        try {
            String json = objectMapper.writeValueAsString(object);
            PrintWriter out = response.getWriter();
            out.print(json);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static <T> T objectFromJsonRequest(HttpServletRequest request, Class<T> aClass) {
        try {
            return objectMapper.readValue(request.getReader(), aClass);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

package servlet;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import entities.Configuracion;
import logic.LogicConfiguracion;

@WebServlet("/configuracion")
public class ServletConfiguracion extends HttpServlet {
    private LogicConfiguracion lc = new LogicConfiguracion();

    private Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> 
            new JsonPrimitive(src.toString())) 
        .create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setContentType("application/json;charset=UTF-8");
        String action = req.getParameter("action");

        try {
            if ("actual".equals(action)) {
                resp.getWriter().write("{\"cupo\":" + lc.getCupoActual() + "}");
            } else {
                LinkedList<Configuracion> lista = lc.getAll();
                resp.getWriter().write(gson.toJson(lista));
            }
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setContentType("application/json;charset=UTF-8");
        String action = req.getParameter("action");

        try {
            if ("update_cupo".equals(action)) {
                int nuevoCupo = Integer.parseInt(req.getParameter("valor"));
                lc.setCupo(nuevoCupo);
                resp.getWriter().write("{\"status\":\"ok\"}");
            } else if ("eliminar".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                lc.delete(id);
                resp.getWriter().write("{\"status\":\"ok\"}");
            }
        } catch (Exception e) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
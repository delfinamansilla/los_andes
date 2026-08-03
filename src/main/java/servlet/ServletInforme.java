package servlet;

import java.io.IOException;
import java.util.LinkedList;

import entities.InformeRecaudacion;
import logic.LogicInformeRecaudacion;
import entities.InformeOcupacion;
import logic.LogicInformeOcupacion;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDate;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/informe")
public class ServletInforme extends HttpServlet {

    private LogicInformeRecaudacion logicInforme;
    private Gson gson;


    public ServletInforme() {
        logicInforme = new LogicInformeRecaudacion();

        gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class,
                (com.google.gson.JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                    new com.google.gson.JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDate.class,
                (com.google.gson.JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                    LocalDate.parse(json.getAsString()))
            .create();
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setHeader("Access-Control-Allow-Origin", "*"); 
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setContentType("application/json;charset=UTF-8");

        try {
            String action = request.getParameter("action");

            if (action == null) {
                response.setStatus(400);
                response.getWriter().write("{\"error\":\"Falta el parámetro action\"}");
                return;
            }

            String mesStr = request.getParameter("mes");
            String anioStr = request.getParameter("anio");

            if (mesStr == null || anioStr == null) {
                response.setStatus(400);
                response.getWriter().write("{\"error\":\"Faltan parámetros de fecha (mes/anio)\"}");
                return;
            }

            int mes = Integer.parseInt(mesStr);
            int anio = Integer.parseInt(anioStr);

            if ("recaudacion".equalsIgnoreCase(action)) {
                LinkedList<InformeRecaudacion> informe = logicInforme.generarInforme(mes, anio);
                response.getWriter().write(gson.toJson(informe));

            } else if ("ocupacion".equalsIgnoreCase(action)) {
                LogicInformeOcupacion logicOcupacion = new LogicInformeOcupacion();
                LinkedList<InformeOcupacion> informe = logicOcupacion.generarInforme(mes, anio);
                response.getWriter().write(gson.toJson(informe));

            } else {
                response.setStatus(400);
                response.getWriter().write("{\"error\":\"Acción inválida: " + action + "\"}");
            }

        } catch (NumberFormatException e) {
            response.setStatus(400);
            response.getWriter().write("{\"error\":\"Mes o Año deben ser números válidos\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            response.getWriter().write("{\"error\":\"Error interno: " + e.getMessage() + "\"}");
        }
    }
}
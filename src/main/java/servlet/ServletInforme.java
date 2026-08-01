package servlet;

import java.io.IOException;
import java.util.LinkedList;

import entities.InformeRecaudacion;
import logic.LogicInformeRecaudacion;

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


    	response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "*");

        response.setContentType("application/json;charset=UTF-8");

        try {

            String action = request.getParameter("action");


            if ("recaudacion".equalsIgnoreCase(action)) {


                int mes = Integer.parseInt(request.getParameter("mes"));
                int anio = Integer.parseInt(request.getParameter("anio"));


                LinkedList<InformeRecaudacion> informe =
                        logicInforme.generarInforme(mes, anio);


                response.getWriter().write(
                    gson.toJson(informe)
                );


            } else {

                response.setStatus(400);
                response.getWriter().write(
                    "{\"error\":\"Acción inválida\"}"
                );

            }


        } catch(Exception e) {

            e.printStackTrace();

            response.setStatus(500);
            response.getWriter().write(
                "{\"error\":\"Error al generar informe\"}"
            );
        }
    }
}
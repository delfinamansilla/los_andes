package servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import entities.Cuota;
import logic.LogicCuota;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@WebServlet({"/cuota", "/Cuota", "/CUOTA"})
public class ServletCuota extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private LogicCuota logicCuota;
    private Gson gson;

    public ServletCuota() {
        super();
        logicCuota = new LogicCuota();
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
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        String action = request.getParameter("action");
        response.setContentType("application/json;charset=UTF-8");
        
        try {
            if (action == null) {
                response.getWriter().write("{\"error\":\"Debe especificar una acción.\"}");
                return;
            }
            
            switch (action.toLowerCase()) {
                case "listar":
                    LinkedList<Cuota> cuotas = new LinkedList<>(logicCuota.getAll());
                    response.getWriter().write(gson.toJson(cuotas));
                    break;
                case "buscar":
                    int id = Integer.parseInt(request.getParameter("id"));
                    Cuota c = logicCuota.getById(id);
                    if (c != null) response.getWriter().write(gson.toJson(c));
                    else response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    break;
                case "eliminar":
                    int idElim = Integer.parseInt(request.getParameter("id"));
                    logicCuota.delete(idElim);
                    response.getWriter().write("{\"mensaje\":\"Cuota eliminada\"}");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        String action = request.getParameter("action");
        response.setContentType("application/json;charset=UTF-8");

        try {
            if (action == null) return;

            switch (action.toLowerCase()) {
                case "crear": {
                    int nroCuota = Integer.parseInt(request.getParameter("nro_cuota"));
                    String fechaVencimientoStr = request.getParameter("fecha_vencimiento");

                    Cuota nueva = new Cuota();
                    nueva.setNro_cuota(nroCuota);
                    nueva.setFecha_cuota(LocalDate.now());
                    nueva.setFecha_vencimiento(LocalDate.parse(fechaVencimientoStr));
                    
                    logicCuota.add(nueva);

                    response.getWriter().write(gson.toJson(nueva));
                    break;
                }

                case "actualizar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    int nroCuota = Integer.parseInt(request.getParameter("nro_cuota"));
                    String fechaVencimientoStr = request.getParameter("fecha_vencimiento");

                    Cuota c = new Cuota();
                    c.setId(id);
                    c.setNro_cuota(nroCuota);
                    c.setFecha_cuota(LocalDate.now());
                    c.setFecha_vencimiento(LocalDate.parse(fechaVencimientoStr));

                    logicCuota.update(c);
                    response.getWriter().write("{\"mensaje\":\"Cuota actualizada\"}");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
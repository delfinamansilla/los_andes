package servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import entities.Alquiler_salon;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logic.LogicAlquiler_salon;

@WebServlet({"/alquiler_salon"})
public class ServletAlquiler_salon extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private LogicAlquiler_salon logic;
    private Gson gson;

    public ServletAlquiler_salon() {
        logic = new LogicAlquiler_salon();

        gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class,
                (com.google.gson.JsonSerializer<LocalDate>)
                    (src, type, ctx) -> new com.google.gson.JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalTime.class,
                (com.google.gson.JsonSerializer<LocalTime>)
                    (src, type, ctx) -> new com.google.gson.JsonPrimitive(src.toString()))
            .create();
    }

    // ============================================================
    // GET
    // ============================================================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");
        String action = req.getParameter("action");

        try {
            if (action == null) {
                resp.getWriter().write("{\"error\": \"Debe especificar acción\"}");
                return;
            }

            switch (action) {

                // ------------------------------------
                case "listar": {
                    resp.getWriter().write(gson.toJson(logic.getAll()));
                    break;
                }
                
             // Dentro del switch(action) en doGet:

                case "mis_reservas": {

                    if (req.getParameter("idUsuario") == null) {
                        resp.setStatus(400);
                        resp.getWriter().write("{\"error\": \"Falta idUsuario\"}");
                        return;
                    }
                    
                    int idUser = Integer.parseInt(req.getParameter("idUsuario"));
                    LinkedList<Alquiler_salon> reservas = logic.getByUsuarioFuturos(idUser);
                    
                    resp.getWriter().write(gson.toJson(reservas));
                    break;
                }

                // ------------------------------------
                case "buscar": {
                    int id = Integer.parseInt(req.getParameter("id"));
                    resp.getWriter().write(gson.toJson(logic.getById(id)));
                    break;
                }

                // ------------------------------------
                case "eliminar": {
                    int id = Integer.parseInt(req.getParameter("id"));
                    logic.delete(id);
                    resp.getWriter().write("{\"status\":\"ok\"}");
                    break;
                }

                // ------------------------------------
                case "horarios": {
                    int idSalon = Integer.parseInt(req.getParameter("idSalon"));
                    LocalDate fecha = LocalDate.parse(req.getParameter("fecha"));

                    // corregido: se devuelven horarios OCCUPADOS
                    List<Rango> ocupados = obtenerHorariosOcupados(idSalon, fecha);

                    resp.getWriter().write(
                        "{\"status\":\"ok\",\"ocupados\":" + gson.toJson(ocupados) + "}"
                    );

                    break;
                }

                // ------------------------------------
                default:
                    resp.getWriter().write(
                        "{\"error\":\"Acción GET no válida: " + action + "\"}"
                    );
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ============================================================
    // CLASE para devolver horarios
    // ============================================================
    private static class Rango {
        String horaDesde;
        String horaHasta;

        Rango(String d, String h) {
            this.horaDesde = d;
            this.horaHasta = h;
        }
    }

    // ============================================================
    //   Obtener horarios OCUPADOS
    // ============================================================
    private List<Rango> obtenerHorariosOcupados(int salon, LocalDate fecha) throws Exception {

        LinkedList<Alquiler_salon> reservas =
            logic.getAll()
                .stream()
                .filter(a -> a.getIdSalon() == salon && a.getFecha().equals(fecha))
                .collect(java.util.stream.Collectors.toCollection(LinkedList::new));

        List<Rango> ocupados = new ArrayList<>();

        reservas.forEach(r -> {
            String desde = r.getHoraDesde().toString().substring(0, 5);
            String hasta = r.getHoraHasta().toString().substring(0, 5);

            ocupados.add(new Rango(desde, hasta));
        });

        return ocupados;
    }

    // ============================================================
    // POST
    // ============================================================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");
        String action = req.getParameter("action");

        try {

            if ("crear".equals(action)) {

                Alquiler_salon a = new Alquiler_salon();

                a.setIdSalon(Integer.parseInt(req.getParameter("id_salon")));
                a.setIdUsuario(Integer.parseInt(req.getParameter("id_usuario")));
                a.setFecha(LocalDate.parse(req.getParameter("fecha")));
                a.setHoraDesde(LocalTime.parse(req.getParameter("hora_desde")));
                a.setHoraHasta(LocalTime.parse(req.getParameter("hora_hasta")));

                logic.add(a);

                resp.getWriter().write(
                    "{\"status\":\"ok\",\"alquiler\":" + gson.toJson(a) + "}"
                );
                return;
            }

            resp.getWriter().write("{\"error\":\"Acción POST no válida\"}");

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}

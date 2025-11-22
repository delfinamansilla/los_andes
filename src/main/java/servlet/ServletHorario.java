package servlet;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import entities.Horario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logic.LogicHorario;

@WebServlet({"/horario", "/Horario", "/HORARIO"})
public class ServletHorario extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private LogicHorario logicHorario;
    private Gson gson;

    public ServletHorario() {
        super();
        logicHorario = new LogicHorario();

        // Configuración de Gson con soporte para LocalTime
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalTime.class,
                        (com.google.gson.JsonSerializer<LocalTime>)
                                (src, typeOfSrc, context) ->
                                        new com.google.gson.JsonPrimitive(src.toString()))
                .setPrettyPrinting()
                .create();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        String action = request.getParameter("action");

        try {
            if (action == null) {
                response.getWriter().write("{\"error\":\"Debe especificar una acción (listar, buscar o eliminar).\"}");
                return;
            }

            switch (action.toLowerCase()) {

                case "listar": {
                    List<Horario> horarios = logicHorario.getAll();
                    String json = gson.toJson(horarios);
                    response.getWriter().write(json);
                    break;
                }

                case "buscar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Horario h = logicHorario.getById(id);

                    if (h != null) {
                        String json = gson.toJson(h);
                        response.getWriter().write(json);
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().write("{\"error\":\"No se encontró el horario con ID " + id + "\"}");
                    }
                    break;
                }
                
                case "buscar_por_actividad": {
                    int idActividad = Integer.parseInt(request.getParameter("id_actividad"));
                    List<Horario> horarios = logicHorario.getByActividad(idActividad);

                    if (horarios != null && !horarios.isEmpty()) {
                        String json = gson.toJson(horarios);
                        response.getWriter().write(json);
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().write("{\"error\":\"No se encontraron horarios para la actividad con ID " + idActividad + "\"}");
                    }
                    break;
                }


                case "eliminar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    logicHorario.delete(id);
                    response.getWriter().write("{\"status\":\"ok\", \"mensaje\":\"Horario eliminado correctamente.\"}");
                    break;
                }
                
                //ACA EMPIEZA LO Q AGREGUE
                case "ocupados_profesor": {
                    int idProfesor = Integer.parseInt(request.getParameter("id_profesor"));
                    List<Horario> horarios = logicHorario.getOcupadosProfesor(idProfesor);
                    response.getWriter().write(gson.toJson(horarios));
                    break;
                }

                case "ocupados_cancha": {
                    int idCancha = Integer.parseInt(request.getParameter("id_cancha"));
                    List<Horario> horarios = logicHorario.getOcupadosCancha(idCancha);
                    response.getWriter().write(gson.toJson(horarios));
                    break;
                }
                
                //ACA TERMINA

                default:
                    response.getWriter().write("{\"error\":\"Acción GET no reconocida: " + action + "\"}");
            }

        } catch (NumberFormatException nfe) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"El parámetro ID debe ser un número válido.\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error al procesar la solicitud: " + e.getMessage() + "\"}");
        }
    }

    // -------------------------------
    // MÉTODOS POST → Agregar o Actualizar
    // -------------------------------
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        String action = request.getParameter("action");

        try {
            if (action == null) {
                response.getWriter().write("{\"error\":\"Debe especificar una acción (agregar o actualizar).\"}");
                return;
            }

            switch (action.toLowerCase()) {

                case "agregar": {
                    Horario h = new Horario();
                    h.setDia(request.getParameter("dia"));
                    h.setHoraDesde(LocalTime.parse(request.getParameter("hora_desde")));
                    h.setHoraHasta(LocalTime.parse(request.getParameter("hora_hasta")));
                    h.setIdActividad(Integer.parseInt(request.getParameter("id_actividad")));

                    logicHorario.add(h);

                    String json = gson.toJson(h);
                    response.setStatus(HttpServletResponse.SC_CREATED);
                    response.getWriter().write("{\"status\":\"ok\", \"mensaje\":\"Horario agregado correctamente\", \"horario\":" + json + "}");
                    break;
                }

                case "actualizar": {
                    com.google.gson.JsonObject json = (com.google.gson.JsonObject) request.getAttribute("jsonBody");
                    if (json == null) {
                        json = gson.fromJson(request.getReader(), com.google.gson.JsonObject.class);
                    }

                    System.out.println("📦 JSON recibido para actualizar horario: " + json);
                    
                    Horario h = new Horario();
                    h.setId(json.get("id").getAsInt());
                    h.setDia(json.get("dia").getAsString());
                    h.setHoraDesde(LocalTime.parse(json.get("hora_desde").getAsString()));
                    h.setHoraHasta(LocalTime.parse(json.get("hora_hasta").getAsString()));
                    h.setIdActividad(json.get("id_actividad").getAsInt());

                    logicHorario.update(h);

                    String result = gson.toJson(h);
                    response.getWriter().write("{\"status\":\"ok\", \"mensaje\":\"Horario actualizado correctamente\", \"horario\":" + result + "}");
                    break;
                }

                default:
                    response.getWriter().write("{\"error\":\"Acción POST no reconocida: " + action + "\"}");
            }

        } catch (NumberFormatException nfe) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Uno de los parámetros numéricos no es válido.\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error al procesar el horario: " + e.getMessage() + "\"}");
        }
    }
}
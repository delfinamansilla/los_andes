package servlet;

import java.io.IOException;

import java.time.LocalDate;
import java.util.LinkedList;

import entities.Inscripcion;
import logic.LogicInscripcion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Map;

/**
 * Servlet para gestionar las operaciones CRUD de Inscripcion.
 * Soporta listar, buscar, crear, actualizar y eliminar inscripciones.
 */
@WebServlet({"/inscripcion", "/Inscripcion", "/INSCRIPCION"})
public class ServletInscripcion extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private LogicInscripcion logicInscripcion;
    private Gson gson;

    public ServletInscripcion() {
        super();
        logicInscripcion = new LogicInscripcion();
        
        // ✅ Registrá el adaptador para LocalDate
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
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        String action = request.getParameter("action");
        response.setContentType("application/json;charset=UTF-8");
        
        try {
            if (action == null) {
                response.getWriter().write("{\"error\":\"Debe especificar una acción (listar, buscar, eliminar).\"}");
                return;
            }
            
            // ✅ Limpiá espacios y convertí a minúsculas
            action = action.trim().toLowerCase();
            
            System.out.println("🔹 Acción procesada: '" + action + "'");

            switch (action) {
            
                case "listar": {
                    LinkedList<Inscripcion> inscripciones = new LinkedList<>(logicInscripcion.getAll());
                    response.getWriter().write(gson.toJson(inscripciones));
                    break;
                }

                case "buscar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Inscripcion i = logicInscripcion.getOne(id);
                    if (i != null) {
                        response.getWriter().write(gson.toJson(i));
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().write("{\"error\":\"No se encontró la inscripción con ID " + id + "\"}");
                    }
                    break;
                }
                
                case "porusuario": {
                    System.out.println("✅ Entró al case porusuario");

                    String idParam = request.getParameter("id_usuario");
                    System.out.println("🔹 Parámetro id_usuario recibido: " + idParam);

                    if (idParam == null || idParam.isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"error\":\"Falta el parámetro id_usuario\"}");
                        return;
                    }

                    int idUsuario = Integer.parseInt(idParam);
                    System.out.println("🔹 Buscando inscripciones para usuario: " + idUsuario);

                    // ✅ Usá el nuevo método que trae los detalles
                    LinkedList<Map<String, Object>> inscripciones = logicInscripcion.getInscripcionesConDetalles(idUsuario);
                    System.out.println("🔹 Inscripciones encontradas: " + inscripciones.size());

                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");

                    gson.toJson(inscripciones, response.getWriter());
                    break;
                }

                case "eliminar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    logicInscripcion.delete(id);
                    response.getWriter().write("{\"mensaje\":\"Inscripción eliminada correctamente\"}");
                    break;
                }

                default:
                    System.out.println("❌ Acción no reconocida: '" + action + "'");
                    response.getWriter().write("{\"error\":\"Acción GET no reconocida: " + action + "\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error al procesar GET: " + e.getMessage() + "\"}");
        }
    }

    // -------------------------------------------------
    // MÉTODO POST → crear o actualizar inscripción
    // -------------------------------------------------
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	String action = request.getParameter("action");
        response.setContentType("application/json;charset=UTF-8");

        try {
        	if (action == null) {
                response.getWriter().write("{\"error\":\"Debe especificar una acción en el parámetro 'action'.\"}");
                return;
            }


            switch (action.toLowerCase()) {
                case "crear": {
                    Inscripcion nueva = new Inscripcion();
                    nueva.setFechaInscripcion(LocalDate.parse(request.getParameter("fecha_inscripcion")));
                    nueva.setIdUsuario(Integer.parseInt(request.getParameter("id_usuario")));
                    nueva.setIdActividad(Integer.parseInt(request.getParameter("id_actividad")));

                    logicInscripcion.add(nueva);
                    response.getWriter().write("{\"mensaje\":\"Inscripción creada correctamente\"}");
                    break;
                }

                case "actualizar": {
                    Inscripcion i = new Inscripcion();
                    i.setIdInscripcion(Integer.parseInt(request.getParameter("id")));
                    i.setFechaInscripcion(LocalDate.parse(request.getParameter("fecha_inscripcion")));
                    i.setIdUsuario(Integer.parseInt(request.getParameter("id_usuario")));
                    i.setIdActividad(Integer.parseInt(request.getParameter("id_actividad")));

                    logicInscripcion.update(i);
                    response.getWriter().write("{\"mensaje\":\"Inscripción actualizada correctamente\"}");
                    break;
                }

                default:
                	response.getWriter().write("{\"error\":\"Acción POST no reconocida: " + action + "\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error al procesar POST: " + e.getMessage() + "\"}");
        }
    }
}
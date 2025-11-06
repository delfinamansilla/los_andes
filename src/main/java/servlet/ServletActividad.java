package servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import entities.Actividad;
import logic.LogicActividad;
import java.util.Map;

/**
 * Servlet para gestionar las operaciones CRUD de Actividad.
 * Soporta: listar, buscar, crear, actualizar y eliminar.
 */
@WebServlet({"/actividad", "/Actividad", "/ACTIVIDAD"})
public class ServletActividad extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private LogicActividad logicActividad;

    public ServletActividad() {
        super();
        logicActividad = new LogicActividad();
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String format = request.getParameter("format");
        
        com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
                .registerTypeAdapter(java.time.LocalDate.class,
                        (com.google.gson.JsonSerializer<java.time.LocalDate>)
                        (src, typeOfSrc, context) -> new com.google.gson.JsonPrimitive(src.toString()))
                .setPrettyPrinting()
                .create();

        try {
            if (action == null) {
                response.getWriter().append("Debe especificar una acción (listar, buscar o eliminar).");
                return;
            }

            switch (action.toLowerCase()) {
                case "listar": {
                    LinkedList<Actividad> actividades = new LinkedList<>(logicActividad.getAll());
                    if ("json".equalsIgnoreCase(format)) {
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write(gson.toJson(actividades));
                    } else {
                       
                        request.setAttribute("listaActividades", actividades);
                        request.getRequestDispatcher("WEB-INF/listaActividades.jsp").forward(request, response);
                    }
                    break;
                }
                
                case "listarcondetalles": {
                    String idUsuarioStr = request.getParameter("id_usuario");

                    if (idUsuarioStr == null || idUsuarioStr.trim().isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"error\":\"Falta el parámetro id_usuario\"}");
                        return; 
                    }

                    try {
                        int idUsuario = Integer.parseInt(idUsuarioStr);
                        
                        LinkedList<Map<String, Object>> actividades = logicActividad.getActividadesConDetalles(idUsuario); // <-- ¡AQUÍ ESTÁ LA CORRECCIÓN!
                        
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write(gson.toJson(actividades));

                    } catch (NumberFormatException e) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"error\":\"El parámetro id_usuario debe ser un número entero.\"}");
                    }
                    
                    break;
                }

                case "buscar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Actividad a = logicActividad.getOne(id);
                    if ("json".equalsIgnoreCase(format)) {
                        response.setContentType("application/json;charset=UTF-8");
                        if (a != null) {
                            response.getWriter().write(gson.toJson(a));
                        } else {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            response.getWriter().write("{\"error\":\"Actividad no encontrada\"}");
                        }
                    } else {
                        if (a != null) {
                            request.setAttribute("actividad", a);
                            request.getRequestDispatcher("WEB-INF/detalleActividad.jsp").forward(request, response);
                        } else {
                            request.setAttribute("error", "No se encontró la actividad con ID " + id);
                            request.getRequestDispatcher("error.jsp").forward(request, response);
                        }
                    }
                    break;
                }

                case "eliminar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    logicActividad.delete(id);
                    if ("json".equalsIgnoreCase(format)) {
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write("{\"status\":\"ok\",\"message\":\"Actividad eliminada correctamente\"}");
                    } else {
                        response.sendRedirect("actividad?action=listar");
                    }
                    break;
                }

                default:
                    response.getWriter().append("Acción GET no reconocida: ").append(action);
            }

        } catch (Exception e) {
            e.printStackTrace();
            if ("json".equalsIgnoreCase(format)) {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\":\"Error al procesar: " + e.getMessage() + "\"}");
            } else {
                request.setAttribute("error", "Error al procesar: " + e.getMessage());
                request.getRequestDispatcher("error.jsp").forward(request, response);
            }
        }
    }

    // -------------------------------------
    // MÉTODOS POST → Crear o Actualizar
    // -------------------------------------
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String format = request.getParameter("format");
        com.google.gson.Gson gson = new com.google.gson.GsonBuilder().create();

        try {
            if (action == null) {
                response.getWriter().append("Debe especificar una acción en el parámetro 'action'.");
                return;
            }

            switch (action.toLowerCase()) {
                case "crear": {
                    Actividad nueva = new Actividad();
                    nueva.setNombre(request.getParameter("nombre"));
                    nueva.setDescripcion(request.getParameter("descripcion"));
                    nueva.setCupo(Integer.parseInt(request.getParameter("cupo")));
                    nueva.setInscripcionDesde(LocalDate.parse(request.getParameter("inscripcion_desde")));
                    nueva.setInscripcionHasta(LocalDate.parse(request.getParameter("inscripcion_hasta")));
                    nueva.setIdProfesor(Integer.parseInt(request.getParameter("id_profesor")));
                    nueva.setIdCancha(Integer.parseInt(request.getParameter("id_cancha")));

                    logicActividad.add(nueva);
                    if ("json".equalsIgnoreCase(format)) {
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write("{\"status\":\"ok\",\"message\":\"Actividad creada correctamente\"}");
                    } else {
                        request.setAttribute("mensaje", "Actividad creada correctamente.");
                        request.getRequestDispatcher("WEB-INF/listaActividades.jsp").forward(request, response);
                    }
                    break;
                }

                case "actualizar": {
                    Actividad a = new Actividad();
                    a.setIdActividad(Integer.parseInt(request.getParameter("id")));
                    a.setNombre(request.getParameter("nombre"));
                    a.setDescripcion(request.getParameter("descripcion"));
                    a.setCupo(Integer.parseInt(request.getParameter("cupo")));
                    a.setInscripcionDesde(LocalDate.parse(request.getParameter("inscripcion_desde")));
                    a.setInscripcionHasta(LocalDate.parse(request.getParameter("inscripcion_hasta")));
                    a.setIdProfesor(Integer.parseInt(request.getParameter("id_profesor")));
                    a.setIdCancha(Integer.parseInt(request.getParameter("id_cancha")));

                    logicActividad.update(a);
                    if ("json".equalsIgnoreCase(format)) {
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write("{\"status\":\"ok\",\"message\":\"Actividad actualizada correctamente\"}");
                    } else {
                        response.sendRedirect("actividad?action=listar");
                    }
                    break;
                }

                default:
                    response.getWriter().append("Acción POST no reconocida: ").append(action);
            }

        } catch (Exception e) {
            e.printStackTrace();
            if ("json".equalsIgnoreCase(format)) {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\":\"Error al procesar la actividad: " + e.getMessage() + "\"}");
            } else {
                request.setAttribute("error", "Error al procesar la actividad: " + e.getMessage());
                request.getRequestDispatcher("error.jsp").forward(request, response);
            }
        }
    }
}
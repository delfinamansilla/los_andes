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

    // -------------------------------------
    // MÉTODOS GET → Listar, Buscar, Eliminar
    // -------------------------------------
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if (action == null) {
                response.getWriter().append("Debe especificar una acción (listar, buscar o eliminar).");
                return;
            }

            switch (action.toLowerCase()) {
                case "listar": {
                    LinkedList<Actividad> actividades = new LinkedList<>(logicActividad.getAll());
                    request.setAttribute("listaActividades", actividades);
                    request.getRequestDispatcher("WEB-INF/listaActividades.jsp").forward(request, response);
                    break;
                }

                case "buscar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Actividad a = logicActividad.getOne(id);
                    if (a != null) {
                        request.setAttribute("actividad", a);
                        request.getRequestDispatcher("WEB-INF/detalleActividad.jsp").forward(request, response);
                    } else {
                        request.setAttribute("error", "No se encontró la actividad con ID " + id);
                        request.getRequestDispatcher("error.jsp").forward(request, response);
                    }
                    break;
                }

                case "eliminar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    logicActividad.delete(id);
                    response.sendRedirect("actividad?action=listar");
                    break;
                }

                default:
                    response.getWriter().append("Acción GET no reconocida: ").append(action);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    // -------------------------------------
    // MÉTODOS POST → Crear o Actualizar
    // -------------------------------------
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

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
                    request.setAttribute("mensaje", "Actividad creada correctamente.");
                    request.getRequestDispatcher("WEB-INF/listaActividades.jsp").forward(request, response);
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
                    response.sendRedirect("actividad?action=listar");
                    break;
                }

                default:
                    response.getWriter().append("Acción POST no reconocida: ").append(action);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar la actividad: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
}

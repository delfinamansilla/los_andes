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

/**
 * Servlet para gestionar las operaciones CRUD de Inscripcion.
 * Soporta listar, buscar, crear, actualizar y eliminar inscripciones.
 */
@WebServlet({"/inscripcion", "/Inscripcion", "/INSCRIPCION"})
public class ServletInscripcion extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private LogicInscripcion logicInscripcion;

    public ServletInscripcion() {
        super();
        logicInscripcion = new LogicInscripcion();
    }

    // -------------------------------------------------
    // MÉTODO GET → listar, buscar o eliminar inscripciones
    // -------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if (action == null) {
                response.getWriter().append("Debe especificar una acción (listar, buscar, eliminar).");
                return;
            }

            switch (action.toLowerCase()) {
                case "listar": {
                    LinkedList<Inscripcion> inscripciones = new LinkedList<>(logicInscripcion.getAll());
                    request.setAttribute("listaInscripciones", inscripciones);
                    request.getRequestDispatcher("WEB-INF/listaInscripciones.jsp").forward(request, response);
                    break;
                }

                case "buscar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Inscripcion i = logicInscripcion.getOne(id);
                    if (i != null) {
                        request.setAttribute("inscripcion", i);
                        request.getRequestDispatcher("WEB-INF/detalleInscripcion.jsp").forward(request, response);
                    } else {
                        request.setAttribute("error", "No se encontró la inscripción con ID " + id);
                        request.getRequestDispatcher("error.jsp").forward(request, response);
                    }
                    break;
                }

                case "eliminar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    logicInscripcion.delete(id);
                    response.sendRedirect("inscripcion?action=listar");
                    break;
                }

                default:
                    response.getWriter().append("Acción GET no reconocida: ").append(action);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar GET: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    // -------------------------------------------------
    // MÉTODO POST → crear o actualizar inscripción
    // -------------------------------------------------
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
                    Inscripcion nueva = new Inscripcion();
                    nueva.setFechaInscripcion(LocalDate.parse(request.getParameter("fecha_inscripcion")));
                    nueva.setIdUsuario(Integer.parseInt(request.getParameter("id_usuario")));
                    nueva.setIdActividad(Integer.parseInt(request.getParameter("id_actividad")));

                    logicInscripcion.add(nueva);
                    request.setAttribute("mensaje", "Inscripción creada correctamente.");
                    request.getRequestDispatcher("WEB-INF/listaInscripciones.jsp").forward(request, response);
                    break;
                }

                case "actualizar": {
                    Inscripcion i = new Inscripcion();
                    i.setIdInscripcion(Integer.parseInt(request.getParameter("id")));
                    i.setFechaInscripcion(LocalDate.parse(request.getParameter("fecha_inscripcion")));
                    i.setIdUsuario(Integer.parseInt(request.getParameter("id_usuario")));
                    i.setIdActividad(Integer.parseInt(request.getParameter("id_actividad")));

                    logicInscripcion.update(i);
                    response.sendRedirect("inscripcion?action=listar");
                    break;
                }

                default:
                    response.getWriter().append("Acción POST no reconocida: ").append(action);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar POST: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
}


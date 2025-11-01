package servlet;

import java.io.IOException;
import java.time.LocalTime;
import java.util.LinkedList;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import entities.Horario;
import logic.LogicHorario;

/**
 * Servlet para gestionar las operaciones CRUD de Horario.
 * Acciones posibles: listar, buscar, agregar, actualizar, eliminar.
 */
@WebServlet({"/horario", "/Horario", "/HORARIO"})
public class ServletHorario extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private LogicHorario logicHorario;

    public ServletHorario() {
        super();
        logicHorario = new LogicHorario();
    }

    /**
     * Maneja peticiones GET:
     *  - listar: muestra todos los horarios
     *  - buscar: obtiene un horario por ID
     *  - eliminar: elimina un horario por ID
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        response.setContentType("text/html;charset=UTF-8");

        try {
            if (action == null) {
                response.getWriter().append("Debe especificar una acción en el parámetro 'action'.");
                return;
            }

            switch (action.toLowerCase()) {

                case "listar": {
                    LinkedList<Horario> horarios = logicHorario.getAll();
                    request.setAttribute("listaHorarios", horarios);
                    request.getRequestDispatcher("WEB-INF/listaHorarios.jsp").forward(request, response);
                    break;
                }

                case "buscar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Horario h = logicHorario.getById(id);
                    request.setAttribute("horario", h);
                    request.getRequestDispatcher("WEB-INF/detalleHorario.jsp").forward(request, response);
                    break;
                }

                case "eliminar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    logicHorario.delete(id);
                    response.sendRedirect("horario?action=listar");
                    break;
                }

                default:
                    response.getWriter().append("Acción GET no reconocida: ").append(action);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error en Horario: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    /**
     * Maneja peticiones POST:
     *  - agregar: crea un nuevo horario
     *  - actualizar: modifica un horario existente
     */
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

                case "agregar": {
                    Horario h = new Horario();
                    h.setDia(request.getParameter("dia"));
                    h.setHoraDesde(LocalTime.parse(request.getParameter("hora_desde")));
                    h.setHoraHasta(LocalTime.parse(request.getParameter("hora_hasta")));
                    h.setIdActividad(Integer.parseInt(request.getParameter("id_actividad")));

                    logicHorario.add(h);

                    request.setAttribute("mensaje", "Horario agregado correctamente.");
                    response.sendRedirect("horario?action=listar");
                    break;
                }

                case "actualizar": {
                    Horario h = new Horario();
                    h.setId(Integer.parseInt(request.getParameter("id")));
                    h.setDia(request.getParameter("dia"));
                    h.setHoraDesde(LocalTime.parse(request.getParameter("hora_desde")));
                    h.setHoraHasta(LocalTime.parse(request.getParameter("hora_hasta")));
                    h.setIdActividad(Integer.parseInt(request.getParameter("id_actividad")));

                    logicHorario.update(h);

                    response.sendRedirect("horario?action=listar");
                    break;
                }

                default:
                    response.getWriter().append("Acción POST no reconocida: ").append(action);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar horario: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
}


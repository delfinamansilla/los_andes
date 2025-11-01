package servlet;

import java.io.IOException;
import java.util.LinkedList;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import entities.Cancha;
import logic.LogicCancha;

@WebServlet({"/cancha", "/Cancha", "/CANCHA"})
public class ServletCancha extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private LogicCancha logicCancha;

    public ServletCancha() {
        super();
        logicCancha = new LogicCancha();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        response.getWriter().println("Servlet Cancha activo y corriendo.");
    }
    /*
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if (action == null) {
                response.getWriter().append("Debe especificar una acción en el parámetro 'action'.");
                return;
            }

            switch (action.toLowerCase()) {
                case "listar": {
                    LinkedList<Cancha> canchas = logicCancha.getAll();
                    request.setAttribute("listaCanchas", canchas);
                    request.getRequestDispatcher("WEB-INF/listaCanchas.jsp").forward(request, response);
                    break;
                }
                case "buscar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Cancha c = logicCancha.getOne(id);
                    request.setAttribute("cancha", c);
                    request.getRequestDispatcher("WEB-INF/detalleCancha.jsp").forward(request, response);
                    break;
                }
                case "eliminar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    logicCancha.delete(id);
                    response.sendRedirect("cancha?action=listar");
                    break;
                }
                default:
                    response.getWriter().append("Acción no reconocida en GET: ").append(action);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    } */

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
                case "registrar": {
                    Cancha nueva = new Cancha();
                    nueva.setNro_cancha(Integer.parseInt(request.getParameter("nro_cancha")));
                    nueva.setUbicacion(request.getParameter("ubicacion"));
                    nueva.setDescripcion(request.getParameter("descripcion"));
                    nueva.setTamanio(Float.parseFloat(request.getParameter("tamanio")));
                    nueva.setEstado(Boolean.parseBoolean(request.getParameter("estado")));

                    logicCancha.add(nueva);
                    request.setAttribute("mensaje", "Cancha registrada correctamente.");
                    request.getRequestDispatcher("WEB-INF/listaCanchas.jsp").forward(request, response);
                    break;
                }
                case "actualizar": {
                    Cancha c = new Cancha();
                    c.setId(Integer.parseInt(request.getParameter("id")));
                    c.setNro_cancha(Integer.parseInt(request.getParameter("nro_cancha")));
                    c.setUbicacion(request.getParameter("ubicacion"));
                    c.setDescripcion(request.getParameter("descripcion"));
                    c.setTamanio(Float.parseFloat(request.getParameter("tamanio")));
                    c.setEstado(Boolean.parseBoolean(request.getParameter("estado")));

                    logicCancha.update(c);
                    response.sendRedirect("cancha?action=listar");
                    break;
                }
                default:
                    response.getWriter().append("Acción POST no reconocida: ").append(action);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
}


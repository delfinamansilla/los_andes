package servlet;

import java.io.IOException;
import java.util.LinkedList;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import entities.Profesor;
import logic.LogicProfesor;

/**
 * Servlet para gestionar las operaciones CRUD de Profesor.
 * Acciones disponibles:
 *  - listar
 *  - buscar
 *  - agregar
 *  - actualizar
 *  - eliminar
 */
@WebServlet({"/profesor", "/Profesor", "/PROFESOR"})
public class ServletProfesor extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private LogicProfesor logicProfesor;

    public ServletProfesor() {
        super();
        logicProfesor = new LogicProfesor();
    }

    /**
     * Maneja peticiones GET:
     *  - listar: muestra todos los profesores
     *  - buscar: obtiene un profesor por ID
     *  - eliminar: elimina un profesor por ID
     */
    @Override
    
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
 	   response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
       response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
       response.setHeader("Access-Control-Allow-Headers", "Content-Type");
       

        String action = request.getParameter("action");
        response.setContentType("text/html;charset=UTF-8");

        try {
            if (action == null) {
                response.getWriter().append("Debe especificar una acción en el parámetro 'action'.");
                return;
            }

            switch (action.toLowerCase()) {

                case "listar": {
                    LinkedList<Profesor> profesores = logicProfesor.getAll();
                    request.setAttribute("listaProfesores", profesores);
                    request.getRequestDispatcher("WEB-INF/listaProfesores.jsp").forward(request, response);
                    break;
                }

                case "buscar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Profesor p = logicProfesor.getOne(id);
                    request.setAttribute("profesor", p);
                    request.getRequestDispatcher("WEB-INF/detalleProfesor.jsp").forward(request, response);
                    break;
                }

                case "eliminar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    logicProfesor.delete(id);
                    response.sendRedirect("profesor?action=listar");
                    break;
                }

                default:
                    response.getWriter().append("Acción GET no reconocida: ").append(action);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error en Profesor: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    /**
     * Maneja peticiones POST:
     *  - agregar: crea un nuevo profesor
     *  - actualizar: modifica un profesor existente
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
 	   response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
       response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
       response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        System.out.println("\n--- SERVLET PROFESOR: Petición POST recibida ---");


        String action = request.getParameter("action");

        try {
            if (action == null) {
                response.getWriter().append("Debe especificar una acción en el parámetro 'action'.");
                return;
            }

            switch (action.toLowerCase()) {

            case "agregar": {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                try {
                    Profesor p = new Profesor();
                    p.setNombreCompleto(request.getParameter("nombre_completo"));
                    p.setTelefono(request.getParameter("telefono"));
                    p.setMail(request.getParameter("mail"));

                    logicProfesor.add(p);
                    
                    // ✅ SOLUCIÓN: Envía una respuesta JSON de éxito
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("{\"status\":\"ok\", \"message\":\"¡Profesor agregado exitosamente!\"}");

                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");
                }
                break;
            }


                case "actualizar": {
                    Profesor p = new Profesor();
                    p.setIdProfesor(Integer.parseInt(request.getParameter("id")));
                    p.setNombreCompleto(request.getParameter("nombre_completo"));
                    p.setTelefono(request.getParameter("telefono"));
                    p.setMail(request.getParameter("mail"));

                    logicProfesor.update(p);

                    response.sendRedirect("profesor?action=listar");
                    break;
                }

                default:
                    response.getWriter().append("Acción POST no reconocida: ").append(action);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar profesor: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
}


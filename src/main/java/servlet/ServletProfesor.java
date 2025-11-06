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
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");


        try {
            if (action == null) {
                response.getWriter().append("Debe especificar una acción en el parámetro 'action'.");
                return;
            }

            switch (action.toLowerCase()) {

            case "listar": {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                try {
                    LinkedList<Profesor> profesores = logicProfesor.getAll();
                    
                    // Necesitas una librería como Gson para convertir la lista a JSON
                    // Si no la tienes, añádela a tu pom.xml o a tus librerías
                    com.google.gson.Gson gson = new com.google.gson.Gson();
                    String jsonProfesores = gson.toJson(profesores);
                    
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write(jsonProfesores);
                    
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"error\":\"Error al obtener la lista de profesores: " + e.getMessage() + "\"}");
                }
                break;
            }

            case "buscar": {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                try {
                	String idParam = request.getParameter("id");
                    // AÑADE ESTA LÍNEA PARA VER QUÉ ID LLEGA
                    System.out.println("--- BUSCANDO PROFESOR CON ID: " + idParam + " ---");
                    int id = Integer.parseInt(request.getParameter("id"));
                    Profesor p = logicProfesor.getOne(id);
                   
                    
                    // AÑADE ESTA LÍNEA PARA VER QUÉ ENCONTRÓ
                    System.out.println("--- RESULTADO DE LA BÚSQUEDA: " + p + " ---");
                    if (p != null) {
                        // Usamos Gson para convertir el objeto a JSON
                        com.google.gson.Gson gson = new com.google.gson.Gson();
                        String jsonProfesor = gson.toJson(p);
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write(jsonProfesor);
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().write("{\"error\":\"Profesor no encontrado\"}");
                    }
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
                }
                break;
            }

            case "eliminar": {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                try {
                    // Obtenemos el ID del profesor a eliminar
                    int id = Integer.parseInt(request.getParameter("id"));
                    
                    // Llamamos a la lógica de negocio para que lo borre de la BD
                    logicProfesor.delete(id);

                    // Si no hubo errores, enviamos una respuesta de éxito
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("{\"status\":\"ok\", \"message\":\"Profesor eliminado correctamente\"}");

                } catch (Exception e) {
                    // Si algo falla (ej: el ID no existe, error de BD), enviamos un error
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");
                }
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
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                try {
                    Profesor p = new Profesor();
                    p.setIdProfesor(Integer.parseInt(request.getParameter("id")));
                    p.setNombreCompleto(request.getParameter("nombre_completo"));
                    p.setTelefono(request.getParameter("telefono"));
                    p.setMail(request.getParameter("mail"));

                    logicProfesor.update(p);

                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("{\"status\":\"ok\", \"message\":\"Profesor actualizado correctamente\"}");

                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");
                }
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
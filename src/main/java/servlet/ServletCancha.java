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

@WebServlet("/cancha")
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
    	response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        String action = request.getParameter("action");

        try {
            if (action == null) {
                response.getWriter().append("Debe especificar una acción en el parámetro 'action'.");
                return;
            }

            switch (action.toLowerCase()) {
                case "listar": {
                	LinkedList<Cancha> canchas = logicCancha.getAll();

                	response.setContentType("application/json");
                	response.setCharacterEncoding("UTF-8");

                	StringBuilder sb = new StringBuilder();
                	sb.append("[");
                	for (int i = 0; i < canchas.size(); i++) {
                	    Cancha c = canchas.get(i);
                	    sb.append("{");
                	    sb.append("\"id\":").append(c.getId()).append(",");
                	    sb.append("\"nro_cancha\":").append(c.getNro_cancha()).append(",");
                	    sb.append("\"ubicacion\":\"").append(c.getUbicacion()).append("\",");
                	    sb.append("\"descripcion\":\"").append(c.getDescripcion()).append("\",");
                	    sb.append("\"tamanio\":").append(c.getTamanio()).append(",");
                	    sb.append("\"estado\":").append(c.isEstado());
                	    sb.append("}");
                	    if (i < canchas.size() - 1) sb.append(",");
                	}
                	sb.append("]");

                	response.getWriter().write(sb.toString());

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
    } 
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
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

                // ✅ Responder con JSON para React
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"status\":\"ok\"}");
                break;
            }

                case "buscar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Cancha c = logicCancha.getOne(id);

                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    String json = String.format(
                        "{\"id\":%d,\"nro_cancha\":%d,\"ubicacion\":\"%s\",\"descripcion\":\"%s\",\"tamanio\":%f,\"estado\":%b}",
                        c.getId(), c.getNro_cancha(), c.getUbicacion(), c.getDescripcion(), c.getTamanio(), c.isEstado()
                    );
                    response.getWriter().write(json);
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

                    // Responder con éxito en JSON para React
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"status\":\"ok\"}");
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


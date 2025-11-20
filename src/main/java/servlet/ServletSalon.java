package servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import entities.Salon;
import logic.LogicSalon;

@WebServlet("/salon")
@MultipartConfig(maxFileSize = 2 * 1024 * 1024) // 2MB igual que tu validación
public class ServletSalon extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private LogicSalon logicSalon;

    public ServletSalon() {
        super();
        logicSalon = new LogicSalon();
    }

    private void setCORS(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        setCORS(res);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setCORS(response);

        String action = request.getParameter("action");

        try {
            if (action == null) {
                response.getWriter().append("Debe especificar un parámetro 'action'.");
                return;
            }

            switch (action.toLowerCase()) {

            case "listar": {
                LinkedList<Salon> salones = logicSalon.getAll();

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                StringBuilder sb = new StringBuilder();
                sb.append("[");

                for (int i = 0; i < salones.size(); i++) {
                    Salon s = salones.get(i);

                    sb.append("{");
                    sb.append("\"id\":").append(s.getId()).append(",");
                    sb.append("\"nombre\":\"").append(s.getNombre()).append("\",");
                    sb.append("\"capacidad\":").append(s.getCapacidad()).append(",");
                    sb.append("\"descripcion\":\"").append(s.getDescripcion()).append("\",");
                    if (s.getImagen() != null) {
                        sb.append("\"imagen\":\"data:image/jpeg;base64,");
                        sb.append(java.util.Base64.getEncoder().encodeToString(s.getImagen()));
                        sb.append("\"");
                    } else {
                        sb.append("\"imagen\":null");
                    }

                    sb.append("}");

                    if (i < salones.size() - 1)
                        sb.append(",");
                }

                sb.append("]");
                response.getWriter().write(sb.toString());
                break;
            }

            case "buscar": {
                int id = Integer.parseInt(request.getParameter("id"));
                Salon s = logicSalon.getById(id);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                if (s != null) {
                    StringBuilder json = new StringBuilder();
                    json.append("{");
                    json.append("\"id\":").append(s.getId()).append(",");
                    json.append("\"nombre\":\"").append(s.getNombre()).append("\",");
                    json.append("\"capacidad\":").append(s.getCapacidad()).append(",");
                    json.append("\"descripcion\":\"").append(s.getDescripcion()).append("\",");

                    if (s.getImagen() != null) {
                        json.append("\"imagen\":\"data:image/jpeg;base64,");
                        json.append(java.util.Base64.getEncoder().encodeToString(s.getImagen()));
                        json.append("\"");
                    } else {
                        json.append("\"imagen\":null");
                    }

                    json.append("}");

                    response.getWriter().write(json.toString());
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\":\"Salón no encontrado\"}");
                }

                break;
            }

            case "eliminar": {
                int id = Integer.parseInt(request.getParameter("id"));
                logicSalon.delete(id);
                response.sendRedirect("salon?action=listar");
                break;
            }

            default:
                response.getWriter().append("Acción GET no reconocida: ").append(action);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setCORS(response);

        String action = request.getParameter("action");

        try {
            if (action == null) {
                response.getWriter().append("Debe especificar un parámetro 'action'.");
                return;
            }

            switch (action.toLowerCase()) {

            case "registrar": {
                Salon s = new Salon();
                s.setNombre(request.getParameter("nombre"));
                s.setCapacidad(Integer.parseInt(request.getParameter("capacidad")));
                s.setDescripcion(request.getParameter("descripcion"));

                Part imgPart = request.getPart("imagen");
                if (imgPart != null && imgPart.getSize() > 0) {
                    InputStream is = imgPart.getInputStream();
                    s.setImagen(is.readAllBytes());
                }

                logicSalon.add(s);

                response.setContentType("application/json");
                response.getWriter().write("{\"status\":\"ok\"}");
                break;
            }

            case "buscar": {
                int id = Integer.parseInt(request.getParameter("id"));
                Salon s = logicSalon.getById(id);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                if (s != null) {
                    StringBuilder json = new StringBuilder();
                    json.append("{");
                    json.append("\"id\":").append(s.getId()).append(",");
                    json.append("\"nombre\":\"").append(s.getNombre()).append("\",");
                    json.append("\"capacidad\":").append(s.getCapacidad()).append(",");
                    json.append("\"descripcion\":\"").append(s.getDescripcion()).append("\",");

                    if (s.getImagen() != null) {
                        json.append("\"imagen\":\"data:image/jpeg;base64,");
                        json.append(java.util.Base64.getEncoder().encodeToString(s.getImagen()));
                        json.append("\"");
                    } else {
                        json.append("\"imagen\":null");
                    }

                    json.append("}");
                    response.getWriter().write(json.toString());
                } else {
                    response.getWriter().write("{\"error\":\"No existe\"}");
                }

                break;
            }

            case "actualizar": {
                Salon s = new Salon();
                s.setId(Integer.parseInt(request.getParameter("id")));
                s.setNombre(request.getParameter("nombre"));
                s.setCapacidad(Integer.parseInt(request.getParameter("capacidad")));
                s.setDescripcion(request.getParameter("descripcion"));

                Part imgPart = request.getPart("imagen");
                if (imgPart != null && imgPart.getSize() > 0) {
                    InputStream is = imgPart.getInputStream();
                    s.setImagen(is.readAllBytes());
                }

                logicSalon.update(s);

                response.setContentType("application/json");
                response.getWriter().write("{\"status\":\"ok\"}");
                break;
            }

            default:
                response.getWriter().append("Acción POST no reconocida: ").append(action);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
}

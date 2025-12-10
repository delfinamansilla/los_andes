package servlet;

import java.io.BufferedReader;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.time.LocalDate;
import java.util.List;

@WebServlet({"/actividad", "/Actividad", "/ACTIVIDAD"})
public class ServletActividad extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private LogicActividad logicActividad;
    private Gson gson;

    public ServletActividad() {
        super();
        logicActividad = new LogicActividad();
        gson = new GsonBuilder()
        	    .registerTypeAdapter(LocalDate.class,
        	        (com.google.gson.JsonSerializer<LocalDate>)
        	            (src, typeOfSrc, context) -> new com.google.gson.JsonPrimitive(src.toString()))
        	    .registerTypeAdapter(LocalDate.class,
        	        (com.google.gson.JsonDeserializer<LocalDate>)
        	            (json, typeOfT, context) -> LocalDate.parse(json.getAsString()))
        	    .setPrettyPrinting()
        	    .create();

    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
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
                List<Actividad> actividades = logicActividad.getAll();
                String json = gson.toJson(actividades);
                response.getWriter().write(json);
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

                    if (a != null) {
                        String json = gson.toJson(a);
                        response.getWriter().write(json);
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().write("{\"error\":\"No se encontró la actividad con ID " + id + "\"}");
                    }
                    break;
                }


                case "eliminar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    logicActividad.delete(id);
                    response.getWriter().write("{\"status\":\"ok\", \"mensaje\":\"Actividad eliminada correctamente.\"}");
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json;charset=UTF-8");
        String action = request.getParameter("action");

        try {
            if (action == null) {
                response.getWriter().write("{\"error\":\"Debe especificar una acción (crear o actualizar).\"}");
                return;
            }

            BufferedReader reader = request.getReader();
            JsonObject body = gson.fromJson(reader, JsonObject.class);
            System.out.println("🟢 JSON recibido: " + body);


            switch (action.toLowerCase()) {
                case "crear": {
                    Actividad nueva = new Actividad();
                    nueva.setNombre(body.get("nombre").getAsString());
                    nueva.setDescripcion(body.get("descripcion").getAsString());
                    nueva.setCupo(body.get("cupo").getAsInt());
                    nueva.setInscripcionDesde(LocalDate.parse(body.get("inscripcion_desde").getAsString()));
                    nueva.setInscripcionHasta(LocalDate.parse(body.get("inscripcion_hasta").getAsString()));
                    nueva.setIdProfesor(body.get("id_profesor").getAsInt());
                    nueva.setIdCancha(body.get("id_cancha").getAsInt());

                    logicActividad.add(nueva);

                    String json = gson.toJson(nueva);
                    response.getWriter().write("{\"status\":\"ok\",\"mensaje\":\"Actividad creada correctamente\",\"actividad\":" + json + "}");
                    break;
                }

                case "actualizar": {
                    Actividad a = new Actividad();
                    a.setIdActividad(body.get("id").getAsInt());
                    a.setNombre(body.get("nombre").getAsString());
                    a.setDescripcion(body.get("descripcion").getAsString());
                    a.setCupo(body.get("cupo").getAsInt());
                    a.setInscripcionDesde(LocalDate.parse(body.get("inscripcion_desde").getAsString()));
                    a.setInscripcionHasta(LocalDate.parse(body.get("inscripcion_hasta").getAsString()));
                    a.setIdProfesor(body.get("id_profesor").getAsInt());
                    a.setIdCancha(body.get("id_cancha").getAsInt());

                    logicActividad.update(a);

                    String json = gson.toJson(a);
                    response.getWriter().write("{\"status\":\"ok\",\"mensaje\":\"Actividad actualizada correctamente\",\"actividad\":" + json + "}");
                    break;
                }

                default:
                    response.getWriter().write("{\"error\":\"Acción POST no reconocida: " + action + "\"}");
            }

        } catch (NumberFormatException nfe) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Uno de los parámetros numéricos no es válido.\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error al procesar la actividad: " + e.getMessage() + "\"}");
        }
    }

}
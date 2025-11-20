package servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.stream.Collectors;

import entities.PagoCuota;
import logic.LogicPagoCuota;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@WebServlet({"/pagocuota", "/PagoCuota"})
public class ServletPagoCuota extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private LogicPagoCuota logicPago;
    private Gson gson;

    public ServletPagoCuota() {
        super();
        logicPago = new LogicPagoCuota();
        
        // Configurar Gson para manejar LocalDate
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class,
                        (com.google.gson.JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                                new com.google.gson.JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDate.class,
                        (com.google.gson.JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                                LocalDate.parse(json.getAsString()))
                .create();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Configuración CORS
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        String action = request.getParameter("action");
        response.setContentType("application/json;charset=UTF-8");
        
        try {
            if (action == null) {
                response.getWriter().write("{\"error\":\"Debe especificar una acción.\"}");
                return;
            }

            switch (action.toLowerCase()) {
                case "listar": {
                    // Lista TODOS los pagos de TODOS los usuarios
                    LinkedList<PagoCuota> pagos = logicPago.getAll();
                    response.getWriter().write(gson.toJson(pagos));
                    break;
                }
                
                case "listar_por_usuario": {
                    // Lista SOLO los pagos de un usuario específico
                    String idParam = request.getParameter("id_usuario");
                    if (idParam == null) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"error\":\"Falta id_usuario\"}");
                        return;
                    }
                    
                    int idUsuario = Integer.parseInt(idParam);
                    LinkedList<PagoCuota> todos = logicPago.getAll();
                    
                    // Filtramos en memoria (idealmente esto iría en DataPagoCuota, pero esto funciona bien)
                    var pagosUsuario = todos.stream()
                                            .filter(p -> p.getId_usuario() == idUsuario)
                                            .collect(Collectors.toList());
                                            
                    response.getWriter().write(gson.toJson(pagosUsuario));
                    break;
                }

                default:
                    response.getWriter().write("{\"error\":\"Acción GET no reconocida\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            response.getWriter().write("{\"error\":\"Error servidor: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        String action = request.getParameter("action");
        response.setContentType("application/json;charset=UTF-8");

        try {
            if ("pagar".equalsIgnoreCase(action)) {
                int idUsuario = Integer.parseInt(request.getParameter("id_usuario"));
                int idCuota = Integer.parseInt(request.getParameter("id_cuota"));
                
                PagoCuota pago = new PagoCuota();
                pago.setId_usuario(idUsuario);
                pago.setId_cuota(idCuota);
                pago.setFecha_pago(LocalDate.now()); // Fecha de hoy

                logicPago.add(pago);
                
                response.getWriter().write("{\"mensaje\":\"Pago registrado correctamente\"}");
            } else {
                response.getWriter().write("{\"error\":\"Acción POST no reconocida\"}");
            }
            
        } catch (Exception e) {
            e.printStackTrace(); // Muestra el error en consola de Eclipse
            response.setStatus(500);
            // Aquí devolvemos el error exacto (ej: "Esta cuota ya ha sido pagada")
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
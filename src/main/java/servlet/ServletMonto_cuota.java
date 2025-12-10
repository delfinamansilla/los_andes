package servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;

import entities.Monto_cuota;
import logic.LogicMonto_cuota;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


@WebServlet({"/montocuota", "/MontoCuota", "/MONTOCUOTA"})
public class ServletMonto_cuota extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private LogicMonto_cuota logicMontoCuota;
    private Gson gson;

    public ServletMonto_cuota() {
        super();
        logicMontoCuota = new LogicMonto_cuota();
        
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
    	
    	response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        String action = request.getParameter("action");
        response.setContentType("application/json;charset=UTF-8");
        
        try {
            if (action == null) {
                response.getWriter().write("{\"error\":\"Debe especificar una acción (listar, buscarporfecha, buscarporcuota, eliminar).\"}");
                return;
            }
            
            action = action.trim().toLowerCase();

            switch (action) {
            
                case "listar": {
                    LinkedList<Monto_cuota> montos = new LinkedList<>(logicMontoCuota.getAll());
                    response.getWriter().write(gson.toJson(montos));
                    break;
                }

                case "buscarporfecha": {
                    String fechaStr = request.getParameter("fecha");
                    if (fechaStr == null || fechaStr.isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"error\":\"Falta el parámetro fecha\"}");
                        return;
                    }
                    
                    LocalDate fecha = LocalDate.parse(fechaStr);
                    Monto_cuota mc = logicMontoCuota.getByFecha(fecha);
                    if (mc != null) {
                        response.getWriter().write(gson.toJson(mc));
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().write("{\"error\":\"No se encontró monto para la fecha " + fecha + "\"}");
                    }
                    break;
                }
                
                case "buscarporcuota": {
                    int idCuota = Integer.parseInt(request.getParameter("id_cuota"));
                    LinkedList<Monto_cuota> montos = logicMontoCuota.getMontosPorCuota(idCuota);
                    
                    if (montos.isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        response.getWriter().write("{\"error\":\"No se encontraron montos para la cuota con ID " + idCuota + "\"}");
                    } else {
                        response.getWriter().write(gson.toJson(montos));
                    }
                    break;
                }

                case "eliminar": {
                    String fechaStr = request.getParameter("fecha");
                    if (fechaStr == null || fechaStr.isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"error\":\"Falta el parámetro fecha\"}");
                        return;
                    }
                    
                    LocalDate fecha = LocalDate.parse(fechaStr);
                    logicMontoCuota.delete(fecha);
                    response.getWriter().write("{\"mensaje\":\"Monto de cuota eliminado correctamente\"}");
                    break;
                }

                default:
                    response.getWriter().write("{\"error\":\"Acción GET no reconocida: " + action + "\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error al procesar GET: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

    	String action = request.getParameter("action");
        response.setContentType("application/json;charset=UTF-8");

        try {
        	if (action == null) {
                response.getWriter().write("{\"error\":\"Debe especificar una acción en el parámetro 'action'.\"}");
                return;
            }

            switch (action.toLowerCase()) {
                case "crear": {
                    double monto = Double.parseDouble(request.getParameter("monto"));
                    int idCuota = Integer.parseInt(request.getParameter("id_cuota"));

                    Monto_cuota nuevaMontoCuota = new Monto_cuota();
                    nuevaMontoCuota.setMonto(monto);
                    nuevaMontoCuota.setId_cuota(idCuota);

                    logicMontoCuota.add(nuevaMontoCuota);
                    response.getWriter().write("{\"mensaje\":\"Monto de cuota creado correctamente\"}");
                    break;
                }

                case "actualizar": {
                    String fechaStr = request.getParameter("fecha");
                    double monto = Double.parseDouble(request.getParameter("monto"));
                    int idCuota = Integer.parseInt(request.getParameter("id_cuota"));

                    Monto_cuota mc = new Monto_cuota();
                    mc.setFecha(LocalDate.parse(fechaStr));
                    mc.setMonto(monto);
                    mc.setId_cuota(idCuota);

                    logicMontoCuota.update(mc);
                    response.getWriter().write("{\"mensaje\":\"Monto de cuota actualizado correctamente\"}");
                    break;
                }

                default:
                	response.getWriter().write("{\"error\":\"Acción POST no reconocida: " + action + "\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error al procesar POST: " + e.getMessage() + "\"}");
        }
    }
}
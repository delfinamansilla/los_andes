package servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.stream.Collectors;

import entities.PagoCuota;
import logic.LogicPagoCuota;
import logic.LogicUsuario;
import logic.LogicCuota;
import entities.MailSender;
import entities.Usuario;
import entities.Cuota;
import entities.Monto_cuota;     
import logic.GeneradorArchivos;  
import logic.LogicMonto_cuota;   
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
    private LogicUsuario logicUsuario;
    private LogicCuota logicCuota;
    private LogicMonto_cuota logicMonto;
    
    private Gson gson;

    public ServletPagoCuota() {
        super();
        logicPago = new LogicPagoCuota();
        logicUsuario = new LogicUsuario();
        logicCuota = new LogicCuota();
        logicMonto = new LogicMonto_cuota(); 
        

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

                    LinkedList<PagoCuota> pagos = logicPago.getAll();
                    response.getWriter().write(gson.toJson(pagos));
                    break;
                }
                
                case "listar_por_usuario": {
                    String idParam = request.getParameter("id_usuario");
                    if (idParam == null) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("{\"error\":\"Falta id_usuario\"}");
                        return;
                    }
                    
                    int idUsuario = Integer.parseInt(idParam);
                    LinkedList<PagoCuota> todos = logicPago.getAll();

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
                pago.setFecha_pago(LocalDate.now()); 

                logicPago.add(pago);
                try {
                    
                    Usuario u = logicUsuario.getById(idUsuario);
                    Cuota c = logicCuota.getById(idCuota);
                    
                    Monto_cuota mc = logicMonto.getByCuota(idCuota); 
 
                    double precioFinal = (mc != null) ? mc.getMonto() : 0;
                    
                    if (LocalDate.now().isAfter(c.getFecha_vencimiento())) {
                        precioFinal = precioFinal * 1.10;
                    }

                    GeneradorArchivos gen = new GeneradorArchivos();
                    byte[] pdfBytes = gen.generarReciboPago(u, c, precioFinal);
                    

                    String cuerpoHtml = "<div style='background-color:#20321E;padding:40px;font-family:Arial;color:#E8E4D9;text-align:center;'>"
                            + "<h1 style='border-bottom:2px solid #E8E4D9;padding-bottom:10px;'>Pago Recibido</h1>"
                            + "<p>Hola " + u.getNombreCompleto() + ",</p>"
                            + "<p>Hemos registrado exitosamente el pago de tu <strong>Cuota N° " + c.getNro_cuota() + "</strong>.</p>"
                            + "<p>Adjunto encontrarás el comprobante oficial.</p>"
                            + "<br><p style='font-size:12px;color:#aaa;'>Club Los Andes</p>"
                            + "</div>";

                    // 5. Enviar Mail con Adjunto
                    if (u.getMail() != null && !u.getMail().isEmpty()) {
                        MailSender.enviarCorreoConAdjunto(
                            u.getMail(), 
                            "Comprobante de Pago - Club Los Andes", 
                            cuerpoHtml, 
                            pdfBytes, 
                            "Recibo_Cuota_" + c.getNro_cuota() + ".pdf"
                        );
                        System.out.println("✅ Comprobante enviado a: " + u.getMail());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("⚠️ El pago se guardó, pero falló el envío del mail: " + ex.getMessage());

                }
                
                response.getWriter().write("{\"mensaje\":\"Pago registrado y comprobante enviado.\"}");
            } else {
                response.getWriter().write("{\"error\":\"Acción POST no reconocida\"}");
            }
            
        } catch (Exception e) {
            e.printStackTrace(); 
            response.setStatus(500);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
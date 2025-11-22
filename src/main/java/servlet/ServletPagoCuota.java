package servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import entities.PagoCuota;
import logic.LogicUsuario;
import logic.GeneradorArchivos;
import logic.LogicCuota;
import logic.LogicMonto_cuota;
import entities.MailSender;
import entities.Usuario;
import entities.Cuota;
import entities.Monto_cuota;
import logic.LogicPagoCuota;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@WebServlet({"/pagocuota", "/PagoCuota"})
public class ServletPagoCuota extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private LogicPagoCuota logicPago;
    private LogicUsuario logicUsuario;
    private LogicCuota logicCuota;
    private LogicMonto_cuota logicMonto;
    private Gson gson;
    
    private static final String MP_ACCESS_TOKEN = "TEST-823938148084228-112018-f842aba74684673c394867dc4ef8f1bf-660480912";
    
    

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
    
    private String formatearPeriodo(int nro) {
        if (nro == 0) return "N/A";
        
        int anio = nro / 100;
        int mes = nro % 100;
        
        String[] meses = {"", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", 
                          "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        
        if (mes >= 1 && mes < meses.length) {
            return meses[mes] + " " + anio;
        } else {
            return "Mes inválido " + anio;
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
            if ("crear_orden_pago".equalsIgnoreCase(action)) {
                System.out.println("=== INICIANDO CREACIÓN DE ORDEN DE PAGO ===");
                int idCuota = Integer.parseInt(request.getParameter("id_cuota"));
                Cuota cuotaObj = logicCuota.getById(idCuota);
                int nroCuota = cuotaObj.getNro_cuota();
                int idUsuario = Integer.parseInt(request.getParameter("id_usuario"));
               

                String periodoFormateado = formatearPeriodo(nroCuota);
                double monto = Double.parseDouble(request.getParameter("monto"));
                
                
                System.out.println("Usuario: " + idUsuario + ", Cuota : " + periodoFormateado + ", Monto: " + monto + ", Nro: " + nroCuota);
               
                JsonObject preferenceRequest = new JsonObject();
                
                JsonObject item = new JsonObject();
                item.addProperty("title", "Cuota " + periodoFormateado);
                item.addProperty("quantity", 1);
                item.addProperty("unit_price", monto);
                item.addProperty("currency_id", "ARS");
                
                com.google.gson.JsonArray items = new com.google.gson.JsonArray();
                items.add(item);
                preferenceRequest.add("items", items);
                
                JsonObject payer = new JsonObject();
                payer.addProperty("email", "test_user_123@test.com");
                preferenceRequest.add("payer", payer);
                
                JsonObject backUrls = new JsonObject();
                backUrls.addProperty("success", "http://localhost:3000/pago-exitoso");
                backUrls.addProperty("failure", "http://localhost:3000/pago-fallido");
                backUrls.addProperty("pending", "http://localhost:3000/pago-pendiente");
                preferenceRequest.add("back_urls", backUrls);
                
                preferenceRequest.addProperty("external_reference", "cuota_" + idCuota + "_usuario_" + idUsuario);
                
                String jsonPayload = gson.toJson(preferenceRequest);
                System.out.println("JSON Request: " + jsonPayload);
                
                URL url = new URL("https://api.mercadopago.com/checkout/preferences");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + MP_ACCESS_TOKEN);
                conn.setDoOutput(true);
                
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
                
                int responseCode = conn.getResponseCode();
                System.out.println("Response Code: " + responseCode);
                
                BufferedReader in;
                if (responseCode >= 200 && responseCode < 300) {
                    in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                } else {
                    in = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
                }
                
                StringBuilder responseStr = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    responseStr.append(line);
                }
                in.close();
                
                System.out.println("Response Body: " + responseStr.toString());
                
                if (responseCode >= 200 && responseCode < 300) {

                    JsonObject mpResponse = JsonParser.parseString(responseStr.toString()).getAsJsonObject();
                    
                    String preferenceId = mpResponse.get("id").getAsString();
                    String initPoint = mpResponse.get("init_point").getAsString();
                    String sandboxInitPoint = mpResponse.get("sandbox_init_point").getAsString();
                    
                    String qrData = sandboxInitPoint; // o sea, esto seria el link convertido en qr
                    
                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.addProperty("qr_data", qrData);
                    jsonResponse.addProperty("payment_id", preferenceId);
                    jsonResponse.addProperty("init_point", initPoint);
                    
                    System.out.println("PREFERENCIA CREADA EXITOSAMENTE");
                    System.out.println("Link de pago: " + sandboxInitPoint);
                    response.getWriter().write(gson.toJson(jsonResponse));
                    
                } else {
                    System.err.println("ERROR DE MERCADO PAGO");
                    response.setStatus(500);
                    response.getWriter().write("{\"error\":\"Error de MercadoPago: " + responseStr.toString() + "\"}");
                }
                
            } else if ("pagar".equalsIgnoreCase(action)) {
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

                    if (u.getMail() != null && !u.getMail().isEmpty()) {
                        MailSender.enviarCorreoConAdjunto(
                            u.getMail(), 
                            "Comprobante de Pago - Club Los Andes", 
                            cuerpoHtml, 
                            pdfBytes, 
                            "Recibo_Cuota_" + c.getNro_cuota() + ".pdf"
                        );
                        System.out.println("Comprobante enviado a: " + u.getMail());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("El pago se guardó, pero falló el envío del mail: " + ex.getMessage());

                }
                
                response.getWriter().write("{\"mensaje\":\"Pago registrado y comprobante enviado.\"}");

            } else {
                response.getWriter().write("{\"error\":\"Acción POST no reconocida\"}");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR GENERAL: " + e.getMessage());
            response.setStatus(500);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
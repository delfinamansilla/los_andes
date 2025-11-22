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
    private Gson gson;
    
    // 👇 REEMPLAZA CON TU ACCESS TOKEN DE TEST
    private static final String MP_ACCESS_TOKEN = "TEST-823938148084228-112018-f842aba74684673c394867dc4ef8f1bf-660480912";

    public ServletPagoCuota() {
        super();
        logicPago = new LogicPagoCuota();

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
                
                int idUsuario = Integer.parseInt(request.getParameter("id_usuario"));
                int idCuota = Integer.parseInt(request.getParameter("id_cuota"));
                double monto = Double.parseDouble(request.getParameter("monto"));
                
                System.out.println("Usuario: " + idUsuario + ", Cuota: " + idCuota + ", Monto: " + monto);
                
                // Crear una preferencia de pago (esto genera un link de pago)
                JsonObject preferenceRequest = new JsonObject();
                
                // Items del pago
                JsonObject item = new JsonObject();
                item.addProperty("title", "Cuota #" + idCuota);
                item.addProperty("quantity", 1);
                item.addProperty("unit_price", monto);
                item.addProperty("currency_id", "ARS");
                
                com.google.gson.JsonArray items = new com.google.gson.JsonArray();
                items.add(item);
                preferenceRequest.add("items", items);
                
                // Payer
                JsonObject payer = new JsonObject();
                payer.addProperty("email", "test_user_123@test.com");
                preferenceRequest.add("payer", payer);
                
                // Back URLs (opcional)
                JsonObject backUrls = new JsonObject();
                backUrls.addProperty("success", "http://localhost:3000/pago-exitoso");
                backUrls.addProperty("failure", "http://localhost:3000/pago-fallido");
                backUrls.addProperty("pending", "http://localhost:3000/pago-pendiente");
                preferenceRequest.add("back_urls", backUrls);
                
                //preferenceRequest.addProperty("auto_return", "approved");
                
                // External reference para identificar el pago
                preferenceRequest.addProperty("external_reference", "cuota_" + idCuota + "_usuario_" + idUsuario);
                
                String jsonPayload = gson.toJson(preferenceRequest);
                System.out.println("JSON Request: " + jsonPayload);
                
                // Crear la preferencia en Mercado Pago
                URL url = new URL("https://api.mercadopago.com/checkout/preferences");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + MP_ACCESS_TOKEN);
                conn.setDoOutput(true);
                
                // Enviar el JSON
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
                
                // Leer la respuesta
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
                    // Parsear la respuesta
                    JsonObject mpResponse = JsonParser.parseString(responseStr.toString()).getAsJsonObject();
                    
                    String preferenceId = mpResponse.get("id").getAsString();
                    String initPoint = mpResponse.get("init_point").getAsString();
                    String sandboxInitPoint = mpResponse.get("sandbox_init_point").getAsString();
                    
                    // Para QR, usamos el sandbox_init_point
                    String qrData = sandboxInitPoint; // Este es el link que se convierte en QR
                    
                    // Construir respuesta
                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.addProperty("qr_data", qrData);
                    jsonResponse.addProperty("payment_id", preferenceId);
                    jsonResponse.addProperty("init_point", initPoint);
                    
                    System.out.println("✅ PREFERENCIA CREADA EXITOSAMENTE");
                    System.out.println("Link de pago: " + sandboxInitPoint);
                    response.getWriter().write(gson.toJson(jsonResponse));
                    
                } else {
                    // Error de Mercado Pago
                    System.err.println("❌ ERROR DE MERCADO PAGO");
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
                
                response.getWriter().write("{\"mensaje\":\"Pago registrado correctamente\"}");
                
            } else {
                response.getWriter().write("{\"error\":\"Acción POST no reconocida\"}");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ ERROR GENERAL: " + e.getMessage());
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
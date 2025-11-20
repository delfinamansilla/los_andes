package servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import entities.Alquiler_salon;
import entities.PreReserva;
import entities.MailSender;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import logic.LogicAlquiler_salon;
import logic.LogicPreReserva;

@WebServlet({"/alquiler_salon"})
public class ServletAlquiler_salon extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private LogicAlquiler_salon logic;
    private LogicPreReserva logicPre;
    private Gson gson;

    public ServletAlquiler_salon() {
        logic = new LogicAlquiler_salon();
        logicPre = new LogicPreReserva();

        gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class,
                (com.google.gson.JsonSerializer<LocalDate>)
                    (src, type, ctx) -> new com.google.gson.JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalTime.class,
                (com.google.gson.JsonSerializer<LocalTime>)
                    (src, type, ctx) -> new com.google.gson.JsonPrimitive(src.toString()))
            .create();
    }

    // ============================================================
    // GET
    // ============================================================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");
        String action = req.getParameter("action");

        try {
            if (action == null) {
                resp.getWriter().write("{\"error\": \"Debe especificar acción\"}");
                return;
            }

            switch (action) {

                // ============================================================
                // LISTAR TODOS
                // ============================================================
                case "listar": {
                    resp.getWriter().write(gson.toJson(logic.getAll()));
                    break;
                }

                // ============================================================
                // MIS RESERVAS FUTURAS
                // ============================================================
                case "mis_reservas": {

                    if (req.getParameter("idUsuario") == null) {
                        resp.setStatus(400);
                        resp.getWriter().write("{\"error\": \"Falta idUsuario\"}");
                        return;
                    }

                    int idUser = Integer.parseInt(req.getParameter("idUsuario"));
                    LinkedList<Alquiler_salon> reservas = logic.getByUsuarioFuturos(idUser);

                    resp.getWriter().write(gson.toJson(reservas));
                    break;
                }

                // ============================================================
                // BUSCAR POR ID
                // ============================================================
                case "buscar": {
                    int id = Integer.parseInt(req.getParameter("id"));
                    resp.getWriter().write(gson.toJson(logic.getById(id)));
                    break;
                }

                // ============================================================
                // ELIMINAR
                // ============================================================
                case "eliminar": {
                    int id = Integer.parseInt(req.getParameter("id"));
                    logic.delete(id);
                    resp.getWriter().write("{\"status\":\"ok\"}");
                    break;
                }

                // ============================================================
                // HORARIOS OCUPADOS
                // ============================================================
                case "horarios": {
                    int idSalon = Integer.parseInt(req.getParameter("idSalon"));
                    LocalDate fecha = LocalDate.parse(req.getParameter("fecha"));

                    List<Rango> ocupados = obtenerHorariosOcupados(idSalon, fecha);

                    resp.getWriter().write(
                        "{\"status\":\"ok\",\"ocupados\":" + gson.toJson(ocupados) + "}"
                    );

                    break;
                }

                // ============================================================
                // 🌟 NUEVA PRE-RESERVA (envía mail)
                // ============================================================
                case "pre_reserva": {
                    int idSalon = Integer.parseInt(req.getParameter("id_salon"));
                    int idUsuario = Integer.parseInt(req.getParameter("id_usuario"));
                    LocalDate fecha = LocalDate.parse(req.getParameter("fecha"));
                    LocalTime desde = LocalTime.parse(req.getParameter("hora_desde"));
                    LocalTime hasta = LocalTime.parse(req.getParameter("hora_hasta"));
                    String emailDestino = req.getParameter("email");

                    // Crear pre-reserva
                    PreReserva p = logicPre.crearPreReserva(idUsuario, idSalon, fecha, desde, hasta);

                    // Armar link
                    String link = "http://localhost:8080/club/alquiler_salon?action=confirmar&token=" + p.getToken();

                    // --- DISEÑO DEL EMAIL EN HTML ---
                    String cuerpo = "<div style='background-color: #f4f4f4; padding: 40px; font-family: Arial, sans-serif;'>"
                            + "<div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.1);'>"
                            
                            // Cabecera Verde
                            + "<div style='background-color: #20321E; padding: 30px; text-align: center;'>"
                            + "<h1 style='color: #DDD8CA; margin: 0; font-size: 24px;'>Confirmación de Reserva</h1>"
                            + "</div>"
                            
                            // Contenido
                            + "<div style='padding: 30px; color: #333;'>"
                            + "<p style='font-size: 16px;'>Hola,</p>"
                            + "<p style='font-size: 16px; line-height: 1.5;'>Has solicitado una reserva en el Club Deportivo Los Andes. Para confirmarla y asegurar tu lugar, por favor haz clic en el botón de abajo.</p>"
                            
                            // Caja de detalles
                            + "<div style='background-color: #f9f9f9; padding: 15px; border-left: 5px solid #466245; margin: 20px 0;'>"
                            + "<p style='margin: 5px 0;'><strong> Fecha:</strong> " + fecha + "</p>"
                            + "<p style='margin: 5px 0;'><strong> Horario:</strong> " + desde + " - " + hasta + "</p>"
                            + "</div>"
                            
                            // Botón
                            + "<div style='text-align: center; margin-top: 30px;'>"
                            + "<a href='" + link + "' style='background-color: #b91c1c; color: #ffffff; padding: 15px 30px; text-decoration: none; font-weight: bold; border-radius: 5px; display: inline-block;'>CONFIRMAR RESERVA AHORA</a>"
                            + "</div>"
                            
                            + "<p style='font-size: 12px; color: #999; margin-top: 30px; text-align: center;'>Si no realizaste esta solicitud, puedes ignorar este correo. El enlace expirará en 30 minutos.</p>"
                            + "</div>" // Fin padding content
                            + "</div>" // Fin card
                            + "</div>"; // Fin background

                    // Importante: Indicar que es HTML
                    MailSender.enviarCorreo(emailDestino, "Acción requerida: Confirmá tu reserva", cuerpo);

                    resp.getWriter().write("{\"status\":\"mail_enviado\"}");
                    break;
                }
                case "confirmar": {
                    String token = req.getParameter("token");
                    PreReserva pr = logicPre.obtenerPorToken(token);

                    // Configurar respuesta como HTML
                    resp.setContentType("text/html;charset=UTF-8");

                    // Estilos CSS compartidos para la página de respuesta
                    String estiloCss = "<style>"
                            + "body { font-family: 'Segoe UI', sans-serif; background-color: #20321E; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }"
                            + ".card { background-color: #DDD8CA; padding: 40px; border-radius: 15px; text-align: center; max-width: 500px; box-shadow: 0 10px 25px rgba(0,0,0,0.5); }"
                            + "h1 { color: #20321E; margin-bottom: 20px; }"
                            + "p { color: #333; font-size: 18px; margin-bottom: 30px; }"
                            + ".btn { background-color: #466245; color: #DDD8CA; padding: 15px 30px; text-decoration: none; border-radius: 8px; font-weight: bold; transition: 0.3s; display: inline-block; }"
                            + ".btn:hover { background-color: #20321E; color: #fff; }"
                            + ".icon { font-size: 50px; color: #466245; margin-bottom: 20px; }"
                            + ".error { color: #b91c1c; }"
                            + "</style>";

                    if (pr == null) {
                        resp.getWriter().write("<html><head><title>Error</title>" + estiloCss + "</head><body>");
                        resp.getWriter().write("<div class='card'>");
                        resp.getWriter().write("<div class='icon' style='color:#b91c1c'>✖</div>");
                        resp.getWriter().write("<h1 class='error'>Enlace inválido</h1>");
                        resp.getWriter().write("<p>La reserva no pudo ser confirmada. El enlace es incorrecto o ya fue utilizado.</p>");
                        resp.getWriter().write("<a href='http://localhost:3000/salones' class='btn'>Volver al Club</a>");
                        resp.getWriter().write("</div></body></html>");
                        return;
                    }

                    if (pr.getExpiracion().isBefore(LocalDateTime.now())) {
                        resp.getWriter().write("<html><head><title>Expirado</title>" + estiloCss + "</head><body>");
                        resp.getWriter().write("<div class='card'>");
                        resp.getWriter().write("<div class='icon' style='color:#b91c1c'>⏳</div>");
                        resp.getWriter().write("<h1 class='error'>Enlace expirado</h1>");
                        resp.getWriter().write("<p>Lo sentimos, el tiempo para confirmar la reserva ha finalizado (30 minutos).</p>");
                        resp.getWriter().write("<a href='http://localhost:3000/salones' class='btn'>Intentar de nuevo</a>");
                        resp.getWriter().write("</div></body></html>");
                        return;
                    }

                    // --- CONFIRMACIÓN EXITOSA ---
                    
                    // Crear la reserva DEFINITIVA
                    Alquiler_salon a = new Alquiler_salon();
                    a.setIdSalon(pr.getIdSalon());
                    a.setIdUsuario(pr.getIdUsuario());
                    a.setFecha(pr.getFecha());
                    a.setHoraDesde(pr.getHoraDesde());
                    a.setHoraHasta(pr.getHoraHasta());

                    logic.add(a);

                    // Eliminar la pre-reserva
                    logicPre.eliminarPorToken(token);

                    // Respuesta Visual Linda
                    resp.getWriter().write("<html><head><title>Confirmada</title>" + estiloCss + "</head><body>");
                    resp.getWriter().write("<div class='card'>");
                    resp.getWriter().write("<div class='icon'>✔</div>");
                    resp.getWriter().write("<h1>¡Reserva Confirmada!</h1>");
                    resp.getWriter().write("<p>Tu turno ha sido registrado exitosamente. Te esperamos en el club.</p>");
                    // Botón que redirige a tu React Frontend
                    
                    resp.getWriter().write("</div></body></html>");
                    break;
                }

                default:
                    resp.getWriter().write(
                        "{\"error\":\"Acción GET no válida: " + action + "\"}"
                    );
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ============================================================
    // CLASE Rango
    // ============================================================
    private static class Rango {
        String horaDesde;
        String horaHasta;

        Rango(String d, String h) {
            this.horaDesde = d;
            this.horaHasta = h;
        }
    }

    // ============================================================
    // Obtener horarios ocupados
    // ============================================================
    private List<Rango> obtenerHorariosOcupados(int salon, LocalDate fecha) throws Exception {

        LinkedList<Alquiler_salon> reservas =
            logic.getAll()
                .stream()
                .filter(a -> a.getIdSalon() == salon && a.getFecha().equals(fecha))
                .collect(java.util.stream.Collectors.toCollection(LinkedList::new));

        List<Rango> ocupados = new ArrayList<>();

        reservas.forEach(r -> {
            String desde = r.getHoraDesde().toString().substring(0, 5);
            String hasta = r.getHoraHasta().toString().substring(0, 5);

            ocupados.add(new Rango(desde, hasta));
        });

        return ocupados;
    }

    // ============================================================
    // POST — Crear reserva directa
    // ============================================================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");
        String action = req.getParameter("action");

        try {

            if ("crear".equals(action)) {

                Alquiler_salon a = new Alquiler_salon();

                a.setIdSalon(Integer.parseInt(req.getParameter("id_salon")));
                a.setIdUsuario(Integer.parseInt(req.getParameter("id_usuario")));
                a.setFecha(LocalDate.parse(req.getParameter("fecha")));
                a.setHoraDesde(LocalTime.parse(req.getParameter("hora_desde")));
                a.setHoraHasta(LocalTime.parse(req.getParameter("hora_hasta")));

                logic.add(a);

                resp.getWriter().write(
                    "{\"status\":\"ok\",\"alquiler\":" + gson.toJson(a) + "}"
                );
                return;
            }

            resp.getWriter().write("{\"error\":\"Acción POST no válida\"}");

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}

package servlet;

import java.io.IOException;
import java.util.LinkedList;

import entities.InformeRecaudacion;
import logic.LogicInformeRecaudacion;
import entities.InformeOcupacion;
import logic.LogicInformeOcupacion;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDate;
import logic.GeneradorArchivos;
import java.io.OutputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/informe")
public class ServletInforme extends HttpServlet {

    private LogicInformeRecaudacion logicInforme;
    private Gson gson;
    private GeneradorArchivos generadorArchivos;



    public ServletInforme() {
        logicInforme = new LogicInformeRecaudacion();
        generadorArchivos = new GeneradorArchivos();

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
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setContentType("application/json;charset=UTF-8");

        try {
            String action = request.getParameter("action");

            if (action == null) {
                response.setStatus(400);
                response.getWriter().write("{\"error\":\"Falta el parámetro action\"}");
                return;
            }
            
            if ("pdf".equalsIgnoreCase(action)) {

                String desdeStr = request.getParameter("desde");
                String hastaStr = request.getParameter("hasta");

                if (desdeStr == null || hastaStr == null) {
                    response.setStatus(400);
                    response.getWriter().write(
                        "{\"error\":\"Faltan fechas\"}"
                    );
                    return;
                }


                LocalDate desde = LocalDate.parse(desdeStr);
                LocalDate hasta = LocalDate.parse(hastaStr);


                LinkedList<InformeRecaudacion> informe =
                    logicInforme.generarInforme(desde, hasta);

                String rutaLogo = getServletContext().getRealPath("/WEB-INF/los_andes.png");
                byte[] pdf =
                    generadorArchivos.generarInformeRecaudacionPDF(
                        informe,
                        desde,
                        hasta,
                        rutaLogo
                    );


                response.setContentType("application/pdf");

                response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=informe_recaudacion.pdf"
                );


                response.getOutputStream().write(pdf);

                return;
            }
            if ("pdfOcupacion".equalsIgnoreCase(action)) {

                String desdeStr = request.getParameter("desde");
                String hastaStr = request.getParameter("hasta");

                if (desdeStr == null || hastaStr == null) {
                    response.setStatus(400);
                    response.getWriter().write("{\"error\":\"Faltan fechas\"}");
                    return;
                }

                LocalDate desde = LocalDate.parse(desdeStr);
                LocalDate hasta = LocalDate.parse(hastaStr);

                LogicInformeOcupacion logicOcupacion =
                        new LogicInformeOcupacion();

                LinkedList<InformeOcupacion> informe =
                        logicOcupacion.generarInforme(desde, hasta);

                String rutaLogo =
                        getServletContext().getRealPath("/WEB-INF/los_andes.png");

                byte[] pdf =
                        generadorArchivos.generarInformeOcupacionPDF(
                                informe,
                                desde,
                                hasta,
                                rutaLogo
                        );

                response.setContentType("application/pdf");

                response.setHeader(
                        "Content-Disposition",
                        "attachment; filename=informe_ocupacion.pdf"
                );

                OutputStream os = response.getOutputStream();
                os.write(pdf);
                os.flush();

                return;
            }

            if ("recaudacion".equalsIgnoreCase(action)) {

                String desdeStr = request.getParameter("desde");
                String hastaStr = request.getParameter("hasta");

                if (desdeStr == null || hastaStr == null) {
                    response.setStatus(400);
                    response.getWriter().write("{\"error\":\"Faltan parámetros desde/hasta\"}");
                    return;
                }

                LocalDate desde = LocalDate.parse(desdeStr);
                LocalDate hasta = LocalDate.parse(hastaStr);

                LinkedList<InformeRecaudacion> informe =
                        logicInforme.generarInforme(desde, hasta);

                response.getWriter().write(gson.toJson(informe));

            } else if ("ocupacion".equalsIgnoreCase(action)) {

                String desdeStr = request.getParameter("desde");
                String hastaStr = request.getParameter("hasta");

                if (desdeStr == null || hastaStr == null) {
                    response.setStatus(400);
                    response.getWriter().write("{\"error\":\"Faltan parámetros desde/hasta\"}");
                    return;
                }

                LocalDate desde = LocalDate.parse(desdeStr);
                LocalDate hasta = LocalDate.parse(hastaStr);

                LogicInformeOcupacion logicOcupacion = new LogicInformeOcupacion();

                LinkedList<InformeOcupacion> informe =
                        logicOcupacion.generarInforme(desde, hasta);

                response.getWriter().write(gson.toJson(informe));

            }else {

                response.setStatus(400);
                response.getWriter().write("{\"error\":\"Acción inválida: " + action + "\"}");

            }

        } catch (NumberFormatException e) {
            response.setStatus(400);
            response.getWriter().write("{\"error\":\"Mes o Año deben ser números válidos\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            response.getWriter().write("{\"error\":\"Error interno: " + e.getMessage() + "\"}");
        } 
    }
    
}
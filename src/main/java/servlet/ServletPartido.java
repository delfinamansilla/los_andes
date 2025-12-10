package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logic.LogicPartido;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;


import entities.Partido;


@WebServlet({"/partido", "/Partido", "/PARTIDO" })
public class ServletPartido extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private LogicPartido logicPartido;
    private Gson gson;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletPartido() {
        super();
        logicPartido = new LogicPartido();
        gson = new GsonBuilder()
       
                .registerTypeAdapter(LocalDate.class,
                    (com.google.gson.JsonSerializer<LocalDate>)
                        (src, typeOfSrc, context) -> new com.google.gson.JsonPrimitive(src.toString()))
               
                .registerTypeAdapter(LocalDate.class,
                    (com.google.gson.JsonDeserializer<LocalDate>)
                        (json, typeOfT, context) -> LocalDate.parse(json.getAsString()))

                .registerTypeAdapter(LocalTime.class,
                    (com.google.gson.JsonSerializer<LocalTime>)
                        (src, typeOfSrc, context) -> new com.google.gson.JsonPrimitive(src.toString()))
             
                .registerTypeAdapter(LocalTime.class,
                    (com.google.gson.JsonDeserializer<LocalTime>)
                        (json, typeOfT, context) -> LocalTime.parse(json.getAsString()))

                .setPrettyPrinting()
                .create();
    }
        
    

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		String action = request.getParameter("action");

        
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class,
                    (JsonSerializer<LocalDate>) (src, type, ctx)
                            -> new JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDate.class,
                    (JsonDeserializer<LocalDate>) (json, type, ctx)
                            -> LocalDate.parse(json.getAsString()))

                .registerTypeAdapter(LocalTime.class,
                    (JsonSerializer<LocalTime>) (src, type, ctx)
                            -> new JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalTime.class,
                    (JsonDeserializer<LocalTime>) (json, type, ctx)
                            -> LocalTime.parse(json.getAsString()))

                .setPrettyPrinting()
                .create();
        
        try {
            if (action == null) {
                response.getWriter().append("Debe especificar una acción (listar, buscar, buscar_por_rango o eliminar).");
                return;
            }
            switch(action.toLowerCase()) {
            case "listar":{
                List<Partido> partidos = logicPartido.getAll(); 
                String json = gson.toJson(partidos);
                response.getWriter().write(json);
                break;  
            }
            
            case "listar_por_rango":{
                String desdeStr = request.getParameter("desde");
                String hastaStr = request.getParameter("hasta");

                if (desdeStr == null || hastaStr == null) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                        "Faltan los parámetros 'desde' y/o 'hasta'");
                    break;
                }

                try {
 
                    LocalDate desde = LocalDate.parse(desdeStr);
                    LocalDate hasta = LocalDate.parse(hastaStr);

                    List<Partido> partidos = logicPartido.getByFechaRango(desde, hasta);

                    String json = gson.toJson(partidos);

                    response.setContentType("application/json");
                    response.getWriter().write(json);

                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                        "Error al obtener los partidos por rango");
                }

                break;
            }
            case "buscar":{
                int id = Integer.parseInt(request.getParameter("id"));
                Partido p = logicPartido.getById(id);

                if (p != null) {
                    String json = gson.toJson(p);
                    response.getWriter().write(json);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\":\"No se encontró el partido con ID " + id + "\"}");
                }
                break;
            }
            
            case "eliminar": {
                int id = Integer.parseInt(request.getParameter("id"));
                logicPartido.delete(id);
                response.getWriter().write("{\"status\":\"ok\", \"mensaje\":\"Partido eliminado correctamente.\"}");
                break;
            }
            default:
                response.getWriter().append("Acción GET no reconocida: ").append(action);
            }
		} catch (Exception e) {
		    e.printStackTrace();
		    response.setContentType("application/json;charset=UTF-8");
		    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

		    String errorJson = gson.toJson(
		        Map.of("error", "Error al procesar la solicitud", "detalle", e.getMessage())
		    );

		    response.getWriter().write(errorJson);
		}
		
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
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
            System.out.println("JSON recibido: " + body);
            
            switch (action.toLowerCase()) {
            case "crear":{
                Partido nuevo = new Partido();
                nuevo.setFecha(LocalDate.parse(body.get("fecha").getAsString()));
                nuevo.setOponente(body.get("oponente").getAsString());
                nuevo.setHora_desde(LocalTime.parse(body.get("hora_desde").getAsString()));
                nuevo.setHora_hasta(LocalTime.parse(body.get("hora_hasta").getAsString()));
                nuevo.setCategoria(body.get("categoria").getAsString());
                nuevo.setPrecio_entrada(body.get("precio_entrada").getAsDouble());
                nuevo.setId_cancha(
                	    body.get("id_cancha").isJsonNull() 
                        ? null 
                        : body.get("id_cancha").getAsInt()
                );

                nuevo.setId_actividad(body.get("id_actividad").getAsInt());

                logicPartido.add(nuevo);

                String json = gson.toJson(nuevo);
                response.getWriter().write(
                    "{\"status\":\"ok\",\"mensaje\":\"Partido creado correctamente\",\"partido\":" + json + "}"
                );
                break;
            }
            case "actualizar":{
                Partido p = new Partido();
                p.setId(body.get("id").getAsInt());
                p.setFecha(LocalDate.parse(body.get("fecha").getAsString()));
                p.setOponente(body.get("oponente").getAsString());
                p.setHora_desde(LocalTime.parse(body.get("hora_desde").getAsString()));
                p.setHora_hasta(LocalTime.parse(body.get("hora_hasta").getAsString()));
                p.setCategoria(body.get("categoria").getAsString());
                p.setPrecio_entrada(body.get("precio_entrada").getAsDouble());
                p.setId_cancha(
                	    body.get("id_cancha").isJsonNull() 
                        ? null 
                        : body.get("id_cancha").getAsInt()
                );

                p.setId_actividad(body.get("id_actividad").getAsInt());

                logicPartido.update(p);

                String json = gson.toJson(p);
                response.getWriter().write(
                    "{\"status\":\"ok\",\"mensaje\":\"Partido actualizado correctamente\",\"partido\":" + json + "}"
                );
                break;
            }
            
            default:
                response.getWriter().write("{\"error\":\"Acción POST no reconocida: " + action + "\"}");
            }

		} catch (Exception e) {
		    e.printStackTrace();

		    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		    response.setContentType("application/json;charset=UTF-8");

		    String msg = e.getMessage().replace("\"", "'"); 

		    response.getWriter().write(
		        "{\"error\":\"Error al procesar la solicitud: " + msg + "\"}"
		    );
		}
	}

}

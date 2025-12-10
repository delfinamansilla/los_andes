package servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;
import java.io.InputStream;


import entities.Usuario;
import logic.LogicUsuario;
import logic.LogicRecuperacionPass;

@WebServlet("/usuario")
@MultipartConfig
public class ServletUsuario extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private LogicUsuario logicUsuario;
    private LogicRecuperacionPass logicRecupero;

    public ServletUsuario() {
        super();
        logicUsuario = new LogicUsuario();
        logicRecupero = new LogicRecuperacionPass(); 
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	   response.setHeader("Access-Control-Allow-Origin", "*");
    	   response.setHeader("Access-Control-Allow-Credentials", "true");
           response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
           response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        String action = request.getParameter("action");

        try {
            if (action == null) {
                response.getWriter().append("Debe especificar una acción en el parámetro 'action'.");
                return;
            }

            switch (action.toLowerCase()) {
            case "listar": {
                LinkedList<Usuario> usuarios = logicUsuario.getAll();
                
                com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
                    .registerTypeAdapter(java.time.LocalDate.class,
                        (com.google.gson.JsonSerializer<java.time.LocalDate>) (src, typeOfSrc, context) ->
                            new com.google.gson.JsonPrimitive(src.toString()))
                    .create();

                String json = gson.toJson(usuarios);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(json);
                break;
            }
            case "buscar": {
                int id = Integer.parseInt(request.getParameter("id"));
                
                Usuario u = logicUsuario.getById(id);

                response.setContentType("application/json;charset=UTF-8");
                if (u != null) {
                    response.getWriter().write("{\"id\":" + u.getIdUsuario() + 
                        ", \"nombre\":\"" + u.getNombreCompleto() + 
                        "\", \"mail\":\"" + u.getMail() + "\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\":\"Usuario no encontrado\"}");
                }
                break;
            }
            
            case "buscarc": {
                int id = Integer.parseInt(request.getParameter("id"));
                Usuario u = logicUsuario.getById(id);
                response.setContentType("application/json;charset=UTF-8");

                if (u != null) {
                    com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
                        .registerTypeAdapter(java.time.LocalDate.class,
                            (com.google.gson.JsonSerializer<java.time.LocalDate>)
                            (src, typeOfSrc, context) -> new com.google.gson.JsonPrimitive(src.toString()))
                        .create();
                    String usuarioJson = gson.toJson(u);
                    response.getWriter().write(usuarioJson);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\":\"Usuario no encontrado\"}");
                }
                break;
            }
            
            case "verfoto": {
                int id = Integer.parseInt(request.getParameter("id"));
                byte[] fotoData = logicUsuario.getFotoById(id);

                if (fotoData != null && fotoData.length > 0) {
                    response.setContentType("image/jpeg");
                    response.setContentLength(fotoData.length);
                    response.getOutputStream().write(fotoData);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
                break;
            }


                case "eliminar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    logicUsuario.delete(id);
                    response.sendRedirect("usuario?action=listar");
                    break;
                }
                default:
                    response.getWriter().append("Acción no reconocida en GET: ").append(action);
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
    	   response.setHeader("Access-Control-Allow-Origin", "*");
    	   response.setHeader("Access-Control-Allow-Credentials", "true");
           response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
           response.setHeader("Access-Control-Allow-Headers", "Content-Type");

           String contentType = request.getContentType();
           boolean isMultipart = contentType != null && contentType.toLowerCase().startsWith("multipart/form-data");

           if (isMultipart) {
               response.setContentType("application/json;charset=UTF-8");
               try {
                   int idUsuario = Integer.parseInt(request.getParameter("idUsuario"));
                   Part filePart = request.getPart("foto");
                   InputStream fileContent = filePart.getInputStream();
                   logicUsuario.updateFoto(idUsuario, fileContent);
                   response.setStatus(HttpServletResponse.SC_OK);
                   response.getWriter().write("{\"message\":\"Foto actualizada.\"}");
               } catch (Exception e) {
            	   response.setContentType("application/json");
                   response.setCharacterEncoding("UTF-8");
                   response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                   response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
               }
           } else {
           
           String action = request.getParameter("action");

	        try {
	            if (action == null) {
	                response.getWriter().append("Debe especificar una acción en el parámetro 'action'.");
	                return;
	            }
	
	            switch (action.toLowerCase()) {
	
	            case "login": {
	                String mail = request.getParameter("mail");
	                String contrasenia = request.getParameter("contrasenia");
	
	                Usuario u = new Usuario();
	                u.setMail(mail);
	                u.setContrasenia(contrasenia);
	
	                Usuario usuarioLogueado = logicUsuario.login(u);
	
	                response.setContentType("application/json;charset=UTF-8");
	
	                if (usuarioLogueado != null) {
	                    request.getSession().setAttribute("usuarioActual", usuarioLogueado);
	
	                    com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
	                        .registerTypeAdapter(java.time.LocalDate.class,
	                            (com.google.gson.JsonSerializer<java.time.LocalDate>)
	                            (src, typeOfSrc, context) ->
	                                new com.google.gson.JsonPrimitive(src.toString()))
	                        .create();
	
	                    String usuarioJson = gson.toJson(usuarioLogueado);
	                    response.getWriter().write("{\"status\":\"ok\", \"usuario\":" + usuarioJson + "}");
	                } else {
	                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	                    response.getWriter().write("{\"status\":\"error\", \"mensaje\":\"Correo o contraseña incorrectos.\"}");
	                }
	                break;
	            }
	
	
	
	                case "registrar": {
	                    Usuario nuevo = new Usuario();
	                    nuevo.setNombreCompleto(request.getParameter("nombre_completo"));
	                    nuevo.setDni(request.getParameter("dni"));
	                    nuevo.setTelefono(request.getParameter("telefono"));
	                    nuevo.setMail(request.getParameter("mail"));
	                    nuevo.setContrasenia(request.getParameter("contrasenia"));
	                    nuevo.setRol(request.getParameter("rol"));
	                    nuevo.setEstado(true);
	
	                    String fecha = request.getParameter("fecha_nacimiento");
	                    if (fecha != null && !fecha.isEmpty()) {
	                        nuevo.setFechaNacimiento(LocalDate.parse(fecha));
	                    }
	
	                    try {
	                        logicUsuario.add(nuevo);
	
	                        response.setContentType("application/json");
	                        response.setCharacterEncoding("UTF-8");
	                        response.setStatus(HttpServletResponse.SC_OK);
	                        response.getWriter().write("{\"message\":\"Usuario registrado correctamente\"}");
	
	                    } catch (Exception e) {
	                        response.setContentType("application/json");
	                        response.setCharacterEncoding("UTF-8");
	                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	                        response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
	                    }
	
	                    break;
	                }
	
	                case "actualizar": {
	                    Usuario u = new Usuario();
	                    u.setIdUsuario(Integer.parseInt(request.getParameter("id")));
	                    u.setNombreCompleto(request.getParameter("nombre_completo"));
	                    u.setDni(request.getParameter("dni"));
	                    u.setTelefono(request.getParameter("telefono"));
	                    u.setMail(request.getParameter("mail"));
	                    u.setContrasenia(request.getParameter("contrasenia"));
	                    u.setRol(request.getParameter("rol"));
	                    u.setEstado(Boolean.parseBoolean(request.getParameter("estado")));
	                    String fecha = request.getParameter("fecha_nacimiento");
	                    if (fecha != null && !fecha.isEmpty()) {
	                        u.setFechaNacimiento(LocalDate.parse(fecha));
	                    }
	
	                    try {
	                        logicUsuario.update(u);
	
	                        response.setContentType("application/json");
	                        response.setCharacterEncoding("UTF-8");
	                        response.setStatus(HttpServletResponse.SC_OK);
	                        response.getWriter().write("{\"id\":" + u.getIdUsuario() +
	                            ",\"nombre_completo\":\"" + u.getNombreCompleto() + "\"" +
	                            ",\"mail\":\"" + u.getMail() + "\"" +
	                            ",\"telefono\":\"" + u.getTelefono() + "\"" +
	                            ",\"rol\":\"" + u.getRol() + "\"" +
	                            "}");
	                    } catch (Exception e) {
	                        response.setContentType("application/json");
	                        response.setCharacterEncoding("UTF-8");
	                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	                        response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
	                    }
	                    break;
	                }
	                
	                case "recuperar": {
	                    String mail = request.getParameter("mail");
	                    LinkedList<Usuario> todos = logicUsuario.getAll();
	                    Usuario uEncontrado = null;
	                    for(Usuario u : todos) {
	                        if(u.getMail().equalsIgnoreCase(mail)) {
	                            uEncontrado = u;
	                            break;
	                        }
	                    }
	                    
	                    if (uEncontrado != null) {
	                    	
	                        entities.RecuperacionPass rp = logicRecupero.crearSolicitud(uEncontrado.getIdUsuario());
	                        if (rp == null) {
	                            System.err.println("❌ [ERROR] El objeto RecuperacionPass vino NULL. Falló la lógica de crear solicitud.");
	                            response.setStatus(500);
	                            response.getWriter().write("{\"error\":\"Error interno al generar token\"}");
	                            break;
	                        }

	                        
	                        String link = "https://los-andes-six.vercel.app/cambiar-contrasenia?token=" + rp.getToken();
	                        String cuerpo = "<div style='background-color: #20321E; padding: 50px; font-family: Arial, sans-serif;'>"
	                                + "  <div style='max-width: 500px; margin: 0 auto; background-color: #E8E4D9; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.5);'>"
	                                + "    <div style='background-color: #1a2918; padding: 20px; text-align: center; border-bottom: 4px solid #466245;'>"
	                                + "      <h1 style='color: #E8E4D9; margin: 0; font-size: 24px; text-transform: uppercase; letter-spacing: 1px;'>Club Los Andes</h1>"
	                                + "    </div>"
	                                + "    <div style='padding: 40px; color: #20321E; text-align: center;'>"
	                                + "      <h2 style='margin-top: 0; color: #20321E;'>Recuperar Contraseña</h2>"
	                                + "      <p style='font-size: 16px; line-height: 1.5;'>Hola <strong>" + uEncontrado.getNombreCompleto() + "</strong>,</p>"
	                                + "      <p style='font-size: 15px; margin-bottom: 30px;'>Hemos recibido una solicitud para cambiar tu clave. Hacé clic en el botón de abajo para crear una nueva:</p>"
	                                + "      <a href='" + link + "' style='background-color: #20321E; color: #E8E4D9; padding: 15px 30px; text-decoration: none; font-weight: bold; border-radius: 5px; display: inline-block; font-size: 16px;'>CAMBIAR CONTRASEÑA</a>"
	                                + "      <p style='margin-top: 30px; font-size: 12px; color: #555;'>Este enlace expirará en 1 hora.<br>Si no lo solicitaste, ignorá este mensaje.</p>"
	                                + "    </div>"
	                                + "  </div>"
	                                + "</div>";
	                        
	                        try {
	                            entities.MailSender.enviarCorreo(mail, "Recupero de Clave", cuerpo);
	                            response.getWriter().write("{\"message\":\"Correo enviado\"}");
	                        } catch (Exception e) {
	                            e.printStackTrace();
	                            System.err.println("[ERROR FATAL] Falló el envío del mail:");
	                            e.printStackTrace(); 
	                            System.err.println("Mensaje de error: " + e.getMessage());
	                            
	                            response.setStatus(500);
	                            response.getWriter().write("{\"error\":\"Error al enviar mail\"}");
	                        }
	                    } else {
	                        response.setStatus(400);
	                        response.getWriter().write("{\"error\":\"Email no encontrado\"}");
	                    }
	                    break;
	                }

	                case "restablecer": {
	                    String token = request.getParameter("token");
	                    String nuevaPass = request.getParameter("nueva_pass");
	                    
	                    entities.RecuperacionPass rp = logicRecupero.obtenerPorToken(token);
	                    
	                    if (rp != null && rp.getExpiracion().isAfter(java.time.LocalDateTime.now())) {
	                        Usuario u = logicUsuario.getById(rp.getIdUsuario());
	                        u.setContrasenia(nuevaPass);
	                        
	                        try {
	                            logicUsuario.update(u); 
	                            logicRecupero.eliminarToken(token);
	                            
	                            response.getWriter().write("{\"message\":\"Contraseña actualizada\"}");
	                        } catch (Exception e) {
	                            response.setStatus(500);
	                            response.getWriter().write("{\"error\":\"La contraseña debe tener al menos 8 caracteres.\"}");
	                        }
	                    } else {
	                        response.setStatus(400);
	                        response.getWriter().write("{\"error\":\"El enlace es inválido o ha expirado.\"}");
	                    }
	                    break;
	                }
	
	                default:
	                    response.getWriter().append("Acción POST no reconocida: ").append(action);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            request.setAttribute("error", "Error al procesar: " + e.getMessage());
	            request.getRequestDispatcher("error.jsp").forward(request, response);
	        }
        }
    }
}
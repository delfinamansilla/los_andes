package servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;



import entities.Usuario;
import logic.LogicUsuario;

/**
 * Servlet para gestionar las operaciones CRUD de Usuario.
 * Incluye login, alta, modificación, eliminación y listado.
 */
@WebServlet({"/usuario", "/Usuario", "/USUARIO"})
public class ServletUsuario extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private LogicUsuario logicUsuario;

    public ServletUsuario() {
        super();
        logicUsuario = new LogicUsuario();
    }

    /**
     * Maneja peticiones GET: listar, buscar por ID o eliminar usuario.
     * Ejemplos:
     *   GET /usuario?action=listar
     *   GET /usuario?action=eliminar&id=5
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        response.getWriter().println("Servlet Usuario activo y corriendo.");
    }
    /*@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if (action == null) {
                response.getWriter().append("Debe especificar una acción en el parámetro 'action'.");
                return;
            }

            switch (action.toLowerCase()) {
                case "listar": {
                    LinkedList<Usuario> usuarios = logicUsuario.getAll();
                    request.setAttribute("listaUsuarios", usuarios);
                    request.getRequestDispatcher("WEB-INF/listaUsuarios.jsp").forward(request, response);
                    break;
                }
                case "buscar": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Usuario u = logicUsuario.getAll().stream()
                            .filter(x -> x.getIdUsuario() == id)
                            .findFirst().orElse(null);
                    request.setAttribute("usuario", u);
                    request.getRequestDispatcher("WEB-INF/detalleUsuario.jsp").forward(request, response);
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
    }*/

    /**
     * Maneja peticiones POST: login, registrar y actualizar usuario.
     * Ejemplos:
     *   POST /usuario?action=login
     *   POST /usuario?action=registrar
     *   POST /usuario?action=actualizar
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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

                    if (usuarioLogueado != null) {
                        request.getSession().setAttribute("usuarioActual", usuarioLogueado);
                        // Si usás JSP:
                        request.getRequestDispatcher("WEB-INF/panelUsuario.jsp").forward(request, response);
                        // Si usás React, en cambio podrías responder con JSON:
                        // response.setContentType("application/json");
                        // response.getWriter().write("{\"status\":\"ok\", \"usuario\":\""+usuarioLogueado.getNombreCompleto()+"\"}");
                    } else {
                        request.setAttribute("error", "Correo o contraseña incorrectos.");
                        request.getRequestDispatcher("login.jsp").forward(request, response);
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

                    String nroSocioStr = request.getParameter("nro_socio");
                    if (nroSocioStr != null && !nroSocioStr.isEmpty()) {
                        nuevo.setNroSocio(Integer.parseInt(nroSocioStr));
                    }

                    logicUsuario.add(nuevo);
                    request.setAttribute("mensaje", "Usuario registrado correctamente.");
                    request.getRequestDispatcher("login.jsp").forward(request, response);
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
                    String nroSocioStr = request.getParameter("nro_socio");
                    if (nroSocioStr != null && !nroSocioStr.isEmpty()) {
                        u.setNroSocio(Integer.parseInt(nroSocioStr));
                    }

                    logicUsuario.update(u);
                    response.sendRedirect("usuario?action=listar");
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
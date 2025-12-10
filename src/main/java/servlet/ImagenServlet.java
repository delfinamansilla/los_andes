package servlet;

import java.io.IOException;
import data.DataUsuario; // Importa tu DAO
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/imagen")
public class ImagenServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int idUsuario = Integer.parseInt(request.getParameter("id"));
        
        DataUsuario du = new DataUsuario();
        byte[] imagenData = du.getFotoById(idUsuario);

        if (imagenData != null && imagenData.length > 0) {
            response.setContentType("image/jpeg"); 
            response.setContentLength(imagenData.length);
            response.getOutputStream().write(imagenData);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
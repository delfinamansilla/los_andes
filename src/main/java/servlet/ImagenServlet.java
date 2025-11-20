// En tu paquete servlet, crea este nuevo archivo
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
            // Asumimos que las imágenes son JPEG, puedes hacerlo más inteligente
            // si guardas el tipo de imagen en otra columna de la BD.
            response.setContentType("image/jpeg"); 
            response.setContentLength(imagenData.length);
            // Escribimos los bytes de la imagen directamente en la respuesta
            response.getOutputStream().write(imagenData);
        } else {
            // Si no hay imagen, devolvemos un error 404 (Not Found)
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
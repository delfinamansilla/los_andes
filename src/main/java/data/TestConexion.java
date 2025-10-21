package data;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConexion {

	public static void main(String[] args) {
		
		System.out.println("Iniciando prueba de conexión...");
		
		// 1. Obtenemos una instancia del DbConnector
		DbConnector connector = DbConnector.getInstancia();
		
		// 2. Pedimos una conexión a la base de datos
		Connection conn = null;
		
		try {
			conn = connector.getConn();
			
			// 3. Verificamos si la conexión es válida
			if (conn != null && !conn.isClosed()) {
				// Si la conexión no es nula y no está cerrada, ¡funcionó!
				System.out.println("¡Conexión a la base de datos 'los_andes' exitosa!");
				System.out.println("Detalles de la conexión: " + conn.toString());
			} else {
				// Si es nula o está cerrada, algo salió mal.
				System.out.println("ERROR: No se pudo conectar a la base de datos.");
			}
			
		} catch (SQLException e) {
			// Si ocurre una excepción SQL, la conexión falló.
			System.out.println("ERROR: Ocurrió un error de SQL al intentar conectar.");
			e.printStackTrace(); // Imprime el detalle del error
			
		} finally {
			// 4. Cerramos la conexión para ser ordenados
			if (conn != null) {
				connector.releaseConn();
				System.out.println("Conexión liberada.");
			}
		}
		
		System.out.println("Prueba de conexión finalizada.");
	}
}
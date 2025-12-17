package data;

import java.sql.*;

public class DbConnector {

    private static DbConnector instancia;
    private Connection conn = null;

    private DbConnector() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static synchronized DbConnector getInstancia() {
        if (instancia == null) {
            instancia = new DbConnector();
        }
        return instancia;
    }

    public Connection getConn() {
        try {
            if (conn == null || conn.isClosed()) {
                String host = System.getenv("MYSQLHOST");
                String port = System.getenv("MYSQLPORT");
                String user = System.getenv("MYSQLUSER");
                String password = System.getenv("MYSQLPASSWORD");
                String db = System.getenv("MYSQLDATABASE");

                if (host == null) {
                
                    host = "switchyard.proxy.rlwy.net"; 
                    port = "18386"; 
                    user = "root";
                    password = "sbZNmaGRhgoaqiMYhPysbuiRDOGeCuVl"; 
                    db = "railway";
                }

                String url = "jdbc:mysql://" + host + ":" + port + "/" + db + "?allowPublicKeyRetrieval=true&useSSL=false";
                
                conn = DriverManager.getConnection(url, user, password);
                System.out.println("🟢 Conexión establecida con éxito.");
            }
        } catch (SQLException e) {
            System.err.println("🔴 Error al conectar a la BD:");
            e.printStackTrace();
        }
        return conn;
    }

    public void releaseConn() {
    }
    
}
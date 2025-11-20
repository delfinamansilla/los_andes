package data;

import java.sql.*;

public class DbConnector {

    private static DbConnector instancia;
    
    private String driver="com.mysql.cj.jdbc.Driver";
    private String host="localhost";
    private String port="3306";
    private String user="los_andes_user";
    private String password="lasnenas";
    private String db="los_andes";
    
    private Connection conn=null;
    
    private DbConnector() {
        try {
            Class.forName(driver);
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
            // Si no hay conexión o se cayó, la creamos de nuevo
            if(conn==null || conn.isClosed()) {
                conn=DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+db, user, password);
                System.out.println("🟢 Conexión a BD establecida.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
    
    public void releaseConn() {
        // 🛑 NO HACEMOS NADA AQUÍ.
        // No cerramos la conexión. La dejamos abierta para las siguientes peticiones.
        // Al ser un Singleton, se reutiliza la misma siempre.
    }
}
package data;

import java.sql.*;

public class DbConnector {

    private static DbConnector instancia;
    
    private String driver="com.mysql.cj.jdbc.Driver";
    private String host="mysql.railway.internal";
    private String port="3306";
    private String user="root";
    private String password="DZzFsjFltPtFZLhFqPSPHyExwsgUVnIm";
    private String db="railway";
    
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
            if(conn==null || conn.isClosed()) {
            	conn=DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+db+"?allowPublicKeyRetrieval=true&useSSL=false", user, password);
                System.out.println("🟢 Conexión a BD establecida.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
    
    public void releaseConn() {

    }
}
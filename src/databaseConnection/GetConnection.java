package databaseConnection;


import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class GetConnection {
    private static Connection con= null;
    private static final String PROPERTIES_FILE="db.properties";

    public static Connection connectWithDatabase() throws SQLException, IOException{
        if(con==null || con.isClosed()){
            Properties properties =new Properties();
            FileInputStream fs=new FileInputStream(PROPERTIES_FILE);
            properties.load(fs);
            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");
            con = DriverManager.getConnection(url, user, password);
            con.setAutoCommit(false);
        }
        return con;
    }

}

package conn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/etc_db";
        String user = "postgres";
        String password = "root";

        return DriverManager.getConnection(url, user, password);
    }
}

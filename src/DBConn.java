import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConn {
    private static final String URL      = "jdbc:mysql://localhost:3306/librarydb";
    private static final String USER     = "root";
    private static final String PASSWORD = "password";

    private static Connection instance;

    private DBConn() {}

    // Singleton pattern: reuse one connection across the app
    public static Connection getInstance() throws SQLException {
        if (instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(URL, USER, PASSWORD);
        }

        return instance;
    }
}
package database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConn {
    private static Connection instance;

    private DBConn() {}

    public static synchronized Connection getInstance() throws SQLException {
        if (instance == null || instance.isClosed()) {
            Path dataDirectory = dataDirectory();
            try {
                Files.createDirectories(dataDirectory);
            } catch (IOException error) {
                throw new SQLException("The local library data folder could not be created.", error);
            }

            String databasePath = dataDirectory.resolve("librarydb").toAbsolutePath().toString();
            String url = "jdbc:h2:file:" + databasePath
                    + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;NON_KEYWORDS=USER";
            instance = DriverManager.getConnection(url, "sa", "");
            EmbeddedDatabase.initialize(instance);
        }
        return instance;
    }

    public static Path dataDirectory() {
        String override = System.getProperty("libris.data.dir");
        if (override != null && !override.isBlank()) return Path.of(override);
        return Path.of(System.getProperty("user.home"), ".libris");
    }
}

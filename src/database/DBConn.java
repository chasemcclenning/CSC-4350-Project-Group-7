package database;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConn {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/librarydb";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "password";
    private static final Path CONFIG_FILE = Path.of("config", "database.properties");
    private static final Properties LOCAL_SETTINGS = loadLocalSettings();

    private static Connection instance;

    private DBConn() {}

    public static synchronized Connection getInstance() throws SQLException {
        if (instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(
                    setting("LIBRARY_DB_URL", "url", DEFAULT_URL),
                    setting("LIBRARY_DB_USER", "user", DEFAULT_USER),
                    setting("LIBRARY_DB_PASSWORD", "password", DEFAULT_PASSWORD)
            );
        }
        return instance;
    }

    public static synchronized void saveLocalSettings(String url, String user, String password)
            throws IOException, SQLException {
        Files.createDirectories(CONFIG_FILE.getParent());
        Properties properties = new Properties();
        properties.setProperty("url", url);
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        try (OutputStream output = Files.newOutputStream(CONFIG_FILE)) {
            properties.store(output, "Local MySQL settings - ignored by Git");
        }
        LOCAL_SETTINGS.clear();
        LOCAL_SETTINGS.putAll(properties);
        if (instance != null && !instance.isClosed()) instance.close();
        instance = null;
    }

    private static String setting(String environmentName, String propertyName, String fallback) {
        String environmentValue = System.getenv(environmentName);
        if (environmentValue != null && !environmentValue.isBlank()) return environmentValue;

        String localValue = LOCAL_SETTINGS.getProperty(propertyName);
        return localValue == null || localValue.isBlank() ? fallback : localValue;
    }

    private static Properties loadLocalSettings() {
        Properties properties = new Properties();
        if (!Files.isRegularFile(CONFIG_FILE)) return properties;
        try (InputStream input = Files.newInputStream(CONFIG_FILE)) {
            properties.load(input);
            return properties;
        } catch (IOException error) {
            System.err.println("Could not read " + CONFIG_FILE + ": " + error.getMessage());
            return properties;
        }
    }
}

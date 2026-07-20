package database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

final class EmbeddedDatabase {
    private static final String SEED_RESOURCE = "/database/seed-h2.sql";

    private EmbeddedDatabase() {}

    static void initialize(Connection connection) throws SQLException {
        if (hasSchema(connection)) return;

        boolean previousAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            for (String statement : seedStatements()) {
                if (statement.isBlank()) continue;
                try (Statement sql = connection.createStatement()) {
                    sql.execute(statement);
                }
            }
            connection.commit();
        } catch (SQLException | IOException error) {
            connection.rollback();
            throw new SQLException("The bundled library data could not be initialized.", error);
        } finally {
            connection.setAutoCommit(previousAutoCommit);
        }
    }

    private static boolean hasSchema(Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES "
                + "WHERE TABLE_SCHEMA='public' AND TABLE_NAME='title'";
        try (Statement statement = connection.createStatement(); ResultSet result = statement.executeQuery(sql)) {
            result.next();
            return result.getInt(1) > 0;
        }
    }

    private static String[] seedStatements() throws IOException {
        try (InputStream input = EmbeddedDatabase.class.getResourceAsStream(SEED_RESOURCE)) {
            if (input == null) throw new IOException("Missing resource " + SEED_RESOURCE);
            String script = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            return script.lines()
                    .filter(line -> !line.stripLeading().startsWith("--"))
                    .reduce("", (left, right) -> left + right + "\n")
                    .split(";(?=(?:[^']*'[^']*')*[^']*$)");
        }
    }
}

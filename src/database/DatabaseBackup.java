package database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseBackup {
    private DatabaseBackup() {}

    public static void write(Path destination) throws SQLException, IOException {
        Path absoluteDestination=destination.toAbsolutePath();
        if(absoluteDestination.getParent()!=null)Files.createDirectories(absoluteDestination.getParent());
        String escapedPath=absoluteDestination.toString().replace("'","''");
        try(Statement statement=DBConn.getInstance().createStatement()){
            statement.execute("SCRIPT DROP TO '"+escapedPath+"' CHARSET 'UTF-8'");
        }
    }
}

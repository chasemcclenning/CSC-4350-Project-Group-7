package database;

import dao.UserDAO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class EmbeddedDatabaseIntegrationTest {
    @TempDir Path dataDirectory;

    @Test
    void seedsCurrentSnapshotAndCompletesCirculationWorkflow() throws Exception {
        System.setProperty("libris.data.dir",dataDirectory.toString());

        LibraryRepository repository=new LibraryRepository();
        assertEquals(4,repository.countTitles());
        assertEquals(8,repository.findUsers("").size());
        assertEquals(4,repository.findTitles("","","").size());
        assertTrue(new UserDAO().authenticate("librarian@example.com","libexample1").isPresent());
        assertTrue(new UserDAO().authenticate("member@example.com","memexample1").isPresent());

        int activeBefore=repository.countActiveCheckouts();
        repository.checkoutTitle("u-007","t-003",LocalDateTime.now().plusDays(14));
        assertEquals(activeBefore+1,repository.countActiveCheckouts());

        var borrowedDune=repository.findCheckouts("u-007",true).stream()
                .filter(row->"Dune".equals(row.title()))
                .findFirst();
        assertTrue(borrowedDune.isPresent());
        repository.returnCheckout(borrowedDune.orElseThrow().id());
        assertEquals(activeBefore,repository.countActiveCheckouts());
        assertFalse(repository.findAuditRows().isEmpty());

        Path backup=dataDirectory.resolve("library-backup.sql");
        DatabaseBackup.write(backup);
        assertTrue(Files.size(backup)>0);
    }
}

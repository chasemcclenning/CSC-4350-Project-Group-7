package database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dao.UserDAO;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class LibrisTest {
    @TempDir Path dataDirectory;

    private UserDAO userDAO;
    private LibraryRepository repository;

    @BeforeEach
    void setup() throws SQLException {
        System.setProperty("libris.data.dir", dataDirectory.toString());

        // Close a connection left by another test so this test opens its own
        // temporary database instead of changing the user's library data.
        DBConn.getInstance().close();

        userDAO = new UserDAO();
        repository = new LibraryRepository();
    }

    @AfterEach
    void cleanup() throws SQLException {
        DBConn.getInstance().close();
        System.clearProperty("libris.data.dir");
    }

    @Test
    void blocksLoginWhenPasswordIsIncorrect() throws SQLException {
        String email = "member@example.com";

        // Confirm the account exists and its correct password is accepted.
        assertTrue(userDAO.authenticate(email, "memexample1").isPresent());

        // The same account must not authenticate with an incorrect password.
        assertTrue(userDAO.authenticate(email, "wrong-password").isEmpty());
    }

    @Test
    void blocksCheckoutWhenNoCopyIsAvailable() throws SQLException {
        String unavailableTitleId = "t-001";
        int activeCheckoutsBefore = repository.countActiveCheckouts();

        assertEquals(0, repository.findTitleById(unavailableTitleId).availableCopies());

        SQLException error = assertThrows(SQLException.class, () ->
                repository.checkoutTitle("u-007", unavailableTitleId,
                        LocalDateTime.now().plusDays(14)));

        assertEquals("No available copy remains for this title.", error.getMessage());
        assertEquals(activeCheckoutsBefore, repository.countActiveCheckouts());
    }
}

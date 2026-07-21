package database;

import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dao.UserDAO;
import model.User;

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

    @Test
    void createAndVerifyUser() throws SQLException {
        String email = "newuser@example.com";
        String password = "newuserpassword";
        String name = "Test User";

        UserDAO userDAO = new UserDAO();
        User user = new User(name, email, password, "patron", 0.0);

        userDAO.createUser(user);

        assertTrue(userDAO.authenticate(email, password).isPresent());
    }
}
package test.java.database;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;
import java.util.ArrayList;

import dao.*;
import database.DBConn;

public class LibrisTest {
    
    private static Connection conn;
    private static AuditLogDAO auditLog;
    private static UserDAO userDAO;
    private static CheckoutDAO checkoutDAO;
    private static HoldDAO holdDAO;
    private static FineDAO fineDAO;

    // sets up objects which will be used by everything
    //@BeforeAll
    static void setup() throws SQLException {
        conn = DBConn.getInstance();
        auditLog = new AuditLogDAO();
        userDAO = new UserDAO();
        checkoutDAO = new CheckoutDAO();
        holdDAO = new HoldDAO();
        fineDAO = new FineDAO();
    }

    // runs before each test, inserts base data to be tested
    //@BeforeEach

}

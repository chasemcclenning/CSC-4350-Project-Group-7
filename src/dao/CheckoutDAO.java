package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import database.DBConn;
import model.Checkout;

public class CheckoutDAO {

    // create a new checkout (14-day loan period)
    public void createCheckout(Checkout checkout) throws SQLException {
        Connection conn = DBConn.getInstance();

        String sql = "INSERT INTO checkout (user_id, copy_id, checked_out_at, due_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, checkout.getMemberId());
            stmt.setString(2, checkout.getCopyId());
            stmt.setDate(3, new java.sql.Date(checkout.getCheckoutDate().getTime()));
            stmt.setDate(4, new java.sql.Date(checkout.getDueDate().getTime()));
            stmt.executeUpdate();
        }
    }

    // instead of just reading one at a time, easier to read them all and then cut down with a filter class later
    public ArrayList<Checkout> getAllCheckouts() throws SQLException {
        Connection conn = DBConn.getInstance();

        ArrayList<Checkout> checkouts = new ArrayList<>();

        String sql = "SELECT * FROM checkout";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String checkoutId = rs.getString("id");
                String memberId = rs.getString("user_id");
                String copyId = rs.getString("copy_id");
                java.util.Date checkoutDate = rs.getTimestamp("checked_out_at");
                java.util.Date dueDate = rs.getTimestamp("due_at");
                java.util.Date returnDate = rs.getTimestamp("returned_at");

                Checkout checkout = new Checkout(checkoutId, memberId, copyId, checkoutDate, dueDate, returnDate);
                checkouts.add(checkout);
            }
        }
        return checkouts;
    }

    // update checkout details (NOT for returns)
    public void updateCheckoutDetails(Checkout checkout) throws SQLException {
        Connection conn = DBConn.getInstance();

        String sql = "UPDATE checkout SET user_id = ?, copy_id = ?, checked_out_at = ?, due_at = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, checkout.getMemberId());
            stmt.setString(2, checkout.getCopyId());
            stmt.setDate(3, new java.sql.Date(checkout.getCheckoutDate().getTime()));
            stmt.setDate(4, new java.sql.Date(checkout.getDueDate().getTime()));
            stmt.setString(5, checkout.getCheckoutId());
            stmt.executeUpdate();
        }
    }

    // update checkout (JUST FOR RETURNS)
    public void updateCheckoutReturn(Checkout checkout) throws SQLException {
        Connection conn = DBConn.getInstance();

        String sql = "UPDATE checkout SET returned_at = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(checkout.getReturnDate().getTime()));
            stmt.setString(2, checkout.getCheckoutId());
            stmt.executeUpdate();
        }
    }

    // delete a checkout
    public void deleteCheckout(String checkoutId) throws SQLException {
        Connection conn = DBConn.getInstance();

        String sql = "DELETE FROM checkout WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, checkoutId);
            stmt.executeUpdate();
        }
    }
}

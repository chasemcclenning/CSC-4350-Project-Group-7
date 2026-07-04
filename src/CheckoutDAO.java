import java.sql.*;
import java.util.ArrayList;

public class CheckoutDAO {

    // create a new checkout (14-day loan period)
    public void createCheckout(Checkout checkout) throws SQLException {
        Connection conn = DBConn.getInstance();

        String sql = "INSERT INTO checkout (member_id, copy_id, checkout_date, due_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, checkout.getMemberId());
            stmt.setInt(2, checkout.getCopyId());
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
                int checkoutId = rs.getInt("checkout_id");
                int memberId = rs.getInt("member_id");
                int copyId = rs.getInt("copy_id");
                Date checkoutDate = rs.getDate("checkout_date");
                Date dueDate = rs.getDate("due_date");
                Date returnDate = rs.getDate("return_date");

                Checkout checkout = new Checkout(checkoutId, memberId, copyId, checkoutDate, dueDate, returnDate);
                checkouts.add(checkout);
            }
        }
        return checkouts;
    }

    // update checkout details (NOT for returns)
    public void updateCheckoutDetails(Checkout checkout) throws SQLException {
        Connection conn = DBConn.getInstance();

        String sql = "UPDATE checkout SET member_id = ?, copy_id = ?, checkout_date = ?, due_date = ? WHERE checkout_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, checkout.getMemberId());
            stmt.setInt(2, checkout.getCopyId());
            stmt.setDate(3, new java.sql.Date(checkout.getCheckoutDate().getTime()));
            stmt.setDate(4, new java.sql.Date(checkout.getDueDate().getTime()));
            stmt.setInt(5, checkout.getCheckoutId());
            stmt.executeUpdate();
        }
    }

    // update checkout (JUST FOR RETURNS)
    public void updateCheckoutReturn(Checkout checkout) throws SQLException {
        Connection conn = DBConn.getInstance();

        String sql = "UPDATE checkout SET return_date = ? WHERE checkout_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(checkout.getReturnDate().getTime()));
            stmt.setInt(2, checkout.getCheckoutId());
            stmt.executeUpdate();
        }
    }

    // delete a checkout
    public void deleteCheckout(int checkoutId) throws SQLException {
        Connection conn = DBConn.getInstance();

        String sql = "DELETE FROM checkout WHERE checkout_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, checkoutId);
            stmt.executeUpdate();
        }
    }
}

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

public class CheckoutDAO {

    public Date calculateDueDate(Date checkoutDate) {
        long dueDateMillis = checkoutDate.getTime() + (14L * 24 * 60 * 60 * 1000); // 14 days are added onto the checkout date (done in milliseconds)
        Date dueDate = new Date(dueDateMillis);
        return dueDate;
    }

    // create a new checkout (14-day loan period)
    // use the 1st cvonstructor in Checkout.java (without id or return date)
    public void createCheckout(Checkout checkout) throws SQLException {
        Connection conn = DBConn.getInstance();

        String sql = "INSERT INTO checkout (user_id, copy_id, checked_out_at, due_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, checkout.getMemberId());
            stmt.setString(2, checkout.getCopyId());
            stmt.setDate(3, new java.sql.Date(checkout.getCheckoutDate().getTime()));
            stmt.setDate(4, new java.sql.Date(calculateDueDate(checkout.getCheckoutDate()).getTime()));
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
                String checkoutID = rs.getString("id");
                String memberId = rs.getString("user_id");
                String copyID = rs.getString("copy_id");
                Date checkoutDate = rs.getDate("checked_out_at");
                Date dueDate = rs.getDate("due_at");
                Date returnDate = rs.getDate("returned_at");

                Checkout checkout = new Checkout(checkoutID, memberId, copyID, checkoutDate, dueDate, returnDate);
                checkouts.add(checkout);
            }
        }
        return checkouts;
    }

    // update checkout details (NOT for returns)
    public void updateCheckoutDetails(Checkout checkout) throws SQLException {
        Connection conn = DBConn.getInstance();

        String sql = "UPDATE checkout SET user_id = ?, copy_id = ?, checked_out_at = ?, due_at = ? WHERE checkout_id = ?";
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
    public void returnBook(String checkoutID) throws SQLException {
        Connection conn = DBConn.getInstance();

        String sql = "UPDATE checkout SET returned_at = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(new Date().getTime()));
            stmt.setString(2, checkoutID);
            stmt.executeUpdate();
        }
    }

    // delete a checkout
    public void deleteCheckout(int checkoutId) throws SQLException {
        Connection conn = DBConn.getInstance();

        String sql = "DELETE FROM checkout WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, checkoutId);
            stmt.executeUpdate();
        }
    }
}

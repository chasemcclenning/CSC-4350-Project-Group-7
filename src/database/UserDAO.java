package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;

import model.User;

public class UserDAO {

    public Optional<User> authenticate(String email, String password) throws SQLException {
        String sql = "SELECT id, name, email, password, role, fines_owed "
                + "FROM user WHERE LOWER(email) = LOWER(?) AND password = ?";
        try (PreparedStatement stmt = DBConn.getInstance().prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(readUser(rs)) : Optional.empty();
            }
        }
    }

    public void createUser(User user) throws SQLException {
        String sql = "INSERT INTO user (name, email, password, role, fines_owed) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DBConn.getInstance().prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole());
            stmt.setDouble(5, user.getFinesOwed());
            stmt.executeUpdate();
        }
    }

    public ArrayList<User> getAllUsers() throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        try (Statement stmt = DBConn.getInstance().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name, email, password, role, fines_owed FROM user")) {
            while (rs.next()) users.add(readUser(rs));
        }
        return users;
    }

    public void updateUserDetails(User user) throws SQLException {
        String sql = "UPDATE user SET name = ?, email = ?, password = ?, role = ?, fines_owed = ? WHERE id = ?";
        try (PreparedStatement stmt = DBConn.getInstance().prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole());
            stmt.setDouble(5, user.getFinesOwed());
            stmt.setString(6, user.getUserID());
            stmt.executeUpdate();
        }
    }

    public void deleteUser(String userID) throws SQLException {
        try (PreparedStatement stmt = DBConn.getInstance().prepareStatement("DELETE FROM user WHERE id = ?")) {
            stmt.setString(1, userID);
            stmt.executeUpdate();
        }
    }

    public boolean resetPassword(String email, String newPassword) throws SQLException {
        String sql = "UPDATE user SET password = ? WHERE LOWER(email) = LOWER(?)";
        try (PreparedStatement stmt = DBConn.getInstance().prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, email);
            return stmt.executeUpdate() == 1;
        }
    }

    private User readUser(ResultSet rs) throws SQLException {
        return new User(rs.getString("id"), rs.getString("name"), rs.getString("email"),
                rs.getString("password"), rs.getString("role"), rs.getDouble("fines_owed"));
    }
}

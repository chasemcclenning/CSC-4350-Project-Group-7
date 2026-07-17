package model;

public class User {
    private final String userID;
    private final String name;
    private final String email;
    private final String password;
    private final String role;
    private final double finesOwed;

    public User(String userID, String name, String email, String password, String role, double finesOwed) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.finesOwed = finesOwed;
    }

    public boolean isLibrarian() {
        return "librarian".equalsIgnoreCase(role) || "admin".equalsIgnoreCase(role);
    }

    public boolean canCheckout() {
        return finesOwed <= 0;
    }

    public String getUserID() { return userID; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public double getFinesOwed() { return finesOwed; }
}

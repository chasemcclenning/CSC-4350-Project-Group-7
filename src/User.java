public class User {
    private String userID;
    private String name;
    private String email;
    private String password;    // need to come up with a way to store these
    private String role;        // either "admin" or "member"
    private double finesOwed;  // total of all outstanding fines for this user

    // constructor for creating a user for the first time, where fines owed is initialized to 0.0
    public User(String userID, String name, String email, String password, String role) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.finesOwed = 0.0; // Initialize fines owed to 0.0
    }

    public User(String userID, String name, String email, String password, String role, double finesOwed) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.finesOwed = finesOwed;
    }

    public boolean isAdmin() {
        return role.equals("admin");
    }

    public boolean canCheckout() {
        return finesOwed <= 0;
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public double getFinesOwed() {
        return finesOwed;
    }

    @Override
    public String toString() {
        return "User ID: " + userID + "\n" +
                "Name: " + name + "\n" +
                "Email: " + email + "\n" +
                "Role: " + role + "\n" +
                "Fines Owed: $" + finesOwed;
    }
}

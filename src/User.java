public class User {
    private String userID;
    private String name;
    private String email;
    private String password;    // need to come up with a way to store these
    private String role;        // either "admin" or "member"
    private double fines_owed;  // total of all outstanding fines for this user

    public User(String userID, String name, String email, String password, String role, double fines_owed) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.fines_owed = fines_owed;
    }

    public boolean isAdmin() {
        return role.equals("admin");
    }

    public boolean canCheckout() {
        return fines_owed <= 0;
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

    public double getFines_owed() {
        return fines_owed;
    }

    @Override
    public String toString() {
        return "User ID: " + userID + "\n" +
                "Name: " + name + "\n" +
                "Email: " + email + "\n" +
                "Role: " + role + "\n" +
                "Fines Owed: $" + fines_owed;
    }
}

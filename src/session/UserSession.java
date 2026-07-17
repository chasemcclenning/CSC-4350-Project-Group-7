package session;

import model.User;

public final class UserSession {
    private static User currentUser;

    private UserSession() {}

    public static User getCurrentUser() { return currentUser; }
    public static void signIn(User user) { currentUser = user; }
    public static void signOut() { currentUser = null; }
    public static boolean isSignedIn() { return currentUser != null; }
}

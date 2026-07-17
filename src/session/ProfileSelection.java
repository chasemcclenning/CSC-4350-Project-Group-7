package session;

public final class ProfileSelection {
    private static String bookId;
    private static String userId;
    private ProfileSelection() {}
    public static String getBookId(){return bookId;}
    public static void setBookId(String id){bookId=id;}
    public static String getUserId(){return userId;}
    public static void setUserId(String id){userId=id;}
}

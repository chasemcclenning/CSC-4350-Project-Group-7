import java.util.Date;

public class Hold {
    private int holdID;
    private int userID;
    private int titleID;
    private int queuePosition;
    private String status;  // "Active", "Fulfilled", "Cancelled"
    private Date placedDate;
    // unsure if there should be a fulfilledDate or not
    private Date expirationDate;

    public Hold(int holdID, int userID, int titleID, int queuePosition, String status, Date placedDate, Date expirationDate) {
        this.holdID = holdID;
        this.userID = userID;
        this.titleID = titleID;
        this.queuePosition = queuePosition;
        this.status = status;
        this.placedDate = placedDate;
        this.expirationDate = expirationDate;
    }

    public int getHoldID() {
        return holdID;
    }

    public int getUserID() {
        return userID;
    }

    public int getTitleID() {
        return titleID;
    }

    public int getQueuePosition() {
        return queuePosition;
    }

    public String getStatus() {
        return status;
    }

    public Date getPlacedDate() {
        return placedDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    @Override
    public String toString() {
        return "Hold ID: " + holdID +
               ", User ID: " + userID +
               ", Title ID: " + titleID +
               ", Queue Position: " + queuePosition +
               ", Status: " + status +
               ", Placed Date: " + placedDate +
               ", Expiration Date: " + expirationDate;
    }
}
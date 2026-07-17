import java.util.Date;

public class Hold {
    private String holdID;
    private String userID;
    private String titleID;
    private int queuePosition;
    private String status;          // "Active", "Fulfilled", "Cancelled"
    private Date placedAt;
    private Date expiresAt;

    public Hold(String holdID, String userID, String titleID, int queuePosition, String status, Date placedAt, Date expiresAt) {
        this.holdID = holdID;
        this.userID = userID;
        this.titleID = titleID;
        this.queuePosition = queuePosition;
        this.status = status;
        this.placedAt = placedAt;
        this.expiresAt = expiresAt;
    }

    public String getHoldID() {
        return holdID;
    }

    public String getUserID() {
        return userID;
    }

    public String getTitleID() {
        return titleID;
    }

    public int getQueuePosition() {
        return queuePosition;
    }

    public String getStatus() {
        return status;
    }

    public Date getPlacedAt() {
        return placedAt;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    @Override
    public String toString() {
        return "Hold ID: " + holdID +
               ", User ID: " + userID +
               ", Title ID: " + titleID +
               ", Queue Position: " + queuePosition +
               ", Status: " + status +
               ", Placed Date: " + placedAt +
               ", Expiration Date: " + expiresAt;
    }
}

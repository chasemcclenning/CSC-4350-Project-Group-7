import java.util.Date;

public class Hold {
    private String holdID;
    private String userID;
    private String copyID;
    private int queuePosition;
    private String status;          // "Active", "Fulfilled", "Cancelled"
    private Date placedAt;
    private Date expiresAt;

    public Hold(String holdID, String userID, String copyID, int queuePosition, String status, Date placedAt, Date expiresAt) {
        this.holdID = holdID;
        this.userID = userID;
        this.copyID = copyID;
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

    public String getCopyID() {
        return copyID;
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
               ", Copy ID: " + copyID +
               ", Queue Position: " + queuePosition +
               ", Status: " + status +
               ", Placed Date: " + placedAt +
               ", Expiration Date: " + expiresAt;
    }
}
import java.util.Date;

public class AuditLog {
    private int LogID;
    private int userID;
    private String action;
    private String entityType;
    private int entityID;
    private String notes;
    private Date timestamp;

    public AuditLog(int logID, int userID, String action, String entityType, int entityID, String notes, Date timestamp) {
        this.LogID = logID;
        this.userID = userID;
        this.action = action;
        this.entityType = entityType;
        this.entityID = entityID;
        this.notes = notes;
        this.timestamp = timestamp;
    }

    public int getLogID() {
        return LogID;
    }

    public int getUserID() {
        return userID;
    }

    public String getAction() {
        return action;
    }

    public String getEntityType() {
        return entityType;
    }

    public int getEntityID() {
        return entityID;
    }

    public String getNotes() {
        return notes;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
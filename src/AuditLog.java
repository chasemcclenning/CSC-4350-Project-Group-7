import java.util.Date;

public class AuditLog {
    private int logID;
    private int userID;
    private String action;
    private String entityType;
    private int entityID;
    private String notes;
    private Date createdAt;

    public AuditLog(int logID, int userID, String action, String entityType, int entityID, String notes, Date createdAt) {
        this.logID = logID;
        this.userID = userID;
        this.action = action;
        this.entityType = entityType;
        this.entityID = entityID;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public int getLogID() {
        return logID;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "logID=" + logID +
                ", userID=" + userID +
                ", action='" + action + '\'' +
                ", entityType='" + entityType + '\'' +
                ", entityID=" + entityID +
                ", notes='" + notes + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
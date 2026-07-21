package model;
import java.util.Date;

public class AuditLog {
    private String logID;
    private String userID;
    private String action;
    private String entityType;
    private String entityID;
    private String notes;
    private Date createdAt;

    public AuditLog(String logID, String userID, String action, String entityType, String entityID, String notes, Date createdAt) {
        this.logID = logID;
        this.userID = userID;
        this.action = action;
        this.entityType = entityType;
        this.entityID = entityID;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public String getLogID() {
        return logID;
    }

    public String getUserID() {
        return userID;
    }

    public String getAction() {
        return action;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEntityID() {
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

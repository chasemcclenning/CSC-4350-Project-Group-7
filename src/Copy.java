public class Copy {
    private String copyID;
    private String titleID;     // reference to the title this copy belongs to
	private String status;		// available, checked_out, reserved, lost, damaged
	private String condition;	// new, good, fair, poor

    public Copy(String copyID, String titleID, String status, String condition) {
        this.copyID = copyID;
        this.titleID = titleID;
        this.status = status;
        this.condition = condition;
    }

    public String getCopyID() {
        return copyID;
    }

    public String getTitleID() {
        return titleID;
    }

    public String getStatus() {
        return status;
    }

    public String getCondition() {
        return condition;
    }

    @Override
    public String toString() {
        return "Copy ID: " + copyID + "\n" +
                "Title ID: " + titleID + "\n" +
                "Status: " + status + "\n" +
                "Condition: " + condition;
    }
}

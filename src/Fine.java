public class Fine {
    private String fineID;
    private int checkoutID;
    private int userID;
    private double amount;  // dollar value of fine
    private String status;  // "Paid", "Unpaid", "Waived"

    public Fine(String fineID, int checkoutID, int userID, double amount, String status) {
        this.fineID = fineID;
        this.checkoutID = checkoutID;
        this.userID = userID;
        this.amount = amount;
        this.status = status;
    }

    public String getFineID() {
        return fineID;
    }

    public int getCheckoutID() {
        return checkoutID;
    }

    public int getUserID() {
        return userID;
    }

    public double getFineAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Fine ID: " + fineID +
               ", Checkout ID: " + checkoutID +
               ", User ID: " + userID +
               ", Fine Amount: " + amount +
               ", Status: " + status;
    }
}
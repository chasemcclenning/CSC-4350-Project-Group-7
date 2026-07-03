public class Fine {
    private int fineID;
    private int CheckoutID;
    private int userID;
    private double fineAmount;
    private String status;  // "Paid", "Unpaid", "Waived"

    public Fine(int fineID, int checkoutID, int userID, double fineAmount, String status) {
        this.fineID = fineID;
        this.CheckoutID = checkoutID;
        this.userID = userID;
        this.fineAmount = fineAmount;
        this.status = status;
    }

    public int getFineID() {
        return fineID;
    }

    public int getCheckoutID() {
        return CheckoutID;
    }

    public int getUserID() {
        return userID;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Fine ID: " + fineID +
               ", Checkout ID: " + CheckoutID +
               ", User ID: " + userID +
               ", Fine Amount: " + fineAmount +
               ", Status: " + status;
    }
}
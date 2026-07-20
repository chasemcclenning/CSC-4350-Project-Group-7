import java.util.Date;

public class Checkout {
    private String checkoutID;
    private String memberID;
    private String copyID;
    private Date checkoutDate;
    private Date dueDate;
    private Date returnDate;

    // constrctor for a new checkout (checkoutID AND returnDate are unknown)
    public Checkout(String memberID, String copyID, Date checkoutDate, Date dueDate) {
        this.memberID = memberID;
        this.copyID = copyID;
        this.checkoutDate = checkoutDate;
        this.dueDate = dueDate;
        this.returnDate = null;
    }

    // full constructor (for read statements)
    public Checkout(String checkoutID, String memberID, String copyID, Date checkoutDate, Date dueDate, Date returnDate) {
        this.checkoutID = checkoutID;
        this.memberID = memberID;
        this.copyID = copyID;
        this.checkoutDate = checkoutDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    public String getCheckoutId() {
        return checkoutID;
    }

    public String getMemberId() {
        return memberID;
    }

    public String getCopyId() {
        return copyID;
    }

    public Date getCheckoutDate() {
        return checkoutDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }
    
    @Override
    public String toString() {
        return "Checkout ID: " + checkoutID +
               ", Member ID: " + memberID +
               ", Copy ID: " + copyID +
               ", Checkout Date: " + checkoutDate +
               ", Due Date: " + dueDate +
               ", Return Date: " + returnDate;
    }
}

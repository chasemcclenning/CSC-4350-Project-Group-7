import java.util.Date;

public class Checkout {
    private int checkoutId;
    private int memberID;
    private int copyID;
    private Date checkoutDate;
    private Date dueDate;
    private Date returnDate;

    // constrctor for a new checkout (checkoutID AND returnDate are unknown)
    public Checkout(int memberID, int copyID, Date checkoutDate, Date dueDate) {
        this.memberID = memberID;
        this.copyID = copyID;
        this.checkoutDate = checkoutDate;
        this.dueDate = dueDate;
        this.returnDate = null;
    }

    // full constructor (for read statements)
    public Checkout(int checkoutId, int memberID, int copyID, Date checkoutDate, Date dueDate, Date returnDate) {
        this.checkoutId = checkoutId;
        this.memberID = memberID;
        this.copyID = copyID;
        this.checkoutDate = checkoutDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    public int getCheckoutId() {
        return checkoutId;
    }

    public int getMemberId() {
        return memberID;
    }

    public int getCopyId() {
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
/* will figure out this logic later
    public void returnBook(Date returnDate) {
        this.returnDate = returnDate;
        book.returnBook();
    }
*/
    @Override
    public String toString() {
        return "Checkout ID: " + checkoutId +
               ", Member ID: " + memberID +
               ", Copy ID: " + copyID +
               ", Checkout Date: " + checkoutDate +
               ", Due Date: " + dueDate +
               ", Return Date: " + returnDate;
    }
}

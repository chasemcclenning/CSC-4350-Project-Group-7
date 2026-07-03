public class Checkout {
    private int checkoutId;
    private Member member;
    private Book book;
    private String checkoutDate;
    private String dueDate;
    private String returnDate;

    public Checkout(int checkoutId, Member member, Book book, String checkoutDate, String dueDate) {
        this.checkoutId = checkoutId;
        this.member = member;
        this.book = book;
        this.checkoutDate = checkoutDate;
        this.dueDate = dueDate;
        this.returnDate = "Not returned";
    }

    public int getCheckoutId() {
        return checkoutId;
    }

    public Member getMember() {
        return member;
    }

    public Book getBook() {
        return book;
    }

    public String getCheckoutDate() {
        return checkoutDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void returnBook(String returnDate) {
        this.returnDate = returnDate;
        book.returnBook();
    }

    @Override
    public String toString() {
        return "Checkout ID: " + checkoutId +
               ", Member: " + member.getName() +
               ", Book: " + book.getTitle() +
               ", Checkout Date: " + checkoutDate +
               ", Due Date: " + dueDate +
               ", Return Date: " + returnDate;
    }
}

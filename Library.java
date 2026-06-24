import java.util.ArrayList;

public class Library {
    private ArrayList<Book> books;
    private ArrayList<Member> members;

    public Library() {
        books = new ArrayList<Book>();
        members = new ArrayList<Member>();
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public void addMember(Member member) {
        members.add(member);
    }

    public void displayBooks() {
        for (Book book : books) {
            System.out.println(book);
        }
    }

    public Book searchBookByTitle(String title) {
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                return book;
            }
        }

        return null;
    }

    public Book searchBookByAuthor(String author) {
        for (Book book : books) {
            if (book.getAuthor().equalsIgnoreCase(author)) {
                return book;
            }
        }

        return null;
    }

    public void borrowBook(String title) {
        Book book = searchBookByTitle(title);

        if (book == null) {
            System.out.println("Book not found.");
        } else if (!book.isAvailable()) {
            System.out.println("Book is already borrowed.");
        } else {
            book.borrowBook();
            System.out.println("Book borrowed successfully.");
        }
    }

    public void returnBook(String title) {
        Book book = searchBookByTitle(title);

        if (book == null) {
            System.out.println("Book not found.");
        } else if (book.isAvailable()) {
            System.out.println("Book was not borrowed.");
        } else {
            book.returnBook();
            System.out.println("Book returned successfully.");
        }
    }
}

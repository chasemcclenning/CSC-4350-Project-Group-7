import java.util.ArrayList;

import model.Checkout;

// Library class manages books, members, and checkout records
public class Library {

    // Stores all books in the library
    private ArrayList<Book> books;

    // Stores all registered members
    private ArrayList<Member> members;

    // Stores all borrowing records
    private ArrayList<Checkout> checkouts;

    // Constructor initializes the ArrayLists
    public Library() {
        books = new ArrayList<Book>();
        members = new ArrayList<Member>();
        checkouts = new ArrayList<Checkout>();
    }

    // Adds a new book to the library collection
    public void addBook(Book book) {
        books.add(book);
    }

	// Removes a book from the library by its title
	public void removeBook(String title) {
    		Book book = searchBookByTitle(title);

    		if (book == null) {
        		System.out.println("Book not found.");
    		} else {
        		books.remove(book);
        		System.out.println("Book removed successfully.");
    		}
	}

	// Updates a book's information
	public void updateBook(String oldTitle, String newTitle, String newAuthor, String newGenre, String newIsbn) {
    		Book book = searchBookByTitle(oldTitle);

    		if (book == null) {
        	System.out.println("Book not found.");
    		} else {
        		book.setTitle(newTitle);
        		book.setAuthor(newAuthor);
        		book.setGenre(newGenre);
        		book.setIsbn(newIsbn);

		       	System.out.println("Book updated successfully.");
    		}
	}

    // Adds a new member to the library
    public void addMember(Member member) {
        members.add(member);
    }

    // Displays all books currently stored in the library
    public void displayBooks() {
        for (Book book : books) {
            System.out.println(book);
        }
    }

    // Displays all registered members
    public void displayMembers() {
        for (Member member : members) {
            System.out.println(member);
        }
    }

    // Searches for a book by title and returns it if found
    public Book searchBookByTitle(String title) {

        // Loop through every book in the collection
        for (Book book : books) {

            // Compare titles without considering uppercase/lowercase
            if (book.getTitle().equalsIgnoreCase(title)) {
                return book;
            }
        }

        // Return null if no matching book is found
        return null;
    }

    // Searches for a book by author and returns it if found
    public Book searchBookByAuthor(String author) {

        // Loop through every book in the collection
        for (Book book : books) {

            // Compare authors without considering uppercase/lowercase
            if (book.getAuthor().equalsIgnoreCase(author)) {
                return book;
            }
        }

        // Return null if no matching author is found
        return null;
    }

    // Searches for a book using its ISBN number
    public Book searchBookByIsbn(String isbn) {

        // Loop through every book in the collection
        for (Book book : books) {

            // Compare ISBN values
            if (book.getIsbn().equalsIgnoreCase(isbn)) {
                return book;
            }
        }

        // Return null if no matching ISBN is found
        return null;
    }

    // Allows a member to borrow a book
    public void borrowBook(Member member, String title,
                           String checkoutDate, String dueDate) {

        // Search for the requested book
        Book book = searchBookByTitle(title);

        // Check whether the book exists
        if (book == null) {

            System.out.println("Book not found.");

        // Check whether the book is already borrowed
        } else if (!book.isAvailable()) {

            System.out.println("Book is already borrowed.");

        } else {

            // Mark the book as borrowed
            book.borrowBook();

            // Generate a checkout ID
            int checkoutId = checkouts.size() + 1;

            // Create a new checkout record
            Checkout checkout = new Checkout(
                    checkoutId,
                    member,
                    book,
                    checkoutDate,
                    dueDate
            );

            // Store the checkout record
            checkouts.add(checkout);

            System.out.println("Book borrowed successfully.");
        }
    }

    // Allows a member to return a borrowed book
    public void returnBook(String title, String returnDate) {

        // Loop through all checkout records
        for (Checkout checkout : checkouts) {

            // Find an active checkout for this title
            if (checkout.getBook().getTitle().equalsIgnoreCase(title)
                    && checkout.getReturnDate().equals("Not returned")) {

                // Update the checkout record
                checkout.returnBook(returnDate);

                System.out.println("Book returned successfully.");

                return;
            }
        }

        // No active checkout record was found
        System.out.println("No active checkout found for this book.");
    }

    // Displays all borrowing records
    public void displayCheckouts() {

        // Loop through all checkout records
        for (Checkout checkout : checkouts) {
            System.out.println(checkout);
        }
    }
}

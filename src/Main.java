import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    private static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        Library library = new Library();
        seedLibrary(library);

        System.out.println("Library Management System");
        System.out.println("Main console demo created by Jamison Braxton");

        boolean running = true;

        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1:
                    library.displayBooks();
                    break;
                case 2:
                    searchBooks(library);
                    break;
                case 3:
                    borrowBook(library);
                    break;
                case 4:
                    returnBook(library);
                    break;
                case 5:
                    library.displayCheckouts();
                    break;
                case 6:
                    addBook(library);
                    break;
                case 7:
                    updateBook(library);
                    break;
                case 8:
                    removeBook(library);
                    break;
                case 9:
                    library.displayMembers();
                    break;
                case 0:
                    running = false;
                    System.out.println("Exiting Library Management System.");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void seedLibrary(Library library) {
    	library.addBook(new Book(1, "Fantasy", "978-0547928227", "The Hobbit", "J.R.R. Tolkien"));
    	library.addBook(new Book(2, "Technology", "978-0132350884", "Clean Code", "Robert C. Martin"));
    	library.addBook(new Book(3, "Science Fiction", "978-0441013593", "Dune", "Frank Herbert"));

    	library.addMember(new Member(1, "Jamison Braxton", "jamison@example.com", "jamison", "password1"));
    	library.addMember(new Member(2, "Emmanuel Gohourou", "emmanuel@example.com", "emmanuel", "password2"));
    	library.addMember(new Member(3, "Chase McClenning", "chase@example.com", "chase", "password3"));
}
    }

    private static void printMenu() {
        System.out.println("\n----- Main Menu -----");
        System.out.println("1. Display all books");
        System.out.println("2. Search for a book");
        System.out.println("3. Borrow a book");
        System.out.println("4. Return a book");
        System.out.println("5. Display checkout records");
        System.out.println("6. Add a book");
        System.out.println("7. Update a book");
        System.out.println("8. Remove a book");
        System.out.println("9. Display members");
        System.out.println("0. Exit");
    }

    private static void searchBooks(Library library) {
        System.out.println("\nSearch by:");
        System.out.println("1. Title");
        System.out.println("2. Author");
        System.out.println("3. ISBN");

        int choice = readInt("Choose search type: ");
        Book result = null;

        if (choice == 1) {
            String title = readText("Enter title: ");
            result = library.searchBookByTitle(title);
        } else if (choice == 2) {
            String author = readText("Enter author: ");
            result = library.searchBookByAuthor(author);
        } else if (choice == 3) {
            String isbn = readText("Enter ISBN: ");
            result = library.searchBookByIsbn(isbn);
        } else {
            System.out.println("Invalid search type.");
            return;
        }

        if (result == null) {
            System.out.println("No matching book found.");
        } else {
            System.out.println(result);
        }
    }

    private static void borrowBook(Library library) {
        Member member = new Member(
                readInt("Enter member ID: "),
                readText("Enter member name: "),
                readText("Enter member email: "),
                readText("Enter username: "),
                readText("Enter password: ")
        );

        library.addMember(member);

        String title = readText("Enter book title to borrow: ");
        String checkoutDate = LocalDate.now().toString();
        String dueDate = LocalDate.now().plusDays(14).toString();

        library.borrowBook(member, title, checkoutDate, dueDate);
    }

    private static void returnBook(Library library) {
        String title = readText("Enter book title to return: ");
        String returnDate = LocalDate.now().toString();

        library.returnBook(title, returnDate);
    }

    private static void addBook(Library library) {
        int id = readInt("Enter book ID: ");
        String genre = readText("Enter genre: ");
        String isbn = readText("Enter ISBN: ");
        String title = readText("Enter title: ");
        String author = readText("Enter author: ");

        library.addBook(new Book(id, genre, isbn, title, author));
        System.out.println("Book added successfully.");
    }

    private static void updateBook(Library library) {
        String oldTitle = readText("Enter current title: ");
        String newTitle = readText("Enter new title: ");
        String newAuthor = readText("Enter new author: ");
        String newGenre = readText("Enter new genre: ");
        String newIsbn = readText("Enter new ISBN: ");

        library.updateBook(oldTitle, newTitle, newAuthor, newGenre, newIsbn);
    }

    private static void removeBook(Library library) {
        String title = readText("Enter title to remove: ");

        library.removeBook(title);
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);

            try {
                int value = Integer.parseInt(input.nextLine());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static String readText(String prompt) {
        System.out.print(prompt);
        return input.nextLine();
    }
}

//Creating and defining the Book class
public class Book {
	
	//Var for storing our books' unique IDs
	private int id;
	
	//Var to store titles of books
	private String title;
	
	// Genre of the book
	private String genre;
	
	//Book's unique ISBN
	private String isbn;

	//Var to store name of book authors
	private String author;

	//Bool to track whehter a book is available to borrow
	private boolean available;


	//Constructor for book objects
	public Book (int id, String genre, String isbn, String title, String author){

	//Will store the id parameter as the book's id value
	this.id= id;

	//Will store the title parameter as the book's title
	this.title= title;

	//Will store author parameter as the book's author name
	this.author = author;

	//Setting new books as available
	this.available = true;

	//Storing the book's isbn
	this.isbn= isbn;

	//Storing the genre of the book
	this.genre= genre;
	}


	//Method to return the title of a book
	public String getTitle() {
		return title;
	}

	// Returns the author's name
	public String getAuthor() {
    		return author;
	}

	// Returns the book's unique ID
	public int getId() {
    		return id;
	}
	
	// Returns the book's genre
	public String getGenre() {
    		return genre;
	}

	// Returns the book's ISBN
	public String getIsbn() {
    		return isbn;
	}		
	
	// Returns whether or not the book is available
	public boolean isAvailable() {
		return available;
	}
	
	//Allow update to a Book's title
	public void setTitle(String title) {
    		this.title = title;
	}

	//Allow update to Author name
	public void setAuthor(String author) {
	    this.author = author;
	}

	//Allow update to book's genre
	public void setGenre(String genre) {
	    this.genre = genre;
	}

	//Allow update to book's ISBN
	public void setIsbn(String isbn) {
	    this.isbn = isbn;
	}

	//MArks book as borrowed by setting availability to false
	public void borrowBook() {
		available = false;
	}
	
	//Marks book as returned by setting availability to true
	public void returnBook() {
		available = true;
	}

	@Override
	public String toString() {
    		String status = available ? "Available" : "Borrowed";
    		return "ID: " + id + ", Title: " + title + ", Author: " + author + ", Genre: " + genre + ", ISBN: " + isbn + ", Status: " + status;
	}

}

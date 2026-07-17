public class Title {
	private String titleID;
	private String name;
	private String author;
	private String genre;
	private String isbn;


	public Title (String titleID, String name, String author, String genre, String isbn) {
		this.titleID = titleID;
		this.name = name;
		this.author = author;
		this.genre = genre;
		this.isbn = isbn;
	}

    public String getTitleID() {
        return titleID;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public String getIsbn() {
        return isbn;
    }

	@Override
	public String toString() {
		return "Title ID: " + titleID + "\n" +
				"Name: " + name + "\n" +
				"Author: " + author + "\n" +
				"Genre: " + genre + "\n" +
				"ISBN: " + isbn;
	}
}
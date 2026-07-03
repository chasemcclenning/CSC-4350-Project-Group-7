public class Librarian {
	private int librarianId;
	private String name;
	private String email;
	private String username;
	private String password;

	public Librarian(int librarianId, String name, String email, String username, String password) {
		this.librarianId= librarianId;
		this.name= name;
		this.email= email;
		this.username= username;
		this.password= password;
	}
	
	public int getLibrarianId() {
		return librarianId;
	}

	public String getName(){
		return name;
	}
	
	public String getEmail() {
		return email;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String toString () {
		return "Librarian ID: " + librarianId + ", Name: " + name + ", Email: " + email + ", Username: " + username;
	}
}


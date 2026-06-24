public class Member {
	private int memberId;
	private String name;
	private String email;
	private String username;
	private String password;

	public Member(int memberId, String name, String email, String username, String password) {
		this.memberId = memberId;
		this.name= name;
		this.email= email;
		this.username= username;
		this.password= password;
	}
	// Returns the member's ID
	public int getMemberId() {
	    return memberId;
	}

	// Returns the member's name
	public String getName() {
	    return name;
	}

	// Returns the member's email
	public String getEmail() {
	    return email;
	}

	// Returns the member's username
	public String getUsername() {
	    return username;
	}

	// Returns the member's password
	public String getPassword() {
	    return password;
	}

	@Override
	public String toString() {
    		return "Member ID: " + memberId +", Name: " + name + ", Email: " + email +", Username: " + username;
	}
}



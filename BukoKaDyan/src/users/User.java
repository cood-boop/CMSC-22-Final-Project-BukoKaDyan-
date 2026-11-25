package users;

import java.io.Serializable;

public class User implements Serializable{
	private static final long serialVersionUID = 2L;
	private String email;
	private String password;
	private Role role;
	
	public User(String email, String password, Role role) {
		this.email = email;
		this.password = password;
		this.role=role;
	}
	
	public boolean checkPassword(String initial) {
		 if(password.equals(initial)) {
			 return true;
		 }
		 return false;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
}

package users;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class UserDatabase implements Serializable {
	private static final long serialVersionUID = 1L;
	private HashMap<String, User> users = new HashMap<>();

	public boolean addUser(String name, String password, Role role) {
		if (name.isEmpty() || name.contains("@") || users.containsKey(name)) {
			return false;
		}
		users.put(name, new User(name, password, role));
		return true;
	}

	public User login(String email, String password) {
		Role requested;
		String name;

		if (email.endsWith("@manager")) {
			requested = Role.MANAGER;
			name = email.substring(0, email.length() - "@manager".length());
		} else if (email.endsWith("@clerk")) {
			requested = Role.CLERK;
			name = email.substring(0, email.length() - "@clerk".length());
		} else {
			return null;
		}

		User user = users.get(name);
		if (user == null || !user.checkPassword(password)) return null;
		if (user.getRole() != requested) return null;
		return user;
	}

	public boolean userExists(String name) {
		return users.containsKey(name.trim().toLowerCase());
	}

	public void save(Path savePath) {
		try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(savePath))) {
			out.writeObject(this);
		} catch (IOException e) {
			System.out.println("Could not save users: " + e.getMessage());
		}
	}

	public static UserDatabase load(Path loadPath) {
		try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(loadPath))) {
			return (UserDatabase) in.readObject();
		} catch (IOException | ClassNotFoundException e) {
			return null;
		}
	}

	// call this at startup: loads saved users, or seeds a default manager on first run
	public static UserDatabase loadOrCreate(Path path) {
		UserDatabase db = load(path);
		if (db == null) {
			db = new UserDatabase();
			db.save(path);
		}
		return db;
	}
}
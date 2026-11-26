package application;

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import users.Role;
import users.User;
import users.UserDatabase;

public class LogIn extends Application {

	// ADDED: Backend database reference and save file path
	private UserDatabase userDatabase;
	private final Path userDbPath = Paths.get("users.dat");

	@Override
	public void start(Stage logStage) throws Exception {
		// ADDED: Load existing users or create database file on first run
		userDatabase = UserDatabase.loadOrCreate(userDbPath);

		// [NODES]
		Parent fxmlRoot = FXMLLoader.load(getClass().getResource("logIn.fxml"));

		TextField emailField = (TextField) fxmlRoot.lookup("#emailField");
		emailField.setPromptText("Enter your email (e.g. juan@manager)");
		PasswordField passField = (PasswordField) fxmlRoot.lookup("#passField");
		passField.setPromptText("Enter your password");
		Button logButton = (Button) fxmlRoot.lookup("#logButton");
		Hyperlink createAccountButton = (Hyperlink) fxmlRoot.lookup("#createAccountButton");

		// [EVENTS]
		logButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				String email = emailField.getText().trim();
				String password = passField.getText();

				if (email.isEmpty() || password.isEmpty()) {
					showAlert(AlertType.WARNING, "Please enter your email and password.");
					return;
				}

				if (!email.contains("@manager") && !email.contains("@clerk")) {
					showAlert(AlertType.ERROR, "Email must contain @manager or @clerk.");
					return;
				}

				// ADDED: Authenticate against UserDatabase
				User loggedInUser = userDatabase.login(email, password);
				if (loggedInUser == null) {
					showAlert(AlertType.ERROR, "Invalid email or password.");
					return;
				}

				// ADDED: Extract display name and role from matched User
				String displayName = loggedInUser.getEmail();
				String roleLabel = (loggedInUser.getRole() == Role.MANAGER) ? "Manager" : "Clerk";
				showAlert(AlertType.INFORMATION, "Welcome, " + roleLabel + " " + displayName + "!");
			}
		});

		createAccountButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				createAccountPopUp(logStage);
			}
		});

		// [SCENE]
		Scene logScene = new Scene(fxmlRoot, 960, 683);
		String cssLoc = this.getClass().getResource("logIn.css").toExternalForm();
		logScene.getStylesheets().add(cssLoc);

		// [STAGE]
		logStage.setTitle("Log In to BukoKaDyan");
		logStage.setScene(logScene);
		logStage.show();
	}

	private void createAccountPopUp(Stage logStage) {
		Stage createdStage = new Stage();
		createdStage.setTitle("Create an Account");
		createdStage.initOwner(logStage);
		createdStage.initModality(Modality.APPLICATION_MODAL);

		// [NODES]
		Label titleLabel = new Label("Create an Account");
		titleLabel.getStyleClass().add("popupTitle");

		Label nameLabel = new Label("Name");
		TextField nameField = new TextField();
		nameField.setPromptText("Enter your name (no @)");

		Label emailLabel = new Label("Email Tag / Role");
		TextField emailField = new TextField();
		emailField.setPromptText("Example: juan@manager");

		Label passLabel = new Label("Password");
		PasswordField passField = new PasswordField();
		passField.setPromptText("Enter your password");

		Button createButton = new Button("Create Account");
 
		// BUTTON EVENT - create account
		createButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				String name = nameField.getText().trim();
				String email = emailField.getText().trim();
				String password = passField.getText();

				if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
					showAlert(AlertType.WARNING, "Please fill in all the fields.");
					return;
				}

				// ADDED: Map email tag to Role enum
				Role role;
				if (email.contains("@manager")) {
					role = Role.MANAGER;
				} else if (email.contains("@clerk")) {
					role = Role.CLERK;
				} else {
					showAlert(AlertType.ERROR, "Email must contain @manager or @clerk.");
					return;
				}

				// ADDED: Save new user into UserDatabase and persist to users.dat
				boolean added = userDatabase.addUser(name, password, role);
				if (!added) {
					showAlert(AlertType.ERROR, "Could not create account. Name may already exist or contain '@'.");
					return;
				}

				userDatabase.save(userDbPath);
				showAlert(AlertType.INFORMATION, "Account created for " + role + " " + name + "!");
				createdStage.close();
			}
		});

		// [LAYOUT]
		VBox popupBox = new VBox(10, titleLabel, nameLabel, nameField, emailLabel, emailField, passLabel, passField, createButton);
		popupBox.setPadding(new Insets(30));
		popupBox.getStyleClass().add("popupBox");

		// [SCENE]
		Scene createScene = new Scene(popupBox, 380, 420);
		createScene.getStylesheets().add(getClass().getResource("logIn.css").toExternalForm());

		createdStage.setScene(createScene);
		createdStage.showAndWait();
	}

	private void showAlert(AlertType type, String message) {
		Alert alert = new Alert(type);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
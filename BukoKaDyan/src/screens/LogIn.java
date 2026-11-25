package screens;


import javafx.application.Application;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LogIn extends Application{

	
	@Override
	public void start(Stage logStage) throws Exception {		
		//[NODES]
		//root node - stackpane
		Parent fxmlRoot = FXMLLoader.load(getClass().getResource("logIn.fxml"));
		
		//getting the nodes from fxml
		TextField emailField = (TextField) fxmlRoot.lookup("#emailField");
		emailField.setPromptText("Enter your email");
		PasswordField passField = (PasswordField) fxmlRoot.lookup("#passField");
		passField.setPromptText("Enter your password");
		Button logButton = (Button) fxmlRoot.lookup("#logButton");
		Hyperlink createAccountButton = (Hyperlink) fxmlRoot.lookup("#createAccountButton");
		
		
		//[EVENTS]
		logButton.setOnAction(e -> {

			//extract the inputs of the user
			String email = emailField.getText().trim();
			String password = passField.getText();

			//if something is empty
			if (email.isEmpty() || password.isEmpty()) {
				showAlert(AlertType.WARNING, "Please enter your email and password.");
				return;
			}

			//find the user type from the email field
			String userType;
			if (email.contains("@manager")) {
				userType = "Manager";
			} else if (email.contains("@clerk")) {
				userType = "Clerk";
			} else {
				showAlert(AlertType.ERROR, "Email must contain @manager or @clerk.");
				return;
			}
			
			//Welcome pop up - fix user here!!!
			showAlert(AlertType.INFORMATION, "Welcome, " + User.getUserType() + " " + User.getName() + "!");
		});
		
		
		createAccountButton.setOnAction(e -> {
			createAccountPopUp(logStage);
		});
		
		
		
		//[SCENE] - container
		Scene logScene = new Scene(fxmlRoot,960, 683);  //put the fxml into the scene
		String cssLoc = this.getClass().getResource("logIn.css").toExternalForm(); //can be reuse by other scenes
		logScene.getStylesheets().add(cssLoc);  //connects css file style to scene

		
		//[STAGE] 
		logStage.setTitle("Log In to BukoKaDyan");
		logStage.setScene(logScene);
		logStage.show();   
		
	}
	
	private void createAccountPopUp(Stage logStage) {
		Stage createdStage = new Stage();
		createdStage.setTitle("Create an Account");
		createdStage.initOwner(logStage);  
		
		createdStage.initModality(Modality.APPLICATION_MODAL);
		
//		[NODES]
		Label titleLabel = new Label("Create an Account");
		titleLabel.getStyleClass().add("popupTitle");

		Label nameLabel = new Label("Name");
		TextField nameField = new TextField();
		nameField.setPromptText("Enter your name");

		Label emailLabel = new Label("Email Address");
		TextField emailField = new TextField();
		emailField.setPromptText("Example: juan@manager.com");

		Label passLabel = new Label("Password");
		PasswordField passField = new PasswordField();
		passField.setPromptText("Enter your password");

		Button createButton = new Button("Create Account");
		
		
		//BUTTON EVENT - create account
		createButton.setOnAction(e -> {

			//extract the inputs of the user
			String name = nameField.getText().trim();
			String email = emailField.getText().trim();
			String password = passField.getText();

			//if something is empty
			if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
				showAlert(AlertType.WARNING, "Please fill in all the fields.");
				return;
			}

			//find the user type from the email field
			String userType;
			if (email.contains("@manager")) {
				userType = "Manager";
			} else if (email.contains("@clerk")) {
				userType = "Clerk";
			} else {
				showAlert(AlertType.ERROR, "Email must contain @manager or @clerk.");
				return;
			}

			//[import user here]
			User newUser = new User(name, email, password, userType);
			showAlert(AlertType.INFORMATION, "Account created for " + newUser.getUserType() + " " + newUser.getName() + "!");
			createdStage.close();
		});

		//[LAYOUT] 
		VBox popupBox = new VBox(10, titleLabel, nameLabel, nameField, emailLabel, emailField, passLabel, passField, createButton);
		popupBox.setPadding(new Insets(30));
		popupBox.getStyleClass().add("popupBox");   //ID FOR CSS 

		//[SCENE]
		Scene createScene = new Scene(popupBox, 380, 420);
		createScene.getStylesheets().add(getClass().getResource("logIn.css").toExternalForm());

		createdStage.setScene(createScene);
		createdStage.showAndWait();   
	}

	
	//small helper to show a popup message
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

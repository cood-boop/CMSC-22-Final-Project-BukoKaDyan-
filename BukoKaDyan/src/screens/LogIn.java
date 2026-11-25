package screens;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class LogIn extends Application{

	
	@Override
	public void start(Stage logStage) throws Exception {		
		//[NODES]
		//root node - stackpane
		Parent fxmlRoot = FXMLLoader.load(getClass().getResource("logIn.fxml"));
		
		//getting the nodes from fxml
		TextField emailField = (TextField) fxmlRoot.lookup("#emailField");
		PasswordField passField = (PasswordField) fxmlRoot.lookup("#passField");
		Button logButton = (Button) fxmlRoot.lookup("#logButton");
		
		
		//BUTTON EVENT
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
			
			//Welcome pop up
			showAlert(AlertType.INFORMATION, "Welcome, " + User.getUserType() + " " + User.getName() + "!");
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

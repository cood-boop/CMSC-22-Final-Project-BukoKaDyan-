package ui_copy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class LogIn extends Application{

	
	@Override
	public void start(Stage logStage) throws Exception {		
		//root node - stackpane
//		StackPane logRoot = new StackPane();
//		logRoot.getStyleClass().add("logRoot");
		Parent fxmlRoot = FXMLLoader.load(getClass().getResource("logIn.fxml"));
		
		
		//scene - container
		Scene logScene = new Scene(fxmlRoot,960, 683);  //put the fxml into the scene
		String cssLoc = this.getClass().getResource("logIn.css").toExternalForm(); //can be reuse by other scenes
		logScene.getStylesheets().add(cssLoc);  //connects css file style to scene

		
		//stage 
		logStage.setTitle("Log In to BukoKaDyan");
		logStage.setScene(logScene);
		logStage.show();   
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

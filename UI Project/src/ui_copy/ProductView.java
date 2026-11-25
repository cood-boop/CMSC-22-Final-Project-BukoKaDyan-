package ui_copy;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ProductView extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage prodStage) throws Exception {
		//root node - stackpane
		Parent fxmlRoot = FXMLLoader.load(getClass().getResource("logIn.fxml"));
		
		
		//scene - container
		Scene prodScene = new Scene(fxmlRoot,960, 683);  //put the fxml into the scene
		String cssLoc = this.getClass().getResource("productView.css").toExternalForm(); //can be reuse by other scenes
		prodScene.getStylesheets().add(cssLoc);  //connects css file style to scene

		
		//stage 
		prodStage.setTitle("Log In to BukoKaDyan");
		prodStage.setScene(prodScene);
		prodStage.show(); 
	}

}

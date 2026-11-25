package application;

import javafx.application.Application;
import app.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

<<<<<<< HEAD
public class ClerkDashboard extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("ClerkDashboard.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Dashboard");
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
=======
public class DashboardScreen extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("ClerkDashboard.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Dashboard");
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
>>>>>>> branch 'main' of https://github.com/cood-boop/CMSC-22-Final-Project-BukoKaDyan-

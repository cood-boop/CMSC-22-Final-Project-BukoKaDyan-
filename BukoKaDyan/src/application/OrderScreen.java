package application;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.function.Consumer;

import app.Receipt;
import app.TransactionManagement;
import javafx.application.Application;
import javafx.scene.control.ButtonBar;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class OrderScreen extends Application {
	//ATTRIBUTES
	private TransactionManagement transactionManagement;
	
	
	@Override
    public void start(Stage primaryStage) {
        try {
        	Parent root = FXMLLoader.load(getClass().getResource("OrderScreen.fxml"));
			transactionManagement = new TransactionManagement();
			transactionManagement.load();
			
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Order");
			primaryStage.show(); // <-- Builds the SplitPane and all nodes inside it
			
			
			Button addNewBtn = (Button) root.lookup("#addNewBtn");
			
			
			//[EVENT ON ADD NEW]
			addNewBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			    @Override
			    public void handle(MouseEvent event) {
			        showAddReceiptPopup();
			    }
			});
			
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	//HELPER FUNCTIONS
	private void showAddReceiptPopup() {
	    Dialog<ButtonType> dialog = new Dialog<>();
	    dialog.setTitle("Add New Order");
	    dialog.setHeaderText("Enter Receipt Details");

	    // Add "Save" and "Cancel" buttons to the popup
	    ButtonType saveButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
	    dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

	    // Create the 3 text fields
	    TextField idField = new TextField();
	    idField.setPromptText("e.g. SALE-001");

	    TextField nameField = new TextField();
	    nameField.setPromptText("Enter your name");

	    TextField totalField = new TextField();
	    totalField.setPromptText("Enter your total");

	    // Arrange labels and text fields in a neat grid
	    GridPane grid = new GridPane();
	    grid.setHgap(10);
	    grid.setVgap(10);
	    grid.setPadding(new Insets(20, 20, 10, 10));

	    grid.add(new Label("Order ID:"), 0, 0);
	    grid.add(idField, 1, 0);
	    grid.add(new Label("Customer Name:"), 0, 1);
	    grid.add(nameField, 1, 1);
	    grid.add(new Label("Total:"), 0, 2);
	    grid.add(totalField, 1, 2);

	    dialog.getDialogPane().setContent(grid);

	    // Wait for the user to click a button
	    dialog.showAndWait().ifPresent(new Consumer<ButtonType>() {
	        @Override
	        public void accept(ButtonType response) {
	            if (response == saveButton) {
	                String id = idField.getText().trim();
	                String buyer = nameField.getText().trim();
	                float total = Float.parseFloat(totalField.getText().trim());

	                // Automatically get current date & time matching Receipt.java's format
	                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");
	                String date = LocalDateTime.now().format(formatter);

	                // Build the Receipt and add it to TransactionManagement
	                Receipt newReceipt = new Receipt(buyer, total, date, new HashMap<>());
	                transactionManagement.addReceipt(id, newReceipt);
	                transactionManagement.save();

	                System.out.println("Saved! ID: " + id + " | Buyer: " + buyer + " | Total: " + total);
	            }
	        }
	    });
	}
	
    public static void main(String[] args) {
        launch(args);
    }
}
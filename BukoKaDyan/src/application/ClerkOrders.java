package application;


import app.Receipt;
import app.TransactionManagement;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ClerkOrders extends Application {
	// ATTRIBUTES
	private TransactionManagement transactionManagement;
	private ObservableList<java.util.Map.Entry<String, Receipt>> tableRows;

	@Override
	public void start(Stage primaryStage) {
		NavController.isClerk = true;
		try {
			Parent root = FXMLLoader.load(getClass().getResource("ClerkOrders.fxml"));
			transactionManagement = new TransactionManagement();

			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Orders");
			primaryStage.show();

			// 1. Run setup on initial Orders screen
			setupScreen(root);

			// 2. Re-run setup whenever NavController swaps the screen root
			scene.rootProperty().addListener(new ChangeListener<Parent>() {
				@Override
				public void changed(ObservableValue<? extends Parent> obs, Parent oldRoot, Parent newRoot) {
					setupScreen(newRoot);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// HELPER FUNCTIONS

	// fixes the screen whenever you switch tabs
	@SuppressWarnings("unchecked")
	private void setupScreen(Parent screenRoot) {
		if (screenRoot == null) {
			return;
		}

		screenRoot.applyCss();

		TableView<java.util.Map.Entry<String, Receipt>> orderTable =
				(TableView<java.util.Map.Entry<String, Receipt>>) screenRoot.lookup("#orderTable");

		// If we switched to Dashboard or Products, #orderTable won't be there—stop safely
		if (orderTable == null) {
			return;
		}

		// Reload saved receipts every time Orders is shown
		transactionManagement.load();

		TableColumn<java.util.Map.Entry<String, Receipt>, String> idCol =
				(TableColumn<java.util.Map.Entry<String, Receipt>, String>) orderTable.getColumns().get(0);
		TableColumn<java.util.Map.Entry<String, Receipt>, String> nameCol =
				(TableColumn<java.util.Map.Entry<String, Receipt>, String>) orderTable.getColumns().get(1);
		TableColumn<java.util.Map.Entry<String, Receipt>, String> dateCol =
				(TableColumn<java.util.Map.Entry<String, Receipt>, String>) orderTable.getColumns().get(2);
		TableColumn<java.util.Map.Entry<String, Receipt>, String> totalCol =
				(TableColumn<java.util.Map.Entry<String, Receipt>, String>) orderTable.getColumns().get(3);

		idCol.setCellValueFactory(new Callback<CellDataFeatures<java.util.Map.Entry<String, Receipt>, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<java.util.Map.Entry<String, Receipt>, String> cell) {
				return new SimpleStringProperty(cell.getValue().getKey());
			}
		});

		nameCol.setCellValueFactory(new Callback<CellDataFeatures<java.util.Map.Entry<String, Receipt>, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<java.util.Map.Entry<String, Receipt>, String> cell) {
				return new SimpleStringProperty(cell.getValue().getValue().getBuyer());
			}
		});

		dateCol.setCellValueFactory(new Callback<CellDataFeatures<java.util.Map.Entry<String, Receipt>, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<java.util.Map.Entry<String, Receipt>, String> cell) {
				return new SimpleStringProperty(cell.getValue().getValue().getDate());
			}
		});

		totalCol.setCellValueFactory(new Callback<CellDataFeatures<java.util.Map.Entry<String, Receipt>, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<java.util.Map.Entry<String, Receipt>, String> cell) {
				return new SimpleStringProperty(String.format("%.2f", cell.getValue().getValue().getTotal()));
			}
		});

		tableRows = FXCollections.observableArrayList(transactionManagement.getTransactionHistory().entrySet());
		orderTable.setItems(tableRows);
		orderTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				java.util.Map.Entry<String, Receipt> selectedRow = orderTable.getSelectionModel().getSelectedItem();
				if (selectedRow != null) {
					showReceiptItemsPopup(selectedRow.getKey(), selectedRow.getValue());
				}
			}
		});
	}

	private void showReceiptItemsPopup(String orderId, Receipt receipt) {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("Order Details - " + orderId);
		dialog.setHeaderText("Items bought by " + receipt.getBuyer() + " (Date: " + receipt.getDate() + ")");
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

		javafx.scene.control.ListView<String> itemsList = new javafx.scene.control.ListView<>();
		itemsList.setPrefSize(350, 200);

		if (receipt.getItems() == null || receipt.getItems().isEmpty()) {
			itemsList.getItems().add("No items recorded for this receipt.");
		} else {
			for (app.Item item : receipt.getItems().values()) {
				float subtotal = item.getPrice() * item.getAmount();
				String line = item.getName() + "  |  Qty: " + item.getAmount()
						+ "  |  Price: " + String.format("%.2f", item.getPrice())
						+ "  |  Subtotal: " + String.format("%.2f", subtotal);
				itemsList.getItems().add(line);
			}
		}

		Label totalLabel = new Label("Total: " + String.format("%.2f", receipt.getTotal()));

		javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10);
		content.setPadding(new Insets(15));
		content.getChildren().addAll(itemsList, totalLabel);

		dialog.getDialogPane().setContent(content);
		dialog.showAndWait();
	}

	public static void main(String[] args) {
		launch(args);
	}
}

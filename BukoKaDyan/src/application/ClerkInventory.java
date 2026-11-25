package application;

<<<<<<< HEAD
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import app.Inventory;
import app.Product;
import app.ProductDatabase;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class ClerkInventory extends Application {
    private static final Path PRODUCT_FILE = Paths.get("src/storage/products.txt");
    private static final Path STOCK_FILE = Paths.get("src/storage/stock.txt");

    private static ProductDatabase database;
    private static Inventory inventory;

    public ClerkInventory(Inventory inventory, ProductDatabase database) {
    	ClerkInventory.inventory = inventory;
    	ClerkInventory.database = database;
    }

    public ClerkInventory() {
        if (database == null) {
            database = new ProductDatabase("Main");
            if (Files.exists(PRODUCT_FILE)) {
                database.readProductsFromFile(PRODUCT_FILE);
            }
            inventory = new Inventory(database);
            if (Files.exists(STOCK_FILE)) {
                inventory.loadProducts(STOCK_FILE);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("ClerkInventory.fxml"));

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Inventory");
            primaryStage.show();


            TableView<Product> table = (TableView<Product>) root.lookup(".dataTable");

            // columns by position: 0 Visual, 1 Name, 2 Stock, 3 Price
            TableColumn<Product, String> nameCol = (TableColumn<Product, String>) table.getColumns().get(1);
            TableColumn<Product, Number> stockCol = (TableColumn<Product, Number>) table.getColumns().get(2);
            TableColumn<Product, String> priceCol = (TableColumn<Product, String>) table.getColumns().get(3);

            nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
            stockCol.setCellValueFactory(d -> new SimpleIntegerProperty(inventory.getStock(d.getValue().getName())));
            priceCol.setCellValueFactory(d -> new SimpleStringProperty(String.format("%.2f", d.getValue().getPrice())));

            for (String name : inventory.getInventory().keySet()) {
                Product p = database.getByName(name);
                if (p != null) {
                    table.getItems().add(p);
                }
            }

            // double-click a row to see that product's stock info
            table.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2) {
                    Product p = table.getSelectionModel().getSelectedItem();
                    if (p != null) {
                        showAlert(AlertType.INFORMATION, inventory.getStockInfo(p.getName()));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
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
=======
import javafx.application.Application;
import app.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DashboardScreen extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("ClerkInventory.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Inventory");
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}package state;

public class ClerkOrders {

	public ClerkOrders() {
		// TODO Auto-generated constructor stub
	}

}
>>>>>>> branch 'main' of https://github.com/cood-boop/CMSC-22-Final-Project-BukoKaDyan-

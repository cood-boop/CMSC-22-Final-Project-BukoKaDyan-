package application;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.control.ChoiceDialog;
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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class InventoryScreen extends Application {
    private static final Path PRODUCT_FILE = Paths.get("src/storage/products.txt");
    private static final Path STOCK_FILE = Paths.get("src/storage/stock.txt");

    private static ProductDatabase database;
    private static Inventory inventory;

    public InventoryScreen(Inventory inventory, ProductDatabase database) {
        this.inventory = inventory;
        this.database = database;
    }

    // used when launched directly: load saved data if it exists
    public InventoryScreen() {
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
            Parent root = FXMLLoader.load(getClass().getResource("InventoryScreen.fxml"));

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Inventory");
            primaryStage.show();

            // lookups AFTER show() so the SplitPane contents exist
            Button addButton = (Button) root.lookup("#addButton");
            Button removeButton = (Button) root.lookup("#removeButton");
            Button saveButton = (Button) root.lookup("#saveButton");
            Button newButton = (Button) root.lookup("#newButton");
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
                if (p != null) table.getItems().add(p);
            }

            // double-click a row to see that product's stock info
            table.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2) {
                    Product p = table.getSelectionModel().getSelectedItem();
                    if (p != null) showAlert(AlertType.INFORMATION, inventory.getStockInfo(p.getName()));
                }
            });
            
            newButton.setOnAction(e -> {
                if (database.getProducts().isEmpty()) {
                    showAlert(AlertType.WARNING, "No products loaded in database.");
                    return;
                }

                List<String> availableNames = database.getProducts().stream()
                        .filter(p -> !table.getItems().contains(p))
                        .map(Product::getName)
                        .collect(Collectors.toList());

                if (availableNames.isEmpty()) {
                    showAlert(AlertType.INFORMATION, "All database products are already in the table.");
                    return;
                }

                ChoiceDialog<String> dialog = new ChoiceDialog<>(availableNames.get(0), availableNames);
                dialog.setTitle("Add Product");
                dialog.setHeaderText(null);
                dialog.setContentText("Select a product:");

                dialog.showAndWait().ifPresent(selectedName -> {
                    Product match = database.getByName(selectedName);
                    if (match != null) {
                        table.getItems().add(match);
                        table.getSelectionModel().select(match);
                    }
                });
            });
            
            addButton.setOnAction(e -> {
                Product p = table.getSelectionModel().getSelectedItem();
                if (p == null) {
                    showAlert(AlertType.WARNING, "Select a product first.");
                    return;
                }
                Integer amount = askAmount("Add stock", p.getName());
                if (amount == null) return;
                inventory.addStock(p.getName(), amount);
                inventory.saveProducts(STOCK_FILE);
                table.refresh();
            });

            removeButton.setOnAction(e -> {
                Product p = table.getSelectionModel().getSelectedItem();
                if (p == null) {
                    showAlert(AlertType.WARNING, "Select a product first.");
                    return;
                }
                Integer amount = askAmount("Remove stock", p.getName());
                if (amount == null) return;
                int inStock = inventory.getStock(p.getName());
                if (amount > inStock) {
                    showAlert(AlertType.ERROR, "Only " + inStock + " in stock.");
                    return;
                }
                inventory.removeStock(p.getName(), amount);
                inventory.saveProducts(STOCK_FILE);
                table.refresh();
            });

            saveButton.setOnAction(e -> {
                inventory.saveProducts(STOCK_FILE);
                database.saveProductsToFile(PRODUCT_FILE);
                showAlert(AlertType.INFORMATION, "Saved.");
            });
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Integer askAmount(String title, String productName) {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(title + " for " + productName + ":");
        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return null;
        try {
            int amount = Integer.parseInt(result.get().trim());
            if (amount <= 0) {
                showAlert(AlertType.ERROR, "Amount must be greater than 0.");
                return null;
            }
            return amount;
        } catch (NumberFormatException ex) {
            showAlert(AlertType.ERROR, "Amount must be a whole number.");
            return null;
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
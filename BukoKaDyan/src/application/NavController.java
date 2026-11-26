package application;

import java.nio.file.Paths;

import app.ProductDatabase;
import app.TransactionManagement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NavController {

    public static boolean isClerk = false;

    @FXML protected void goDashboard(ActionEvent e) {
        show(e, "DashboardScreen.fxml", "Dashboard");
    }

    @FXML protected void goProducts(ActionEvent e) {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        if (isClerk) {
            new ClerkInventory().start(stage);
        } else {
            new InventoryScreen().start(stage);
        }
    }

    @FXML protected void goInventory(ActionEvent e) {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        if (isClerk) {
            new ClerkInventory().start(stage);
        } else {
            new InventoryScreen().start(stage);
        }
    }

    @FXML protected void goAllProducts(ActionEvent e) {
        show(e, "productView.fxml", "Product View");
    }

    @FXML protected void goOrders(ActionEvent e) {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        new OrderScreen().start(stage);
    }

    @FXML protected void signOut(ActionEvent e) {
        ((Stage) ((Node) e.getSource()).getScene().getWindow()).close();
    }

    private void show(ActionEvent e, String fxml, String title) {
        try {
            Scene scene = ((Node) e.getSource()).getScene();
            Parent root = FXMLLoader.load(NavController.class.getResource(fxml));
            scene.setRoot(root);                     
            ((Stage) scene.getWindow()).setTitle(title);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ---------- Data loading ----------
    // Load the product database (falls back to a new store if no saved file exists)
    private static ProductDatabase loadDb() {
        ProductDatabase loaded = ProductDatabase.load(Paths.get("ProductDatabase.ser"));
        System.out.println(Paths.get("ProductDatabase.ser").toAbsolutePath());
        return (loaded != null) ? loaded : new ProductDatabase("bukoKaDyan Store");
    }

    // Load all saved transactions (TransactionHistory.txt, also saved by Main)
    private static TransactionManagement loadTransactions() {
        TransactionManagement tm = new TransactionManagement();
        tm.load();
        return tm;
    }

    // ---------- Helpers ----------
    // Inventory is built in code (InventoryScreen), not from an FXML file
    private void openInventory(ActionEvent e) {
        try {
            new InventoryScreen().start(getStage(e));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Stage getStage(ActionEvent e) {
        return (Stage) ((Node) e.getSource()).getScene().getWindow();
    }
}

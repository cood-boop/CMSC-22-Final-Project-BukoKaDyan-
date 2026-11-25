package application;
 
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
 
public class NavController {
 
    @FXML protected void goDashboard(ActionEvent e)   { show(e, "DashboardScreen.fxml", "Dashboard"); }
    @FXML protected void goProducts(ActionEvent e)    { show(e, "InventoryScreen.fxml", "Inventory"); }
    @FXML protected void goInventory(ActionEvent e)   { show(e, "InventoryScreen.fxml", "Inventory"); }
    @FXML protected void goAllProducts(ActionEvent e) { show(e, "productView.fxml",     "Product View"); }
    @FXML protected void goOrders(ActionEvent e)      { show(e, "OrderScreen.fxml",     "Order"); }
 
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
}
 
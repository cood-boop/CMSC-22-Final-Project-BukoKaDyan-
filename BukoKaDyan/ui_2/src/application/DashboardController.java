package application;
 
import javafx.fxml.FXML;
import javafx.scene.control.Label;
 

public class DashboardController extends NavController {
 
    @FXML private Label monthlySalesLabel;
    @FXML private Label totalProductsLabel;
 
    @FXML
    private void initialize() {
        setMonthlySales(0);
        setTotalProducts(0);
    }

    public void setMonthlySales(double amount) {
        monthlySalesLabel.setText(String.format("₱ %,.2f", amount));
    }
 
    public void setTotalProducts(int count) {
        totalProductsLabel.setText(String.valueOf(count));
    }
}
 
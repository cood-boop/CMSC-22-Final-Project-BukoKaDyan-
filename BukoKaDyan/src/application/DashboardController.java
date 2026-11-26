package application;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import app.Item;
import app.ProductDatabase;
import app.Receipt;
import app.TransactionManagement;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DashboardController extends NavController {
    //initializations
    private static ProductDatabase database;
    private static TransactionManagement transactions;
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");

    @FXML private Label monthlySalesLabel;
    @FXML private Label totalProductsLabel;
    @FXML private ListView<Map.Entry<String, Receipt>> transactionList;
    //the file makes sure that the files will exist either way even if it was deleted
    @FXML
    private void initialize() {
        if (transactions == null) {
            transactions = new TransactionManagement();
        }
        transactions.load();
        
        if (database == null) {
            database = new ProductDatabase("Main");
            Path productFile = Paths.get("src/storage/products.txt");
            if (Files.exists(productFile)) {
                database.readProductsFromFile(productFile);
            }
        }
        
        setMonthlySales(transactions.getGrossRevenue());
        setTotalProducts(database.getProducts().size());

        transactionList.setPlaceholder(new Label("No transactions yet"));
        transactionList.setCellFactory(lv -> new TransactionCell());
        loadTransactions();
    }

    //fills the list with every receipt, newest first
    public void loadTransactions() {
        List<Map.Entry<String, Receipt>> entries =
                new ArrayList<>(transactions.getTransactionHistory().entrySet());
        entries.sort(Comparator.comparing(
                (Map.Entry<String, Receipt> e) -> parseDate(e.getValue().getDate())).reversed());
        transactionList.setItems(FXCollections.observableArrayList(entries));
    }

    private static LocalDateTime parseDate(String text) {
        try {
            return LocalDateTime.parse(text, DATE_FORMAT);
        } catch (Exception ex) {
            return LocalDateTime.MIN; // bad/missing date goes to the bottom
        }
    }

    //total of the items still on the receipt (refunded items already removed) 
    private static float netTotal(Receipt r) {
        float net = 0f;
        for (Item item : r.getItems().values()) {
            net += item.getPrice() * item.getAmount();
        }
        return net;
    }
    //SETTER functions for better OOP DESIGN
    public void setMonthlySales(double amount) {
        monthlySalesLabel.setText(String.format("₱ %,.2f", amount));
    }

    public void setTotalProducts(int count) {
        totalProductsLabel.setText(String.valueOf(count));
    }

    //for the total amount, the refunded amount(possibly) and the net total
    private HBox summaryRow(String label, String value, boolean bold) {
        Label l = new Label(label);
        Label v = new Label(value);
        if (bold) {
            l.setStyle("-fx-font-weight: bold;");
            v.setStyle("-fx-font-weight: bold;");
        }
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return new HBox(10, l, spacer, v);
    }

    //popup of the receipt with all the details including order id, name, time, date, items bought and the total amount
    private void showReceipt(String id, Receipt r) {
        Label title = new Label("Receipt");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        //header order id, buyer, date and time
        LocalDateTime when = parseDate(r.getDate());
        String dateText = (when == LocalDateTime.MIN) ? r.getDate()
                : when.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
        String timeText = (when == LocalDateTime.MIN) ? "-"
                : when.format(DateTimeFormatter.ofPattern("hh:mm a"));

        VBox header = new VBox(3,
                new Label("Order ID: " + id),
                new Label("Buyer: " + r.getBuyer()),
                new Label("Date: " + dateText),
                new Label("Time: " + timeText));

        //items
        VBox itemRows = new VBox(6);
        boolean any = false;
        for (Item item : r.getItems().values()) {
            if (item.getAmount() <= 0) continue; // fully refunded items
            any = true;
            Label name = new Label(item.getName() + "  x" + item.getAmount()
                    + "  @ ₱ " + String.format("%,.2f", item.getPrice()));
            Label sub = new Label(String.format("₱ %,.2f", item.getPrice() * item.getAmount()));
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            itemRows.getChildren().add(new HBox(10, name, spacer, sub));
        }
        if (!any) {
            itemRows.getChildren().add(new Label("(no items)"));
        }

        //refund = original total - what the remaining items add up to
        float net = netTotal(r);
        float refunded = r.getTotal() - net;

        VBox totalRow = new VBox(4);
        if (refunded > 0.005f) {
            totalRow.getChildren().add(summaryRow("Original Total",
                    String.format("₱ %,.2f", r.getTotal()), false));
            totalRow.getChildren().add(summaryRow("Refunded",
                    String.format("- ₱ %,.2f", refunded), false));
        }
        totalRow.getChildren().add(summaryRow("NET TOTAL",
                String.format("₱ %,.2f", net), true));

        Stage popup = new Stage();
        Button close = new Button("Close");
        close.setOnAction(e -> popup.close());
        HBox buttonRow = new HBox(close);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(12, title, header, new Separator(), itemRows,
                new Separator(), totalRow, buttonRow);
        root.setPadding(new Insets(20));
        root.setPrefWidth(360);

        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initOwner(transactionList.getScene().getWindow());
        popup.setTitle("Receipt - " + id);
        popup.setScene(new Scene(root));
        popup.setResizable(false);
        popup.showAndWait();
    }

   //order id---------------------amount-------view receipt            
    private class TransactionCell extends ListCell<Map.Entry<String, Receipt>> {
        @Override
        protected void updateItem(Map.Entry<String, Receipt> entry, boolean empty) {
            super.updateItem(entry, empty);
            if (empty || entry == null) {
                setGraphic(null);
                setText(null);
                return;
            }

            Label id = new Label(entry.getKey());
            Label amount = new Label(String.format("₱ %,.2f", netTotal(entry.getValue())));
            amount.setStyle("-fx-font-weight: bold;");

            Button view = new Button("View Receipt");
            view.getStyleClass().add("viewReceiptButton");   // <- add this
            view.setOnAction(e -> showReceipt(entry.getKey(), entry.getValue()));

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox row = new HBox(12, id, spacer, amount, view);
            row.setAlignment(Pos.CENTER_LEFT);
            setGraphic(row);
            setText(null);
        }
    }
}

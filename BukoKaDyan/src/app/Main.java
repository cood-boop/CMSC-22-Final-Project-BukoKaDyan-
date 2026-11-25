package app;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Main {
  public static void main(String[] args) {
    Path dbPath = Paths.get("ProductDatabase.ser");

    ProductDatabase db = new ProductDatabase("bukoKaDyan Store");
    Inventory inventory = new Inventory(db);
    SalesManagement sales = new SalesManagement(db, inventory);
    TransactionManagement transactions = new TransactionManagement();

    // 1. Add products and restock inventory
    db.addProduct("Buko Juice", 35.0f, "Fresh chilled buko juice");
    db.addProduct("Buko Pie", 120.0f, "Classic baked coconut pie");
    db.addProduct("Buko Salad", 65.0f, "Creamy young coconut dessert");

    inventory.addStock("Buko Juice", 10);
    inventory.addStock("Buko Pie", 5);
    inventory.addStock("Buko Salad", 8);

    System.out.println("--- Initial Stock ---");
    inventory.checkStock();

    // 2. Process a sale and cancel another sale
    String sale1Id = sales.newSale();
    System.out.println("\nStarted Sale 1: " + sale1Id);
    sales.updateCart(sale1Id, "Buko Juice", 3);
    sales.updateCart(sale1Id, "Buko Pie", 2);
    sales.updateCart(sale1Id, "Buko Juice", -1);
    sales.checkout(sale1Id, "Juan Dela Cruz", transactions);

    String sale2Id = sales.newSale();
    System.out.println("Started Sale 2 (will cancel): " + sale2Id);
    sales.updateCart(sale2Id, "Buko Pie", 2);
    sales.closeSale(sale2Id);

    System.out.println("\n--- Stock After Sales & Cancelled Sale ---");
    inventory.checkStock();

    // 3. Inspect receipt, test refund, and filter by buyer
    Receipt r1 = transactions.findById(sale1Id);
    if (r1 != null) {
      System.out.println("\n--- Receipt " + sale1Id + " ---");
      System.out.println(
          "Buyer: " + r1.getBuyer() + " | Date: " + r1.getDate() + " | Total: " + r1.getTotal());
      for (Map.Entry<String, Item> entry : r1.getItems().entrySet()) {
        Item item = entry.getValue();
        System.out.println(
            "  " + item.getName() + " x" + item.getAmount() + " @ " + item.getPrice());
      }
    }

    System.out.println("\nGross Revenue Before Refund: " + transactions.getGrossRevenue());
    boolean refunded = transactions.issueRefund(sale1Id, "Buko Juice", 1);
    if (refunded) {
      inventory.addStock("Buko Juice", 1);
    }
    System.out.println("Refund issued: " + refunded);
    System.out.println("Gross Revenue After Refund: " + transactions.getGrossRevenue());

    HashMap<String, Receipt> buyerReceipts = transactions.nameFilter("Juan Dela Cruz");
    System.out.println("Receipts for Juan Dela Cruz: " + buyerReceipts.size());

    // 4. Save and reload persistence state
    db.save(dbPath);
    transactions.save();

    ProductDatabase reloadedDb = ProductDatabase.load(dbPath);
    TransactionManagement reloadedTransactions = new TransactionManagement();
    reloadedTransactions.load();

    System.out.println("\nReloaded Gross Revenue: " + reloadedTransactions.getGrossRevenue());
    if (reloadedDb != null) {
      System.out.println("\n--- Reloaded Database ---");
      reloadedDb.viewAllProducts();
    }
  }
}

package app;

import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class SalesManagement {
  private HashMap<String, Cart> activeSales;
  private ProductDatabase db;
  private Inventory inventory;

  /*
    connects database and inventory
  */
  public SalesManagement(ProductDatabase db, Inventory inventory) {
    this.db = db;
    this.inventory = inventory;
    this.activeSales = new HashMap<>();
  }

  /*
    new sale for assigning saleId and checking if a sale like this already exists
  */
  public String newSale() {
    java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("SALE-yyyyMMddHHmmss");
    String saleId = java.time.LocalDateTime.now().format(formatter);

    int suffix = 1;
    String candidateId = saleId;
    while (this.activeSales.containsKey(candidateId)) {
      candidateId = saleId + "-" + suffix++;
    }
    this.activeSales.put(candidateId, new Cart(this.db, this.inventory));
    return candidateId;
  }

  /*
    closes the sale, but not checkout
  */
  public void closeSale(String saleId) {
    Cart removed = this.activeSales.remove(saleId);
    if (removed != null && this.inventory != null) {
      for (Map.Entry<String, Item> entry : removed.getItems().entrySet()) {
        int qty = entry.getValue().getAmount();
        if (qty > 0) {
          this.inventory.addStock(entry.getValue().getName(), qty);
        }
      }
    }
  }

  /*
    update the amount of an item in the cart
  */
  public void updateCart(String saleId, String product, int change) {
    Cart cart = this.activeSales.get(saleId);
    if (cart != null) {
      cart.updateAmount(product, change);
    }
  }

  /*
    checkout passes the cart to the transaction transaction management
  */
  public void checkout(String saleId, String buyerName, TransactionManagement tm) {
    Cart cart = this.activeSales.remove(saleId);
    if (cart != null) {
      tm.addReceipt(saleId, new Receipt(buyerName, cart));
    }
  }
}

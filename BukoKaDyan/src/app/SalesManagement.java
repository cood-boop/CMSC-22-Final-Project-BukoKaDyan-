package app;

import java.util.HashMap;
import java.util.Map;

public class SalesManagement {
  private HashMap<String, Cart> activeSales;
  private ProductDatabase db;
  private Inventory inventory;

  public SalesManagement(ProductDatabase db) {
    this(db, null);
  }

  public SalesManagement(ProductDatabase db, Inventory inventory) {
    this.db = db;
    this.inventory = inventory;
    this.activeSales = new HashMap<>();
  }

  public void newSale(String saleId) {
    if (this.activeSales.containsKey(saleId)) {
      System.out.println("Sale ID " + saleId + " is already active; overwriting existing cart.");
      closeSale(saleId);
    }
    this.activeSales.put(saleId, new Cart(this.db, this.inventory));
  }

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

  public void updateCart(String saleId, String product, int change) {
    Cart cart = this.activeSales.get(saleId);
    if (cart != null) {
      cart.updateAmount(product, change);
    }
  }

  public void checkout(String saleId, String buyerName, TransactionManagement tm) {
    Cart cart = this.activeSales.remove(saleId);
    if (cart != null) {
      tm.addReceipt(saleId, new Receipt(buyerName, cart));
    }
  }
}

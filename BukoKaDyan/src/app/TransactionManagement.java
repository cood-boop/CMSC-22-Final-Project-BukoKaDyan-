package app;

import java.util.HashMap;

public class TransactionManagement {
  private HashMap<String, Receipt> transactionHistory;
  private float grossRevenue;

  public TransactionManagement() {
    this.transactionHistory = new HashMap<>();
    this.grossRevenue = 0;
  }

  public void addReceipt(String id, Receipt receipt) {
    this.transactionHistory.put(id, receipt);
    this.grossRevenue += receipt.getTotal();
  }

  public Receipt findById(String id) {
    return this.transactionHistory.get(id);
  }

  public HashMap<String, Receipt> nameFilter(String buyerName) {
    HashMap<String, Receipt> filtered = new HashMap<>();
    for (String key : this.transactionHistory.keySet()) {
      Receipt r = this.transactionHistory.get(key);
      if (r.getBuyer() != null && r.getBuyer().equals(buyerName)) {
        filtered.put(key, r);
      }
    }
    return filtered;
  }

  public boolean issueRefund(String id, String product, int amount) {
    if (amount <= 0) return false;
    Receipt receipt = this.findById(id);
    if (receipt == null) return false;
    if (!receipt.getItems().containsKey(product)) return false;

    Item item = receipt.getItems().get(product);
    if (item.getAmount() < amount) return false;

    item.changeAmount(-amount);
    this.grossRevenue -= item.getPrice() * amount;
    return true;
  }

  public HashMap<String, Receipt> getTransactionHistory() {
    return this.transactionHistory;
  }

  public float getGrossRevenue() {
    return this.grossRevenue;
  }
}
package app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

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

  public void load() {
    Path path = Paths.get("TransactionHistory.txt");
    if (!Files.exists(path)) {
      return;
    }

    try (BufferedReader reader = Files.newBufferedReader(path)) {
      String line = reader.readLine();
      if (line == null || line.isBlank()) {
        this.grossRevenue = 0;
        return;
      }
      this.grossRevenue = Float.parseFloat(line.trim());
      this.transactionHistory.clear();

      while ((line = reader.readLine()) != null) {
        if (line.isBlank()) continue;
        String[] entry = line.split("\\|");
        if (entry.length < 4) continue;

        String id = entry[0];
        String buyer = entry[1];
        String date = entry[2];
        float total = Float.parseFloat(entry[3]);
        HashMap<String, Item> items = new HashMap<>();

        for (int i = 4; i + 2 < entry.length; i += 3) {
          String itemName = entry[i];
          float itemPrice = Float.parseFloat(entry[i + 1]);
          int itemAmount = Integer.parseInt(entry[i + 2]);
          items.put(itemName, new Item(itemName, itemPrice, itemAmount));
        }

        this.transactionHistory.put(id, new Receipt(buyer, total, date, items));
      }
    } catch (IOException | NumberFormatException e) {
      System.out.println("Failed to load transaction history: " + e.getMessage());
    }
  }

  public void save() {
    Path path = Paths.get("TransactionHistory.txt");

    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
      writer.write(String.valueOf(this.grossRevenue));
      writer.newLine();

      for (Map.Entry<String, Receipt> entryPair : this.transactionHistory.entrySet()) {
        String id = entryPair.getKey();
        Receipt receipt = entryPair.getValue();

        StringBuilder sb = new StringBuilder();
        sb.append(id).append("|")
          .append(receipt.getBuyer()).append("|")
          .append(receipt.getDate()).append("|")
          .append(receipt.getTotal());

        for (Item item : receipt.getItems().values()) {
          sb.append("|")
            .append(item.getName()).append("|")
            .append(item.getPrice()).append("|")
            .append(item.getAmount());
        }

        writer.write(sb.toString());
        writer.newLine();
      }
    } catch (IOException e) {
      System.out.println("Failed to save transaction history: " + e.getMessage());
    }
  }
}

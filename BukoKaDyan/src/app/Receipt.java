package app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Receipt {
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");
  private String buyer;
  private HashMap<String, Item> items;
  private float total;
  private String dateOfPurchase;

  public Receipt(String buyer, Cart cart) {
    this.buyer = buyer;
    this.items = new HashMap<>();
    for (Map.Entry<String, Item> entry : cart.getItems().entrySet()) {
      this.items.put(entry.getKey(), new Item(entry.getValue()));
    }
    this.total = cart.getTotal();
    this.dateOfPurchase = LocalDateTime.now().format(FORMATTER);
  }

  
  public void addItem() {
	  
  }
  
  public String getBuyer() {
    return this.buyer;
  }

  public String getDate() {
    return this.dateOfPurchase;
  }

  public float getTotal() {
    return this.total;
  }

  public HashMap<String, Item> getItems() {
    return this.items;
  }
}
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

  /*
    new instance
  */
  public Receipt(String buyer, Cart cart) { //assigns buyer then copies the cart :d
    this.buyer = buyer;
    this.items = new HashMap<>();
    for (Map.Entry<String, Item> entry : cart.getItems().entrySet()) { //isolates the items so that no changes will happen to it outside of this class
      this.items.put(entry.getKey(), new Item(entry.getValue()));
    }
    this.total = cart.getTotal();
    this.dateOfPurchase = LocalDateTime.now().format(FORMATTER);
  }

  /*
    loading constructor
  */
  public Receipt(String buyer, float total, String dateOfPurchase, HashMap<String, Item> items) {
    this.buyer = buyer;
    this.total = total;
    this.dateOfPurchase = dateOfPurchase;
    this.items = (items != null) ? items : new HashMap<>();
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

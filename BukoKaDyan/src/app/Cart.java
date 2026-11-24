package app;

import java.util.HashMap;

public class Cart {
  private HashMap<String, Item> items;
  private float total;
  private ProductDatabase db;

  public Cart(ProductDatabase db) {
    this.db = db;
    this.items = new HashMap<>();
    this.total = 0;
  }

  private void addItem(String item) {
    Item found = this.db.getByName(item); //willfix later pero gets ko idea basta 
    if (found != null) {
      Item product = new Item(found.getName(), found.getPrice());
      this.items.put(item, product);
    }
  }

  public void updateAmount(String item, int change) {
    if (!this.items.containsKey(item)) {
      this.addItem(item);
    }
    Item current = this.items.get(item);
    if (current != null) {
      int before = current.getAmount();
      current.changeAmount(change);
      int actualDelta = current.getAmount() - before;
      this.total += current.getPrice() * actualDelta;
    }
  }

  public void removeItem(String item) {
    if (this.items.containsKey(item)) {
      Item removed = this.items.remove(item);
      this.total -= removed.getPrice() * removed.getAmount();
    } else {
      System.out.println("Item " + item + " not in cart.");
    }
  }

  public HashMap<String, Item> getItems() {
    return this.items;
  }

  public float getTotal() {
    return this.total;
  }
}
package app;

import java.util.HashMap;

public class Cart {
  private HashMap<String, Item> items;
  private float total;
  private ProductDatabase db;
  private Inventory inventory;

  public Cart(ProductDatabase db) {
    this(db, null);
  }

  public Cart(ProductDatabase db, Inventory inventory) {
    this.db = db;
    this.inventory = inventory;
    this.items = new HashMap<>();
    this.total = 0f;
  }

  private void addItem(String item) {
    Product found = this.db.getByName(item);
    if (found != null) {
      Item product = new Item(found.getName(), found.getPrice());
      this.items.put(found.getName(), product);
    }
  }

  public void updateAmount(String item, int change) {
    if (change == 0) {
      return;
    }

    Product found = this.db.getByName(item);
    if (found == null) {
      System.out.println("Product not found: " + item);
      return;
    }
    String canonicalName = found.getName();

    if (!this.items.containsKey(canonicalName)) {
      if (change < 0) {
        return;
      }
      this.addItem(canonicalName);
    }

    Item current = this.items.get(canonicalName);
    if (current == null) {
      return;
    }

    int effectiveChange = change;
    if (change > 0 && this.inventory != null) {
      int available = this.inventory.getStock(canonicalName);
      if (available <= 0) {
        System.out.println("Out of stock: " + canonicalName);
        if (current.getAmount() == 0) {
          this.items.remove(canonicalName);
        }
        return;
      }
      if (change > available) {
        System.out.println("Only " + available + " left for " + canonicalName + "; adding max available.");
        effectiveChange = available;
      }
    } else if (change < 0) {
      effectiveChange = -Math.min(Math.abs(change), current.getAmount());
    }

    int before = current.getAmount();
    current.changeAmount(effectiveChange);
    int actualDelta = current.getAmount() - before;

    if (actualDelta == 0) {
      if (current.getAmount() == 0) {
        this.items.remove(canonicalName);
      }
      return;
    }

    this.total += current.getPrice() * actualDelta;

    if (this.inventory != null) {
      if (actualDelta > 0) {
        this.inventory.removeStock(canonicalName, actualDelta);
      } else {
        this.inventory.addStock(canonicalName, -actualDelta);
      }
    }

    if (current.getAmount() == 0) {
      this.items.remove(canonicalName);
    }
  }

  public void removeItem(String item) {
    Product found = this.db.getByName(item);
    String key = (found != null && this.items.containsKey(found.getName())) ? found.getName() : item;

    if (this.items.containsKey(key)) {
      Item removed = this.items.remove(key);
      this.total -= removed.getPrice() * removed.getAmount();
      if (this.inventory != null && removed.getAmount() > 0) {
        this.inventory.addStock(removed.getName(), removed.getAmount());
      }
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

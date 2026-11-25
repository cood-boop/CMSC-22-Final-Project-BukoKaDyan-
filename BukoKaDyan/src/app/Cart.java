package app;

import java.util.HashMap;

public class Cart {
  private HashMap<String, Item> items;
  private float total;
  private ProductDatabase db;
  private Inventory inventory;

  /*
    links inventory and database to this cart
    to make it easier to add products and
    directly change amounts in the inventory
  */
  public Cart(ProductDatabase db, Inventory inventory) {
    this.db = db;
    this.inventory = inventory;
    this.items = new HashMap<>();
    this.total = 0f;
  }

  /*
    creates a new Item class to add to the cart
    if item already exist in cart dont add
  */
  private void addItem(String item) {
    Product found = this.db.getByName(item);
    if (found != null) {
      Item product = new Item(found.getName(), found.getPrice());
      this.items.put(found.getName(), product);
    }
  }

  /*
    updates product amount, accepts both + and negative values
  */
  public void updateAmount(String item, int change) {
    if (change == 0) {  //if no changes are happening return early
      return;
    }

    Product found = this.db.getByName(item);
    if (found == null) { //if item does not exist in database return early
      System.out.println("Product not found: " + item);
      return;
    }

    if (!this.items.containsKey(item)) {  //if the item is not yet in the cart add it
      if (change < 0) {
        return;
      }
      this.addItem(item);
    }

    Item current = this.items.get(item);

    int effectiveChange = change;
    if (change > 0 && this.inventory != null) {
      int available = this.inventory.getStock(item);
      if (available <= 0) { //out of stock
        System.out.println("Out of stock: " + item);
        if (current.getAmount() == 0) { //if no amount of item was actually added to cart remove item
          this.items.remove(item);
        }
        return;
      }
      if (change > available) { //if what is being bought is greater than what is available, add maximum amount
        System.out.println("Only " + available + " left for " + item + "; adding max available.");
        effectiveChange = available;
      }
    } else if (change < 0) { //if reducing amount to buy, reduce the amount or until 0
      effectiveChange = -Math.min(Math.abs(change), current.getAmount());
    }

    int before = current.getAmount(); //gets the change in amount
    current.changeAmount(effectiveChange);
    int actualDelta = current.getAmount() - before;

    if (actualDelta == 0) { //product did not actually change
      if (current.getAmount() == 0) { //if product has no amount remove it
        this.items.remove(item);
      }
      return;
    }

    this.total += current.getPrice() * actualDelta; //update current cart total

    if (this.inventory != null) {
      if (actualDelta > 0) {  //reserves stock when customer is added to cart
        this.inventory.removeStock(item, actualDelta);
      } else {  //adds the reserved stock if customer didnt actually buy them or subtracted the amount
        this.inventory.addStock(item, -actualDelta);
      }
    }

    if (current.getAmount() == 0) { //if product has no amount remove it
      this.items.remove(item);
    }
  }

  /*
    removes item :D
  */
  public void removeItem(String item) {
    if (this.items.containsKey(item)) { //chcecks if item exists in cart
      Item removed = this.items.remove(item);
      this.total -= removed.getPrice() * removed.getAmount(); //updates total
      if (this.inventory != null && removed.getAmount() > 0) {  //updates the stock
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

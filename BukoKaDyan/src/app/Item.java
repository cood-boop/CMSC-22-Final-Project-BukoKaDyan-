package app;

public class Item {
  private String name;
  private float price;
  private int amount;

  public Item(String name, float price) {
    this.name = name;
    this.price = price;
    this.amount = 0;
  }

  public Item(Item other) {
    this.name = other.name;
    this.price = other.price;
    this.amount = other.amount;
  }

  public void changeAmount(int change) {
    if (this.amount + change < 0) {
      this.amount = 0;
    } else {
      this.amount += change;
    }
  }

  public String getName() {
    return this.name;
  }

  public float getPrice() {
    return this.price;
  }

  public int getAmount() {
    return this.amount;
  }
}
package app;

import java.util.HashMap;

public class Receipt{
  private HashMap<Product,Integer> items = new HashMap<>();
  private float total = 0;

  public void addItem(Product item, int amount){
    if(this.items.containsKey(item)){
      this.items.put(item, this.items.get(item) + amount);
    }else{
      this.items.put(item,amount);
    }
  }

  public void removeItem(Product item){ //i'll change this para string comparison shea instead of product comparison
    if(this.items.containsKey(item)){
      this.items.remove(item);
    }else{
      System.out.println("Item " + item.getName() + " not in receipt.");
    }
  }

  public HashMap<Product, Integer> getItems(){
    return this.items;
  }

  public float getTotal(){
    total = 0;
    for(Product key: items.keySet()){
      total += items.get(key) * key.getPrice();
    }
    return total;
  }
}

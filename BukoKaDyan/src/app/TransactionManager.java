package app;

import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;
import java.nio.file.Path;
//import java.nio; someone help me do this please ;-;
//import java.io;

public class TransactionManager{
  private ProductDatabase database;
  private Inventory inventory; 
  private HashMap<String, Receipt> transactionHistory = new HashMap<>();

  public TransactionManager(ProductDatabase database, Inventory inventory){
    //loadHistory();
	  this.database = database;
	  this.inventory = inventory;
  }

  public void newTransaction(Inventory inventory){
	Scanner sc = new Scanner(System.in);
	System.out.println("Enter buyer name:");
	String name = sc.nextLine();
    Receipt receipt = new Receipt(null, null);
    ArrayList<Product> productList = new ArrayList<>();
    for (String productName : inventory.getInventory().keySet()) {
        Product product = database.getByName(productName);
        if (product != null) {
            productList.add(product);
        }
    }

    while(true){ //implement terminal ui first here
      //show product list here
      int i = 1;
      for(Product key: productList){
        System.out.println("[" + i + "] " + key.getName() + "\t|" + key.getPrice() + "\t|" + inventory.get(key));
        i++;
      }
      int purchase = Integer.parseInt(sc.nextLine());
      
      int amount = Integer.parseInt(sc.nextLine());
      receipt.addItem(productList.get(purchase-1),amount);

      System.out.println("Checkout? Y/N");
      
      if(sc.nextLine().equalsIgnoreCase("Y")) break;
    }
    this.transactionHistory.put(name,receipt);
  }

  public void printHistory(){
    for(String key: this.transactionHistory.keySet()){
      System.out.println("========================");
      System.out.println("Customer: " + key);
      
      System.out.println("------");
      for(Product item: this.transactionHistory.get(key).getItems().keySet()){
        System.out.println(item.getName() + "\t" + item.getPrice() + "\t" + this.transactionHistory.get(key).getItems().get(item));
      }
      System.out.println("------");
      System.out.println("Total: " + transactionHistory.get(key).getTotal());

      System.out.println("========================");
    }
  }

  HashMap<String,Receipt> getTransactionHistory(){
    return this.transactionHistory;
  }
}

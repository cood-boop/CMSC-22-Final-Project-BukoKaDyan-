package app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.BufferedWriter;

public class Inventory {
	private ProductDatabase database;
	private HashMap<String, Integer> inventory = new HashMap<>();
	
	public Inventory(ProductDatabase database) {
		this.database = database;
	}

	public void addStock(String name, int amount) {
		Product newStock = database.getByName(name);
		if(newStock == null) {
			System.out.println("Product not found!");
			return;
		}
		int current = inventory.getOrDefault(newStock.getName(), 0);
		inventory.put(newStock.getName(), current + amount);
		System.out.println("Added product: " + name + " with amount of " + amount);
	}
	
	public void removeStock(String name, int amount) {
		Product product = database.getByName(name);
		if (product == null) {
			System.out.println("Product not found: " + name);
			return;
		}
		int current = inventory.getOrDefault(product.getName(), 0);
		if (amount <= 0 || amount > current) {
			System.out.println("Cannot remove " + amount + " of " + product.getName()
					+ " (in stock: " + current + ")");
			return;
		}
		inventory.put(product.getName(), current - amount); // stays at 0 = out of stock
		System.out.println("Removed " + amount + " of " + product.getName());
	}
	
	public void checkStock() {
		System.out.println("Current stock\n");
		if (inventory.isEmpty()) {
			System.out.println("(none)");
			return;
		}
		for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
			Product product = database.getByName(entry.getKey());
			if (product == null) {
				System.out.println(entry.getValue() + "x  " + entry.getKey() + " (not in database)");
			} else {
				System.out.print(entry.getValue() + "x  ");
				product.productInfo();
			}
		}
	}
	
	public int getStock(String name) {
		if(inventory.containsKey(name)) {
			int stock = inventory.get(name);
			System.out.println(name + "has amount: " + stock);
			return stock;
		}
		return 0;
	}
	
	
	/*
	 * saveProducts and loadProducts are both file readers and file writers that
	 * save the infomation on product stocks and their information
	 * */
	public void saveProducts(Path savePath) {
	    System.out.println("Saving products...");
	    if (inventory == null || inventory.isEmpty()) {
	        System.out.println("There are no products to save!");
	        return;
	    }

	    try (BufferedWriter writer = Files.newBufferedWriter(savePath)) {
	        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
	            writer.write(entry.getKey() + "|" + entry.getValue());
	            writer.newLine();
	        }
	    } catch (IOException e) {
	        System.out.println("Could not save the products: " + e.getMessage());
	    }
	}
	
	public void loadProducts(Path loadPath) {
		System.out.println("Loading Stock...");
		try {
			List<String> lines = Files.readAllLines(loadPath);
			HashMap<String, Integer> loadedInventory = new HashMap<>();

			for (String line : lines) {
				String[] data = line.split("\\|");
				if (data.length == 2) {
					String productName = data[0].trim();
					int quantity = Integer.parseInt(data[1].trim());;
					Product product = database.getByName(productName);

					// check if product exists
					if (product == null) {
						System.out.println("Product not Found: " + productName);
						continue;
					}
					if (quantity < 0) {
						System.out.println("Cannot load negative stock for " + product.getName());
						continue;
					}

					loadedInventory.put(product.getName(), quantity);
					System.out.println("You Added " + quantity + " units of " + product.getName());
				}
			}
			this.inventory = loadedInventory;
		} catch (IOException e) {
			System.out.println("Could not load/find the stock in: " + loadPath);
		}
	}

	public ProductDatabase getDatabase() {
		return database;
	}

	public void setDatabase(ProductDatabase database) {
		this.database = database;
	}

	public HashMap<String, Integer> getInventory() {
		return inventory;
	}

	public void setInventory(HashMap<String, Integer> inventory) {
		this.inventory = inventory;
	}
	
	
	//default constructor for the time being
//	public void deleteProduct(String name) {
//		for (Product key: inventory.keySet()) {
//			if (name.equals(key.getName())) {
//				inventory.remove(key);
//			}
//		}
//	}
//	
//	public void updateStock (String name, int stock) {
//		for (Product key: inventory.keySet()) {
//			if (name.equals(key.getName())) {
//				inventory.replace(key,stock);
//			}
//		}
//	}
//	
//	public void getStock(String name) {
//        for (HashMap.Entry<Product, Integer> entry : inventory.entrySet()) {
//        	if(name.equals(entry.getKey().getName())){
//            System.out.println(entry.getKey().getName() + ", has this amount of stocks left: " + entry.getValue());
//        }
//		}
}

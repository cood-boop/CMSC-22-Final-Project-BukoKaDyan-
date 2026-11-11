package app;

import java.util.HashMap;

public class Inventory {
	private HashMap<Product, Integer> inventory = new HashMap<>();
	
	
	//default constructor for the time being
	
	
	public void createProduct(String name, float price, String description) {
		Product newProductToAdd = new Product(name,price,description);
		inventory.put(newProductToAdd, 1);
	}
	
	public void deleteProduct(String name) {
		for (Product key: inventory.keySet()) {
			if (name.equals(key.getName())) {
				inventory.remove(key);
			}
		}
	}
	
	public void updateStock (String name, int stock) {
		for (Product key: inventory.keySet()) {
			if (name.equals(key.getName())) {
				inventory.replace(key,stock);
			}
		}
	}
	
	public void updatePrice (String name, float price) {
		for (Product key: inventory.keySet()) {
			if (name.equals(key.getName())) {
				inventory.get(key);
				key.setPrice(price);
			}
		}
	}
	
	public void updateDescription (String name, String description) {
		for (Product key: inventory.keySet()) {
			if (name.equals(key.getName())) {
				inventory.get(key);
				key.setDescription(description);
			}
		}
	}
	
	public void getStock(String name) {
        for (HashMap.Entry<Product, Integer> entry : inventory.entrySet()) {
        	if(name.equals(entry.getKey().getName())){
            System.out.println(entry.getKey().getName() + ", has this amount of stocks left: " + entry.getValue());
        }
		}
	}
	
	/*
	 * saveProducts and loadProducts are both file readers and file writers that
	 * save the infomation on product stocks and their information
	 * */

}

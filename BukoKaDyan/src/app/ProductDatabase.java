package app;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.Serializable;


public class ProductDatabase implements Serializable {
	
	private static final long serialVersionUID = 2L;
	private String name;
	private ArrayList<Product> products;

	public ProductDatabase(String name) {
		this.name = name;
		this.products = new ArrayList<>();
	}
	
	// getters and setters
	public ArrayList<Product> getProducts() {
		return products;
	}

	public void setProducts(ArrayList<Product> products) {
		this.products = products;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void addProduct(String name, float price, String description) {
		Product productToAdd = new Product(name,price,description);
		products.add(productToAdd);
		return;
	}
	
	public void removeProduct(String name) {
		Product productToRemove = getByName(name);
		if(productToRemove == null) {
			System.out.println("Product not found!");
			return;
		}
		System.out.println("Removed Product: " + productToRemove.getName());
		products.remove(productToRemove);
	}
	
	public void updateName(String oldName,String newName) {
		Product productToUpdate = getByName(oldName);
		if(productToUpdate == null) {
			System.out.println("Product not found");
			return;
		}
		if(getByName(newName) != null) {//mmeaning new name already exists
			System.out.println("Product already exists! Choose a new name");
			return;
		}
		productToUpdate.setName(newName);
		System.out.println("Renamed " + oldName + "to "+ newName);
	}
	
	public void updatePrice(String name, float price) {
		Product productToUpdate = getByName(name);
		if(productToUpdate == null) {
			System.out.println("Product not found");
			return;
		}
		productToUpdate.setPrice(price);
	}
	
	public void updateDescription(String name, String description) {
		Product productToUpdate = getByName(name);
		if(productToUpdate == null) {
			System.out.println("Product not found");
			return;
		}
		productToUpdate.setDescription(description);
	}
	
	
	public Product getByName(String productName) {//finds the product
		boolean found = false;
		for (int i = 0; i<products.size(); i++) {
			if (products.get(i).getName().equals(productName)) {
				found = true;
				Product product = products.get(i);
				return product;
			}else if (i == products.size() && found == false) {
				System.out.println("Not Found!");
			}else {
				continue;
			}
		}
		return null;
	}

	// Reads products from a text file: name|price|description (one per line)
	public void readProductsFromFile(Path path) {
		try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))){
			String line = reader.readLine();
			while (line != null) {
				System.out.println(line + "\n");
				// limit of 3 so a "|" inside the description doesn't break parsing
				String[] data = line.split("\\|", 3);
				products.add(new Product(data[0],Float.parseFloat(data[1]),data[2]));
				line = reader.readLine();
			}
		} catch (IOException e) {
			System.out.println("Could not read file: " + path);
		}
	}

	public void viewAllProducts() {
		System.out.println("All products in the system\n");
		if (products.isEmpty()) {
			System.out.println("(none)");
			return;
		}
		for (Product product : products) {
			System.out.println(product);
		}
	}


	public void save(Path savePath) {
		//create the file first if it doesn't exist (go to catch if it already exists)
		System.out.println("Saving.....");
		//serializer
		try {
			//create ObjectOutputStream using the newOutputStream method of Files on our path argument
			ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(savePath));
			
			out.writeObject(this);
			System.out.println("Saving Complete");
			//close the file
			out.close();
		} catch (IOException e) {
			System.out.println("Could not read file: " + savePath);
			e.printStackTrace();
		}
		
	}
	
	public void saveProductsToFile(Path path) {
	    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
	        for (Product p : products) {
	            writer.write(p.getName() + "|" + p.getPrice() + "|" + p.getDescription().replace("\n", " "));
	            writer.newLine();
	        }
	    } catch (IOException e) {
	        System.out.println("Could not save products: " + e.getMessage());
	    }
	}

	public static ProductDatabase load(Path loadPath) {
		System.out.println("Loading.......");
		ProductDatabase database;
		
		try {
			//create ObjectInputStream using the newInputStream method of Files on our path argument
			ObjectInputStream in = new ObjectInputStream(Files.newInputStream(loadPath));
			//read the object in the text file and cast it into an album
			database = (ProductDatabase) in.readObject();
			System.out.println("Loading Complete");
			//close the file
			in.close();
			return database;
			
		} catch (IOException e) { //catch exceptions if file does not exist or file is corrupted
			System.out.println("Could not load file: " + loadPath);
		} catch (ClassNotFoundException e) { //catch exceptions if wrong file is read or the data is corrupted
			System.out.println("Could not find database");
		}
		return null;
	}

}

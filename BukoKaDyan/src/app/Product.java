package app;

import java.io.Serializable;

public class Product implements Serializable{
	private static final long serialVersionUID = 1L;
	private String name;
	private float price;
	private String description;
	
	
	public Product(String name, float price, String description) {
		this.name = name;
		this.price = price;
		this.description = description;
	}
	
	public void productInfo() {//prints the card info
	    System.out.printf("%-30s | %10.2f | %s%n", name, price, description);
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public float getPrice() {
		return price;
	}


	public void setPrice(float price) {
		this.price = price;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}
	

}

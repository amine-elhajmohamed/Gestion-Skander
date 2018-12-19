package models;

public class Product {

	private String code;
	private String name;
	private ProductPrice price;
	
	
	public Product(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public Product(String code, String name, ProductPrice price) {
		this.code = code;
		this.name = name;
		this.price = price;
	}
	
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public ProductPrice getPrice() {
		return price;
	}
	public void setPrice(ProductPrice price) {
		this.price = price;
	}

}

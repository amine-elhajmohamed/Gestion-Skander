package models;

public class ProductBill extends ProductStock {
	private double priceSelled;
	
	public ProductBill(Product product, double priceSelled, double qnt) {
		super(product, qnt);
		this.priceSelled = priceSelled;
	}

	public double getPriceSelled() {
		return priceSelled;
	}
	public void setPriceSelled(double priceSelled) {
		this.priceSelled = priceSelled;
	}
	
	public double getTotalPrice() {
		return priceSelled * getQnt();
	}
	
}

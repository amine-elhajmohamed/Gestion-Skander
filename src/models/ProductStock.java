package models;

public class ProductStock extends Product{

	private double qnt;

	public ProductStock(Product product, double qnt) {
		super(product.getCode(), product.getName(), product.getPrice());
		this.qnt = qnt;
	}

	public double getQnt() {
		return qnt;
	}

	public void setQnt(double qnt) {
		this.qnt = qnt;
	}

}

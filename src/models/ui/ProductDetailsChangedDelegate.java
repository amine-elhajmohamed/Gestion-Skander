package models.ui;

public interface ProductDetailsChangedDelegate {
	public void didChangeProductDetails(String oldProductCode, String newProductCode);
}

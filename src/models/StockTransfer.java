package models;

import java.sql.Timestamp;
import java.util.ArrayList;

public class StockTransfer {
	
	private Timestamp date;
	private int fromDepotCode;
	private int toDepotCode;
	private ArrayList<ProductStock> products;
	
	
	public StockTransfer(Timestamp date, int fromDepotCode, int toDepotCode, ArrayList<ProductStock> products) {
		this.date = date;
		this.fromDepotCode = fromDepotCode;
		this.toDepotCode = toDepotCode;
		this.products = products;
	}

	public Timestamp getDate() {
		return date;
	}
	
	public int getFromDepotCode() {
		return fromDepotCode;
	}


	public int getToDepotCode() {
		return toDepotCode;
	}


	public ArrayList<ProductStock> getProducts() {
		return products;
	}

	
}

package models;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import managers.AppDataBaseManager;
import models.ui.AlertError;

public class Bill {

	private String billCode;
	private String clientCode;
	private int depotCode;
	private Timestamp date;
	private double discount;
	private ArrayList<ProductBill> products = new ArrayList<>();
	
	
	public Bill(String billCode, String clientCode, int depotCode, Timestamp date, double discount,
			ArrayList<ProductBill> products) {
		this.billCode = billCode;
		this.clientCode = clientCode;
		this.depotCode = depotCode;
		this.date = date;
		this.discount = discount;
		this.products = products;
	}


	public void setBillCode(String billCode) {
		this.billCode = billCode;
	}

	public String getBillCode() {
		return billCode;
	}
	

	public String getClientCode() {
		return clientCode;
	}


	public int getDepotCode() {
		return depotCode;
	}


	public Timestamp getDate() {
		return date;
	}


	public double getDiscount() {
		return discount;
	}


	public ArrayList<ProductBill> getProducts() {
		return products;
	}
	
	public double calculateTotal(){
		double total = 0;
		
		for (int i=0;i<products.size();i++) {
			total += products.get(i).getTotalPrice();
		}
		
		return total;
	}
	
	public double calculatTotalWithDiscount(){
		double total = calculateTotal();
		return total-(total*(discount/100));
	}
	
	public double calculateGainedAmount(){
		double totalPriceAchat = 0;
		
		for (int i=0;i<products.size();i++){
			ProductBill product = products.get(i);
			totalPriceAchat += (product.getPrice().getPrixAchatTTC() * product.getQnt());
		}
		
		return calculatTotalWithDiscount()-totalPriceAchat;
	}
	
	public double calculateAmountNotPayed(){
		double payedAmount = 0;
		try {
			payedAmount = AppDataBaseManager.shared.getPayedAmountForBillByBillCode(billCode);
		} catch (SQLException e) {
			AlertError alert = new AlertError("ERROR ERR0022", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();
		}
		return calculatTotalWithDiscount() - payedAmount;
	}
	
}

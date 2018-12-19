package models;

import java.sql.Timestamp;

public class Payment {
	
	static public String TYPE_CHECK = "Chèque";
	static public String TYPE_CASH = "Espèce";
	
	private double ammount;
	private String type;
	private Timestamp date;
	
	public Payment(double ammount, String type, Timestamp date) {
		this.ammount = ammount;
		this.type = type;
		this.date = date;
	}

	public double getAmmount() {
		return ammount;
	}

	public String getType() {
		return type;
	}

	public Timestamp getDate() {
		return date;
	}

	
	
}

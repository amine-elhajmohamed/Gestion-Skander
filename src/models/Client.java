package models;

import java.util.ArrayList;

public class Client {

	private String code;
	private String name;
	private String lastName;
	private String address;
	
	private ArrayList<String> phonesNumbers;
	private ArrayList<String> faxNumbers;
	
	
	public Client(String code, String name, String lastName, String address) {
		this.code = code;
		this.name = name;
		this.lastName = lastName;
		this.address = address;
	}
	
	public Client(String code, String name, String lastName, String address, 
			ArrayList<String> phonesNumbers, ArrayList<String> faxNumbers) {
		
		this(code,name,lastName,address);
		this.phonesNumbers = phonesNumbers;
		this.faxNumbers = faxNumbers;
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


	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}


	public ArrayList<String> getPhonesNumbers() {
		return phonesNumbers;
	}


	public void setPhonesNumbers(ArrayList<String> phonesNumbers) {
		this.phonesNumbers = phonesNumbers;
	}


	public ArrayList<String> getFaxNumbers() {
		return faxNumbers;
	}


	public void setFaxNumbers(ArrayList<String> faxNumbers) {
		this.faxNumbers = faxNumbers;
	}
	
	
	
}

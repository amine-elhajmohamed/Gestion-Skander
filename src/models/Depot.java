package models;

public class Depot {
	
	private int code;
	private String name;
	private String comments;
	
	public Depot(int code, String name, String comments) {
		this.code = code;
		this.name = name;
		this.comments = comments;
	}
	
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	
	@Override
	public String toString() {
		return getName();
	}
}

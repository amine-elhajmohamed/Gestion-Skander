package models;

public class DepotStock extends Depot{

	private double qnt;

	public DepotStock(Depot depot, double qnt){
		super(depot.getCode(), depot.getName(), depot.getComments());
		this.qnt = qnt;
	}

	
	public double getQnt() {
		return qnt;
	}

	public void setQnt(double qnt) {
		this.qnt = qnt;
	}

}

package models.ui;


public interface ClientDetailsChangedDelegate {
	public void didChangeClientDetails(String oldClientCode, String newClientCode);
}

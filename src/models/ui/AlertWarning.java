package models.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertWarning {
	private Alert alert;
	
	public AlertWarning(String title,String header,String content) {

		alert = new Alert(AlertType.WARNING);
		
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
	}
	
	public void show(){
		alert.show();
	}
	
	public void showAndWait(){
		alert.showAndWait();
	}
}

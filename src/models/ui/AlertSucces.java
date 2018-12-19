package models.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertSucces {

	private Alert alert;
	
	public AlertSucces(String title,String header,String content) {
		alert = new Alert(AlertType.INFORMATION);
		
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		
		//Image image = new Image(getClass().getResource("../../views/icons/SuccesIcon.png").toExternalForm());
		//ImageView imageView = new ImageView(image);
		//alert.setGraphic(imageView);
	}
	
	public void show(){
		alert.show();
	}
	
	public void showAndWait(){
		alert.showAndWait();
	}
}

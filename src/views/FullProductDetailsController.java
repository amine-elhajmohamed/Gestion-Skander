package views;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import managers.AppDataBaseManager;
import models.Product;
import models.ui.AlertError;
import models.ui.ProductDetailsChangedDelegate;

public class FullProductDetailsController implements Initializable, ProductDetailsChangedDelegate{

	@FXML Button btnModifier;

	@FXML FlowPane containerFP;

	@FXML Label lblTotalQntSelled;
	@FXML Label lblTotalGain;


	private Product product = null;
	private ProductDetailsController productDetailsController;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loadProductDetailsView();

		ActionEventHandler actionEventHandler = new ActionEventHandler();
		btnModifier.setOnAction(actionEventHandler);
	}


	public void showProductDetails(Product product){
		
		try {
			double totalQntSelled = AppDataBaseManager.shared
					.getProductTotalQntSelledByProductCode(product.getCode());
			lblTotalQntSelled.setText(Double.toString(totalQntSelled));
			
			double totalGained = AppDataBaseManager.shared
					.getProductTotalGainedAmmountsByProductCode(product.getCode());
			lblTotalGain.setText(Double.toString(totalGained));
			
		} catch (SQLException e) {
			AlertError alert = new AlertError("ERROR ERR0041", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();
		}
		
		this.product = product;
		productDetailsController.loadProductDetails(product);
	}


	private void loadProductDetailsView(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/ProductDetails.fxml"));
			Pane clientDetailsPane = loader.load();
			productDetailsController = loader.getController();
			containerFP.getChildren().add(0, clientDetailsPane);

			productDetailsController.changeEditsStats(false);
		} catch (IOException e) {
			AlertError alert = new AlertError("ERROR ERR0031", "Fatal Error", e.getMessage());
			alert.showAndWait();
			closeStage();
		}
	}

	private void closeStage(){
		((Stage)containerFP.getScene().getWindow()).close();
	}

	private class ActionEventHandler implements EventHandler<ActionEvent>{

		@Override
		public void handle(ActionEvent event) {
			if (event.getSource() == btnModifier && product != null) {
				RootViewController.selfRef.showEditProductView(product, FullProductDetailsController.this);
			}
		}

	}

	@Override
	public void didChangeProductDetails(String oldProductCode, String newProductCode) {
		try {
			Product product = AppDataBaseManager.shared.getProductByCode(newProductCode);
			showProductDetails(product);
		} catch (SQLException e) {
			AlertError alert = new AlertError("ERROR ERR0033", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();
			closeStage();
		}
	}

}

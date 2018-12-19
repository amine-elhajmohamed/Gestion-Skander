package views;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import managers.AppDataBaseManager;
import models.DepotStock;
import models.Product;
import models.ProductPrice;
import models.ProductStock;
import models.ui.AlertError;
import models.ui.AlertSucces;
import models.ui.ProductDetailsChangedDelegate;

public class AddEditProductDetailsController implements Initializable {


	@FXML Button btnAdd;
	@FXML Button btnCancel;
	@FXML FlowPane containerFP;

	public ProductDetailsChangedDelegate delegate;


	private boolean editMode = false;
	private String currentEditingProductCode = null;
	private ProductDetailsController productDetailsController;


	@Override
	public void initialize(URL location, ResourceBundle resources) {

		loadProductDetailsView();

		ButtonActionEventHandler buttonActionEventHandler = new ButtonActionEventHandler();
		btnAdd.setOnAction(buttonActionEventHandler);
		btnCancel.setOnAction(buttonActionEventHandler);

		TextFieldChangeListener textFieldChangeListener = new TextFieldChangeListener();

		productDetailsController.txtCode.textProperty().addListener(textFieldChangeListener);
		productDetailsController.txtLibelle.textProperty().addListener(textFieldChangeListener);
		productDetailsController.txtPrixDachatTTC.textProperty().addListener(textFieldChangeListener);
		productDetailsController.txtTVA.textProperty().addListener(textFieldChangeListener);
		productDetailsController.txtPrixDeVentHT.textProperty().addListener(textFieldChangeListener);
		productDetailsController.txtPrixDeVentTTC.textProperty().addListener(textFieldChangeListener);
	}

	private void loadProductDetailsView(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/ProductDetails.fxml"));
			Pane paneProductDetails = loader.load();
			productDetailsController = loader.getController();
			containerFP.getChildren().add(paneProductDetails);
		} catch (IOException e) {
			AlertError alert = new AlertError("ERROR ERR0003", "Fatal Error", e.getMessage());
			alert.showAndWait();
			closeStage();
		}
	}

	public void forceSetEditModeForProduct(Product product){
		productDetailsController.loadProductDetails(product);
		editMode = true;
		currentEditingProductCode = product.getCode();
		btnAdd.setText("Sauvegarder");

		updateBtnAddView();
	}

	private void closeStage(){
		((Stage) containerFP.getScene().getWindow()).close();
	}

	private void createTheProduct(){

		String productCode = productDetailsController.txtCode.getText();
		String productTitle = productDetailsController.txtLibelle.getText();
		double productPrixDachatTTC = Double.parseDouble(productDetailsController.txtPrixDachatTTC.getText());
		//double productTVA = Double.parseDouble(productDetailsController.txtTVA.getText());
		double productTVA = 0;
		//double productPrixDeVentHT = Double.parseDouble(productDetailsController.txtPrixDeVentHT.getText());
		double productPrixDeVentHT = 0;
		double productPrixDeVentTTC = Double.parseDouble(productDetailsController.txtPrixDeVentTTC.getText());


		try {
			if (AppDataBaseManager.shared.isProductCodeExist(productCode)) {
				AlertError alert = new AlertError("Code existe déjà", 
						"Le code ' "+productCode+" ' existe déjà !", 
						"Réessayer avec un autre nom");
				alert.showAndWait();
			}else{

				ProductPrice price = new ProductPrice(productPrixDachatTTC, productTVA, productPrixDeVentHT, productPrixDeVentTTC);
				Product product = new Product(productCode, productTitle, price);

				AppDataBaseManager.shared.addNewProduct(product);

				ArrayList<DepotStock> depotsStocks = productDetailsController.getDepotsStocksEntredByUser();

				for (int i=0; i<depotsStocks.size();i++){
					double qnt = depotsStocks.get(i).getQnt();
					if (qnt != 0){

						ProductStock productStock = new ProductStock(product, qnt);
						ArrayList<ProductStock> productStockInArrayList = new ArrayList<>();
						productStockInArrayList.add(productStock);
						AppDataBaseManager.shared.transferStock(0, depotsStocks.get(i).getCode(), productStockInArrayList);

					}
				}

				closeStage();
				
				AlertSucces alert = new AlertSucces("Article ajouté avec succès", 
						"Article ajouté avec succès", 
						"L'article ' "+productTitle+" ' a été ajouté avec succés.");
				alert.showAndWait();
			}
		} catch (SQLException e) {
			AlertError alert = new AlertError("ERROR ERR0004", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();
		}
	}
	
	private void updateTheProduct(){
		String productCode = productDetailsController.txtCode.getText();
		String productTitle = productDetailsController.txtLibelle.getText();
		double productPrixDachatTTC = Double.parseDouble(productDetailsController.txtPrixDachatTTC.getText());
		//double productTVA = Double.parseDouble(productDetailsController.txtTVA.getText());
		double productTVA = 0;
		//double productPrixDeVentHT = Double.parseDouble(productDetailsController.txtPrixDeVentHT.getText());
		double productPrixDeVentHT = 0;
		double productPrixDeVentTTC = Double.parseDouble(productDetailsController.txtPrixDeVentTTC.getText());


		try {
			if (!currentEditingProductCode.equals(productCode) 
					&& AppDataBaseManager.shared.isProductCodeExist(productCode)) {
				AlertError alert = new AlertError("Code existe déjà", 
						"Le code ' "+productCode+" ' existe déjà !", 
						"Réessayer avec un autre nom");
				alert.showAndWait();
			}else{

				ProductPrice price = new ProductPrice(productPrixDachatTTC, productTVA, productPrixDeVentHT, productPrixDeVentTTC);
				Product product = new Product(productCode, productTitle, price);

				AppDataBaseManager.shared.updateProductDetailsByProductCode(currentEditingProductCode, product);
				
				ArrayList<DepotStock> depotsStocks = productDetailsController.getDepotsStocksEntredByUser();

				for (int i=0; i<depotsStocks.size();i++){
					
					double newQnt = depotsStocks.get(i).getQnt();
					
					double oldQnt = AppDataBaseManager.shared.getProductsStock(productCode, 
							depotsStocks.get(i).getCode());
					
					if (newQnt > oldQnt) {
						ProductStock productStock = new ProductStock(product, newQnt-oldQnt);
						ArrayList<ProductStock> productStockInArrayList = new ArrayList<>();
						productStockInArrayList.add(productStock);
						AppDataBaseManager.shared.transferStock(0, depotsStocks.get(i).getCode(), productStockInArrayList);
					} else if (newQnt < oldQnt){
						ProductStock productStock = new ProductStock(product, oldQnt-newQnt);
						ArrayList<ProductStock> productStockInArrayList = new ArrayList<>();
						productStockInArrayList.add(productStock);
						AppDataBaseManager.shared.transferStock(depotsStocks.get(i).getCode(), 0, productStockInArrayList);	
					}
				}
				
				delegate.didChangeProductDetails(currentEditingProductCode, productCode);
				closeStage();
				AlertSucces alert = new AlertSucces("Article modifiée avec succès", 
						"Article modifiée avec succès", 
						"Les informations de l'article ont été modifiée avec succés.");
				alert.showAndWait();
			}
		} catch (SQLException e) {
			AlertError alert = new AlertError("ERROR ERR0032", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();
		}
	}

	
	private class ButtonActionEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (event.getSource() == btnAdd) {
				if (editMode){
					updateTheProduct();
				}else{
					createTheProduct();	
				}
			}
			else if (event.getSource() == btnCancel) {
				closeStage();
			}

		}
	}

	private void updateBtnAddView(){
		if ( (productDetailsController.txtCode.getText().equals("")) || 
				(productDetailsController.txtLibelle.getText().equals("")) ||
				(productDetailsController.txtPrixDachatTTC.getText().equals("")) ||
				//(productDetailsController.txtTVA.getText().equals("")) ||
				//(productDetailsController.txtPrixDeVentHT.getText().equals("")) ||
				(productDetailsController.txtPrixDeVentTTC.getText().equals("")) ){
			btnAdd.setDisable(true);
		}else{
			btnAdd.setDisable(false);
		}
	}

	private class TextFieldChangeListener implements ChangeListener<String> {

		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			updateBtnAddView();
		}

	}

}

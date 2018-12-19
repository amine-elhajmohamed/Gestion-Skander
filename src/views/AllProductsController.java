package views;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import managers.AppDataBaseManager;
import managers.StringsManager;
import models.Product;
import models.ProductPrice;
import models.ProductStock;
import models.ui.AlertError;

public class AllProductsController implements Initializable{


	@FXML TextField txtCode;
	@FXML TextField txtName;
	@FXML TextField txtStockLessThan;

	@FXML Button btnSearch;

	@FXML CheckBox checkBoxShowMoreDetails;

	@FXML TableView<ProductStock> tableViewProductsStocks;
	@FXML TableColumn<ProductStock, String> columnCode;
	@FXML TableColumn<ProductStock, String> columnName;
	@FXML TableColumn<ProductStock, Double> columnPrixDAachatTTC;
	@FXML TableColumn<ProductStock, Double> columnPrixDeVentTTC;
	@FXML TableColumn<ProductStock, Integer> columnStock;
	@FXML TableColumn<ProductStock, Double> columnQntSelled;
	@FXML TableColumn<ProductStock, Double> columnTotalGain;


	private ObservableList<ProductStock> productsStockData = FXCollections.observableArrayList();
	
	private boolean showPlusDetailsInTableView = false;
	private StringsManager stringsManager = new StringsManager();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		refrechColumnsPlusDetailsViews();

		columnCode.setCellValueFactory(new PropertyValueFactory<>("Code"));
		columnName.setCellValueFactory(new PropertyValueFactory<>("Name"));
		columnStock.setCellValueFactory(new PropertyValueFactory<>("Qnt"));
		columnPrixDAachatTTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ProductStock,Double>, ObservableValue<Double>>() {
			@Override
			public ObservableValue<Double> call(CellDataFeatures<ProductStock, Double> param) {
				return new SimpleDoubleProperty(param.getValue().getPrice().getPrixAchatTTC()).asObject();
			}
		});
		columnPrixDeVentTTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ProductStock,Double>, ObservableValue<Double>>() {
			@Override
			public ObservableValue<Double> call(CellDataFeatures<ProductStock, Double> param) {
				return new SimpleDoubleProperty(param.getValue().getPrice().getPrixVenteTTC()).asObject();
			}
		});
		columnQntSelled.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ProductStock,Double>, ObservableValue<Double>>() {
			@Override
			public ObservableValue<Double> call(CellDataFeatures<ProductStock, Double> param) {
				if (!showPlusDetailsInTableView) {
					return null;
				}
				try {
					double totalQntSelled = AppDataBaseManager.shared
							.getProductTotalQntSelledByProductCode(param.getValue().getCode());
					return new SimpleDoubleProperty(totalQntSelled).asObject();
				} catch (SQLException e) {
					AlertError alert = new AlertError("ERROR ERR0030", "SQL error code : "+e.getErrorCode(),e.getMessage());
					alert.showAndWait();
					return null;
				}
			}
		});
		columnTotalGain.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ProductStock,Double>, ObservableValue<Double>>() {
			@Override
			public ObservableValue<Double> call(CellDataFeatures<ProductStock, Double> param) {
				if (!showPlusDetailsInTableView) {
					return null;
				}
				try {
					double totalGained = AppDataBaseManager.shared
							.getProductTotalGainedAmmountsByProductCode(param.getValue().getCode());
					return new SimpleDoubleProperty(totalGained).asObject();
				} catch (SQLException e) {
					AlertError alert = new AlertError("ERROR ERR0030", "SQL error code : "+e.getErrorCode(),e.getMessage());
					alert.showAndWait();
					return null;
				}
			}
		});
		
		tableViewProductsStocks.setRowFactory(new TableViewRowFactory());

		ActionEventHandler actionEventHandler = new ActionEventHandler();
		btnSearch.setOnAction(actionEventHandler);
		txtCode.setOnAction(actionEventHandler);
		txtName.setOnAction(actionEventHandler);
		txtStockLessThan.setOnAction(actionEventHandler);
		checkBoxShowMoreDetails.setOnAction(actionEventHandler);
		
		TextFieldChangeListener textFieldChangeListener = new TextFieldChangeListener();
		txtCode.textProperty().addListener(textFieldChangeListener);
		txtName.textProperty().addListener(textFieldChangeListener);
		txtStockLessThan.textProperty().addListener(textFieldChangeListener);

		tableViewProductsStocks.setOnMousePressed(new TableViewMousePressedHandler());
		
		tableViewProductsStocks.setItems(productsStockData);
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtCode.requestFocus();
			}
		});
	}

	private void refrechColumnsPlusDetailsViews(){
		columnQntSelled.setVisible(showPlusDetailsInTableView);
		columnTotalGain.setVisible(showPlusDetailsInTableView);
	}

	public void refrechTableViewData(){

		productsStockData.clear();


		Double stockMax = null;

		try {
			stockMax = Double.parseDouble(txtStockLessThan.getText());
		}catch (NumberFormatException e){}


		ArrayList<String> productsCodes;

		try {
			productsCodes = AppDataBaseManager.shared.getAllProductsCodes(txtCode.getText(), txtName.getText(), stockMax);

			for (int i=0;i<productsCodes.size();i++){

				Product product = AppDataBaseManager.shared.getProductByCode(productsCodes.get(i));

				ProductPrice productPrice = AppDataBaseManager.shared.getProductPrice(productsCodes.get(i));

				product.setPrice(productPrice);

				Double stock = AppDataBaseManager.shared.getProductsStock(productsCodes.get(i));

				productsStockData.add(new ProductStock(product, stock));

			}



		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class ActionEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (event.getSource() == btnSearch || event.getSource() == txtCode 
					|| event.getSource() == txtName || event.getSource() == txtStockLessThan) {
				refrechTableViewData();
			}else if (event.getSource() == checkBoxShowMoreDetails) {
				showPlusDetailsInTableView = checkBoxShowMoreDetails.isSelected();
				refrechColumnsPlusDetailsViews();				
			}
		}
	}
	
	private class TextFieldChangeListener implements ChangeListener<String> {

		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			TextField txt = (TextField) ((StringProperty)observable).getBean();
			if (txt == txtCode) {
				String ch = stringsManager.getOnlyLettersAndNumbers(txt.getText());
				ch = ch.replace(" ", "");
				ch = ch.toUpperCase();
				txt.setText(ch);
			}else if (txt == txtName) {
				String ch = txt.getText();
				ch = ch.replace("  ", " ");
				txt.setText(ch);
			}else if (txt == txtStockLessThan) {
				String ch = stringsManager.getDoubleFormat(txt.getText());
				ch = ch.replace("  ", " ");
				txt.setText(ch);
			}

		}

	}
	
	
	private class TableViewMousePressedHandler implements EventHandler<MouseEvent>{

		@Override
		public void handle(MouseEvent event) {
			
			if (event.isPrimaryButtonDown() && event.getClickCount() == 2 
					&& tableViewProductsStocks.getSelectionModel().getSelectedItem() != null)  {
				
				Product product = tableViewProductsStocks.getSelectionModel().getSelectedItem();
				
				RootViewController.selfRef.presentProductFullDetailsView(product);

			}
			
		}
		
	}
	
	private class TableViewRowFactory implements Callback<TableView<ProductStock>, TableRow<ProductStock>> {

		@Override
		public TableRow<ProductStock> call(TableView<ProductStock> param) {
			return new TableRow<ProductStock>() {
				@Override
				protected void updateItem(ProductStock item, boolean empty) {
					super.updateItem(item, empty);
					
					if (item == null) {
						setStyle("");
					}else {
						if (item.getQnt() > 0) {
							setStyle("");
						}else{
							setStyle("-fx-background-color: red;");
						}
					}
					
				}
			};
		}
		
	}
	


}

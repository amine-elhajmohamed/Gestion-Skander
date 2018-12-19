package views;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableRow;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import managers.AppDataBaseManager;
import managers.StringsManager;
import models.Depot;
import models.Product;
import models.ProductStock;
import models.StockTransfer;
import models.ui.AlertError;
import models.ui.AlertSucces;
import models.ui.StringConverterLocalDate;
import models.ui.ProductSearchPickedProductDelegate;
import models.ui.StringConverterDouble;
import models.ui.TimesSpinerConfigurator;

public class StockTransferController implements Initializable, ProductSearchPickedProductDelegate{


	@FXML Button btnTransfere;
	@FXML Button btnAddProduct;
	@FXML Button btnCancel;

	@FXML TextField txtCode;
	@FXML TextField txtName;
	@FXML TextField txtQnt;

	@FXML DatePicker date;

	@FXML Label lblFromDepot;

	@FXML Spinner<Integer> spinerHoures;
	@FXML Spinner<Integer> spinerMinutes;

	@FXML ComboBox<Depot> comboBoxFromDepot;
	@FXML ComboBox<Depot> comboBoxToDepot;

	@FXML TableView<ProductStock> tableViewProductsStocksTransfert;
	@FXML TableColumn<ProductStock, String> columnCode;
	@FXML TableColumn<ProductStock, String> columnName;
	@FXML TableColumn<ProductStock, Double> columnQNT;

	@FXML FlowPane flowPaneAddProduct;

	private StringsManager stringsManager = new StringsManager();
	private boolean fromAdminMode = false;
	private boolean onlyShowDetailMode = false;

	private ArrayList<Depot> allDepots;
	private ObservableList<Depot> toDepotsList = FXCollections.observableArrayList();
	private ObservableList<Depot> fromDepotsList = FXCollections.observableArrayList();

	private ObservableList<ProductStock> productsStockData = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		configureTimeView();

		comboBoxFromDepot.setItems(fromDepotsList);
		comboBoxToDepot.setItems(toDepotsList);

		comboBoxFromDepot.valueProperty().addListener(new DepotChangeListener(comboBoxFromDepot));
		comboBoxToDepot.valueProperty().addListener(new DepotChangeListener(comboBoxToDepot));

		columnCode.setCellValueFactory(new PropertyValueFactory<>("Code"));
		columnName.setCellValueFactory(new PropertyValueFactory<>("Name"));
		columnQNT.setCellValueFactory(new PropertyValueFactory<>("Qnt"));


		columnQNT.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverterDouble()));
		columnQNT.setOnEditCommit(new TableColumnChangeEventHandler());

		tableViewProductsStocksTransfert.setOnMousePressed(new TableViewMousePressedHandler());

		tableViewProductsStocksTransfert.setItems(productsStockData);

		tableViewProductsStocksTransfert.setRowFactory(new TableViewRowFactory());

		ActionEventHandler actionEventHandler = new ActionEventHandler();
		btnTransfere.setOnAction(actionEventHandler);
		btnCancel.setOnAction(actionEventHandler);
		txtCode.setOnAction(actionEventHandler);
		txtQnt.setOnAction(actionEventHandler);
		btnAddProduct.setOnAction(actionEventHandler);

		TextFieldChangeListener textFieldChangeListener = new TextFieldChangeListener();

		txtCode.textProperty().addListener(textFieldChangeListener);
		txtQnt.textProperty().addListener(textFieldChangeListener);

		txtCode.setOnKeyPressed(new KeyEventHandler());

		try {
			allDepots = AppDataBaseManager.shared.getAllDepots();
			fromDepotsList.addAll(allDepots);
			toDepotsList.addAll(allDepots);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			closeStage();
		}
	}


	public void forceSetTransferFromAdminMode(){
		fromAdminMode = true;

		comboBoxFromDepot.setVisible(false);
		lblFromDepot.setVisible(false);

		try {
			comboBoxFromDepot.setValue(AppDataBaseManager.shared.getAdminDepot());
		} catch (SQLException e) {
			tableViewProductsStocksTransfert.setVisible(false);
			txtCode.setVisible(false);
			comboBoxToDepot.setVisible(false);
			AlertError alert = new AlertError("ERROR ERR0005", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();
		}

	}

	private void configureTimeView(){
		TimesSpinerConfigurator timesSpinerConfigurator = new TimesSpinerConfigurator(spinerHoures, spinerMinutes);
		timesSpinerConfigurator.configure();

		date.setConverter(new StringConverterLocalDate());

		date.setValue(LocalDate.now());
	}


	public void showStockTransferDetails(StockTransfer stockTransfer){
		onlyShowDetailMode = true;
		disableViewEditing();

		date.setValue(stockTransfer.getDate().toLocalDateTime().toLocalDate());
		spinerHoures.getValueFactory().setValue(stockTransfer.getDate().toLocalDateTime().getHour());
		spinerMinutes.getValueFactory().setValue(stockTransfer.getDate().toLocalDateTime().getMinute());

		try {
			Depot fromDepot = AppDataBaseManager.shared.getDepotByCode(stockTransfer.getFromDepotCode());
			Depot toDepot = AppDataBaseManager.shared.getDepotByCode(stockTransfer.getToDepotCode());
			comboBoxFromDepot.setValue(fromDepot);
			comboBoxToDepot.setValue(toDepot);
			
			productsStockData.clear();
			productsStockData.addAll(stockTransfer.getProducts());
		} catch (SQLException e) {
			AlertError alert = new AlertError("ERROR ERR0045", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();
		}

	}

	private void disableViewEditing(){
		tableViewProductsStocksTransfert.setEditable(false);

		date.setDisable(true);
		spinerHoures.setDisable(true);
		spinerMinutes.setDisable(true);
		comboBoxFromDepot.setDisable(true);
		comboBoxToDepot.setDisable(true);

		date.setStyle("-fx-opacity: 1 ;");
		spinerHoures.setStyle("-fx-opacity: 1 ;");
		spinerMinutes.setStyle("-fx-opacity: 1 ;");
		comboBoxFromDepot.setStyle("-fx-opacity: 1 ;");
		comboBoxToDepot.setStyle("-fx-opacity: 1 ;");


		btnTransfere.setVisible(false);
		btnCancel.setVisible(false);
		flowPaneAddProduct.setVisible(false);

		AnchorPane.setBottomAnchor(tableViewProductsStocksTransfert, 0.0);
	}

	private void transferTheProducts(){

		if (comboBoxFromDepot.getValue() == null) {
			AlertError alert = new AlertError("Choisissez depuis quelle dépôt", null, 
					"Vous devez choisissez depuis quelle dépôt !");
			alert.showAndWait();
			comboBoxFromDepot.requestFocus();
			return;
		}

		if (comboBoxToDepot.getValue() == null) {
			AlertError alert = new AlertError("Choisissez vers quelle dépôt", null, 
					"Vous devez choisissez vers quelle dépôt !");
			alert.showAndWait();
			comboBoxToDepot.requestFocus();
			return;
		}

		if (productsStockData.size() < 1){
			AlertError alert = new AlertError("Entrer au moins un article", null, "Vous devez entrer au moins un article !");
			alert.showAndWait();
			txtCode.requestFocus();
			return;
		}


		LocalDateTime localDateTime = 
				date.getValue().atTime(spinerHoures.getValue(), spinerMinutes.getValue());
		Timestamp timestamp = Timestamp.valueOf(localDateTime);

		ArrayList<ProductStock> productsWithStocks = new ArrayList<>();

		for (int i=0; i<productsStockData.size(); i++) {
			productsWithStocks.add(productsStockData.get(i));
		}

		try {
			AppDataBaseManager.shared.transferStock(comboBoxFromDepot.getValue().getCode(), 
					comboBoxToDepot.getValue().getCode(), productsWithStocks, timestamp);
			closeStage();
			if (fromAdminMode) {
				AlertSucces alert = new AlertSucces("Entrées en stock effectué avec succès", null, "Entrées en stock effectué avec succès !");
				alert.showAndWait();
			}else{
				AlertSucces alert = new AlertSucces("Transfert effectué avec succès", null, "Transfert effectué avec succès !");
				alert.showAndWait();				
			}
		} catch (SQLException e) {
			AlertError alert = new AlertError("ERROR ERR0006", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();			
		}
	}

	private void closeStage(){
		((Stage)tableViewProductsStocksTransfert.getScene().getWindow()).close();
	}

	private class ActionEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {

			if (event.getSource() == btnTransfere) {
				transferTheProducts();
			}else if (event.getSource() == btnCancel) {
				closeStage();
			}else if (event.getSource() == txtCode) {

				try {
					Product product = AppDataBaseManager.shared.getProductByCode(txtCode.getText());

					if (product != null) {
						txtName.setText(product.getName());
						txtQnt.setEditable(true);
						txtQnt.requestFocus();
					}

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			}else if (event.getSource() == txtQnt || event.getSource() == btnAddProduct) {

				try {
					Product product = AppDataBaseManager.shared.getProductByCode(txtCode.getText());

					if (txtQnt.getText().equals("")) {
						txtQnt.requestFocus();
					}else if (product != null) {

						double qntToAdd = Double.parseDouble(txtQnt.getText());

						int i = 0;

						while (i<productsStockData.size() 
								&& !productsStockData.get(i).getCode().equals(txtCode.getText())) {
							i++;
						}

						if (i<productsStockData.size()){
							productsStockData.get(i).setQnt(productsStockData.get(i).getQnt()+qntToAdd);
							tableViewProductsStocksTransfert.refresh();
						}else{
							productsStockData.add(new ProductStock(product, qntToAdd));
						}

						txtCode.setText("");
						txtName.setText("");
						txtQnt.setText("");
						txtCode.requestFocus();
					}

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NumberFormatException e){

				}
			}

		}
	}


	private class KeyEventHandler implements EventHandler<KeyEvent>{

		@Override
		public void handle(KeyEvent event) {
			if (event.getSource() == txtCode && event.getCode() == KeyCode.SPACE) {
				RootViewController.selfRef.presentProductSearchView(comboBoxFromDepot.getSelectionModel().getSelectedItem()
						,StockTransferController.this, txtCode.getText(), "");
			}
		}

	}


	private class TableViewMousePressedHandler implements EventHandler<MouseEvent>{

		@Override
		public void handle(MouseEvent event) {

			if (event.isSecondaryButtonDown()  
					&& tableViewProductsStocksTransfert.getSelectionModel().getSelectedItem() != null)  {

				Product product = tableViewProductsStocksTransfert.getSelectionModel().getSelectedItem();
				RootViewController.selfRef.presentProductDetailsView(product);

			}

		}

	}

	private class TextFieldChangeListener implements ChangeListener<String> {

		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

			TextField txt = (TextField) ((StringProperty)observable).getBean();
			if (txt == txtCode) {
				String ch = txt.getText().replaceAll(" ", "");
				ch = stringsManager.getOnlyLettersAndNumbers(ch);
				txt.setText(ch.toUpperCase());
				txtQnt.setEditable(false);
				txtName.setText("");
			} else if (txt == txtQnt) {
				txt.setText(stringsManager.getDoubleFormat(txt.getText()));
			}

		}

	}

	private class TableColumnChangeEventHandler implements EventHandler<CellEditEvent<ProductStock, Double>> {

		@Override
		public void handle(CellEditEvent<ProductStock, Double> event) {
			if (event.getNewValue() == 0) {
				productsStockData.remove(event.getTablePosition().getRow());
			}else{
				ProductStock productStock = productsStockData.get(event.getTablePosition().getRow());
				productStock.setQnt(event.getNewValue());
				tableViewProductsStocksTransfert.refresh();
			}
		}

	}


	private class DepotChangeListener implements ChangeListener<Depot> {

		private ComboBox<Depot> comboBox;

		public DepotChangeListener(ComboBox<Depot> comboBox) {
			this.comboBox = comboBox;
		}

		@Override
		public void changed(ObservableValue<? extends Depot> observable, Depot oldValue, Depot newValue) {

			if (comboBoxFromDepot == comboBox) {
				tableViewProductsStocksTransfert.refresh();
			}

			if (comboBoxFromDepot.getValue() == comboBoxToDepot.getValue()) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						comboBoxToDepot.setValue(null);
					}
				});
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

					if (item == null || comboBoxFromDepot.getValue() == null || fromAdminMode || onlyShowDetailMode) {
						setStyle("");
					}else {
						try {
							double availableStock = AppDataBaseManager.shared.getProductsStock(item.getCode(), 
									comboBoxFromDepot.getValue().getCode());

							if (availableStock >= item.getQnt()) {
								setStyle("");
							}else{
								setStyle("-fx-background-color: red;");
							}

						} catch (SQLException e) {
							setStyle("");
							AlertError alert = new AlertError("ERROR ERR0007", "SQL error code : "+e.getErrorCode(),e.getMessage());
							alert.showAndWait();
						}

					}

				}
			};
		}
	}

	@Override
	public void productSearchPickedProduct(Product product) {
		txtCode.setText(product.getCode());
		txtName.setText(product.getName());
		txtQnt.setEditable(true);
		txtQnt.requestFocus();
	}
}

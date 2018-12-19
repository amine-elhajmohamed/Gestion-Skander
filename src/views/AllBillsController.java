package views;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import managers.AppDataBaseManager;
import managers.StringsManager;
import models.Bill;
import models.Client;
import models.ui.AlertError;
import models.ui.StringConverterLocalDate;
import models.ui.StringConverterTimestamp;

public class AllBillsController implements Initializable{

	@FXML Button btnSearch;
	
	@FXML TextField txtBillsNumber;
	
	@FXML DatePicker datePicker;
	
	@FXML TableView<Bill> tableViewBills;
	
	@FXML TableColumn<Bill, String> columnBillCode;
	@FXML TableColumn<Bill, Timestamp> columnBillDate;
	@FXML TableColumn<Bill, String> columnBillClientName;
	@FXML TableColumn<Bill, Double> columnBillTotalAmount;
	@FXML TableColumn<Bill, Double> columnBillAmmountToPay;
	
	
	private ObservableList<Bill> billsData = FXCollections.observableArrayList();
	
	private StringsManager stringsManager = new StringsManager();
	
	private String showOnlyForClientCode = ""; // if == "" it will show for all clients
	private Integer showOnlyForDepotCode = null;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		

		columnBillCode.setCellValueFactory(new PropertyValueFactory<>("billCode"));
		columnBillDate.setCellValueFactory(new PropertyValueFactory<>("date"));
		columnBillClientName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Bill,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Bill, String> param) {
				try {
					Client client = AppDataBaseManager.shared.getClientByCode(param.getValue().getClientCode());
					return new SimpleStringProperty(client.getName()+" "+client.getLastName());
				} catch (SQLException e) {
					AlertError alert = new AlertError("ERROR ERR0014", "SQL error code : "+e.getErrorCode(),e.getMessage());
					alert.showAndWait();
					return null;
				}
			}
		});
		columnBillTotalAmount.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Bill,Double>, ObservableValue<Double>>() {
			@Override
			public ObservableValue<Double> call(CellDataFeatures<Bill, Double> param) {
				return new SimpleDoubleProperty(param.getValue().calculatTotalWithDiscount()).asObject();
			}
		});
		columnBillAmmountToPay.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Bill,Double>, ObservableValue<Double>>() {
			@Override
			public ObservableValue<Double> call(CellDataFeatures<Bill, Double> param) {
				try {
					double payedAmmount = AppDataBaseManager.shared.
							getPayedAmountForBillByBillCode(param.getValue().getBillCode());
					return new SimpleDoubleProperty(param.getValue().calculatTotalWithDiscount()-payedAmmount).asObject();
				} catch (SQLException e) {
					AlertError alert = new AlertError("ERROR ERR0015", "SQL error code : "+e.getErrorCode(),e.getMessage());
					alert.showAndWait();
					return null;
				}
			}
		});
		
		columnBillDate.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverterTimestamp()));
		
		tableViewBills.setRowFactory(new TableViewRowFactory());
		
		datePicker.setConverter(new StringConverterLocalDate());
		
		ActionEventHandler actionEventHandler = new ActionEventHandler();
		btnSearch.setOnAction(actionEventHandler);
		txtBillsNumber.setOnAction(actionEventHandler);
		
		TextFieldChangeListener textFieldChangeListener = new TextFieldChangeListener();
		txtBillsNumber.textProperty().addListener(textFieldChangeListener);
		
		TableViewMousePressedHandler tableViewMousePressedHandler = new TableViewMousePressedHandler();
		tableViewBills.setOnMousePressed(tableViewMousePressedHandler);
		
		tableViewBills.setItems(billsData);
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtBillsNumber.requestFocus();
			}
		});
	}
	
	
	public void forceSearchToShowOnlyForClientCode(String clientCode){
		showOnlyForClientCode = clientCode;
	}
	
	public void forceSearchToShowOnlyForDepotCode(int depotCode){
		showOnlyForDepotCode = depotCode;
	}
	
	public void refrechTableViewData(){

		billsData.clear();

		try {
			ArrayList<String> billsCodes = AppDataBaseManager.shared.getAllBillsCodes(txtBillsNumber.getText(), 
					datePicker.getValue(), showOnlyForClientCode, showOnlyForDepotCode);

			for (int i=0;i<billsCodes.size();i++){
				Bill bill = AppDataBaseManager.shared.getBillByCode(billsCodes.get(i));
				billsData.add(bill);
			}


		} catch (SQLException e) {
			AlertError alert = new AlertError("ERROR ERR0016", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();
		}
	}
	
	private class ActionEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (event.getSource() == btnSearch || event.getSource() == txtBillsNumber 
					|| event.getSource() == datePicker) {
				refrechTableViewData();
			}
		}
	}
	
	
	private class TextFieldChangeListener implements ChangeListener<String> {

		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			TextField txt = (TextField) ((StringProperty)observable).getBean();
			if (txt == txtBillsNumber) {
				txt.setText(stringsManager.getOnlyNumbers(txt.getText()));
			}

		}

	}
	
	private class TableViewMousePressedHandler implements EventHandler<MouseEvent>{
		@Override
		public void handle(MouseEvent event) {
			if (event.isPrimaryButtonDown() && event.getClickCount() == 2 
					&& tableViewBills.getSelectionModel().getSelectedItem() != null)  {
				Bill bill = tableViewBills.getSelectionModel().getSelectedItem();
				RootViewController.selfRef.presentBillDetailsView(bill);
			}
			
		}
		
	}
	
	
	private class TableViewRowFactory implements Callback<TableView<Bill>, TableRow<Bill>> {

		@Override
		public TableRow<Bill> call(TableView<Bill> param) {
			return new TableRow<Bill>(){
				@Override
				protected void updateItem(Bill item, boolean empty) {
					super.updateItem(item, empty);
					
					if (item == null) {
						setStyle("");
					}else {
						try {
							double payedAmmount = AppDataBaseManager.shared.
									getPayedAmountForBillByBillCode(item.getBillCode());

							if (payedAmmount >= item.calculatTotalWithDiscount()) {
								setStyle("");
							}else{
								setStyle("-fx-background-color: red;");
							}

						} catch (SQLException e) {
							setStyle("");
							AlertError alert = new AlertError("ERROR ERR0008", "SQL error code : "+e.getErrorCode(),e.getMessage());
							alert.showAndWait();
						}

					}
					
					
					
				}
				
			};
		}
		
	}
	
	

}

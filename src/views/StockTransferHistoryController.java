package views;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import managers.AppDataBaseManager;
import models.Depot;
import models.StockTransfer;
import models.ui.AlertError;
import models.ui.StringConverterTimestamp;

public class StockTransferHistoryController implements Initializable {

	public static String STOCK_TRANSFER_TYPE_INPUT = "Entrée";
	public static String STOCK_TRANSFER_TYPE_OUTPUT = "Sortie";
	
	@FXML TableView<StockTransfer> tableViewStockTransfer;
	@FXML TableColumn<StockTransfer, Timestamp> columnDate;
	@FXML TableColumn<StockTransfer, String> columnType;
	@FXML TableColumn<StockTransfer, String> columnDepotName;
	
	private Integer depotCode = null;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
		columnType.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<StockTransfer,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<StockTransfer, String> param) {
				if (param.getValue().getFromDepotCode() == depotCode) {
					return new SimpleStringProperty(STOCK_TRANSFER_TYPE_OUTPUT);
				}else{
					return new SimpleStringProperty(STOCK_TRANSFER_TYPE_INPUT);
				}
			}
		});
		columnDepotName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<StockTransfer,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<StockTransfer, String> param) {
				try {
					int depotCode;
					
					if (param.getValue().getFromDepotCode() == StockTransferHistoryController.this.depotCode) {
						depotCode = param.getValue().getToDepotCode();
					}else{
						depotCode = param.getValue().getFromDepotCode();
					}
					
					Depot depot = AppDataBaseManager.shared.getDepotByCode(depotCode);
					return new SimpleStringProperty(depot.getName());
				} catch (SQLException e) {
					AlertError alert = new AlertError("ERROR ERR0044", "SQL error code : "+e.getErrorCode(),e.getMessage());
					alert.showAndWait();
					return null;
				}
			}
		});
		
		columnDate.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverterTimestamp()));
		
		
		tableViewStockTransfer.setOnMousePressed(new TableViewMousePressedHandler());
	}

	
	public void setDepotCode(int depotCode){
		this.depotCode = depotCode;
		
		try {
			tableViewStockTransfer.getItems().setAll(AppDataBaseManager.shared.
					getAllStockTransferForDepotByDepotCode(depotCode));
		} catch (SQLException e) {
			AlertError alert = new AlertError("ERROR ERR0015", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();
		}
		
	}
	
	private class TableViewMousePressedHandler implements EventHandler<MouseEvent>{

		@Override
		public void handle(MouseEvent event) {
			
			if (event.isPrimaryButtonDown() && event.getClickCount() == 2 
					&& tableViewStockTransfer.getSelectionModel().getSelectedItem() != null)  {
				StockTransfer stockTransfer = tableViewStockTransfer.getSelectionModel().getSelectedItem();
				
				RootViewController.selfRef.presentStockTransferDetailsView(stockTransfer);
			}
			
		}
		
	}
}

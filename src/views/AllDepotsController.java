package views;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import managers.AppDataBaseManager;
import managers.StringsManager;
import models.Depot;
import models.ui.AlertError;
import models.ui.DepotDetailsChangedDelegate;

public class AllDepotsController implements Initializable, DepotDetailsChangedDelegate{


	@FXML Button btnNumberOfBills;
	@FXML Button btnNumberOfProducts;
	@FXML Button btnNumberOfStockTransfer;
	@FXML Button btnModify;

	@FXML TextArea txtObservations;

	@FXML Label lblDepotName;
	@FXML Label lblTotalGain;
	@FXML Label lblDebt;

	@FXML ListView<Depot> listViewDepots;

	@FXML AnchorPane anchorPaneDepotDetails;

	private Depot currentShowenDepot;
	
	private StringsManager stringManager = new StringsManager();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		anchorPaneDepotDetails.setVisible(false);

		listViewDepots.getSelectionModel().selectedItemProperty().addListener(new ChangeListenerDepot());

		ActionEventHandler actionEventHandler = new ActionEventHandler();
		btnNumberOfBills.setOnAction(actionEventHandler);
		btnNumberOfProducts.setOnAction(actionEventHandler);
		btnNumberOfStockTransfer.setOnAction(actionEventHandler);
		btnModify.setOnAction(actionEventHandler);

		try {
			listViewDepots.getItems().setAll(AppDataBaseManager.shared.getAllDepots());
		} catch (SQLException e) {
			AlertError alert = new AlertError("ERROR ERR0034", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();
		}

	}


	private void setDepotDetailsInView(Depot depot){
		currentShowenDepot = depot;
		
		if (depot == null) {
			anchorPaneDepotDetails.setVisible(false);
			return;
		}
		
		anchorPaneDepotDetails.setVisible(true);
		
		lblDepotName.setText(depot.getName());
		txtObservations.setText(depot.getComments());

		try {
			int productsCount = AppDataBaseManager.shared.getProductsCountForDepotCode(depot.getCode());
			btnNumberOfProducts.setText(productsCount+"\n"+"articles en stock");
		} catch (SQLException e) {
			AlertError alert = new AlertError("ERROR ERR0035", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();
		}
		
		try {
			int billsCount = AppDataBaseManager.shared.getBillsCountForDepotCode(depot.getCode());
			btnNumberOfBills.setText(billsCount+"\n"+"factures");
		} catch (SQLException e) {
			AlertError alert = new AlertError("ERROR ERR0036", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();
		}
		
		try {
			int stockTransferCount = AppDataBaseManager.shared.getTransferCountForDepotCode(depot.getCode());
			btnNumberOfStockTransfer.setText(stockTransferCount+"\n"+"transfert de stock");
		} catch (SQLException e) {
			AlertError alert = new AlertError("ERROR ERR0037", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();
		}
		
		
		try {
			String totalGain = Double.toString(AppDataBaseManager.shared.
					getDepotTotalGainedAmmountsByDepotCode(depot.getCode()));
			
			lblTotalGain.setText(totalGain);
		} catch (SQLException e) {
			AlertError alert = new AlertError("ERROR ERR0038", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();
		}
		
		try {
			String totalAmountNotPayed = Double.toString(AppDataBaseManager.shared.
					getDepotAllBillsNotPayedAmmountsByDepotCode(depot.getCode()));
			
			lblDebt.setText(totalAmountNotPayed);
		} catch (SQLException e) {
			AlertError alert = new AlertError("ERROR ERR0039", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();
		}

	}

	private void presentAllBillsView(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/AllBills.fxml"));
			Pane allBillsPane = loader.load();
			AllBillsController allBillsController = loader.getController();
			
			allBillsController.forceSearchToShowOnlyForDepotCode(currentShowenDepot.getCode());
			
			RootViewController.selfRef.showPaneInAlertMode("", allBillsPane, 1100, 600);
		} catch (IOException e) {
			AlertError alert = new AlertError("ERROR ERR0040", "Fatal Error", e.getMessage());
			alert.showAndWait();
		}
	}
	
	private void presentAllProductsView(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/AllProductsInDepot.fxml"));
			Pane allProductsPane = loader.load();
			AllProductsInDepotController allProductsInDepotController = loader.getController();
			allProductsInDepotController.setDepotCode(currentShowenDepot.getCode());
			RootViewController.selfRef.showPaneInAlertMode("", allProductsPane, 770, 500);
		} catch (IOException e) {
			AlertError alert = new AlertError("ERROR ERR0042", "Fatal Error", e.getMessage());
			alert.showAndWait();
		}
	}
	
	private void presentAllStockTransfersView(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/StockTransferHistory.fxml"));
			Pane stockTransferHistoryPane = loader.load();
			StockTransferHistoryController stockTransferHistoryController = loader.getController();
			stockTransferHistoryController.setDepotCode(currentShowenDepot.getCode());
			RootViewController.selfRef.showPaneInAlertMode("", stockTransferHistoryPane, 600, 500);
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					stockTransferHistoryPane.requestFocus();
				}
			});
		} catch (IOException e) {
			AlertError alert = new AlertError("ERROR ERR0042", "Fatal Error", e.getMessage());
			alert.showAndWait();
		}
	}
	
	
	private class ChangeListenerDepot implements ChangeListener<Depot>{

		@Override
		public void changed(ObservableValue<? extends Depot> observable, Depot oldValue, Depot newValue) {
			setDepotDetailsInView(newValue);
		}

	}
	
	private class ActionEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (event.getSource() == btnNumberOfBills) {
				if (Integer.parseInt(stringManager.getOnlyNumbers(btnNumberOfBills.getText())) > 0) {
					presentAllBillsView();
				}
			}else if (event.getSource() == btnNumberOfProducts) {
					presentAllProductsView();
			}else if (event.getSource() == btnNumberOfStockTransfer) {
				if (Integer.parseInt(stringManager.getOnlyNumbers(btnNumberOfStockTransfer.getText())) > 0) {
					presentAllStockTransfersView();
				}
			}else if (event.getSource() == btnModify && currentShowenDepot != null) {
				RootViewController.selfRef.showEditDepotView(currentShowenDepot, AllDepotsController.this);
			}
		}
	}

	@Override
	public void depotDetailsChanged(int depotCode) {
		if (currentShowenDepot.getCode() == depotCode) {
			try {
				Depot depot = AppDataBaseManager.shared.getDepotByCode(depotCode);
				lblDepotName.setText(depot.getName());
				txtObservations.setText(depot.getComments());
				
				currentShowenDepot.setName(depot.getName());
				currentShowenDepot.setComments(depot.getComments());
				
				listViewDepots.refresh();
			} catch (SQLException e) {
				AlertError alert = new AlertError("ERROR ERR0047", "Fatal Error", e.getMessage());
				alert.showAndWait();
			}
		}
	}

}

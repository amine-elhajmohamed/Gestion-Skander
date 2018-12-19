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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import managers.AppDataBaseManager;
import models.Client;
import models.ui.AlertError;
import models.ui.ClientDetailsChangedDelegate;

public class FullClientDetailsController implements Initializable, ClientDetailsChangedDelegate {

	@FXML Button btnModifier;

	@FXML FlowPane containerFPForClientDetails;
	@FXML AnchorPane containerAPForBillsList;

	@FXML Label lblTotalBuys;
	@FXML Label lblTotalGain;
	@FXML Label lblTotalNotPayed;

	
	
	private ClientDetailsController clientDetailsController;
	private AllBillsController allBillsController;

	private Client client = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loadClientDetailsView();
		loadBillsListView();

		ActionEventHandler actionEventHandler = new ActionEventHandler();
		btnModifier.setOnAction(actionEventHandler);
	}


	public void showClientDetails(String clientCode){
		loadClientInformations(clientCode);
		loadClientPaymentInformations(clientCode);
	}

	private void loadClientInformations(String clientCode){
		allBillsController.forceSearchToShowOnlyForClientCode(clientCode);
		
		this.client = clientDetailsController.setClientDetails(clientCode);
	}

	private void loadClientPaymentInformations(String clientCode){
		try {
			double totalBuys = AppDataBaseManager.shared
					.getClientAllBillsTotalsByClientCode(clientCode);
			lblTotalBuys.setText(Double.toString(totalBuys));

			double totalGain = AppDataBaseManager.shared
					.getClientAllBillsGainedAmmountsByClientCode(clientCode);
			lblTotalGain.setText(Double.toString(totalGain));

			double totalNotPayed = AppDataBaseManager.shared
					.getClientAllBillsNotPayedAmmountsByClientCode(clientCode);
			lblTotalNotPayed.setText(Double.toString(totalNotPayed));

		} catch (SQLException e) {
			AlertError alert = new AlertError("ERROR ERR0028", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();
		}
	}
	
	private void closeStage(){
		((Stage)containerFPForClientDetails.getScene().getWindow()).close();
	}

	private void loadBillsListView(){

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/AllBills.fxml"));
			Pane allBillsPane = loader.load();
			allBillsController = loader.getController();

			AnchorPane.setTopAnchor(allBillsPane, 15.0);
			AnchorPane.setBottomAnchor(allBillsPane, 15.0);
			AnchorPane.setLeftAnchor(allBillsPane, 15.0);
			AnchorPane.setRightAnchor(allBillsPane, 15.0);

			containerAPForBillsList.getChildren().clear();
			containerAPForBillsList.getChildren().add(allBillsPane);

			allBillsController.columnBillClientName.setVisible(false);
		} catch (IOException e) {
			AlertError alert = new AlertError("ERROR ERR0026", "Fatal Error", e.getMessage());
			alert.showAndWait();
			closeStage();
		}

	}



	private void loadClientDetailsView(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/ClientDetails.fxml"));
			Pane clientDetailsPane = loader.load();
			clientDetailsController = loader.getController();
			containerFPForClientDetails.getChildren().add(clientDetailsPane);

			clientDetailsController.changeEditsStats(false);
		} catch (IOException e) {
			AlertError alert = new AlertError("ERROR ERR0025", "Fatal Error", e.getMessage());
			alert.showAndWait();
			closeStage();
		}
	}

	private class ActionEventHandler implements EventHandler<ActionEvent>{

		@Override
		public void handle(ActionEvent event) {
			if (event.getSource() == btnModifier && client != null) {
				RootViewController.selfRef.showEditClientView(client, FullClientDetailsController.this);
			}
		}

	}

	@Override
	public void didChangeClientDetails(String oldClientCode, String newClientCode) {
		loadClientInformations(newClientCode);
	}

}

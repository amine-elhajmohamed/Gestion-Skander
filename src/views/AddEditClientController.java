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
import models.Client;
import models.ui.AlertError;
import models.ui.AlertSucces;
import models.ui.ClientDetailsChangedDelegate;

public class AddEditClientController implements Initializable{

	@FXML Button btnAdd;
	@FXML Button btnCancel;

	@FXML FlowPane containerFP;

	public ClientDetailsChangedDelegate delegate = null;
	
	private boolean editMode = false;
	private String currentEditingClientCode = null;
	private ClientDetailsController clientDetailsController;
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		loadClientDetailsView();

		ActionEventHandler actionEventHandler = new ActionEventHandler();
		btnAdd.setOnAction(actionEventHandler);
		btnCancel.setOnAction(actionEventHandler);

		ChangeListenerString changeListenerString = new ChangeListenerString();
		clientDetailsController.txtCode.textProperty().addListener(changeListenerString);
		clientDetailsController.txtName.textProperty().addListener(changeListenerString);
		clientDetailsController.txtLastName.textProperty().addListener(changeListenerString);

		updateBtnAddView();
	}
	
	
	public void editModeForClient(Client client){
		editMode = true;
		currentEditingClientCode = client.getCode();
		btnAdd.setText("Sauvegarder");
		clientDetailsController.txtCode.setText(client.getCode());
		clientDetailsController.txtName.setText(client.getName());
		clientDetailsController.txtLastName.setText(client.getLastName());
		clientDetailsController.txtAdress.setText(client.getAddress());
		
		clientDetailsController.phonesNumbersData.setAll(client.getPhonesNumbers());
		clientDetailsController.faxNumbersData.setAll(client.getFaxNumbers());
		
		clientDetailsController.phonesNumbersData.add(ClientDetailsController.STRING_TAP_TO_ADD_NEW_CLIENT);
		clientDetailsController.faxNumbersData.add(ClientDetailsController.STRING_TAP_TO_ADD_NEW_CLIENT);
		
		updateBtnAddView();
	}

	private void loadClientDetailsView(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/ClientDetails.fxml"));
			Pane clientDetailsPane = loader.load();
			clientDetailsController = loader.getController();
			containerFP.getChildren().add(clientDetailsPane);
		} catch (IOException e) {
			AlertError alert = new AlertError("ERROR ERR0023", "Fatal Error", e.getMessage());
			alert.showAndWait();
			closeStage();
		}
	}

	private void closeStage(){
		((Stage)containerFP.getScene().getWindow()).close();
	}

	private void updateBtnAddView(){
		if ( (clientDetailsController.txtCode.getText().equals("")) || 
				(clientDetailsController.txtName.getText().equals("")) ||
				(clientDetailsController.txtLastName.getText().equals(""))  ){
			btnAdd.setDisable(true);
		}else{
			btnAdd.setDisable(false);
		}
	}

	private void createTheClient(){
		String clientCode = clientDetailsController.txtCode.getText();
		String clientName = clientDetailsController.txtName.getText();
		String clientLastName = clientDetailsController.txtLastName.getText();
		String clientAdress = clientDetailsController.txtAdress.getText();
		ArrayList<String> clientPhonesNumbers = clientDetailsController.getPhonesNumbers();
		ArrayList<String> clientFaxNumbers = clientDetailsController.getFaxNumbers();

		try {

			if (AppDataBaseManager.shared.isClientCodeExist(clientCode)) {
				AlertError alert = new AlertError("Code existe déjà", 
						"Le code ' "+clientCode+" ' existe déjà !", 
						"Réessayer avec un autre code");
				alert.showAndWait();
			}else{
				Client client = new Client(clientCode, clientName, clientLastName, clientAdress, 
						clientPhonesNumbers, clientFaxNumbers);
				AppDataBaseManager.shared.createNewClient(client);
				closeStage();
				AlertSucces alert = new AlertSucces("Client ajouté avec succès", 
						"Client ajouté avec succès", 
						"Le client "+clientName+" "+clientLastName+" avec code "+clientCode
						+" a été ajouté avec succés.");
				alert.showAndWait();
			}

		} catch (SQLException e) {
			AlertError alert = new AlertError("ERROR ERR0024", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();
		}
	}
	
	private void updateTheClient(){
		String clientCode = clientDetailsController.txtCode.getText();
		String clientName = clientDetailsController.txtName.getText();
		String clientLastName = clientDetailsController.txtLastName.getText();
		String clientAdress = clientDetailsController.txtAdress.getText();
		ArrayList<String> clientPhonesNumbers = clientDetailsController.getPhonesNumbers();
		ArrayList<String> clientFaxNumbers = clientDetailsController.getFaxNumbers();
		
		try {
			
			if (!currentEditingClientCode.equals(clientCode)
					&& AppDataBaseManager.shared.isClientCodeExist(clientCode)) {
				AlertError alert = new AlertError("Code existe déjà", 
						"Le code ' "+clientCode+" ' existe déjà !", 
						"Réessayer avec un autre code");
				alert.showAndWait();
			}else{
				Client client = new Client(clientCode, clientName, clientLastName, clientAdress, 
						clientPhonesNumbers, clientFaxNumbers);
				AppDataBaseManager.shared.updateClientDetailsByClientCode(currentEditingClientCode, client);
				delegate.didChangeClientDetails(currentEditingClientCode, clientCode);
				closeStage();
				AlertSucces alert = new AlertSucces("Client modifiée avec succès", 
						"Client modifiée avec succès", 
						"Les informations de la client ont été modifiée avec succés.");
				alert.showAndWait();
			}

		} catch (SQLException e) {
			AlertError alert = new AlertError("ERROR ERR0029", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();
		}
	}

	private class ActionEventHandler implements EventHandler<ActionEvent>{

		@Override
		public void handle(ActionEvent event) {
			if (event.getSource() == btnCancel) {
				closeStage();
			}else if (event.getSource() == btnAdd) {
				if (editMode){
					updateTheClient();
				}else{
					createTheClient();	
				}
			}
		}

	}


	private class ChangeListenerString implements ChangeListener<String> {

		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			updateBtnAddView();
		}

	}
}

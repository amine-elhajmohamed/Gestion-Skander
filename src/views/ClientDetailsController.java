package views;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.ListView.EditEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import managers.AppDataBaseManager;
import managers.StringsManager;
import models.Client;
import models.ui.AlertError;

public class ClientDetailsController implements Initializable{

	static public String STRING_TAP_TO_ADD_NEW_CLIENT = "Ajouter (+)";

	@FXML TextField txtCode;
	@FXML TextField txtName;
	@FXML TextField txtLastName;
	@FXML TextArea txtAdress;

	@FXML ListView<String> listViewPhones;
	@FXML ListView<String> listViewFaxes;

	private StringsManager stringsManager = new StringsManager();

	public ObservableList<String> phonesNumbersData = FXCollections.observableArrayList();
	public ObservableList<String> faxNumbersData = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		listViewPhones.setItems(phonesNumbersData);
		listViewPhones.setCellFactory(TextFieldListCell.forListView());
		listViewPhones.setOnEditCommit(new StringEditEventHandler(listViewPhones));

		listViewFaxes.setItems(faxNumbersData);
		listViewFaxes.setCellFactory(TextFieldListCell.forListView());
		listViewFaxes.setOnEditCommit(new StringEditEventHandler(listViewFaxes));

		ChangeListenerString changeListenerString = new ChangeListenerString();
		txtCode.textProperty().addListener(changeListenerString);
		txtName.textProperty().addListener(changeListenerString);
		txtLastName.textProperty().addListener(changeListenerString);
		txtAdress.textProperty().addListener(changeListenerString);


		phonesNumbersData.add(STRING_TAP_TO_ADD_NEW_CLIENT);
		faxNumbersData.add(STRING_TAP_TO_ADD_NEW_CLIENT);

	}
	
	public Client setClientDetails(String clientCode){
		Client client = null;
		
		try {
			client = AppDataBaseManager.shared.getClientByCode(clientCode);
			
			client.setPhonesNumbers(AppDataBaseManager.shared.getClientsPhonesNumbersByClientCode(clientCode));
			client.setFaxNumbers(AppDataBaseManager.shared.getClientsFaxNumbersByClientCode(clientCode));
			
			txtCode.setText(client.getCode());
			txtName.setText(client.getName());
			txtLastName.setText(client.getLastName());
			txtAdress.setText(client.getAddress());

			phonesNumbersData.setAll(client.getPhonesNumbers());
			faxNumbersData.setAll(client.getFaxNumbers());
		} catch (SQLException e) {
			AlertError alert = new AlertError("ERROR ERR0027", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();
		}
		
		return client;
	}

	public void changeEditsStats(boolean enabled){
		txtCode.setEditable(enabled);
		txtName.setEditable(enabled);
		txtLastName.setEditable(enabled);
		txtAdress.setEditable(enabled);
		listViewPhones.setEditable(enabled);
		listViewFaxes.setEditable(enabled);
	}

	public ArrayList<String> getPhonesNumbers(){
		ArrayList<String> phonesNumbers = new ArrayList<>();
		for (int i=0; i<phonesNumbersData.size()-1; i++) {
			phonesNumbers.add(phonesNumbersData.get(i));
		}
		return phonesNumbers;
	}
	
	public ArrayList<String> getFaxNumbers(){
		ArrayList<String> faxNumbers = new ArrayList<>();
		for (int i=0; i<faxNumbersData.size()-1; i++) {
			faxNumbers.add(faxNumbersData.get(i));
		}
		return faxNumbers;
	}


	private class ChangeListenerString implements ChangeListener<String> {

		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

			if ( ((StringProperty)observable).getBean() instanceof TextField ){
				TextField txt = (TextField) ((StringProperty)observable).getBean();

				if (txt == txtCode){
					String ch = stringsManager.getOnlyLettersAndNumbers(txtCode.getText());
					ch = ch.replace(" ", "");
					ch = ch.toUpperCase();
					txtCode.setText(ch);
				}else if (txt == txtName) {
					String ch = stringsManager.getOnlyLetters(txtName.getText());
					ch = ch.replace("  ", " ");
					txtName.setText(ch);
				}else if (txt == txtLastName) {
					String ch = stringsManager.getOnlyLetters(txtLastName.getText());
					ch = ch.replace("  ", " ");
					txtLastName.setText(ch);
				}

			}else if ( ((StringProperty)observable).getBean() instanceof TextArea ){
				TextArea txtArea = (TextArea) ((StringProperty)observable).getBean();

				if (txtArea == txtAdress) {
					String ch = txtAdress.getText();
					ch = ch.replace("  ", " ");
					txtAdress.setText(ch);
				}
			}

		}

	}

	private class StringEditEventHandler implements EventHandler<EditEvent<String>>{

		private ListView<String> listView;

		public StringEditEventHandler(ListView<String> listView) {
			this.listView = listView;
		}

		@Override
		public void handle(EditEvent<String> event) {

			boolean editMode = false;

			if (listView == listViewPhones) {
				editMode = event.getIndex() < phonesNumbersData.size()-1;
			}else if (listView == listViewFaxes){
				editMode = event.getIndex() < faxNumbersData.size()-1;
			}

			String firstChar = "";

			if (event.getNewValue().startsWith("+")) {
				firstChar = "+";
			}

			String newNumber = stringsManager.getOnlyNumbers(event.getNewValue());

			if (newNumber.equals("") && editMode) {

				if (listView == listViewPhones) {
					phonesNumbersData.remove(event.getIndex());
				}else if (listView == listViewFaxes){
					faxNumbersData.remove(event.getIndex());
				}

			}else if (!newNumber.equals("")){

				newNumber = firstChar+newNumber;

				if (editMode){

					if (listView == listViewPhones) {
						phonesNumbersData.set(event.getIndex(), newNumber);
					}else if (listView == listViewFaxes){
						faxNumbersData.set(event.getIndex(), newNumber);
					}

				}else{

					if (listView == listViewPhones) {
						if (phonesNumbersData.contains(newNumber)) {
							AlertError alert = new AlertError("Numéro déja ajouté", null, "Numéro déja ajouté");
							alert.showAndWait();
						}else{
							phonesNumbersData.add(phonesNumbersData.size()-1, newNumber);	
						}
					}else if (listView == listViewFaxes){
						if (faxNumbersData.contains(newNumber)) {
							AlertError alert = new AlertError("Numéro déja ajouté", null, "Numéro déja ajouté");
							alert.showAndWait();
						}else{
							faxNumbersData.add(faxNumbersData.size()-1, newNumber);	
						}
					}

				}

			}

		}

	}
}

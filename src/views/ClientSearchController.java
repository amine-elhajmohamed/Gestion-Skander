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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import managers.AppDataBaseManager;
import managers.StringsManager;
import models.Client;
import models.ui.ClientSearchPickedClientDelegate;

public class ClientSearchController implements Initializable{

	@FXML TextField txtCode;
	@FXML TextField txtName;
	@FXML TextField txtLastName;
	@FXML TextField txtAddress;
	
	@FXML Button btnSearch;
	
	@FXML TableView<Client> tableViewClients;
	@FXML TableColumn<Client, String> columnCode;
	@FXML TableColumn<Client, String> columnName;
	@FXML TableColumn<Client, String> columnLastName;
	@FXML TableColumn<Client, String> columnAddress;
	
	public ClientSearchPickedClientDelegate delegate;
	
	private StringsManager stringsManager = new StringsManager();
	
	private ObservableList<Client> clientsData = FXCollections.observableArrayList();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		columnCode.setCellValueFactory(new PropertyValueFactory<>("code"));
		columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		columnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
		columnAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
		
		ActionEventHandler actionEventHandler = new ActionEventHandler();
		btnSearch.setOnAction(actionEventHandler);
		txtCode.setOnAction(actionEventHandler);
		txtName.setOnAction(actionEventHandler);
		txtLastName.setOnAction(actionEventHandler);
		txtAddress.setOnAction(actionEventHandler);
		
		TextFieldChangeListener textFieldChangeListener = new TextFieldChangeListener();
		txtCode.textProperty().addListener(textFieldChangeListener);
		
		tableViewClients.setOnMousePressed(new MouseEventHandler());
		
		tableViewClients.setItems(clientsData);
	}

	
	// Pass "" Empty strings if not want searchConstraint
	public void forceSearch(String clientCode){
		txtCode.setText(clientCode);
		refrechTableViewData();
	}
	
	private void refrechTableViewData(){

		clientsData.clear();

		ArrayList<String> clientsCodes;

		try {
			clientsCodes = AppDataBaseManager.shared.getAllClientsCodes(txtCode.getText(), txtName.getText(), 
					txtLastName.getText(), txtAddress.getText());

			for (int i=0;i<clientsCodes.size();i++){
				
				Client client = AppDataBaseManager.shared.getClientByCode(clientsCodes.get(i));
				
				clientsData.add(client);

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void closeStage(){
		((Stage) tableViewClients.getScene().getWindow()).close();
	}
	
	private class ActionEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (event.getSource() == btnSearch || event.getSource() == txtCode 
					|| event.getSource() == txtName || event.getSource() == txtLastName 
					|| event.getSource() == txtAddress) {
				refrechTableViewData();
			}
		}
	}
	
	private class MouseEventHandler implements EventHandler<MouseEvent>{

		@Override
		public void handle(MouseEvent event) {

			if (event.isPrimaryButtonDown() && event.getClickCount() == 2 
					&& tableViewClients.getSelectionModel().getSelectedItem() != null )  {
				delegate.clientSearchPickedClient(tableViewClients.getSelectionModel().getSelectedItem());
				closeStage();
			}else if (event.isSecondaryButtonDown() 
					&& tableViewClients.getSelectionModel().getSelectedItem() != null) {
				Client client = tableViewClients.getSelectionModel().getSelectedItem();
				
				RootViewController.selfRef.presentClientDetailsView(client.getCode());
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
			}

		}

	}
	
}

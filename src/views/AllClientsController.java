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
import models.Client;
import models.ui.AlertError;

public class AllClientsController implements Initializable {

	@FXML Button btnSearch;

	@FXML TextField txtCode;
	@FXML TextField txtName;
	@FXML TextField txtLastName;
	@FXML TextField txtAdress;

	@FXML CheckBox checkBoxShowMoreDetails;

	@FXML TableView<Client> tableViewAllClients;
	@FXML TableColumn<Client, String> columnClientCode;
	@FXML TableColumn<Client, String> columnClientName;
	@FXML TableColumn<Client, String> columnClientLastName;
	@FXML TableColumn<Client, Double> columnClientTotalBuys;
	@FXML TableColumn<Client, Double> columnClientTotalGain;
	@FXML TableColumn<Client, Double> columnClientTotalNotPayed;


	private ObservableList<Client> clientsData = FXCollections.observableArrayList();

	private boolean showPlusDetailsInTableView = false;
	private StringsManager stringsManager = new StringsManager();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		refrechColumnsPlusDetailsViews();

		columnClientCode.setCellValueFactory(new PropertyValueFactory<>("code"));
		columnClientName.setCellValueFactory(new PropertyValueFactory<>("name"));
		columnClientLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
		columnClientTotalBuys.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Client,Double>, ObservableValue<Double>>() {
			@Override
			public ObservableValue<Double> call(CellDataFeatures<Client, Double> param) {
				if (!showPlusDetailsInTableView) {
					return null;
				}
				try {
					double totalBuys = AppDataBaseManager.shared
							.getClientAllBillsTotalsByClientCode(param.getValue().getCode());
					return new SimpleDoubleProperty(totalBuys).asObject();
				} catch (SQLException e) {
					AlertError alert = new AlertError("ERROR ERR0018", "SQL error code : "+e.getErrorCode(),e.getMessage());
					alert.showAndWait();
					return null;
				}
			}
		});
		columnClientTotalGain.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Client,Double>, ObservableValue<Double>>() {
			@Override
			public ObservableValue<Double> call(CellDataFeatures<Client, Double> param) {
				if (!showPlusDetailsInTableView) {
					return null;
				}
				try {
					double totalGain = AppDataBaseManager.shared
							.getClientAllBillsGainedAmmountsByClientCode(param.getValue().getCode());
					return new SimpleDoubleProperty(totalGain).asObject();
				} catch (SQLException e) {
					AlertError alert = new AlertError("ERROR ERR0019", "SQL error code : "+e.getErrorCode(),e.getMessage());
					alert.showAndWait();
					return null;
				}
			}
		});
		columnClientTotalNotPayed.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Client,Double>, ObservableValue<Double>>() {
			@Override
			public ObservableValue<Double> call(CellDataFeatures<Client, Double> param) {
				if (!showPlusDetailsInTableView) {
					return null;
				}
				try {
					double totalNotPayed = AppDataBaseManager.shared
							.getClientAllBillsNotPayedAmmountsByClientCode(param.getValue().getCode());
					return new SimpleDoubleProperty(totalNotPayed).asObject();
				} catch (SQLException e) {
					AlertError alert = new AlertError("ERROR ERR0020", "SQL error code : "+e.getErrorCode(),e.getMessage());
					alert.showAndWait();
					return null;
				}
			}
		});

		tableViewAllClients.setRowFactory(new TableViewRowFactory());

		ActionEventHandler actionEventHandler = new ActionEventHandler();
		btnSearch.setOnAction(actionEventHandler);
		txtCode.setOnAction(actionEventHandler);
		txtName.setOnAction(actionEventHandler);
		txtLastName.setOnAction(actionEventHandler);
		txtAdress.setOnAction(actionEventHandler);
		checkBoxShowMoreDetails.setOnAction(actionEventHandler);
		
		TextFieldChangeListener textFieldChangeListener = new TextFieldChangeListener();
		txtCode.textProperty().addListener(textFieldChangeListener);
		txtName.textProperty().addListener(textFieldChangeListener);
		txtLastName.textProperty().addListener(textFieldChangeListener);
		txtAdress.textProperty().addListener(textFieldChangeListener);
		
		TableViewMousePressedHandler tableViewMousePressedHandler = new TableViewMousePressedHandler();
		tableViewAllClients.setOnMousePressed(tableViewMousePressedHandler);

		tableViewAllClients.setItems(clientsData);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtCode.requestFocus();
			}
		});

	}


	public void refrechTableViewData(){

		clientsData.clear();

		try {

			ArrayList<String>  clientsCodes = AppDataBaseManager.shared.getAllClientsCodes(txtCode.getText(), 
					txtName.getText(), txtLastName.getText(), txtAdress.getText());

			for (int i=0;i<clientsCodes.size();i++){
				Client client = AppDataBaseManager.shared.getClientByCode(clientsCodes.get(i));
				clientsData.add(client);
			}


		} catch (SQLException e) {
			AlertError alert = new AlertError("ERROR ERR0017", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();
		}
	}


	private void refrechColumnsPlusDetailsViews(){
		columnClientTotalBuys.setVisible(showPlusDetailsInTableView);
		columnClientTotalGain.setVisible(showPlusDetailsInTableView);
		columnClientTotalNotPayed.setVisible(showPlusDetailsInTableView);
	}

	private class ActionEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (event.getSource() == checkBoxShowMoreDetails) {
				showPlusDetailsInTableView = checkBoxShowMoreDetails.isSelected();
				refrechColumnsPlusDetailsViews();				
			}else if (event.getSource() == btnSearch || event.getSource() == txtCode 
					|| event.getSource() == txtName || event.getSource() == txtLastName
					|| event.getSource() == txtAdress) {
				refrechTableViewData();
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
				String ch = stringsManager.getOnlyLetters(txt.getText());
				ch = ch.replace("  ", " ");
				txt.setText(ch);
			}else if (txt == txtLastName) {
				String ch = stringsManager.getOnlyLetters(txt.getText());
				ch = ch.replace("  ", " ");
				txt.setText(ch);
			}else if (txt == txtAdress) {
				txt.setText(txt.getText().replace("  ", " "));
			}

		}

	}
	
	private class TableViewMousePressedHandler implements EventHandler<MouseEvent>{
		@Override
		public void handle(MouseEvent event) {
			
			if (event.isPrimaryButtonDown() && event.getClickCount() == 2 
					&& tableViewAllClients.getSelectionModel().getSelectedItem() != null)  {
				Client client = tableViewAllClients.getSelectionModel().getSelectedItem();
				RootViewController.selfRef.presentClientFullDetailsView(client.getCode());
			}
			
		}
		
	}

	private class TableViewRowFactory implements Callback<TableView<Client>, TableRow<Client>> {

		@Override
		public TableRow<Client> call(TableView<Client> param) {
			return new TableRow<Client>() {
				@Override
				protected void updateItem(Client item, boolean empty) {
					super.updateItem(item, empty);
					
					if (item == null) {
						setStyle("");
					}else {
						try {
							double amountNotPayed = AppDataBaseManager.shared
									.getClientAllBillsNotPayedAmmountsByClientCode(item.getCode());

							if (amountNotPayed == 0) {
								setStyle("");
							}else{
								setStyle("-fx-background-color: red;");
							}
						} catch (SQLException e) {
							setStyle("");
							AlertError alert = new AlertError("ERROR ERR0021", "SQL error code : "+e.getErrorCode(),e.getMessage());
							alert.showAndWait();
						}

					}
					
				}
			};
		}
		
	}

}

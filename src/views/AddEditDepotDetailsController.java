package views;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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
import models.Depot;
import models.ui.AlertError;
import models.ui.AlertSucces;
import models.ui.AlertWarning;
import models.ui.DepotDetailsChangedDelegate;

public class AddEditDepotDetailsController implements Initializable {

	@FXML Button btnAdd;
	@FXML Button btnCancel;

	@FXML FlowPane containerFP;

	public DepotDetailsChangedDelegate delegate;
	
	private Pane paneDepotDetails;
	private DepotDetailsController depotDetailsController;
	
	private boolean editMode = false;
	private Depot currentEditingDepot = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

		loadDepoDetailsView();
		updateBtnAddView();

		ButtonActionEventHandler buttonActionEventHandler = new ButtonActionEventHandler();
		btnAdd.setOnAction(buttonActionEventHandler);
		btnCancel.setOnAction(buttonActionEventHandler);
		
		depotDetailsController.txtName.textProperty().addListener(new TextFieldChangeListener());
	}

	private void loadDepoDetailsView(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/DepotDetails.fxml"));
			paneDepotDetails = loader.load();
			depotDetailsController = loader.getController();
			containerFP.getChildren().add(paneDepotDetails);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void forceSetEditModeForDepot(Depot depot){
		editMode = true;
		currentEditingDepot = depot;
		
		depotDetailsController.txtName.setText(depot.getName());
		depotDetailsController.txtObservations.setText(depot.getComments());
		
		btnAdd.setText("Sauvegarder");
		updateBtnAddView();
	}

	private void updateBtnAddView(){
		if (depotDetailsController.txtName.getText().equals("")) {
			btnAdd.setDisable(true);
		}else{
			btnAdd.setDisable(false);
		}
	}
	
	private void closeStage(){
		((Stage) containerFP.getScene().getWindow()).close();
	}
	
	private void createTheDepot(){
		String name = depotDetailsController.txtName.getText();
		String comments = depotDetailsController.txtObservations.getText();

		try {
			
			if (AppDataBaseManager.shared.isDepotNameExist(name)) {
				AlertWarning alert = new AlertWarning("Nom existe déjà", 
						"Le nom du dépôt ' "+name+" ' existe déjà !", 
						"Réessayer avec un autre nom");
				alert.showAndWait();
			}else{
				AppDataBaseManager.shared.addNewDepot(name, comments);
				closeStage();
				AlertSucces alert = new AlertSucces("Dépôt ajouté avec succès", 
						"Dépôt ajouté avec succès", 
						"Le dépôt "+name+" a été ajouté avec succés.");
				alert.showAndWait();
			}
		} catch (SQLException e) {
			AlertError alert = new AlertError("ERROR ERR0001", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();
		}
	}
	
	
	private void updateTheDepot(){
		String name = depotDetailsController.txtName.getText();
		String comments = depotDetailsController.txtObservations.getText();

		try {
			if (!currentEditingDepot.getName().equals(name) 
					&& AppDataBaseManager.shared.isDepotNameExist(name)) {
				AlertWarning alert = new AlertWarning("Nom existe déjà", 
						"Le nom du dépôt ' "+name+" ' existe déjà !", 
						"Réessayer avec un autre nom");
				alert.showAndWait();
			}else{
				AppDataBaseManager.shared.updateDepotDetails(currentEditingDepot.getCode(), name, comments);
				delegate.depotDetailsChanged(currentEditingDepot.getCode());
				closeStage();
				AlertSucces alert = new AlertSucces("Dépôt modifiée avec succès", 
						"Dépôt modifiée avec succès", 
						"Les informations de la dépôt ont été modifiée avec succés.");
				alert.showAndWait();
			}
		} catch (SQLException e) {
			AlertError alert = new AlertError("ERROR ERR0046", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();
		}
	}
	
	private class ButtonActionEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (event.getSource() == btnAdd) {
				if (editMode){
					updateTheDepot();
				}else{
					createTheDepot();	
				}
			}
			else if (event.getSource() == btnCancel) {
				closeStage();
			}
			
		}
	}
	
	private class TextFieldChangeListener implements ChangeListener<String>{

		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			depotDetailsController.txtName.setText(depotDetailsController.txtName.getText().replaceAll("  ", " "));
			updateBtnAddView();
		}
	}
}

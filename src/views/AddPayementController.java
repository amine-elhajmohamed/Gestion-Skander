package views;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import managers.AppDataBaseManager;
import managers.StringsManager;
import models.Payment;
import models.ui.AddPaymentDelegate;
import models.ui.AlertError;
import models.ui.AlertSucces;
import models.ui.StringConverterLocalDate;
import models.ui.TimesSpinerConfigurator;

public class AddPayementController implements Initializable{

	@FXML Button btnPay;
	@FXML Button btnCancel;
	
	@FXML Label lblBillNumber;
	@FXML Label lblMoneyTotal;
	@FXML Label lblMoneyPayed;
	@FXML Label lblMoneyToPay;
	
	@FXML TextField txtAmount;
	
	@FXML ComboBox<String> comboBoxPaymentType;
	
	@FXML Spinner<Integer> spinerHoures;
	@FXML Spinner<Integer> spinerMinutes;
	
	@FXML DatePicker date;
	
	public AddPaymentDelegate delegate;
	
	private StringsManager stringsManager = new StringsManager();
	
	private double maxAmountToPay;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		configureTimeView();
		
		comboBoxPaymentType.getItems().add(Payment.TYPE_CASH);
		comboBoxPaymentType.getItems().add(Payment.TYPE_CHECK);
		comboBoxPaymentType.getSelectionModel().select(0);
		
		TextFieldChangeListener textFieldChangeListener = new TextFieldChangeListener();
		txtAmount.textProperty().addListener(textFieldChangeListener);
		
		ActionEventHandler actionEventHandler = new ActionEventHandler();
		btnPay.setOnAction(actionEventHandler);
		btnCancel.setOnAction(actionEventHandler);
		
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtAmount.requestFocus();
			}
		});
	}
	
	public void setupDetails(String billCode,double amountTotalToPay, double amountPayed){
		maxAmountToPay = amountTotalToPay-amountPayed;
		
		lblBillNumber.setText(billCode);
					
		lblMoneyTotal.setText(Double.toString(amountTotalToPay));
		lblMoneyPayed.setText(Double.toString(amountPayed));
		lblMoneyToPay.setText(Double.toString(maxAmountToPay));
	}
	
	private void configureTimeView(){
		TimesSpinerConfigurator timesSpinerConfigurator = new TimesSpinerConfigurator(spinerHoures, spinerMinutes);
		timesSpinerConfigurator.configure();

		date.setConverter(new StringConverterLocalDate());

		date.setValue(LocalDate.now());
	}

	private void addThePayment(){
		if (txtAmount.getText().equals("")) {
			AlertError alert = new AlertError("Entrer un montant du paiement", null, 
					"Vous devez entrer un montant du paiement");
			alert.showAndWait();
			return;
		}
		double newAmountPayed = Double.parseDouble(txtAmount.getText());
		
		if (newAmountPayed > maxAmountToPay) {
			AlertError alert = new AlertError("Entrez le montant du paiement à nouveau", null, 
					"Le montant que vous avez entré est supérieur à celui demandé");
			alert.showAndWait();
			return;
		}
		
		
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Confirmation du paiement");
		alert.setHeaderText("Paiement FACTURE N°"+lblBillNumber.getText());
		alert.setContentText("Confirmation de la paiement avec le montant "+newAmountPayed
				+" ("+comboBoxPaymentType.getValue()+").");
		
		ButtonType btnConfirme = new ButtonType("Confirmer");
		ButtonType btnCancel = new ButtonType("Annuler");
		
		alert.getButtonTypes().setAll(btnCancel,btnConfirme);
		
		if (alert.showAndWait().get() == btnConfirme) {
			LocalDateTime localDateTime = 
					date.getValue().atTime(spinerHoures.getValue(), spinerMinutes.getValue());
			Timestamp timestamp = Timestamp.valueOf(localDateTime);
			
			Payment payment = new Payment(newAmountPayed, comboBoxPaymentType.getValue(), timestamp);
			
			try {
				AppDataBaseManager.shared.addPaymentToBill(lblBillNumber.getText(), payment);
				
				AlertSucces alertSucces = new AlertSucces("Paiement effectué avec succès", 
						"Paiement effectué avec succès", 
						"La paiement du facture N°"+lblBillNumber.getText()
						+" avec le montant "+newAmountPayed+" ("+comboBoxPaymentType.getValue()+") "
								+ "est effectué avec succès.");
				
				delegate.didAddNewPayment(payment);
				closeStage();
				alertSucces.showAndWait();
			} catch (SQLException e) {
				AlertError alertError = new AlertError("ERROR ERR0013", "SQL error code : "+e.getErrorCode(),e.getMessage());
				alertError.showAndWait();
			}

		}
		
	}
	
	private void closeStage(){
		((Stage)lblBillNumber.getScene().getWindow()).close();
	}
	
	private class TextFieldChangeListener implements ChangeListener<String> {

		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			TextField txt = (TextField) ((StringProperty)observable).getBean();

			if (txt == txtAmount) {
				String ch = stringsManager.getDoubleFormat(txt.getText());
				
				if (!ch.equals("") && Double.parseDouble(ch) > maxAmountToPay) {
					txt.setText(oldValue);
				}else{
					txt.setText(ch);
				}
				
			}

		}

	}
	
	private class ActionEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (event.getSource() == btnPay) {
				addThePayment();
			}else if (event.getSource() == btnCancel) {
				closeStage();
			}
		}
	}
	
}

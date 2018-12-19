package views;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Bill;
import models.Client;
import models.Depot;
import models.Product;
import models.StockTransfer;
import models.ui.AddPaymentDelegate;
import models.ui.ClientDetailsChangedDelegate;
import models.ui.ClientSearchPickedClientDelegate;
import models.ui.DepotDetailsChangedDelegate;
import models.ui.ProductDetailsChangedDelegate;
import models.ui.ProductSearchPickedProductDelegate;

public class RootViewController implements Initializable, EventHandler<ActionEvent> {

	static public RootViewController selfRef;

	@FXML Button btnClients;
	@FXML Button btnAddClient;
	@FXML Button btnBills;
	@FXML Button btnAddBill;

	@FXML Button btnArticles;
	@FXML Button btnAddArticle;
	@FXML Button btnEntryStock;
	@FXML Button btnTransferStock;
	@FXML Button btnAddDepot;
	@FXML Button btnDepots;
	@FXML Button btnInventaires;
	@FXML Button btnAddInventaire;


	@FXML AnchorPane containerAP;

	public RootViewController() {
		RootViewController.selfRef = this;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		btnClients.setOnAction(this);
		btnAddClient.setOnAction(this);
		btnBills.setOnAction(this);
		btnAddBill.setOnAction(this);

		btnArticles.setOnAction(this);
		btnAddArticle.setOnAction(this);
		btnEntryStock.setOnAction(this);
		btnTransferStock.setOnAction(this);
		btnAddDepot.setOnAction(this);
		btnDepots.setOnAction(this);
		btnInventaires.setOnAction(this);
		btnAddInventaire.setOnAction(this);

	}


	public void showPaneInContainer(Pane p) {

		AnchorPane.setTopAnchor(p, 0.0);
		AnchorPane.setBottomAnchor(p, 0.0);
		AnchorPane.setLeftAnchor(p, 0.0);
		AnchorPane.setRightAnchor(p, 0.0);

		containerAP.getChildren().clear();
		containerAP.getChildren().add(p);
		System.gc();

	}

	public void showPaneInAlertMode(String title,Pane p,double width,double height) {

		Scene scene = new Scene(p, width, height);
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setResizable(false);
		stage.setTitle(title);

		stage.setScene(scene);
		stage.show();
	}


	@Override
	public void handle(ActionEvent event) {

		if (event.getSource() == btnClients) {
			showAllClientsView();
		}

		else if (event.getSource() == btnAddClient) {
			showAddNewClientView();
		}

		else if (event.getSource() == btnBills) {
			showAllBillsView();
		}

		else if (event.getSource() == btnAddBill) {
			showAddNewBillView();
		}

		else if (event.getSource() == btnArticles) {
			showAllProductView();
		}

		else if (event.getSource() == btnAddArticle) {
			showAddNewProductView();
		}

		else if (event.getSource() == btnEntryStock) {
			showStockInputView();
		}

		else if (event.getSource() == btnTransferStock) {
			showStockTransferView();
		}

		else if (event.getSource() == btnAddDepot) {
			showAddNewDepotView();
		}

		else if (event.getSource() == btnDepots) {
			showAllDepotsView();
		}

		else if (event.getSource() == btnInventaires) {
			System.out.println("hhh");
		}

		else if (event.getSource() == btnAddInventaire) {
			System.out.println("mmm");

		}

	}
	
	//Clients
	
	private void showAddNewClientView(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/AddEditClient.fxml"));
			Pane p = loader.load();

			showPaneInAlertMode("Ajouter client", p, 660, 350);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void showEditClientView(Client client, ClientDetailsChangedDelegate delegate){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/AddEditClient.fxml"));
			Pane p = loader.load();
			AddEditClientController controller = loader.getController();
			controller.editModeForClient(client);
			controller.delegate = delegate;
			showPaneInAlertMode("Ajouter client", p, 660, 350);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void presentClientDetailsView(String clientCode){
		
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/ClientDetails.fxml"));
			Pane p = loader.load();
			ClientDetailsController controller = loader.getController();
			controller.setClientDetails(clientCode);
			controller.changeEditsStats(false);
			showPaneInAlertMode("Client détails", p, 660, 280);
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					p.requestFocus();
				}
			});//to remove focus from txtCode
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void presentClientFullDetailsView(String clientCode){
		
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/FullClientDetails.fxml"));
			Pane p = loader.load();
			FullClientDetailsController controller = loader.getController();
			controller.showClientDetails(clientCode);
			showPaneInAlertMode("Client détails", p, 730, 550);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	private void showAllClientsView(){

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/AllClients.fxml"));
			Pane p = loader.load();
			showPaneInContainer(p);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	//Enter "" empty string in search constraint to disable the associated constraint
	public void presentClientSearchView(String forClientCode, ClientSearchPickedClientDelegate delegate){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/ClientSearch.fxml"));
			Pane p = loader.load();
			ClientSearchController clientSearchController = loader.getController();
			clientSearchController.delegate = delegate;
			
			if (! forClientCode.equals("") ){
				clientSearchController.forceSearch(forClientCode);
			}
			
			showPaneInAlertMode("", p, 1135, 500);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//Bills
	
	private void showAddNewBillView(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/AddEditBill.fxml"));
			Pane p = loader.load();

			showPaneInAlertMode("", p, 1400, 750);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void presentBillDetailsView(Bill bill){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/AddEditBill.fxml"));
			Pane p = loader.load();
			AddEditBillController controller = loader.getController();
			controller.showBillDetails(bill);
			showPaneInAlertMode("", p, 1400, 750);
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					p.requestFocus();
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void showAllBillsView(){

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/AllBills.fxml"));
			Pane p = loader.load();
			showPaneInContainer(p);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	//Payments
	
	
	
	public void presentAddPaymentView(String forBillCode,double amountTotalToPay, double amountPayed, 
			AddPaymentDelegate delegate){
		
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/AddPayement.fxml"));
			Pane p = loader.load();
			AddPayementController addPayementController = loader.getController();
			addPayementController.setupDetails(forBillCode, amountTotalToPay, amountPayed);
			addPayementController.delegate = delegate;
			
			showPaneInAlertMode("", p, 930, 340);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//Products
	
	private void showAllProductView(){

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/AllProducts.fxml"));
			Pane p = loader.load();
			showPaneInContainer(p);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void showAddNewProductView(){

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/AddEditProductDetails.fxml"));
			Pane p = loader.load();

			showPaneInAlertMode("", p, 850, 400);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void showEditProductView(Product product, ProductDetailsChangedDelegate delegate){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/AddEditProductDetails.fxml"));
			Pane p = loader.load();
			AddEditProductDetailsController controller = loader.getController();
			controller.forceSetEditModeForProduct(product);
			controller.delegate = delegate;
			showPaneInAlertMode("Ajouter client", p, 850, 400);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void presentProductDetailsView(Product product){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/ProductDetails.fxml"));
			Pane p = loader.load();
			ProductDetailsController productDetailsController = loader.getController();
			
			productDetailsController.loadProductDetails(product);
			productDetailsController.changeEditsStats(false);
			
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					p.requestFocus();
				}
			});//to remove focus from txtCode
			
			showPaneInAlertMode("", p, 750, 300);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void presentProductFullDetailsView(Product product){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/FullProductDetails.fxml"));
			Pane p = loader.load();
			FullProductDetailsController controller = loader.getController();
			
			controller.showProductDetails(product);
			showPaneInAlertMode("Article détails", p, 780, 400);
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					p.requestFocus();
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	//Enter "" empty string in search constraint to disable the associated constraint
	public void presentProductSearchView(Depot forDepot, ProductSearchPickedProductDelegate delegate, 
			String forProductCode, String forProductName){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/ProductSearch.fxml"));
			Pane p = loader.load();
			ProductSearchController productSearchController = loader.getController();
			productSearchController.delegate = delegate;
			if (forDepot != null) {
				productSearchController.configureDepot(forDepot);
			}
			if (! forProductCode.equals("") || ! forProductName.equals("")){
				productSearchController.forceSearch(forProductCode, forProductName);
			}
			
			showPaneInAlertMode("", p, 850, 400);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void presentProductSearchView(ProductSearchPickedProductDelegate delegate, 
			String forProductCode, String forProductName){
		presentProductSearchView(null, delegate, forProductCode, forProductName);
	}

	//Depots
	
	private void showAddNewDepotView() {

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/AddEditDepotDetails.fxml"));
			Pane p = loader.load();

			showPaneInAlertMode("", p, 500, 300);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void showEditDepotView(Depot depot, DepotDetailsChangedDelegate delegate){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/AddEditDepotDetails.fxml"));
			Pane p = loader.load();
			AddEditDepotDetailsController controller = loader.getController();
			controller.forceSetEditModeForDepot(depot);
			controller.delegate = delegate;
			showPaneInAlertMode("", p, 500, 300);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void showAllDepotsView(){

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/AllDepots.fxml"));
			Pane p = loader.load();
			showPaneInContainer(p);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

	
	//Stocks
	
	
	private void showStockInputView() {

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/StockTransfer.fxml"));
			Pane p = loader.load();
			( (StockTransferController) loader.getController() ).forceSetTransferFromAdminMode();
			showPaneInAlertMode("", p, 900, 730);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	private void showStockTransferView() {

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/StockTransfer.fxml"));
			Pane p = loader.load();

			showPaneInAlertMode("", p, 900, 730);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void presentStockTransferDetailsView(StockTransfer stockTransfer) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/StockTransfer.fxml"));
			Pane p = loader.load();
			StockTransferController stockTransferController = loader.getController();
			stockTransferController.showStockTransferDetails(stockTransfer);
			showPaneInAlertMode("", p, 900, 600);
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					p.requestFocus();
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

}

package views;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import managers.AppDataBaseManager;
import models.Depot;
import models.Product;
import models.ProductStock;
import models.ui.AlertError;
import models.ui.StringConverterTimestamp;

public class AllProductsInDepotController implements Initializable{

	@FXML Button btnPrint;

	@FXML TableView<ProductStock> tableViewProducts;
	@FXML TableColumn<ProductStock, String> columnCode;
	@FXML TableColumn<ProductStock, String> columnName;
	@FXML TableColumn<ProductStock, Double> columnStock;

	@FXML TabPane mainTabPane;
	@FXML Tab tabAvailableProducts;
	@FXML Tab tabOutOfStockProducts;
	@FXML Tab tabMissingProducts;

	private Integer depotCode = null;
	private Timestamp lastRefreched;
	private ObservableList<ProductStock> productsStockData = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		columnCode.setCellValueFactory(new PropertyValueFactory<>("Code"));
		columnName.setCellValueFactory(new PropertyValueFactory<>("Name"));
		columnStock.setCellValueFactory(new PropertyValueFactory<>("Qnt"));

		tableViewProducts.setRowFactory(new TableViewRowFactory());

		ActionEventHandler actionEventHandler = new ActionEventHandler();
		btnPrint.setOnAction(actionEventHandler);

		tableViewProducts.setItems(productsStockData);

		tableViewProducts.setOnMousePressed(new TableViewMousePressedHandler());

		mainTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListenerTab());
	}

	public void setDepotCode(int depotCode){
		this.depotCode = depotCode;
		updateTableViewData();
	}

	private void updateTableViewData(){
		lastRefreched = new Timestamp(System.currentTimeMillis());
		Tab selectedTab = mainTabPane.getSelectionModel().getSelectedItem();

		productsStockData.clear();

		if (selectedTab == null || depotCode == null) {
			return;
		}

		try {
			ArrayList<String> productsCodes = new ArrayList<>();

			if (selectedTab == tabAvailableProducts) {
				productsCodes = AppDataBaseManager.shared.getAllAvailableProductsCodesByDepotCode(depotCode);
			}else if (selectedTab == tabOutOfStockProducts) {
				productsCodes = AppDataBaseManager.shared.getAllOutOfStockProductsCodesByDepotCode(depotCode);
			}else if (selectedTab == tabMissingProducts) {
				productsCodes = AppDataBaseManager.shared.getAllMissingProductsCodesByDepotCode(depotCode);
			}

			for (int i=0;i<productsCodes.size();i++){
				Product product = AppDataBaseManager.shared.getProductByCode(productsCodes.get(i));

				Double stock = AppDataBaseManager.shared.getProductsStock(productsCodes.get(i), depotCode);

				productsStockData.add(new ProductStock(product, stock));
			}

		} catch (SQLException e) {
			AlertError alert = new AlertError("ERROR ERR0043", "Fatal Error", e.getMessage());
			alert.showAndWait();
		}

	}


	private class ChangeListenerTab implements ChangeListener<Tab>{

		@Override
		public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
			updateTableViewData();
		}

	}

	private class ActionEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (event.getSource() == btnPrint) {
				print();
			}
		}
	}

	private class TableViewMousePressedHandler implements EventHandler<MouseEvent>{

		@Override
		public void handle(MouseEvent event) {

			if (event.isSecondaryButtonDown()  
					&& tableViewProducts.getSelectionModel().getSelectedItem() != null)  {

				Product product = tableViewProducts.getSelectionModel().getSelectedItem();
				RootViewController.selfRef.presentProductDetailsView(product);
			}

		}

	}

	private class TableViewRowFactory implements Callback<TableView<ProductStock>, TableRow<ProductStock>> {

		@Override
		public TableRow<ProductStock> call(TableView<ProductStock> param) {
			return new TableRow<ProductStock>() {
				@Override
				protected void updateItem(ProductStock item, boolean empty) {
					super.updateItem(item, empty);

					if (item == null || mainTabPane.getSelectionModel().getSelectedItem() == tabMissingProducts) {
						setStyle("");
					}else {
						if (item.getQnt() >= 0) {
							setStyle("");
						}else{
							setStyle("-fx-background-color: red;");
						}
					}

				}
			};
		}

	}


	private void print(){
		
		if (productsStockData.size() == 0) {
			return;
		}
		
		String filePath = "TEMPS/INVENTAIRE.pdf";
		
		new File("TEMPS").mkdir();
		
		StringConverterTimestamp stringConverterTimestamp = new StringConverterTimestamp();
		
		try {
			Depot depot = AppDataBaseManager.shared.getDepotByCode(depotCode);
			
			Document doc = new Document();
			PdfWriter.getInstance(doc, new FileOutputStream(filePath));
			doc.open();
			
			Paragraph parDetails = new Paragraph(stringConverterTimestamp.toString(lastRefreched)
					+"\nDépôt : "+depot.getName());
			parDetails.setAlignment(Paragraph.ALIGN_RIGHT);
			doc.add(parDetails);
			
			Paragraph parTitle = new Paragraph("Inventaire",new Font(FontFamily.TIMES_ROMAN, 30));
			parTitle.setAlignment(Paragraph.ALIGN_CENTER);
			doc.add(parTitle);
			
			Paragraph parSubtTitle = new Paragraph(mainTabPane.getSelectionModel().getSelectedItem().getText(),
					new Font(FontFamily.TIMES_ROMAN, 13));
			parSubtTitle.setAlignment(Paragraph.ALIGN_CENTER);
			doc.add(parSubtTitle);
			
			doc.add(new Paragraph("\n\n\n"));
			
			PdfPTable pdfTable = new PdfPTable(3);
			
			pdfTable.setTotalWidth(new float[]{ 100, 250, 70 });
			pdfTable.setLockedWidth(true);
			
			PdfPCell cellTitle1 = new PdfPCell(new Phrase("Code", new Font(FontFamily.TIMES_ROMAN, 13, Font.BOLD)));
			cellTitle1.setMinimumHeight(25);
			cellTitle1.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			cellTitle1.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			
			PdfPCell cellTitle2 = new PdfPCell(new Phrase("Libellé", new Font(FontFamily.TIMES_ROMAN, 13, Font.BOLD)));
			cellTitle2.setMinimumHeight(25);
			cellTitle2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			cellTitle2.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			
			PdfPCell cellTitle3 = new PdfPCell(new Phrase("Stock", new Font(FontFamily.TIMES_ROMAN, 13, Font.BOLD)));
			cellTitle3.setMinimumHeight(25);
			cellTitle3.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			cellTitle3.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			
			
			pdfTable.addCell(cellTitle1);
			pdfTable.addCell(cellTitle2);
			pdfTable.addCell(cellTitle3);
			
			
			for (int i=0; i<productsStockData.size();i++) {
				ProductStock product = productsStockData.get(i);
				
				PdfPCell cell1 = new PdfPCell(new Phrase(" "+product.getCode(), new Font(FontFamily.TIMES_ROMAN, 9)));
				cell1.setMinimumHeight(20);
				
				PdfPCell cell2 = new PdfPCell(new Phrase(" "+product.getName(), new Font(FontFamily.TIMES_ROMAN, 9)));
				cell2.setMinimumHeight(20);
				
				PdfPCell cell3 = new PdfPCell(new Phrase(" "+Double.toString(product.getQnt()), new Font(FontFamily.TIMES_ROMAN, 9)));
				cell3.setMinimumHeight(20);
				
				pdfTable.addCell(cell1);
				pdfTable.addCell(cell2);
				pdfTable.addCell(cell3);
			}
			
			
			doc.add(pdfTable);
			
			doc.close();
			
			if (Desktop.isDesktopSupported()) {
				try {
					File file = new File(filePath);
					Desktop.getDesktop().open(file);
				} catch (IOException e) {
					AlertError alert = new AlertError("ERROR ERR0048", "Fatal Error", e.getMessage());
					alert.showAndWait();
				}
			}
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}

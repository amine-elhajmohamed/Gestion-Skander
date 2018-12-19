package managers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;

import exceptions.DataBaseDriverLoadFailedException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class MainApp extends Application {

	private Stage primaryStage;


	@Override
	public void start(Stage primaryStage) {

		boolean tryAgain = true;

		while (tryAgain) {
			String password = presentLoginDialog();

			if (password == null){
				tryAgain = false;
			}else if (password.equals("HAR602")) {
				
				
				tryAgain = false;
				this.primaryStage = primaryStage;
				primaryStage.setTitle("Gestion Skander");
				loadRootView();
				primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
					@Override
					public void handle(WindowEvent event) {
						try {
							AppDataBaseManager.shared.closeAllConnections();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				
				
			}
		}
	}


	private void loadRootView(){

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/RootView.fxml"));
			Scene scence = new Scene(loader.load(),1300,750);
			primaryStage.setScene(scence);
			primaryStage.setMinWidth(1000);
			primaryStage.setMinHeight(600);
			primaryStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		try {
			AppDataBaseManager.shared.prepare();

			launch(args);
		} catch (DataBaseDriverLoadFailedException e) {
			System.exit(-1);
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			System.exit(-1);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private String presentLoginDialog(){
		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle("");
		dialog.setHeaderText(" ");

		ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		PasswordField txtPassword = new PasswordField();
		txtPassword.setPromptText("Password");

		grid.add(new Label("  Password:"), 0, 0);
		grid.add(txtPassword, 1, 0);

		dialog.getDialogPane().setContent(grid);

		dialog.setResultConverter(new Callback<ButtonType, String>() {
			@Override
			public String call(ButtonType param) {
				if (param == loginButtonType) {
					return (txtPassword.getText());
				}
				return null;
			}
		});

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtPassword.requestFocus();
			}
		});
		
		Optional<String> result = dialog.showAndWait();

		try{
			return result.get();
		}catch(NoSuchElementException e) {
			return null;
		}

	}
}

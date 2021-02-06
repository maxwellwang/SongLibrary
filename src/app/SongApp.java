package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import view.SongController;

public class SongApp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader();   
		loader.setLocation(
				getClass().getResource("/view/library.fxml"));
		AnchorPane root = (AnchorPane)loader.load();

		SongController listController = loader.getController();
		listController.start(primaryStage);

		Scene scene = new Scene(root, 200, 300);
		primaryStage.setScene(scene);
		primaryStage.show(); 

	}

	public static void main(String[] args) {
		launch(args);
	}

}

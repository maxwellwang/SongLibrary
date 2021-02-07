package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import view.SongController;

public class SongApp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/view/library.fxml"));
		SplitPane root = (SplitPane) loader.load();

		SongController songController = loader.getController();
		songController.start(primaryStage);

		Scene scene = new Scene(root, 800, 300);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Song Library");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}

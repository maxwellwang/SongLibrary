package view;

import java.util.ArrayList;
import java.util.Arrays;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SongController {

	@FXML
	TableView<Song> tableView;
	@FXML
	TableColumn<Song, String> nameColumn;
	@FXML
	TableColumn<Song, String> artistColumn;
	@FXML
	Text nameText;
	@FXML
	Text artistText;
	@FXML
	Text albumText;
	@FXML
	Text yearText;
	@FXML
	Button addButton;
	@FXML
	Button editButton;
	@FXML
	Button deleteButton;

	private ArrayList<Song> initialSongs = new ArrayList<>(Arrays.asList(new Song("Soft Jams", "Sesh", "CS213", "2016"),
			new Song("G Tunes", "Girish", "Red Album", "2021"), new Song("Mix City", "Maxwell", "Spark", "2020"),
			new Song("Soft Jams", "Girish", "Red Album", "2021")));

	public void start(Stage mainStage) {
		// set sorting columns to false since we have our own way of sorting (see sort
		// method)
		nameColumn.setSortable(false);
		artistColumn.setSortable(false);

		// describe how to go from Song to String for each column
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name")); // will call getName()
		artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist")); // will call getArtist()

		// populate library with initial songs and sort them
		ObservableList<Song> songs = FXCollections.observableArrayList(initialSongs);
		songs = sort(songs);
		tableView.setItems(songs);

		// select first song
		TableViewSelectionModel<Song> selectionModel = tableView.getSelectionModel();
		if (!initialSongs.isEmpty()) {
			selectionModel.select(0);
		}
		showDetails();

		// set listener for items
		selectionModel.selectedIndexProperty().addListener((obs, oldVal, newVal) -> showDetails());
	}

	// sorts songs in alphabetical order by name and then artist
	private ObservableList<Song> sort(ObservableList<Song> songs) {
		songs.sort((song1, song2) -> song1.getName().compareTo(song2.getName()) != 0
				? song1.getName().compareTo(song2.getName())
				: song1.getArtist().compareTo(song2.getArtist()));
		return songs;
	}

	// shows details of selected song
	private void showDetails() {
		TableViewSelectionModel<Song> selectionModel = tableView.getSelectionModel();
		if (!selectionModel.isEmpty()) {
			Song selectedSong = selectionModel.getSelectedItem();
			nameText.setText(selectedSong.getName());
			artistText.setText(selectedSong.getArtist());
			albumText.setText(selectedSong.getAlbum());
			yearText.setText(selectedSong.getYear());
		}
	}

}

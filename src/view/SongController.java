package view;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.text.Text;
import javafx.stage.Stage;

// Maxwell Wang and Girish Ganesan

public class SongController {

	@FXML
	ListView<String> listView;
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

	// strings in listView
	private ObservableList<String> obsList;

	public void start(Stage mainStage) {
		// add songs from songs.json to obsList
		obsList = FXCollections.observableArrayList();
		for (Song song : getSongs()) {
			obsList.add(songToString(song));
		}
		listView.setItems(obsList);

		// select first song name and artist if list is not empty
		MultipleSelectionModel<String> selectionModel = listView.getSelectionModel();
		if (!obsList.isEmpty()) {
			selectionModel.select(0);
		}
		showDetails();

		// set listener for items
		selectionModel.selectedIndexProperty().addListener((obs, oldVal, newVal) -> showDetails());
	}

	private ArrayList<Song> getSongs() {
		ArrayList<Song> songs = new ArrayList<>();
		JSONParser parser = new JSONParser();
		JSONArray a = null;
		try {
			a = (JSONArray) parser.parse(new FileReader("src/data/songs.json"));
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		for (Object o : a) {
			songs.add(new Song((JSONObject) o));
		}
		return songs;

	}

	private String songToString(Song song) {
		return song.getName() + " | " + song.getArtist();
	}

	private String stringToName(String s) {
		return s.substring(0, s.indexOf('|') - 1);
	}

	private String stringToArtist(String s) {
		return s.substring(s.indexOf('|') + 2);
	}

	private Song stringToSong(String s) {
		for (Song song : getSongs()) {
			if (song.getName().equals(stringToName(s)) && song.getArtist().equals(stringToArtist(s))) {
				return song;
			}
		}
		return null;
	}

	// shows details of selected song
	private void showDetails() {
		MultipleSelectionModel<String> selectionModel = listView.getSelectionModel();
		if (!obsList.isEmpty()) {
			String selectedString = selectionModel.getSelectedItem();
			Song selectedSong = stringToSong(selectedString);
			nameText.setText(selectedSong.getName());
			artistText.setText(selectedSong.getArtist());
			albumText.setText(selectedSong.getAlbum());
			yearText.setText(selectedSong.getYear());
		}
	}

}

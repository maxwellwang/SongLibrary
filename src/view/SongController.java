package view;

import java.io.FileReader;
import java.io.FileWriter;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

// Maxwell Wang and Girish Ganesan

public class SongController implements EventHandler<ActionEvent> {

	@FXML
	ListView<String> listView;
	@FXML
	TextField nameText;
	@FXML
	TextField artistText;
	@FXML
	TextField albumText;
	@FXML
	TextField yearText;
	@FXML
	Button addButton;
	@FXML
	Button editButton;
	@FXML
	Button deleteButton;
	@FXML
	Button cancelButton;

	ArrayList<Song> songs;

	// strings in listView
	private ObservableList<String> obsList;

	public void start(Stage mainStage) {
		// add songs from songs.json to obsList
		obsList = FXCollections.observableArrayList();
		getSongs();
		for (Song song : songs) {
			obsList.add(songToString(song));
		}
		obsList.sort(null);
		listView.setItems(obsList);

		// select first song name and artist if list is not empty
		MultipleSelectionModel<String> selectionModel = listView.getSelectionModel();
		if (!obsList.isEmpty()) {
			selectionModel.select(0);
		}
		showDetails();

		// set listener for items
		selectionModel.selectedIndexProperty().addListener((obs, oldVal, newVal) -> showDetails());

		// set up event handling for add, edit, and delete buttons
		addButton.setOnAction(this);
		editButton.setOnAction(this);
		deleteButton.setOnAction(this);
		cancelButton.setOnAction(this);
	}

	private void getSongs() {
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
		this.songs = songs;
	}

	private String songToString(Song song) {
		return song.getName() + " | " + song.getArtist();
	}

	private String stringToName(String s) {
		return (s != null) ? s.substring(0, s.indexOf('|') - 1) : "";
	}

	private String stringToArtist(String s) {
		return s.substring(s.indexOf('|') + 2);
	}

	private Song stringToSong(String s) {
		for (Song song : songs) {
			if (song.getName().equals(stringToName(s)) && song.getArtist().equals(stringToArtist(s))) {
				return song;
			}
		}
		return null;
	}

	// control editable field of song information TextFields
	private void enableAllTextfields(boolean enable) {
		nameText.setDisable(!enable);
		artistText.setDisable(!enable);
		albumText.setDisable(!enable);
		yearText.setDisable(!enable);
	}

	private boolean minFieldsFilled() {
		if (!nameText.isDisabled()) {
			return (!nameText.getText().isEmpty() && !artistText.getText().isEmpty());
		}
		return false;
	}

	private boolean duplicateCheck(Song newSong, String old) {
		// System.out.println(old + "/" + songToString(newSong));
		if (songToString(newSong).equals(old)) {
			return false;
		}

		boolean duplicate = false;
		for (Song song : songs) {
			if (songToString(song).equals(songToString(newSong))) {
				duplicate = true;
				break;
			}
		}
		if (duplicate) {
			Alert deleteAlert = new Alert(AlertType.ERROR, "Duplicate song-artist pair detected.");
			deleteAlert.showAndWait();
		}
		return duplicate;
	}

	// handles add, edit, and delete button presses
	public void handle(ActionEvent e) {
		Button b = (Button) e.getSource();
		if (b == deleteButton)
			deleteSong();
		else if (b == cancelButton) {
			enableAllTextfields(false);
			showDetails();
		} else {
			if (minFieldsFilled()) {
				if (b == editButton)
					editSong();
				else
					addSong();
			} else {
				if ((b == editButton || b == addButton) && (!nameText.isDisabled())) {
					Alert fieldsNotFilledAlert = new Alert(AlertType.ERROR, "Name and Artist fields must be filled.");
					fieldsNotFilledAlert.showAndWait();
				}
				enableAllTextfields(true);
			}
		}
	}

	// deal with json at the end -- save whole file again

	private void addSong() {
		MultipleSelectionModel<String> selectionModel = listView.getSelectionModel();
		Alert yearNotIntegerAlert = new Alert(AlertType.ERROR, "Year must be a positive integer.");
		Alert illegalBarAlert = new Alert(AlertType.ERROR, "Name, artist, and album cannot have | in them.");
		int year = 0;
		try {
			year = Integer.parseInt(yearText.getText());
		} catch (NumberFormatException e) {
			yearNotIntegerAlert.showAndWait();
		}
		if (year < 1) {
			yearNotIntegerAlert.showAndWait();
		} else if (nameText.getText().contains("|") || artistText.getText().contains("|")
				|| albumText.getText().contains("|")) {
			illegalBarAlert.showAndWait();
		} else {
			Song newSong = new Song(nameText.getText(), artistText.getText(), albumText.getText(), year);
			if (!duplicateCheck(newSong, null)) {
				songs.add(newSong);
				obsList.add(songToString(newSong));
				obsList.sort(null);
				JSONArray a = new JSONArray();
				for (String s : obsList) {
					JSONObject obj = new JSONObject();
					Song song = stringToSong(s);
					obj.put("name", song.getName());
					obj.put("artist", song.getArtist());
					obj.put("album", song.getAlbum());
					obj.put("year", song.getYear());
					a.add(obj);
				}
				try (FileWriter file = new FileWriter("src/data/songs.json")) {
					file.write(a.toJSONString());
					file.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				selectionModel.select(obsList.indexOf(songToString(newSong)));
			}
		}
	}

	private void editSong() {
		MultipleSelectionModel<String> selectionModel = listView.getSelectionModel();
		String removedName = selectionModel.getSelectedItem();
		Alert yearNotIntegerAlert = new Alert(AlertType.ERROR, "Year must be a positive integer.");
		Alert illegalBarAlert = new Alert(AlertType.ERROR, "Name, artist, and album cannot have | in them.");
		int year = 0;
		try {
			year = Integer.parseInt(yearText.getText());
		} catch (NumberFormatException e) {
			yearNotIntegerAlert.showAndWait();
		}
		if (year < 1) {
			yearNotIntegerAlert.showAndWait();
		} else if (nameText.getText().contains("|") || artistText.getText().contains("|")
				|| albumText.getText().contains("|")) {
			illegalBarAlert.showAndWait();
		} else {
			Song newSong = new Song(nameText.getText(), artistText.getText(), albumText.getText(), year);
			if (!duplicateCheck(newSong, removedName)) {
				for (Song song : songs) {
					if (songToString(song).equals(removedName)) {
						songs.remove(song);
						break;
					}
				}
				obsList.remove(removedName);
				songs.add(newSong);
				obsList.add(songToString(newSong));
				obsList.sort(null);
				JSONArray a = new JSONArray();
				for (String s : obsList) {
					JSONObject obj = new JSONObject();
					Song song = stringToSong(s);
					obj.put("name", song.getName());
					obj.put("artist", song.getArtist());
					obj.put("album", song.getAlbum());
					obj.put("year", song.getYear());
					a.add(obj);
				}
				try (FileWriter file = new FileWriter("src/data/songs.json")) {
					file.write(a.toJSONString());
					file.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				selectionModel.select(obsList.indexOf(songToString(newSong)));
			}
		}
	}

	private void deleteSong() {
		MultipleSelectionModel<String> selectionModel = listView.getSelectionModel();
		String selectedString = selectionModel.getSelectedItem();
		int selectedIndex = selectionModel.getSelectedIndex();

		Alert deleteAlert = new Alert(AlertType.CONFIRMATION,
				"Are you sure you want to delete \"" + selectedString + "\"?", ButtonType.YES, ButtonType.NO);
		deleteAlert.showAndWait();
		if (deleteAlert.getResult() == ButtonType.YES) {
			obsList.remove(selectedIndex);
			JSONParser parser = new JSONParser();
			JSONArray a = null;
			try {
				a = (JSONArray) parser.parse(new FileReader("src/data/songs.json"));
				a.remove(selectedIndex);
				try (FileWriter file = new FileWriter("src/data/songs.json")) {
					file.write(a.toJSONString());
					file.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (IOException | ParseException e) {
				e.printStackTrace();
			}
			selectionModel.select((selectedIndex < obsList.size()) ? selectedIndex : obsList.size() - 1);
		}
	}

	// shows details of selected song
	private void showDetails() {
		MultipleSelectionModel<String> selectionModel = listView.getSelectionModel();
		if (!obsList.isEmpty()) {
			String selectedString = selectionModel.getSelectedItem();
			Song selectedSong = stringToSong(selectedString);
			if (selectedSong != null) {
				enableAllTextfields(true);
				nameText.setText(selectedSong.getName());
				artistText.setText(selectedSong.getArtist());
				albumText.setText(selectedSong.getAlbum());
				yearText.setText("" + selectedSong.getYear());
				enableAllTextfields(false);
			}
		} else {
			enableAllTextfields(true);
			nameText.setText("");
			artistText.setText("");
			albumText.setText("");
			yearText.setText("");
			enableAllTextfields(false);
		}
	}

}

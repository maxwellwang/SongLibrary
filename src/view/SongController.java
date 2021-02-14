package view;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

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
	
	MultipleSelectionModel<String> selectionModel;
	ArrayList<Song> songs; 
	
	/** 
	 * 
	 * TODO: We have all songs created/edited over the course of the program run saved in ArrayList<Song> songs.
	 * If we can write an ArrayList --> JSON, we'll be done with the memory permanence part of the assignment.
	 * 
	 * */
	
	// strings in listView
	private ObservableList<String> obsList;

	public void start(Stage mainStage) {
		// add songs from songs.json to obsList
		obsList = FXCollections.observableArrayList();
		getSongs();
		for (Song song : songs) {
			obsList.add(songToString(song));
		}
		sortObsList();
		
		listView.setItems(obsList);

		// select first song name and artist if list is not empty
		selectionModel = listView.getSelectionModel();
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
	
	// called once, ArrayList of active Songs is stored and updated
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

	private void sortObsList() {
		obsList.sort(new Comparator<String>() {
			public int compare(String a, String b) {
				return a.compareToIgnoreCase(b);
			}
		});
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
	
	// control "disabled" field of song information TextFields
	private void enableAllTextfields(boolean enable) {
		nameText.setDisable(!enable);
		artistText.setDisable(!enable);
		albumText.setDisable(!enable);
		yearText.setDisable(!enable);
	}
	
	// checks if add/edit button press is the second press (i.e. after the required information has been filled in)
	private boolean minFieldsFilled() {
		if (!nameText.isDisabled()) {
			return (!nameText.getText().isEmpty() && !artistText.getText().isEmpty());
		}
		return false;
	}
	
	// checks for a conflict when adding/editing a song. requires a unique name-artist pair for each song
	private boolean duplicateCheck(Song newSong, String old) {
		// two songs with same name-artist except spaces
		if (songToString(newSong).equalsIgnoreCase(old)) { return false; }

		boolean duplicate = false;
		for (Song song : songs) {
			if (songToString(song).equalsIgnoreCase(songToString(newSong))) { duplicate = true; break; }
		}
		if (duplicate) {
			Alert deleteAlert = new Alert(AlertType.ERROR, "Duplicate song-artist pair detected.");
			deleteAlert.showAndWait();
		}
		return duplicate;
	}
	
	// error checking for a positive integer as "year" field
	private int positiveYearInput() {
		Alert yearNotIntegerAlert = new Alert(AlertType.ERROR, 
				"Year must be a positive integer.");
		int year = 0;
		try { year = Integer.parseInt(yearText.getText().strip()); } catch (NumberFormatException e) {}
		if (year < 1) 
			yearNotIntegerAlert.showAndWait();
		
		return year;
	}
	
	// handles add, edit, and delete button presses
	public void handle(ActionEvent e) {
		Button b = (Button)e.getSource();
		if (b == deleteButton) {
			deleteSong();
		} else if (b == cancelButton) {
			enableAllTextfields(false);
			showDetails();
		} else {
			// deals with add and edit button presses
			if (minFieldsFilled()) { 
				// occurs on second press, when fields are filled
				if (b == editButton) editSong();
				else addSong();
			} else {
				// occurs on first press, to enable fields for data entry
				addButton.setDisable(b != addButton);
				editButton.setDisable(b != editButton);
				deleteButton.setDisable(true);
				
				if ((b == editButton || b == addButton) && (!nameText.isDisabled())) {
					Alert fieldsNotFilledAlert = new Alert(AlertType.ERROR, 
							"Name and Artist fields must be filled.");
					fieldsNotFilledAlert.showAndWait();
				}
				enableAllTextfields(true);
			}
		}
	}
	
	// creates a new Song, and if there are no conflicts it is added to the library
	private void addSong() {
		int year = positiveYearInput();
		if (year > 0) {
			Song newSong = new Song(nameText.getText().strip(), artistText.getText().strip(), albumText.getText().strip(), year);
			if (!duplicateCheck(newSong, null)) {
				songs.add(newSong);
				obsList.add(songToString(newSong));
				sortObsList();
			
				selectionModel.select(obsList.indexOf(songToString(newSong)));
			}
		}
		
		editButton.setDisable(false);
		deleteButton.setDisable(false);
	}
	
	// edits fields of an old Song, given no conflicts with another Song
	private void editSong() {
		String removedName = selectionModel.getSelectedItem();
		int year = positiveYearInput();
		if (year > 0){	
			Song newSong = new Song(nameText.getText().strip(), artistText.getText().strip(), albumText.getText().strip(), year);
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
				sortObsList();
				
				selectionModel.select(obsList.indexOf(songToString(newSong)));
			}
		}
		
		addButton.setDisable(false);
		deleteButton.setDisable(false);
	}
	
	// deletes a song after showing a confirmation popup
	private void deleteSong() {
		String selectedString = selectionModel.getSelectedItem();
		int selectedIndex = selectionModel.getSelectedIndex();
		
		Alert deleteAlert = new Alert(AlertType.CONFIRMATION, 
				"Are you sure you want to delete \"" + selectedString + "\"?", ButtonType.YES, ButtonType.NO);
		deleteAlert.showAndWait();
		if (deleteAlert.getResult() == ButtonType.YES) {
			obsList.remove(selectedIndex);
			selectionModel.select((selectedIndex < obsList.size()) ? selectedIndex : obsList.size() - 1);
		}
	}
	
	// shows details of selected song in TextFields
	private void showDetails() {
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

package view;

public class Song {
	private String name;
	private String artist;
	private String album;
	private String year;

	public Song(String name, String artist, String album, String year) {
		this.setName(name);
		this.setArtist(artist);
		this.setAlbum(album);
		this.setYear(year);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}
}

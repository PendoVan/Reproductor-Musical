package reproductor.com.musica.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Playlist {
	
	private String name;
	private final ObservableList<Song> songs = FXCollections.observableArrayList();
	
	public Playlist() {
	}
	
	public Playlist(String name) {
		this.name = name;
	}
	
	public Playlist(String name, Iterable<Song> initialSongs) {
		this.name = name;
		for (Song song : initialSongs) {
			songs.add(song);
		}
	}
	
	public String getName() {
		return name != null ? name : "";
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public ObservableList<Song> getSongs() {
		return songs;
	}
	
	public void addSong(Song song) {
		if (song != null && !songs.contains(song)) {
			songs.add(song);
		}
	}
	
	public void removeSong(Song song) {
		songs.remove(song);
	}
	
	public void clear() {
		songs.clear();
	}
	
	@Override
	public String toString() {
		return getName();
	}
}

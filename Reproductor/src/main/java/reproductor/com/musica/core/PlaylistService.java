package reproductor.com.musica.core;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.nio.file.Path;

public class PlaylistService {
	private final ObservableList<Path> items = FXCollections.observableArrayList();
	private int index = -1;
	
	public ObservableList<Path> getItems() { return items; }
	
	public void add(Path path) { items.add(path); if (index < 0) index = 0; }
	public void remove(Path path) {
		int i = items.indexOf(path);
		items.remove(path);
		if (i >= 0 && i <= index) index = Math.max(0,  index - 1);
		if (items.isEmpty()) index = -1;
	}
	
	public Path current() { return (index >= 0 && index < items.size()) ? items.get(index) : null; }
	public Path next() { if (items.isEmpty()) return null; index = Math.min(items.size() -1, index + 1); return current(); }
	public Path prev() { if (items.isEmpty()) return null; index = Math.max(0, index-1); return current(); }
}

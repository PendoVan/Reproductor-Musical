package reproductor.com.musica.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import reproductor.com.musica.model.Song;

public class SearchController {
	
	@FXML
	private TextField txtSearch;
	
	@FXML
	private ListView<Song> listResults;
	
	@FXML
	private void onSearchClicked() {
		// TODO: llamar a SearchService (Integrante 2)
	}
	
	@FXML
	private void onAddToPlaylistClicked() {
		// TODO: usar PlaylistService (Integrante 3)
	}
}

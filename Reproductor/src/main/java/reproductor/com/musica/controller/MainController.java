package reproductor.com.musica.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class MainController {
	
	@FXML
	private BorderPane root;
	
	// TODO: métodos para cambiar el contenido central
	
	@FXML
	private void openSearchView() {
	    try {
	        FXMLLoader loader = new FXMLLoader(
	            getClass().getResource("/com/musica/view/SearchView.fxml")
	        );
	        Parent root = loader.load();
	        
	        Stage stage = new Stage();
	        stage.setTitle("Buscar Música");
	        stage.setScene(new Scene(root, 800, 600));
	        stage.show();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}

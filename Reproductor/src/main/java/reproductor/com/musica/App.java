package reproductor.com.musica;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
	  @Override
	  public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/reproductor/com/musica/view/MainView.fxml"));
	    var scene = new Scene(root, 900, 560);
	    stage.setTitle("Reproductor - Grupo 3");
	    stage.setScene(scene);
	    stage.show();
	  }

	  public static void main(String[] args) {
	    launch(args);
	  }
	}

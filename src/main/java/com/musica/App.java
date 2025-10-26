package com.musica;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader fx = new FXMLLoader(getClass().getResource("/com/musica/view/MainView.fxml"));
		Scene scene = new Scene(fx.load(), 800, 600);
		stage.setTitle("Reproductor Musical");
		stage.setScene(scene);
		stage.show();
	}
	public static void main(String[] args) {
		launch(args);
	}
}

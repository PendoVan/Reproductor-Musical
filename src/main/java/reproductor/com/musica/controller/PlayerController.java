package reproductor.com.musica.controller;

import reproductor.com.musica.core.PlayerService;
import reproductor.com.musica.core.PlaylistService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.prefs.Preferences;

public class PlayerController {
	
	@FXML private Slider progressSlider;
	@FXML private Slider volumeSlider;
	@FXML private CheckBox muteCheck;
	@FXML private Label trackLabel;
	@FXML private Label currentTime;
	@FXML private Label totalTime;
	@FXML private ListView<Path> playlistView;
	@FXML private javafx.scene.layout.BorderPane root;
	
	private final PlaylistService playlist = new PlaylistService();
	private final PlayerService player = new PlayerService();
	
	private final Preferences prefs = Preferences.userNodeForPackage(PlayerController.class);
	
	@FXML
	public void initialize() {
		updateControlsEnabled(false);
		double vol = prefs.getDouble("volume", 0.7);
		
		// Bind playlist
		playlistView.setItems(playlist.getItems());
		
		// Volumen
		volumeSlider.setMin(0);
		volumeSlider.setMax(1);
		volumeSlider.setValue(vol);
		volumeSlider.valueProperty().addListener((o, ov, nv) -> {
			  player.setVolume(nv.doubleValue());
			  prefs.putDouble("volume", nv.doubleValue());
		});
		
		// Progreso
	    progressSlider.setMin(0);
	    progressSlider.setMax(1);
	    progressSlider.valueChangingProperty().addListener((o, wasChanging, changing) -> {
	      if (!changing) player.seekByRatio(progressSlider.getValue());
	    });
	    
	    // Actualizaciones del reproductor
	    player.onUpdate(u -> Platform.runLater(() -> {
	        currentTime.setText(format(u.current()));
	        totalTime.setText(format(u.total()));
	        if (!progressSlider.isValueChanging()) progressSlider.setValue(u.ratio());
	        trackLabel.setText(u.title() == null ? "(sin archivo)" : u.title());
	      }));
	    
	    // Al seleccionar el la lista, reproducir
	    playlistView.getSelectionModel().selectedItemProperty().addListener((o, old, sel) -> {
	    	if (sel != null) play(sel);
	    });
	    
	    Platform.runLater(() -> {
	    	root.getScene().setOnKeyPressed(e -> {
	    		switch (e.getCode()) {
	    			case SPACE 	-> { if (player.isPlaying()) player.pause(); else player.play(); }
	    			case UP		-> volumeSlider.setValue(Math.min(1.0, volumeSlider.getValue() + 0.05));
	    			case DOWN	-> volumeSlider.setValue(Math.max(0.0, volumeSlider.getValue() - 0.05));
	    			case RIGHT	-> player.seekByRatio(Math.min(1.0, progressSlider.getValue() + 0.05));
	    			case LEFT	-> player.seekByRatio(Math.max(0.0, progressSlider.getValue() - 0.05));
	    			default -> {}
	    		}
	    	});
	    });
	    
	    if (playlist.current() == null) updateControlsEnabled(false);
	    player.onError(msg -> Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, msg).showAndWait()));
	}
	
	@FXML public void onOpenFile() {
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio", List.of("*.mp3","*.wav","*.m4a")));
		List<File> files = fc.showOpenMultipleDialog(null);
		if (files != null && !files.isEmpty()) {
			files.forEach(f -> playlist.add(f.toPath()));
			if (playlistView.getSelectionModel().getSelectedIndex() < 0) {
				playlistView.getSelectionModel().select(0);
			}
		}
	}
	
	@FXML public void onPlay() { player.play(); }
	@FXML public void onPause() { player.pause(); }
	@FXML public void onStop() { player.stop(); }
	@FXML public void onPrev() { play(playlist.prev()); }
	@FXML public void onNext() { play(playlist.next()); }
	@FXML public void onToggleMute() { player.setMute(muteCheck.isSelected()); }
	
	private void play(Path path) {
		if (path == null) return;
		try {
			player.open(path);
			player.setVolume(volumeSlider.getValue());
			updateControlsEnabled(true);
			player.play();
		} catch (IllegalArgumentException ex) {
			new Alert(Alert.AlertType.ERROR, "No se pudo abrir: " + path.getFileName()).showAndWait();
		}
	}
	
	private static String format(Duration d) {
		if (d == null) return "00:00";
		long s = d.getSeconds();
		long m = s / 60; s %= 60;
		return String.format("%02d:%02d", m, s);
	}
	
	@FXML private Button btnPrev, btnPlay, btnPause, btnStop, btnNext;
	@FXML Button btnOpen;
	
	private void updateControlsEnabled(boolean enabled) {
		btnPrev.setDisable(!enabled);
		btnPlay.setDisable(!enabled);
		btnPause.setDisable(!enabled);
		btnStop.setDisable(!enabled);
		btnNext.setDisable(!enabled);
		progressSlider.setDisable(!enabled);
		volumeSlider.setDisable(!enabled);
		muteCheck.setDisable(!enabled);
	}
	
}

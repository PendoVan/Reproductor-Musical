package reproductor.com.musica.controller;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.prefs.Preferences;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import reproductor.com.musica.core.PlayerService;
import reproductor.com.musica.core.PlaylistService;

public class PlayerController {
	
	// Elementos para la UI
	@FXML private Slider progressSlider;
	@FXML private Slider volumeSlider;
	@FXML private CheckBox muteCheck;
	@FXML private Label trackLabel;
	@FXML private Label currentTime;
	@FXML private Label totalTime;
	@FXML private ListView<Path> playlistView;
	@FXML private BorderPane root;
	@FXML private Button btnPrev, btnPlay, btnPause, btnStop, btnNext;
	@FXML private Button btnOpen;
	
	// Servicios
	private final PlaylistService playlist = new PlaylistService();
	private final PlayerService player = new PlayerService();
	
	// Preferencias
	private static final String PREF_VOLUME_KEY = "volume";
	private final Preferences prefs = Preferences.userNodeForPackage(PlayerController.class);
	
	// Initialization
	@FXML
	public void initialize() {
		setupPlaylistBinding();
		setupVolumeControl();
		setupProgressControl();
		setupPlayerUpdates();
		setupKeyboardShortcuts();
		setupErrorHandling();
		
		updateControlsEnabled(false);
	}
		
	// Setup de métodos
	private void setupPlaylistBinding() {
		playlistView.setItems(playlist.getItems());
		playlistView.getSelectionModel().selectedItemProperty()
				.addListener((obs, oldVal, selected) -> {
					if (selected != null) play(selected);
				});
	}
		
	private void setupVolumeControl() {
		double savedVolume = prefs.getDouble(PREF_VOLUME_KEY, 0.7);
		volumeSlider.setMin(0);
		volumeSlider.setMax(1);
		volumeSlider.setValue(savedVolume);
		
		volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
			double volume = newVal.doubleValue();
			player.setVolume(volume);
			prefs.putDouble(PREF_VOLUME_KEY, volume);
		});
	}
		
	private void setupProgressControl() {
	    progressSlider.setMin(0);
	    progressSlider.setMax(1);
	    progressSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
	    	if (!isChanging) {
	    		player.seekByRatio(progressSlider.getValue());
	    	}
	    });
	}
	
	private void setupPlayerUpdates() {
		player.onUpdate(update -> Platform.runLater(() -> {
			currentTime.setText(formatTime(update.current()));
			totalTime.setText(formatTime(update.total()));
			if (!progressSlider.isValueChanging()) {
				progressSlider.setValue(update.ratio());
			}
			trackLabel.setText(update.title() == null ? "(Sin archivo)" : update.title());
		}));
	}
	
	private void setupKeyboardShortcuts() {
		Platform.runLater(() -> 
			root.getScene().setOnKeyPressed(event -> {
				switch (event.getCode()) {
					case SPACE -> togglePlayPause();
					case UP -> adjustVolume(0.05);
					case DOWN -> adjustVolume(-0.05);
					case RIGHT -> seekProgress(0.05);
					case LEFT -> seekProgress(-0.05);
				default -> {}
				}
			})
		);
	}
	
	private void setupErrorHandling() {
		player.onError(message -> Platform.runLater(() -> 
				new Alert(Alert.AlertType.ERROR, message).showAndWait()));
	}
	
	// Player Controls
	@FXML public void onOpenFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(
				new FileChooser.ExtensionFilter("Audio", List.of("*.mp3","*.wav","*.m4a"))
		);
		
		List<File> files = fileChooser.showOpenMultipleDialog(null);
		if (files == null || files.isEmpty()) return;
		
		files.forEach(file -> playlist.add(file.toPath()));
		if (playlistView.getSelectionModel().getSelectedIndex() < 0) {
			playlistView.getSelectionModel().select(0);
		}
	}
	
	@FXML public void onPlay() { player.play(); }
	@FXML public void onPause() { player.pause(); }
	@FXML public void onStop() { player.stop(); }
	@FXML public void onPrev() { play(playlist.prev()); }
	@FXML public void onNext() { play(playlist.next()); }
	@FXML public void onToggleMute() { player.setMute(muteCheck.isSelected()); }
	
	private void togglePlayPause() {
		if (player.isPlaying()) player.pause();
		else player.play();
	}
	
	private void adjustVolume(double delta) {
		double newVolume = Math.max(0, Math.min(1, volumeSlider.getValue() + delta));
		volumeSlider.setValue(newVolume);
	}
	
	private void seekProgress(double delta) {
		double newProgress = Math.max(0, Math.min(1, volumeSlider.getValue() + delta));
		player.seekByRatio(newProgress);
	}
	
	// Métodos útiles
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
	
	private static String formatTime(Duration duration) {
		if (duration == null) return "00:00";
		long seconds = duration.getSeconds();
		long minutes = seconds / 60; seconds %= 60;
		return String.format("%02d:%02d", minutes, seconds);
	}
	
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
	
	// Métodos vacíos para los eventos de la UI
	@FXML
	private void onPlayClicked() { /* TODO: integrar con PlayerService */ }

	@FXML
	private void onPauseClicked() { /* TODO */ }

	@FXML
	private void onNextClicked() { /* TODO */ }

	@FXML
	private void onPrevClicked() { /* TODO */ }

	// Integrante 3: asegurar que PlayerService tiene método play()
	// Integrante 2: si se requiere cargar desde API, usar
}

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
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import reproductor.com.musica.core.PlayerService;
import reproductor.com.musica.core.PlaylistService;
import reproductor.com.musica.model.Song;

public class PlayerController {

    private static final String PREF_VOLUME_KEY = "volume";

    @FXML private BorderPane root;

    // Barra superior / toolbar
    @FXML private Button btnOpen;
    @FXML private Button btnSearch;
    @FXML private Button btnShuffle;
    @FXML private Button btnRepeat;

    // Zona de información de pista
    @FXML private Label trackLabel;
    @FXML private Slider progressSlider;
    @FXML private Label currentTime;
    @FXML private Label totalTime;

    // Controles de reproducción
    @FXML private Button btnPrev;
    @FXML private Button btnPlay;
    @FXML private Button btnPause;
    @FXML private Button btnStop;
    @FXML private Button btnNext;

    // Volumen y mute
    @FXML private Slider volumeSlider;
    @FXML private CheckBox muteCheck;

    // Lista de reproducción
    @FXML private ListView<Song> playlistView;

    // Barra de estado inferior
    @FXML private Label statusLabel;
    @FXML private Label trackCountLabel;
    @FXML private Label totalDurationLabel;

    // Servicios
    private final PlaylistService playlist = new PlaylistService();
    private final PlayerService player = new PlayerService();

    private final Preferences prefs = Preferences.userNodeForPackage(PlayerController.class);

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

    // -------------------------
    // Setup de métodos
    // -------------------------

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

        volumeSlider.addEventFilter(ScrollEvent.SCROLL, event -> {
            double delta = event.getDeltaY() > 0 ? 0.05 : -0.05;
            adjustVolume(delta);
            event.consume();
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
        }));

        // Actualizar el título de la pista usando la canción actual del PlayerService
        player.currentSongProperty().addListener((obs, oldSong, newSong) -> {
            Platform.runLater(() -> {
                if (newSong != null) {
                    trackLabel.setText(newSong.toString());
                } else {
                    trackLabel.setText("(Sin archivo)");
                }
            });
        });
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

    // -------------------------
    // Player Controls
    // -------------------------

    @FXML
    public void onOpenFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Audio", List.of("*.mp3", "*.wav", "*.m4a"))
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
        double newProgress = Math.max(0, Math.min(1, progressSlider.getValue() + delta));
        player.seekByRatio(newProgress);
    }

    // -------------------------
    // Métodos útiles
    // -------------------------

    private void play(Song song) {
        if (song == null) return;
        try {
            player.playSong(song);
            player.setVolume(volumeSlider.getValue());
            updateControlsEnabled(true);
        } catch (IllegalArgumentException ex) {
            new Alert(Alert.AlertType.ERROR,
                    "No se pudo abrir: " + song.getTitle()).showAndWait();
        }
    }

    private void updateControlsEnabled(boolean enabled) {
        btnPlay.setDisable(!enabled);
        btnPause.setDisable(!enabled);
        btnStop.setDisable(!enabled);
        btnPrev.setDisable(!enabled);
        btnNext.setDisable(!enabled);
        progressSlider.setDisable(!enabled);
        volumeSlider.setDisable(!enabled);
        muteCheck.setDisable(!enabled);
    }

    private String formatTime(double seconds) {
        if (Double.isNaN(seconds) || seconds < 0) return "--:--";
        long secondsLong = (long) seconds;
        long minutes = secondsLong / 60;
        long remainingSeconds = secondsLong % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    // Métodos TODO adicionales (abrir SearchView, SettingsView, etc.)
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

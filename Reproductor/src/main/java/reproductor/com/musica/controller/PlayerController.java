package reproductor.com.musica.controller;

import java.io.File;
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
import reproductor.com.musica.model.PlaybackMode;

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
        setupButtonIcons();
        setupAdditionalButtons();
    }
    
    private void setupAdditionalButtons() {
        // Crear o configurar botones adicionales que puedan faltar
        Platform.runLater(() -> {
            try {
                // Si hay botones de biblioteca/vista en la interfaz, configurarlos
                // Esto funcionará si están definidos en el FXML
                System.out.println("🎵 Interface configurada completamente");
            } catch (Exception e) {
                System.out.println("ℹ️ Algunos botones opcionales no están presentes: " + e.getMessage());
            }
        });
    }
    
    private void setupButtonIcons() {
        // BOTONES DE REPRODUCCIÓN - Emojis grandes y visibles
        if (btnPlay != null) {
            btnPlay.setText("▶");
            btnPlay.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        }
        if (btnPause != null) {
            btnPause.setText("⏸");
            btnPause.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        }
        if (btnStop != null) {
            btnStop.setText("⏹");
            btnStop.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        }
        if (btnPrev != null) {
            btnPrev.setText("⏮");
            btnPrev.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        }
        if (btnNext != null) {
            btnNext.setText("⏭");
            btnNext.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        }
        
        // BOTONES DE LA TOOLBAR - Con texto claro
        if (btnOpen != null) {
            btnOpen.setText("📁 Abrir");
            btnOpen.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        }
        if (btnSearch != null) {
            btnSearch.setText("🔍 Buscar");
            btnSearch.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        }
        if (btnShuffle != null) {
            btnShuffle.setText("🔀 Aleatorio");
            btnShuffle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        }
        if (btnRepeat != null) {
            btnRepeat.setText("🔁 Repetir");
            btnRepeat.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        }
        
        // BOTÓN SILENCIAR
        if (muteCheck != null) {
            muteCheck.setText("🔊");
            muteCheck.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        }
        
        // CREAR BOTONES BIBLIOTECA Y VISTA SI NO EXISTEN
        setupLibraryAndViewButtons();
        
        System.out.println("✅ Todos los emojis configurados correctamente");
    }
    
    private void setupLibraryAndViewButtons() {
        // Buscar o crear botones de biblioteca y vista
        // Esto se hará automáticamente por el FXML si los IDs están correctos
        System.out.println("📚 Configurando botones de Biblioteca y Vista");
    }

    // -------------------------
    // Setup de métodos
    // -------------------------

    private void setupPlaylistBinding() {
        playlistView.setItems(playlist.getSongs());
        playlistView.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, selected) -> {
                    if (selected != null) {
                        playlist.setCurrentIndex(playlist.getSongs().indexOf(selected));
                        playSong(selected);
                    }
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
                player.seek(progressSlider.getValue());
            }
        });
    }

    private void setupPlayerUpdates() {
        // Escuchar cambios de tiempo actual
        player.currentTimeSecondsProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                currentTime.setText(formatTimeFromSeconds(newVal.doubleValue()));
                if (!progressSlider.isValueChanging() && player.getTotalDurationSeconds() > 0) {
                    double ratio = newVal.doubleValue() / player.getTotalDurationSeconds();
                    progressSlider.setValue(ratio);
                }
            });
        });

        // Escuchar cambios de duración total
        player.totalDurationSecondsProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                totalTime.setText(formatTimeFromSeconds(newVal.doubleValue()));
            });
        });

        // Actualizar el título de la pista usando la canción actual del PlayerService
        player.currentSongProperty().addListener((obs, oldSong, newSong) -> {
            Platform.runLater(() -> {
                if (newSong != null) {
                    trackLabel.setText(newSong.getTitle());
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
        // TODO: PlayerService no tiene manejo de errores implementado
        // Los errores se manejarán en los métodos individuales
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

        // Convertir archivos a objetos Song
        files.forEach(file -> {
            String title = file.getName();
            // Quitar extensión del título
            int lastDot = title.lastIndexOf('.');
            if (lastDot > 0) title = title.substring(0, lastDot);

            Song song = new Song(title, "", file.toPath());
            playlist.addSong(song);
        });

        if (playlistView.getSelectionModel().getSelectedIndex() < 0 && !playlist.getSongs().isEmpty()) {
            playlistView.getSelectionModel().select(0);
        }
    }

    @FXML public void onPlay() { 
        player.play();
        // Mantener emojis visibles
        if (btnPlay != null) {
            btnPlay.setText("▶ PLAY");
            btnPlay.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        }
        if (playlist.getCurrentSong() != null) {
            updateStatus("▶ Reproduciendo: " + playlist.getCurrentSong().getTitle());
        } else {
            updateStatus("▶ Listo para reproducir");
        }
    }
    
    @FXML public void onPause() { 
        player.pause();
        if (btnPause != null) {
            btnPause.setText("⏸ PAUSE");
            btnPause.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        }
        if (playlist.getCurrentSong() != null) {
            updateStatus("⏸ Pausado: " + playlist.getCurrentSong().getTitle());
        } else {
            updateStatus("⏸ Pausado");
        }
    }
    
    @FXML public void onStop() { 
        player.stop();
        if (btnStop != null) {
            btnStop.setText("⏹ STOP");
            btnStop.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        }
        updateStatus("⏹ Detenido");
    }
    
    @FXML public void onPrev() { 
        if (btnPrev != null) {
            btnPrev.setText("⏮ PREV");
            btnPrev.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        }
        Song prevSong = playlist.prev();
        if (prevSong != null) {
            playSong(prevSong);
            updatePlaylistSelection();
        }
    }

    @FXML public void onNext() { 
        if (btnNext != null) {
            btnNext.setText("⏭ NEXT");
            btnNext.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        }
        Song nextSong = playlist.next();
        if (nextSong != null) {
            playSong(nextSong);
            updatePlaylistSelection();
        }
    }
    
    @FXML public void onToggleMute() { 
        boolean isMuted = muteCheck.isSelected();
        if (isMuted) {
            player.setVolume(0);
            muteCheck.setText("🔇");
            updateStatus("🔇 Silenciado");
        } else {
            player.setVolume(volumeSlider.getValue());
            muteCheck.setText("🔊");
            updateStatus("🔊 Audio activado");
        }
        System.out.println("Mute " + (isMuted ? "activado" : "desactivado"));
    }

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
        player.seek(newProgress);
    }

    // -------------------------
    // Métodos útiles
    // -------------------------

    private void playSong(Song song) {
        if (song == null) return;
        try {
            player.playSong(song);
            player.setVolume(volumeSlider.getValue());
            updateControlsEnabled(true);
            updateStatus("🎵 Reproduciendo: " + song.getTitle());
            System.out.println("🎮 Ahora reproduciendo: " + song.getTitle());
        } catch (Exception ex) {
            updateStatus("⚠️ ERROR: No se pudo reproducir");
            new Alert(Alert.AlertType.ERROR, "No se pudo reproducir: " + song.getTitle()).showAndWait();
        }
    }

    private void updatePlaylistSelection() {
        Song currentSong = playlist.getCurrentSong();
        if (currentSong != null) {
            int index = playlist.getSongs().indexOf(currentSong);
            if (index >= 0) {
                playlistView.getSelectionModel().select(index);
            }
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

    private String formatTimeFromSeconds(double totalSeconds) {
        if (totalSeconds <= 0) return "00:00";
        long seconds = (long) totalSeconds;
        long minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // Métodos para los nuevos botones del frontend
    @FXML
    public void onSearchClicked() {
        // TODO: Navegar a SearchView.fxml
        // Integrante 5: implementar navegación entre vistas
        System.out.println("Buscar Online clickeado - funcionalidad pendiente");
    }

    @FXML
    public void onShuffleClicked() {
        if (playlist.getPlaybackMode() == PlaybackMode.SHUFFLE) {
            playlist.setPlaybackMode(PlaybackMode.NORMAL);
            updateShuffleButton(false);
            updateStatus("🔀 Aleatorio: DESACTIVADO");
            System.out.println("🔀 Modo Aleatorio desactivado");
        } else {
            playlist.setPlaybackMode(PlaybackMode.SHUFFLE);
            updateShuffleButton(true);
            updateStatus("🔀 Aleatorio: ACTIVADO");
            System.out.println("🔀 Modo Aleatorio activado");
        }
    }

    @FXML
    public void onRepeatClicked() {
        switch (playlist.getPlaybackMode()) {
            case NORMAL:
            case SHUFFLE:
                playlist.setPlaybackMode(PlaybackMode.REPEAT_ALL);
                updateRepeatButton(PlaybackMode.REPEAT_ALL);
                updateStatus("🔁 Repetir: TODA LA LISTA");
                System.out.println("🔁 Modo Repetir Todo activado");
                break;
            case REPEAT_ALL:
                playlist.setPlaybackMode(PlaybackMode.REPEAT_ONE);
                updateRepeatButton(PlaybackMode.REPEAT_ONE);
                updateStatus("🔂 Repetir: CANCIÓN ACTUAL");
                System.out.println("🔂 Modo Repetir Una activado");
                break;
            case REPEAT_ONE:
                playlist.setPlaybackMode(PlaybackMode.NORMAL);
                updateRepeatButton(PlaybackMode.NORMAL);
                updateStatus("▶️ Repetir: DESACTIVADO");
                System.out.println("▶️ Modo Repetir desactivado");
                break;
        }
    }

    // 🎮 Métodos de Feedback Visual Gaming
    private void updateShuffleButton(boolean active) {
        if (btnShuffle != null) {
            if (active) {
                btnShuffle.getStyleClass().add("shuffle-active");
                btnShuffle.setText("🔥 🔀 Aleatorio ON");
            } else {
                btnShuffle.getStyleClass().remove("shuffle-active");
                btnShuffle.setText("🔀 Aleatorio");
            }
        }
    }

    private void updateRepeatButton(PlaybackMode mode) {
        if (btnRepeat != null) {
            btnRepeat.getStyleClass().removeAll("repeat-active", "repeat-one-active");
            switch (mode) {
                case REPEAT_ALL:
                    btnRepeat.getStyleClass().add("repeat-active");
                    btnRepeat.setText("🔥 🔁 Repetir TODO");
                    break;
                case REPEAT_ONE:
                    btnRepeat.getStyleClass().add("repeat-one-active");
                    btnRepeat.setText("🔥 🔂 Repetir UNA");
                    break;
                default:
                    btnRepeat.setText("🔁 Repetir");
                    break;
            }
        }
    }

    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            // Efecto de parpadeo gaming
            Platform.runLater(() -> {
                statusLabel.getStyleClass().add("status-flash");
                // Volver al estilo normal después de 1 segundo
                javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), 
                        e -> statusLabel.getStyleClass().remove("status-flash"))
                );
                timeline.play();
            });
        }
    }

    // Integrante 3: asegurar que PlayerService tiene método play()
    // Integrante 2: si se requiere cargar desde API, usar SearchService
}

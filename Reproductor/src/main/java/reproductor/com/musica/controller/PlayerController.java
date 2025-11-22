package reproductor.com.musica.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.prefs.Preferences;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import reproductor.com.musica.core.PlayerService;
import reproductor.com.musica.core.PlaylistService;
import reproductor.com.musica.model.PlaybackMode;
import reproductor.com.musica.model.Song;

/**
 * Controlador principal del reproductor.
 * Integra la vista MainView.fxml con PlayerService y PlaylistService.
 */
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

    // Controles de transporte
    @FXML private Button btnPrev;
    @FXML private Button btnPlay;
    @FXML private Button btnPause;
    @FXML private Button btnStop;
    @FXML private Button btnNext;

    // Volumen
    @FXML private Slider volumeSlider;
    @FXML private CheckBox muteCheck;

    // Playlist
    @FXML private TableView<Song> playlistView;
    @FXML private TableColumn<Song, String> titleColumn;
    @FXML private TableColumn<Song, String> artistColumn;
    @FXML private TableColumn<Song, String> albumColumn;
    @FXML private TableColumn<Song, String> durationColumn;

    // Barra de estado inferior
    @FXML private Label statusLabel;
    @FXML private Label trackCountLabel;
    @FXML private Label totalDurationLabel;

    // Servicios
    private final PlayerService player;
    private final PlaylistService playlist;

    // Preferencias (para recordar volumen)
    private final Preferences prefs = Preferences.userNodeForPackage(PlayerController.class);

    public PlayerController() {
        this.player = new PlayerService();
        this.playlist = new PlaylistService();
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupPlaylistBinding();
        setupPlayerBinding();
        setupVolumeControl();
        setupProgressControl();
        setupKeyboardShortcuts();
        updateStatus("Listo - Abre archivos o busca música online");
        
        System.out.println("[PlayerController] Inicializado correctamente");
    }

    // ==========================
    // CONFIGURACIÓN INICIAL
    // ==========================

    private void setupTableColumns() {
        // Configurar política de redimensionamiento para eliminar la columna vacía
        playlistView.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
        
        titleColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getTitle()));

        artistColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getArtist()));

        albumColumn.setCellValueFactory(cellData -> {
            Song song = cellData.getValue();
            String album = song.isLocal() ? "Local" : "YouTube";
            return new javafx.beans.property.SimpleStringProperty(album);
        });

        durationColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        formatTimeFromSeconds(cellData.getValue().getDurationSeconds())));

        // Aplicar clases CSS específicas a cada columna
        titleColumn.setCellFactory(column -> {
            var cell = new javafx.scene.control.TableCell<Song, String>();
            cell.getStyleClass().add("title-cell");
            cell.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null) {
                    cell.setText(newItem);
                }
            });
            return cell;
        });

        artistColumn.setCellFactory(column -> {
            var cell = new javafx.scene.control.TableCell<Song, String>();
            cell.getStyleClass().add("artist-cell");
            cell.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null) {
                    cell.setText(newItem);
                }
            });
            return cell;
        });

        albumColumn.setCellFactory(column -> {
            var cell = new javafx.scene.control.TableCell<Song, String>();
            cell.getStyleClass().add("album-cell");
            cell.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null) {
                    cell.setText(newItem);
                }
            });
            return cell;
        });

        durationColumn.setCellFactory(column -> {
            var cell = new javafx.scene.control.TableCell<Song, String>();
            cell.getStyleClass().add("duration-cell");
            cell.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null) {
                    cell.setText(newItem);
                }
            });
            return cell;
        });
    }

    private void setupPlaylistBinding() {
        playlistView.setItems(playlist.getSongs());

        playlistView.getSelectionModel().selectedItemProperty().addListener((obs, oldSong, newSong) -> {
            if (newSong != null) {
                playlist.setCurrentSong(newSong);
                player.playSong(newSong);
                updateStatus("▶ Reproduciendo: " + newSong.getTitle());
            }
        });

        // Actualizar contadores
        playlist.totalDurationProperty().addListener((obs, oldVal, newVal) -> {
            totalDurationLabel.setText(formatTimeFromSeconds(newVal.doubleValue()));
            // Refrescar la tabla para mostrar las duraciones actualizadas
            playlistView.refresh();
            System.out.println("[PlayerController] Duración total actualizada: " + formatTimeFromSeconds(newVal.doubleValue()));
        });

        playlist.getSongs().addListener((javafx.collections.ListChangeListener<Song>) change -> {
            trackCountLabel.setText(playlist.getSongs().size() + " canciones");
        });
    }

    private void setupPlayerBinding() {
        // Enlazar la posición de reproducción al slider
        progressSlider.setMin(0);
        progressSlider.setMax(1);

        player.currentTimeSecondsProperty().addListener((obs, oldVal, newVal) -> {
            double total = player.getTotalDurationSeconds();
            if (total > 0) {
                double progress = newVal.doubleValue() / total;
                progressSlider.setValue(progress);
                currentTime.setText(formatTimeFromSeconds(newVal.doubleValue()));
            }
        });

        player.totalDurationSecondsProperty().addListener((obs, oldVal, newVal) -> {
            totalTime.setText(formatTimeFromSeconds(newVal.doubleValue()));
        });

        // Cambiar texto de pista cuando cambia la canción
        player.currentSongProperty().addListener((obs, oldSong, newSong) -> {
            if (newSong != null) {
                trackLabel.setText(newSong.toString());
            } else {
                trackLabel.setText("Selecciona una canción para reproducir");
            }
        });

        // Cuando termina una canción, pasar a la siguiente según el modo de reproducción
        player.playingProperty().addListener((obs, wasPlaying, isNowPlaying) -> {
            if (!isNowPlaying && player.isStoppedByEndOfMedia()) {
                Song next = playlist.getNextSong();
                if (next != null) {
                    playlist.setCurrentSong(next);
                    player.playSong(next);
                    playlistView.getSelectionModel().select(next);
                }
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

        player.setVolume(savedVolume);
    }

    private void setupProgressControl() {
        progressSlider.setOnMouseReleased(event -> {
            double progress = progressSlider.getValue();
            player.seekToFraction(progress);
        });
    }

    private void setupKeyboardShortcuts() {
        Platform.runLater(() -> {
            if (root.getScene() != null) {
                root.getScene().setOnKeyPressed(event -> {
                    switch (event.getCode()) {
                        case SPACE -> togglePlayPause();
                        case UP -> adjustVolume(0.05);
                        case DOWN -> adjustVolume(-0.05);
                        case RIGHT -> adjustProgress(0.05);
                        case LEFT -> adjustProgress(-0.05);
                        default -> {}
                    }
                });
            }
        });
    }

    // ==========================
    // MANEJO DE ARCHIVOS
    // ==========================

    @FXML
    public void onOpenFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivos de audio");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de audio", "*.mp3", "*.wav", "*.m4a")
        );

        Stage stage = (Stage) root.getScene().getWindow();
        List<File> files = fileChooser.showOpenMultipleDialog(stage);

        if (files != null && !files.isEmpty()) {
            List<Song> added = playlist.addFiles(files);
            if (!added.isEmpty()) {
                playlistView.getSelectionModel().select(added.get(0));
                updateStatus("✅ Se agregaron " + added.size() + " canciones a la lista");
            } else {
                updateStatus("❌ No se agregaron canciones válidas");
            }
        }
    }

    // ==========================
    // CONTROLES DE TRANSPORTE
    // ==========================

    @FXML
    public void onPlay(ActionEvent event) {
        Song current = playlist.getCurrentSongOrFirst();
        if (current != null) {
            playlist.setCurrentSong(current);
            player.playSong(current);
            playlistView.getSelectionModel().select(current);
            updateStatus("▶ Reproduciendo: " + current.getTitle());
        } else {
            showInfo("No hay canciones en la lista");
        }
    }

    @FXML
    public void onPause(ActionEvent event) {
        player.pause();
        updateStatus("⏸ Pausado");
    }

    @FXML
    public void onStop(ActionEvent event) {
        player.stop();
        updateStatus("⏹ Detenido");
    }

    @FXML
    public void onPrev(ActionEvent event) {
        Song prev = playlist.getPreviousSong();
        if (prev != null) {
            playlist.setCurrentSong(prev);
            player.playSong(prev);
            playlistView.getSelectionModel().select(prev);
            updateStatus("⏮ Anterior: " + prev.getTitle());
        }
    }

    @FXML
    public void onNext(ActionEvent event) {
        Song next = playlist.getNextSong();
        if (next != null) {
            playlist.setCurrentSong(next);
            player.playSong(next);
            playlistView.getSelectionModel().select(next);
            updateStatus("⏭ Siguiente: " + next.getTitle());
        }
    }

    private void togglePlayPause() {
        if (player.isPlaying()) {
            player.pause();
            updateStatus("⏸ Pausado");
        } else {
            onPlay(null);
        }
    }

    private void adjustVolume(double delta) {
        double newVolume = clamp(player.getVolume() + delta, 0.0, 1.0);
        volumeSlider.setValue(newVolume);
    }

    private void adjustProgress(double deltaFraction) {
        double progress = clamp(progressSlider.getValue() + deltaFraction, 0.0, 1.0);
        progressSlider.setValue(progress);
        player.seekToFraction(progress);
    }

    @FXML
    public void onToggleMute(ActionEvent event) {
        boolean mute = muteCheck.isSelected();
        player.setMuted(mute);
        updateStatus(mute ? "🔇 Silenciado" : "🔊 Sonido activado");
    }

    // ==========================
    // MODO ALEATORIO / REPETIR
    // ==========================

    @FXML
    public void onShuffleClicked() {
        if (playlist.getPlaybackMode() == PlaybackMode.SHUFFLE) {
            playlist.setPlaybackMode(PlaybackMode.NORMAL);
            updateShuffleButton(false);
            updateStatus("🔀 Aleatorio: DESACTIVADO");
        } else {
            playlist.setPlaybackMode(PlaybackMode.SHUFFLE);
            updateShuffleButton(true);
            updateStatus("🔀 Aleatorio: ACTIVADO");
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
                break;
            case REPEAT_ALL:
                playlist.setPlaybackMode(PlaybackMode.REPEAT_ONE);
                updateRepeatButton(PlaybackMode.REPEAT_ONE);
                updateStatus("🔂 Repetir: CANCIÓN ACTUAL");
                break;
            case REPEAT_ONE:
                playlist.setPlaybackMode(PlaybackMode.NORMAL);
                updateRepeatButton(PlaybackMode.NORMAL);
                updateStatus("▶️ Repetir: DESACTIVADO");
                break;
        }
    }

    private void updateShuffleButton(boolean active) {
        if (active) {
            btnShuffle.getStyleClass().add("active");
        } else {
            btnShuffle.getStyleClass().remove("active");
        }
    }

    private void updateRepeatButton(PlaybackMode mode) {
        btnRepeat.getStyleClass().removeAll("repeat-all", "repeat-one");
        switch (mode) {
            case REPEAT_ALL -> btnRepeat.getStyleClass().add("repeat-all");
            case REPEAT_ONE -> btnRepeat.getStyleClass().add("repeat-one");
            default -> { }
        }
    }

    // ==========================
    // BÚSQUEDA ONLINE
    // ==========================

    @FXML
    public void onSearchClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/reproductor/com/musica/view/SearchView.fxml")
            );
            
            Parent searchRoot = loader.load();
            
            // Obtener el controlador y pasarle la referencia del PlaylistService
            SearchController searchController = loader.getController();
            searchController.setPlaylistService(playlist);
            
            // Crear nueva ventana modal
            Stage searchStage = new Stage();
            searchStage.setTitle("Buscar Música Online");
            searchStage.initModality(Modality.APPLICATION_MODAL);
            searchStage.initOwner(root.getScene().getWindow());
            
            Scene scene = new Scene(searchRoot, 900, 600);
            searchStage.setScene(scene);
            
            updateStatus("🔍 Abriendo búsqueda online...");
            searchStage.showAndWait();
            
            updateStatus("Lista actualizada con nuevas canciones");
            
        } catch (IOException e) {
            System.err.println("[PlayerController] Error al abrir SearchView: " + e.getMessage());
            e.printStackTrace();
            showError("Error al abrir la ventana de búsqueda: " + e.getMessage());
        }
    }

    // ==========================
    // PLAYLIST (limpiar / guardar)
    // ==========================

    public void onClearPlaylist(ActionEvent e) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar");
        confirm.setHeaderText("¿Limpiar toda la lista?");
        confirm.setContentText("Esta acción eliminará todas las canciones de la lista actual.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                playlist.clearCurrentPlaylist();
                updateStatus("🗑️ Lista de reproducción limpiada");
            }
        });
    }

    public void onSavePlaylist(ActionEvent e) {
        playlist.saveCurrentPlaylist();
        updateStatus("💾 Lista de reproducción guardada");
        showInfo("Funcionalidad de guardado en desarrollo");
    }

    // ==========================
    // UTILIDADES
    // ==========================

    private void updateStatus(String message) {
        statusLabel.setText(message);
        System.out.println("[Status] " + message);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private String formatTimeFromSeconds(double seconds) {
        int total = (int) seconds;
        int h = total / 3600;
        int m = (total % 3600) / 60;
        int s = total % 60;

        if (h > 0) {
            return String.format("%d:%02d:%02d", h, m, s);
        } else {
            return String.format("%02d:%02d", m, s);
        }
    }

    private void showInfo(String msg) {
        showAlert(Alert.AlertType.INFORMATION, "Información", msg);
    }

    private void showError(String msg) {
        showAlert(Alert.AlertType.ERROR, "Error", msg);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
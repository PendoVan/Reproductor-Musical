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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import reproductor.com.musica.core.PlayerService;
import reproductor.com.musica.core.PlaylistFileService;
import reproductor.com.musica.core.PlaylistService;
import reproductor.com.musica.model.PlaybackMode;
import reproductor.com.musica.model.Song;


public class PlayerController {

    private static final String PREF_VOLUME_KEY = "volume";

    @FXML private BorderPane root;

    @FXML private Button btnOpen;
    @FXML private Button btnSearch;
    @FXML private Button btnShuffle;
    @FXML private Button btnRepeat;
    @FXML private MenuButton btnLibrary;

    @FXML private Label trackLabel;
    @FXML private Slider progressSlider;
    @FXML private Label currentTime;
    @FXML private Label totalTime;
    @FXML private Label playlistNameLabel;

    @FXML private Button btnPrev;
    @FXML private Button btnPlay;
    @FXML private Button btnPause;
    @FXML private Button btnStop;
    @FXML private Button btnNext;

    @FXML private Slider volumeSlider;
    @FXML private CheckBox muteCheck;

    @FXML private TableView<Song> playlistView;
    @FXML private TableColumn<Song, String> titleColumn;
    @FXML private TableColumn<Song, String> artistColumn;
    @FXML private TableColumn<Song, String> albumColumn;
    @FXML private TableColumn<Song, String> durationColumn;

    @FXML private Label statusLabel;
    @FXML private Label trackCountLabel;
    @FXML private Label totalDurationLabel;

    private final PlayerService player;
    private final PlaylistService playlist;
    private final PlaylistFileService playlistFileService = new PlaylistFileService();

    private final Preferences prefs = Preferences.userNodeForPackage(PlayerController.class);
    private String currentPlaylistName = null;

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
        setupPlaylistContextMenu();
        updateStatus("Listo - Abre archivos o busca música online");
        
        System.out.println("[PlayerController] Inicializado correctamente");
    }


    private void setupTableColumns() {
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

        playlistView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {  // Doble clic
                Song selected = playlistView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    playlist.setCurrentSong(selected);
                    player.playSong(selected);
                    updateStatus("▶ Reproduciendo: " + selected.getTitle());
                }
            }
        });

        playlist.totalDurationProperty().addListener((obs, oldVal, newVal) -> {
            totalDurationLabel.setText(formatTimeFromSeconds(newVal.doubleValue()));
            playlistView.refresh();
            System.out.println("[PlayerController] Duración total actualizada: " + formatTimeFromSeconds(newVal.doubleValue()));
        });

        playlist.getSongs().addListener((javafx.collections.ListChangeListener<Song>) change -> {
            trackCountLabel.setText(playlist.getSongs().size() + " canciones");
        });
    }

    private void setupPlayerBinding() {
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

        player.currentSongProperty().addListener((obs, oldSong, newSong) -> {
            if (newSong != null) {
                trackLabel.setText(newSong.toString());
            } else {
                trackLabel.setText("Selecciona una canción para reproducir");
            }
        });

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
                        
                        case DELETE -> {
                            Song selected = playlistView.getSelectionModel().getSelectedItem();
                            if (selected != null) {
                                onRemoveSongFromPlaylist(selected);
                            }
                        }
                        case ENTER -> {
                            Song selected = playlistView.getSelectionModel().getSelectedItem();
                            if (selected != null) {
                                playlist.setCurrentSong(selected);
                                player.playSong(selected);
                                updateStatus("▶ Reproduciendo: " + selected.getTitle());
                            }
                        }
                        
                        default -> {}
                    }
                });
            }
        });
    }
    
    private void setupPlaylistContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        
        javafx.scene.control.MenuItem playItem = new javafx.scene.control.MenuItem("▶ Reproducir");
        playItem.setOnAction(e -> {
            Song selected = playlistView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                playlist.setCurrentSong(selected);
                player.playSong(selected);
                updateStatus("▶ Reproduciendo: " + selected.getTitle());
            }
        });
        
        javafx.scene.control.MenuItem deleteItem = new javafx.scene.control.MenuItem("🗑️ Eliminar de la lista");
        deleteItem.setOnAction(e -> {
            Song selected = playlistView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                onRemoveSongFromPlaylist(selected);
            }
        });
        
        javafx.scene.control.SeparatorMenuItem separator = new javafx.scene.control.SeparatorMenuItem();
        
        javafx.scene.control.MenuItem infoItem = new javafx.scene.control.MenuItem("📋 Información");
        infoItem.setOnAction(e -> {
            Song selected = playlistView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showSongInfo(selected);
            }
        });
        
        javafx.scene.control.MenuItem clearAllItem = new javafx.scene.control.MenuItem("🗑️ Limpiar toda la lista");
        clearAllItem.setOnAction(e -> onClearPlaylist(null));
        
        contextMenu.getItems().addAll(playItem, deleteItem, separator, infoItem, clearAllItem);
        
        playlistView.setContextMenu(contextMenu);
        
        playlistView.setOnContextMenuRequested(event -> {
            Song selected = playlistView.getSelectionModel().getSelectedItem();
            playItem.setDisable(selected == null);
            deleteItem.setDisable(selected == null);
            infoItem.setDisable(selected == null);
        });
        
        System.out.println("[PlayerController] ✓ Menú contextual configurado");
    }


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
    
    private void onRemoveSongFromPlaylist(Song song) {
        if (song == null) return;
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Eliminar esta canción de la lista?");
        confirm.setContentText(song.getTitle() + " - " + song.getArtist());
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (playlist.getCurrentSong() != null && 
                    playlist.getCurrentSong().equals(song)) {
                    player.stop();
                }
                
                playlist.removeSong(song);
                
                if (currentPlaylistName != null) {
                    updateStatus("🗑️ Eliminado: " + song.getTitle() + " (cambios sin guardar en '" + currentPlaylistName + "')");
                } else {
                    updateStatus("🗑️ Eliminado: " + song.getTitle());
                }
                
                System.out.println("[PlayerController] 🗑️ Canción eliminada: " + song.getTitle());
            }
        });
    }

    @FXML
    public void onRemoveSelectedSongs() {
        var selected = playlistView.getSelectionModel().getSelectedItems();
        
        if (selected.isEmpty()) {
            showInfo("Selecciona al menos una canción para eliminar");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Eliminar " + selected.size() + " canción(es)?");
        confirm.setContentText("Esta acción no se puede deshacer.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                java.util.List<Song> toRemove = new java.util.ArrayList<>(selected);
                
                for (Song song : toRemove) {
                    // Si es la actual, detener
                    if (playlist.getCurrentSong() != null && 
                        playlist.getCurrentSong().equals(song)) {
                        player.stop();
                    }
                    
                    playlist.removeSong(song);
                }
                
                updateStatus("🗑️ Eliminadas " + toRemove.size() + " canciones");
            }
        });
    }
    
    private void showSongInfo(Song song) {
        if (song == null) return;
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información de la canción");
        alert.setHeaderText(song.getTitle());
        
        StringBuilder info = new StringBuilder();
        info.append("Artista: ").append(song.getArtist()).append("\n");
        info.append("Duración: ").append(formatTimeFromSeconds(song.getDurationSeconds())).append("\n");
        info.append("\n");
        
        if (song.isLocal()) {
            info.append("Tipo: Archivo local\n");
            info.append("Ubicación: ").append(song.getFilePathString()).append("\n");
        } else if (song.isRemote()) {
            info.append("Tipo: Stream remoto\n");
            info.append("URL: ").append(song.getStreamUrl()).append("\n");
        }
        
        alert.setContentText(info.toString());
        alert.showAndWait();
    }


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
    
    private void updatePlaylistNameIndicator() {
        if (playlistNameLabel != null) {
            if (currentPlaylistName != null && !currentPlaylistName.isBlank()) {
                playlistNameLabel.setText("📂 " + currentPlaylistName);
            } else {
                playlistNameLabel.setText("");
            }
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


    @FXML
    public void onSearchClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/reproductor/com/musica/view/SearchView.fxml")
            );
            
            Parent searchRoot = loader.load();
            
            SearchController searchController = loader.getController();
            searchController.setPlaylistService(playlist);
            
            searchController.setPlaylistUpdateListener(() -> {
                Platform.runLater(() -> {
                    playlistView.refresh();
                    updateStatus("✅ Playlist actualizada con nuevas canciones");
                    System.out.println("[PlayerController] 🔄 Tabla refrescada");
                });
            });
            
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


    public void onClearPlaylist(ActionEvent e) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar");
        confirm.setHeaderText("¿Limpiar toda la lista?");
        confirm.setContentText("Esta acción eliminará todas las canciones de la lista actual.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                playlist.clearCurrentPlaylist();
                currentPlaylistName = null;
                updatePlaylistNameIndicator();
                updateStatus("🗑️ Lista de reproducción limpiada");
            }
        });
    }

    @FXML
    public void onSavePlaylist(ActionEvent e) {
        String defaultName = (currentPlaylistName != null && !currentPlaylistName.isBlank()) 
                             ? currentPlaylistName 
                             : "Mi Playlist";
        
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog(defaultName);
        dialog.setTitle("Guardar Playlist");
        dialog.setHeaderText("Guardar la playlist actual");
        
        if (currentPlaylistName != null && !currentPlaylistName.isBlank()) {
            dialog.setContentText("Nombre (actual: " + currentPlaylistName + "):");
        } else {
            dialog.setContentText("Nombre de la playlist:");
        }
        
        dialog.showAndWait().ifPresent(nombre -> {
            if (nombre != null && !nombre.isBlank()) {
                try {
                    boolean isCurrentPlaylist = nombre.equals(currentPlaylistName);
                    
                    if (playlistFileService.playlistExists(nombre) && !isCurrentPlaylist) {
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                        confirm.setTitle("Confirmar sobrescritura");
                        confirm.setHeaderText("La playlist '" + nombre + "' ya existe");
                        confirm.setContentText("¿Deseas sobrescribirla?");
                        
                        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                            return;
                        }
                    }
                    
                    playlistFileService.savePlaylist(nombre, playlist.getSongs());
                    
                    currentPlaylistName = nombre;
                    updatePlaylistNameIndicator();
                    
                    updateStatus("💾 Playlist guardada: " + nombre);
                    showInfo("Playlist '" + nombre + "' guardada correctamente en:\n" + 
                            playlistFileService.getPlaylistsDirectory());
                    
                } catch (IOException ex) {
                    showError("Error al guardar playlist: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
    }

    @FXML
    public void onOpenLibrary() {
        try {
            List<String> playlists = playlistFileService.listPlaylists();
            
            if (playlists.isEmpty()) {
                showInfo("No hay playlists guardadas.\n\n" +
                        "Guarda la playlist actual usando el botón 'Guardar Lista'.");
                return;
            }
            
            javafx.scene.control.ChoiceDialog<String> dialog = 
                new javafx.scene.control.ChoiceDialog<>(playlists.get(0), playlists);
            
            dialog.setTitle("Biblioteca de Playlists");
            dialog.setHeaderText("Playlists guardadas (" + playlists.size() + ")");
            dialog.setContentText("Selecciona una playlist:");
            
            dialog.showAndWait().ifPresent(nombre -> {
                try {
                    loadPlaylistFromLibrary(nombre);
                } catch (IOException ex) {
                    showError("Error al cargar playlist: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });
            
        } catch (Exception ex) {
            showError("Error al abrir biblioteca: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void loadPlaylistFromLibrary(String nombre) throws IOException {
        // Confirmar si hay canciones actuales
        if (!playlist.getSongs().isEmpty()) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar carga");
            confirm.setHeaderText("La lista actual se reemplazará");
            confirm.setContentText("¿Deseas cargar '" + nombre + "'?");
            
            if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                return;
            }
        }
        
        List<Song> songs = playlistFileService.loadPlaylist(nombre);
        
        if (songs.isEmpty()) {
            showWarning("La playlist '" + nombre + "' no contiene canciones válidas.\n\n" +
                       "Verifica que los archivos MP3 existan en:\n" + 
                       playlistFileService.getDownloadsDirectory());
            return;
        }
        
        playlist.clearCurrentPlaylist();
        playlist.addSongs(songs);
        
        currentPlaylistName = nombre;
        updatePlaylistNameIndicator();
        
        if (!songs.isEmpty()) {
            playlistView.getSelectionModel().select(songs.get(0));
        }
        
        updateStatus("📂 Playlist cargada: " + nombre + " (" + songs.size() + " canciones)");
        showInfo("Playlist '" + nombre + "' cargada correctamente.\n\n" +
                "Canciones: " + songs.size());
    }

    @FXML
    public void onManagePlaylists() {
        try {
            List<String> playlists = playlistFileService.listPlaylists();
            
            if (playlists.isEmpty()) {
                showInfo("No hay playlists guardadas.");
                return;
            }
            
            Stage stage = new Stage();
            stage.setTitle("Gestionar Playlists");
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.initOwner(root.getScene().getWindow());
            
            javafx.scene.control.ListView<String> listView = new javafx.scene.control.ListView<>();
            listView.getItems().addAll(playlists);
            
            javafx.scene.control.Button btnLoad = new javafx.scene.control.Button("Cargar");
            btnLoad.setOnAction(e -> {
                String selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    try {
                        loadPlaylistFromLibrary(selected);
                        stage.close();
                    } catch (IOException ex) {
                        showError("Error: " + ex.getMessage());
                    }
                }
            });
            
            javafx.scene.control.Button btnDelete = new javafx.scene.control.Button("Eliminar");
            btnDelete.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
            btnDelete.setOnAction(e -> {
                String selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Confirmar eliminación");
                    confirm.setHeaderText("¿Eliminar '" + selected + "'?");
                    confirm.setContentText("Esta acción no se puede deshacer.");
                    
                    if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                        if (playlistFileService.deletePlaylist(selected)) {
                            listView.getItems().remove(selected);
                            showInfo("Playlist eliminada: " + selected);
                        }
                    }
                }
            });
            
            javafx.scene.control.Button btnClose = new javafx.scene.control.Button("Cerrar");
            btnClose.setOnAction(e -> stage.close());
            
            javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(10, btnLoad, btnDelete, btnClose);
            buttons.setAlignment(javafx.geometry.Pos.CENTER);
            buttons.setPadding(new javafx.geometry.Insets(10));
            
            javafx.scene.layout.VBox layout = new javafx.scene.layout.VBox(10, 
                new javafx.scene.control.Label("Playlists guardadas:"), 
                listView, 
                buttons);
            layout.setPadding(new javafx.geometry.Insets(15));
            
            javafx.scene.Scene scene = new javafx.scene.Scene(layout, 400, 500);
            stage.setScene(scene);
            stage.showAndWait();
            
        } catch (Exception ex) {
            showError("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void showWarning(String msg) {
        showAlert(Alert.AlertType.WARNING, "Advertencia", msg);
    }


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
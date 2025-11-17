package reproductor.com.musica.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import reproductor.com.musica.api.ApiException;
import reproductor.com.musica.api.SearchService;
import reproductor.com.musica.core.PlaylistService;
import reproductor.com.musica.model.Song;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador de la vista de búsqueda online.
 * Integra SearchService con la UI de JavaFX.
 */
public class SearchController {

    @FXML private TextField searchField;
    @FXML private Button btnSearch;
    @FXML private Button btnClear;
    @FXML private Button btnBack;

    @FXML private TableView<Song> resultsTable;
    @FXML private TableColumn<Song, String> titleColumn;
    @FXML private TableColumn<Song, String> artistColumn;
    @FXML private TableColumn<Song, String> albumColumn;
    @FXML private TableColumn<Song, String> durationColumn;

    @FXML private ProgressIndicator searchProgress;
    @FXML private Label searchStatusLabel;
    @FXML private Label resultsCountLabel;
    @FXML private Label connectionStatusLabel;

    @FXML private Button btnAddSelected;
    @FXML private Button btnSelectAll;

    private final SearchService searchService;
    private PlaylistService playlistService;
    private final ObservableList<Song> searchResults;

    public SearchController() {
        this.searchService = new SearchService();
        this.searchResults = FXCollections.observableArrayList();
    }

    /**
     * Permite inyectar el PlaylistService desde el PlayerController
     */
    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
        System.out.println("[SearchController] PlaylistService inyectado correctamente");
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupTableData();
        checkApiConnection();
        
        // Configurar botón de cerrar
        if (btnBack != null) {
            btnBack.setOnAction(e -> closeWindow());
        }
        
        System.out.println("[SearchController] Inicializado");
    }

    @FXML
    private void onSearchClicked() {
        String query = searchField.getText().trim();

        if (query.isEmpty()) {
            showWarning("Por favor ingresa un término de búsqueda");
            return;
        }

        List<String> searchTerms = parseSearchQuery(query);
        performSearch(searchTerms);
    }

    @FXML
    private void onClearClicked() {
        searchField.clear();
        searchResults.clear();
        resultsCountLabel.setText("");
        searchStatusLabel.setText("Ingresa un término de búsqueda");
    }

    @FXML
    private void onAddToPlaylistClicked() {
        if (playlistService == null) {
            showError("Error: PlaylistService no está inicializado");
            return;
        }

        List<Song> selected = resultsTable.getSelectionModel().getSelectedItems();

        if (selected == null || selected.isEmpty()) {
            showWarning("Selecciona al menos una canción para agregar");
            return;
        }

        playlistService.addSongs(selected);
        showInfo("✅ Agregadas " + selected.size() + " canciones a la playlist");
        
        System.out.println("[SearchController] Agregadas " + selected.size() + " canciones");
    }

    @FXML
    private void onSelectAllClicked() {
        resultsTable.getSelectionModel().selectAll();
    }

    // ===== LÓGICA DE BÚSQUEDA =====

    private void performSearch(List<String> searchTerms) {
        disableSearchControls(true);
        searchProgress.setVisible(true);
        searchStatusLabel.setText("🔍 Buscando y descargando...");

        Task<List<Song>> searchTask = searchService.searchAndDownloadAsync(searchTerms);

        // Vincular progreso a UI
        searchStatusLabel.textProperty().bind(searchTask.messageProperty());

        searchTask.setOnSucceeded(event -> {
            searchStatusLabel.textProperty().unbind();
            List<Song> results = searchTask.getValue();

            searchResults.setAll(results);
            resultsTable.refresh();

            resultsCountLabel.setText(results.size() + " resultados");
            searchStatusLabel.setText("✅ Búsqueda completada - " + results.size() + " canciones encontradas");
            disableSearchControls(false);
            searchProgress.setVisible(false);
            
            System.out.println("[SearchController] Búsqueda exitosa: " + results.size() + " resultados");
        });

        searchTask.setOnFailed(event -> {
            searchStatusLabel.textProperty().unbind();
            Throwable ex = searchTask.getException();
            searchProgress.setVisible(false);
            disableSearchControls(false);

            String errorMsg;
            if (ex instanceof ApiException apiEx) {
                errorMsg = "Error en la API: " + apiEx.getMessage();
            } else {
                errorMsg = "Error inesperado: " + ex.getMessage();
            }
            
            searchStatusLabel.setText("❌ " + errorMsg);
            showError(errorMsg);
            
            System.err.println("[SearchController] Error en búsqueda: " + errorMsg);
            ex.printStackTrace();
        });

        Thread t = new Thread(searchTask, "search-task");
        t.setDaemon(true);
        t.start();
    }

    private void disableSearchControls(boolean disable) {
        btnSearch.setDisable(disable);
        btnClear.setDisable(disable);
        btnAddSelected.setDisable(disable);
        btnSelectAll.setDisable(disable);
        resultsTable.setDisable(disable);
        searchField.setDisable(disable);
    }

    private List<String> parseSearchQuery(String query) {
        // Permite separar por coma o línea nueva
        return Arrays.stream(query.split("[,\\n]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private void setupTableColumns() {
        titleColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getTitle()));

        artistColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getArtist()));

        albumColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty("YouTube"));

        durationColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        formatDuration(cellData.getValue().getDurationSeconds())));

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

        // Permitir selección múltiple
        resultsTable.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE);
    }

    private void setupTableData() {
        resultsTable.setItems(searchResults);

        // Doble clic para agregar a playlist
        resultsTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Song selected = resultsTable.getSelectionModel().getSelectedItem();
                if (selected != null && playlistService != null) {
                    playlistService.addSong(selected);
                    showInfo("✅ Agregado: " + selected.getTitle());
                }
            }
        });
    }

    // ===== HELPERS =====

    private void checkApiConnection() {
        Task<Boolean> connectionTask = searchService.checkApiConnectionAsync();
        
        connectionTask.setOnSucceeded(e -> {
            boolean connected = connectionTask.getValue();
            Platform.runLater(() -> {
                if (connected) {
                    connectionStatusLabel.setText("🟢 Conectado");
                    connectionStatusLabel.setStyle("-fx-text-fill: #1DB954;");
                } else {
                    connectionStatusLabel.setText("🔴 Desconectado");
                    connectionStatusLabel.setStyle("-fx-text-fill: #f44336;");
                }
            });
        });
        
        connectionTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                connectionStatusLabel.setText("🔴 Error de conexión");
                connectionStatusLabel.setStyle("-fx-text-fill: #f44336;");
            });
        });
        
        new Thread(connectionTask).start();
    }

    private String formatDuration(double seconds) {
        int total = (int) seconds;
        int m = total / 60;
        int s = total % 60;
        return String.format("%02d:%02d", m, s);
    }

    private void closeWindow() {
        Stage stage = (Stage) searchField.getScene().getWindow();
        stage.close();
    }

    private void showInfo(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Información", message);
    }

    private void showWarning(String message) {
        showAlert(Alert.AlertType.WARNING, "Advertencia", message);
    }

    private void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "Error", message);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ===== GETTERS =====

    public ObservableList<Song> getSearchResults() {
        return searchResults;
    }
}
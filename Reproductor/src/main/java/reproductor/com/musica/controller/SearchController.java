package reproductor.com.musica.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    private final PlaylistService playlistService;
    private final ObservableList<Song> searchResults;

    public SearchController() {
        this.searchService = new SearchService();
        this.playlistService = new PlaylistService(); // o inyectar desde fuera
        this.searchResults = FXCollections.observableArrayList();
    }

    /**
     * Constructor con inyección de dependencias (opcional si algún día quieres usarlo).
     */
    public SearchController(SearchService searchService, PlaylistService playlistService) {
        this.searchService = searchService != null ? searchService : new SearchService();
        this.playlistService = playlistService != null ? playlistService : new PlaylistService();
        this.searchResults = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupTableData();
        checkApiConnection();
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
        List<Song> selected = resultsTable.getSelectionModel().getSelectedItems();

        if (selected == null || selected.isEmpty()) {
            showWarning("Selecciona al menos una canción para agregar");
            return;
        }

        playlistService.addSongs(selected);
        showInfo("Agregadas " + selected.size() + " canciones a la playlist");
    }

    @FXML
    private void onSelectAllClicked() {
        resultsTable.getSelectionModel().selectAll();
    }

    // ===== LÓGICA DE BÚSQUEDA =====

    private void performSearch(List<String> searchTerms) {
        disableSearchControls(true);
        searchProgress.setVisible(true);
        searchStatusLabel.setText("Buscando...");

        Task<List<Song>> searchTask = searchService.searchAndDownloadAsync(searchTerms);

        // Vincular progreso a UI
        searchStatusLabel.textProperty().bind(searchTask.messageProperty());

        searchTask.setOnSucceeded(event -> {
            searchStatusLabel.textProperty().unbind();
            List<Song> results = searchTask.getValue();

            searchResults.setAll(results);
            resultsTable.refresh();

            resultsCountLabel.setText(results.size() + " resultados");
            searchStatusLabel.setText("Búsqueda completada");
            disableSearchControls(false);
            searchProgress.setVisible(false);
        });

        searchTask.setOnFailed(event -> {
            searchStatusLabel.textProperty().unbind();
            Throwable ex = searchTask.getException();
            searchProgress.setVisible(false);
            disableSearchControls(false);

            if (ex instanceof ApiException apiEx) {
                showError("Error en la API: " + apiEx.getMessage());
            } else {
                showError("Error inesperado al buscar: " + ex.getMessage());
            }
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
    }

    private List<String> parseSearchQuery(String query) {
        return Arrays.stream(query.split(","))
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
                if (selected != null) {
                    playlistService.addSong(selected);
                    showInfo("Agregado: " + selected.getTitle());
                }
            }
        });
    }

    // ===== HELPERS =====

    private void checkApiConnection() {
        // Esto es un ejemplo; si tienes un método real de healthcheck, llámalo aquí.
        new Thread(() -> {
            Platform.runLater(() -> {
                connectionStatusLabel.setText("Conectado");
                connectionStatusLabel.getStyleClass().add("connection-success");
            });
        }).start();
    }

    private String formatDuration(double seconds) {
        int total = (int) seconds;
        int m = total / 60;
        int s = total % 60;
        return String.format("%02d:%02d", m, s);
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

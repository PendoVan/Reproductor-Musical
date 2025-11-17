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
 * Controlador para la vista de búsqueda y descarga de música.
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
        this.playlistService = new PlaylistService(); // O inyectar desde fuera
        this.searchResults = FXCollections.observableArrayList();
    }
    
    /**
     * Constructor con inyección de dependencias.
     */
    public SearchController(SearchService searchService, PlaylistService playlistService) {
        this.searchService = searchService;
        this.playlistService = playlistService;
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
        
        if (selected.isEmpty()) {
            showWarning("Selecciona al menos una canción");
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
            List<Song> songs = searchTask.getValue();
            handleSearchSuccess(songs);
        });
        
        searchTask.setOnFailed(event -> {
            Throwable error = searchTask.getException();
            handleSearchError(error);
        });
        
        searchTask.setOnCancelled(event -> {
            searchStatusLabel.textProperty().unbind();
            searchStatusLabel.setText("Búsqueda cancelada");
            disableSearchControls(false);
            searchProgress.setVisible(false);
        });
        
        new Thread(searchTask).start();
    }
    
    private void handleSearchSuccess(List<Song> songs) {
        Platform.runLater(() -> {
            searchResults.clear();
            searchResults.addAll(songs);
            
            resultsCountLabel.setText(songs.size() + " resultados");
            searchStatusLabel.textProperty().unbind();
            searchStatusLabel.setText("Búsqueda completada");
            
            disableSearchControls(false);
            searchProgress.setVisible(false);
            
            if (songs.isEmpty()) {
                showInfo("No se encontraron resultados");
            } else {
                showInfo("Se descargaron " + songs.size() + " canciones");
            }
        });
    }
    
    private void handleSearchError(Throwable error) {
        Platform.runLater(() -> {
            searchStatusLabel.textProperty().unbind();
            searchStatusLabel.setText("Error en la búsqueda");
            
            String message = error instanceof ApiException 
                    ? error.getMessage() 
                    : "Error desconocido: " + error.getMessage();
            
            showError("Error", message);
            
            disableSearchControls(false);
            searchProgress.setVisible(false);
        });
    }
    
    private void checkApiConnection() {
        Task<Boolean> connectionTask = searchService.checkApiConnectionAsync();
        
        connectionTask.setOnSucceeded(event -> {
            boolean connected = connectionTask.getValue();
            Platform.runLater(() -> {
                if (connected) {
                    connectionStatusLabel.setText("✓ Conectado");
                    connectionStatusLabel.setStyle("-fx-text-fill: #06b6d4;");
                } else {
                    connectionStatusLabel.setText("✗ Sin conexión");
                    connectionStatusLabel.setStyle("-fx-text-fill: #ec4899;");
                    showWarning("No se pudo conectar con la API. " +
                            "Verifica que el servidor esté ejecutándose.");
                }
            });
        });
        
        new Thread(connectionTask).start();
    }
    
    // ===== CONFIGURACIÓN DE TABLA =====
    
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
    
    private List<String> parseSearchQuery(String query) {
        // Soporta búsquedas separadas por coma o línea nueva
        return Arrays.stream(query.split("[,\n]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
    
    private void disableSearchControls(boolean disable) {
        btnSearch.setDisable(disable);
        btnClear.setDisable(disable);
        searchField.setDisable(disable);
        btnAddSelected.setDisable(disable);
        btnSelectAll.setDisable(disable);
    }
    
    private String formatDuration(int seconds) {
        if (seconds <= 0) return "--:--";
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", minutes, secs);
    }
    
    // ===== ALERTAS =====
    
    private void showInfo(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Información", message);
    }
    
    private void showWarning(String message) {
        showAlert(Alert.AlertType.WARNING, "Advertencia", message);
    }
    
    private void showError(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
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
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
import reproductor.com.musica.api.dto.SearchResult;
import reproductor.com.musica.core.PlaylistService;
import reproductor.com.musica.model.Song;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador actualizado con búsqueda sin descarga automática.
 */
public class SearchController {

    @FXML private TextField searchField;
    @FXML private Button btnSearch;
    @FXML private Button btnClear;
    @FXML private Button btnBack;

    @FXML private TableView<SearchResult> resultsTable;
    @FXML private TableColumn<SearchResult, String> titleColumn;
    @FXML private TableColumn<SearchResult, String> artistColumn;
    @FXML private TableColumn<SearchResult, String> albumColumn;
    @FXML private TableColumn<SearchResult, String> durationColumn;

    @FXML private ProgressIndicator searchProgress;
    @FXML private Label searchStatusLabel;
    @FXML private Label resultsCountLabel;
    @FXML private Label connectionStatusLabel;

    @FXML private Button btnAddSelected;
    @FXML private Button btnSelectAll;

    private final SearchService searchService;
    private PlaylistService playlistService;
    private final ObservableList<SearchResult> searchResults;

    public SearchController() {
        this.searchService = new SearchService();
        this.searchResults = FXCollections.observableArrayList();
        System.out.println("[SearchController] Constructor llamado");
    }

    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
        System.out.println("[SearchController] ✅ PlaylistService inyectado correctamente");
    }

    @FXML
    public void initialize() {
        System.out.println("[SearchController] 🔧 Inicializando...");
        
        // Verificar que los componentes están vinculados
        System.out.println("[SearchController] resultsTable: " + (resultsTable != null ? "✅" : "❌"));
        System.out.println("[SearchController] titleColumn: " + (titleColumn != null ? "✅" : "❌"));
        System.out.println("[SearchController] artistColumn: " + (artistColumn != null ? "✅" : "❌"));
        
        setupTableColumns();
        setupTableData();
        checkApiConnection();
        
        if (btnBack != null) {
            btnBack.setOnAction(e -> closeWindow());
        }
        
        System.out.println("[SearchController] ✅ Inicializado en modo búsqueda sin descarga");
    }

    @FXML
    private void onSearchClicked() {
        String query = searchField.getText().trim();

        if (query.isEmpty()) {
            showWarning("Por favor ingresa un término de búsqueda");
            return;
        }

        System.out.println("[SearchController] 🔍 Buscando: " + query);
        performSearchOnly(query);
    }

    @FXML
    private void onClearClicked() {
        searchField.clear();
        searchResults.clear();
        resultsCountLabel.setText("");
        searchStatusLabel.setText("Ingresa un término de búsqueda");
        System.out.println("[SearchController] 🧹 Resultados limpiados");
    }

    @FXML
    private void onAddToPlaylistClicked() {
        if (playlistService == null) {
            showError("Error: PlaylistService no está inicializado");
            return;
        }

        List<SearchResult> selected = resultsTable.getSelectionModel().getSelectedItems()
                .stream()
                .collect(Collectors.toList());

        if (selected.isEmpty()) {
            showWarning("Selecciona al menos una canción para agregar");
            return;
        }

        System.out.println("[SearchController] ⬇ Descargando " + selected.size() + " canciones seleccionadas");
        downloadSelectedResults(selected);
    }

    @FXML
    private void onSelectAllClicked() {
        resultsTable.getSelectionModel().selectAll();
        System.out.println("[SearchController] ✅ Todas las canciones seleccionadas");
    }

    // ===== BÚSQUEDA SIN DESCARGA =====

    private void performSearchOnly(String query) {
        disableSearchControls(true);
        searchProgress.setVisible(true);
        searchStatusLabel.setText("🔍 Buscando resultados...");

        Task<List<SearchResult>> searchTask = searchService.searchOnlyAsync(query);

        searchStatusLabel.textProperty().bind(searchTask.messageProperty());

        searchTask.setOnSucceeded(event -> {
            searchStatusLabel.textProperty().unbind();
            List<SearchResult> results = searchTask.getValue();

            System.out.println("[SearchController] 📥 Respuesta recibida: " + results.size() + " resultados");
            
            // DEBUG: Imprimir primeros 3 resultados
            for (int i = 0; i < Math.min(3, results.size()); i++) {
                SearchResult r = results.get(i);
                System.out.println("[SearchController]   " + (i+1) + ". " + r.getTitulo() + 
                                 " - " + r.getArtista() + " (" + r.getFormattedDuration() + ")");
            }

            // Limpiar y agregar resultados
            searchResults.clear();
            searchResults.addAll(results);
            
            System.out.println("[SearchController] 📋 searchResults.size(): " + searchResults.size());
            System.out.println("[SearchController] 📋 resultsTable.getItems().size(): " + 
                             resultsTable.getItems().size());

            resultsTable.refresh();

            resultsCountLabel.setText(results.size() + " resultados");
            searchStatusLabel.setText("✅ Búsqueda completada - Selecciona las canciones a descargar");
            disableSearchControls(false);
            searchProgress.setVisible(false);
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
            
            System.err.println("[SearchController] ❌ Error en búsqueda: " + errorMsg);
            ex.printStackTrace();
            
            searchStatusLabel.setText("❌ " + errorMsg);
            showError(errorMsg);
        });

        Thread t = new Thread(searchTask, "search-task");
        t.setDaemon(true);
        t.start();
    }

    // ===== DESCARGA DE SELECCIONADOS =====

    private void downloadSelectedResults(List<SearchResult> selected) {
        disableSearchControls(true);
        searchProgress.setVisible(true);
        searchStatusLabel.setText("⬇ Descargando " + selected.size() + " canciones...");

        Task<List<Song>> downloadTask = searchService.downloadSelectedAsync(selected);

        searchStatusLabel.textProperty().bind(downloadTask.messageProperty());

        downloadTask.setOnSucceeded(event -> {
            searchStatusLabel.textProperty().unbind();
            List<Song> songs = downloadTask.getValue();

            if (playlistService != null) {
                playlistService.addSongs(songs);
                System.out.println("[SearchController] ✅ " + songs.size() + " canciones agregadas a playlist");
            }

            searchStatusLabel.setText("✅ Descargadas " + songs.size() + " de " + selected.size() + " canciones");
            searchProgress.setVisible(false);
            disableSearchControls(false);

            showInfo("✅ Agregadas " + songs.size() + " canciones a la playlist");
            resultsTable.getSelectionModel().clearSelection();
        });

        downloadTask.setOnFailed(event -> {
            searchStatusLabel.textProperty().unbind();
            Throwable ex = downloadTask.getException();
            searchProgress.setVisible(false);
            disableSearchControls(false);

            String errorMsg = "Error al descargar: " + ex.getMessage();
            searchStatusLabel.setText("❌ " + errorMsg);
            showError(errorMsg);
            
            System.err.println("[SearchController] ❌ Error en descarga: " + errorMsg);
            ex.printStackTrace();
        });

        Thread t = new Thread(downloadTask, "download-task");
        t.setDaemon(true);
        t.start();
    }

    // ===== CONFIGURACIÓN DE TABLA =====

    private void disableSearchControls(boolean disable) {
        btnSearch.setDisable(disable);
        btnClear.setDisable(disable);
        btnAddSelected.setDisable(disable);
        btnSelectAll.setDisable(disable);
        resultsTable.setDisable(disable);
        searchField.setDisable(disable);
    }

    private void setupTableColumns() {
        System.out.println("[SearchController] 🔧 Configurando columnas...");
        
        // CRÍTICO: Configurar cellValueFactory
        titleColumn.setCellValueFactory(cellData -> {
            String titulo = cellData.getValue().getTitulo();
            return new javafx.beans.property.SimpleStringProperty(titulo);
        });

        artistColumn.setCellValueFactory(cellData -> {
            String artista = cellData.getValue().getArtista();
            return new javafx.beans.property.SimpleStringProperty(artista);
        });

        albumColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty("YouTube");
        });

        durationColumn.setCellValueFactory(cellData -> {
            String duracion = cellData.getValue().getFormattedDuration();
            return new javafx.beans.property.SimpleStringProperty(duracion);
        });

        // Permitir selección múltiple
        resultsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        System.out.println("[SearchController] ✅ Columnas configuradas correctamente");
    }

    private void setupTableData() {
        System.out.println("[SearchController] 🔧 Vinculando datos a tabla...");
        
        // CRÍTICO: Vincular ObservableList a la tabla
        resultsTable.setItems(searchResults);
        
        System.out.println("[SearchController] ✅ Datos vinculados: searchResults.size() = " + searchResults.size());

        // Doble clic para descargar solo esa canción
        resultsTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                SearchResult selected = resultsTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    System.out.println("[SearchController] 🖱️ Doble click en: " + selected.getTitulo());
                    downloadSelectedResults(List.of(selected));
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
                    System.out.println("[SearchController] ✅ API conectada");
                } else {
                    connectionStatusLabel.setText("🔴 Desconectado");
                    connectionStatusLabel.setStyle("-fx-text-fill: #f44336;");
                    System.out.println("[SearchController] ❌ API desconectada");
                }
            });
        });
        
        connectionTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                connectionStatusLabel.setText("🔴 Error de conexión");
                connectionStatusLabel.setStyle("-fx-text-fill: #f44336;");
                System.err.println("[SearchController] ❌ Error al conectar con API");
            });
        });
        
        new Thread(connectionTask).start();
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
}
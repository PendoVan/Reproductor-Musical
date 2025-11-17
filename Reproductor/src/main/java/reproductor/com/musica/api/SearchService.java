package reproductor.com.musica.api;

import javafx.concurrent.Task;
import reproductor.com.musica.api.dto.DownloadResponse;
import reproductor.com.musica.model.Song;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de alto nivel para búsqueda y descarga de música.
 * Coordina entre ApiClient y el modelo de dominio Song.
 * Sigue el patrón Service Layer y proporciona operaciones asíncronas.
 */
public class SearchService {
    
    private static final int POLL_INTERVAL_MS = 1000;
    private static final int MAX_RETRIES = 30;
    
    private final ApiClient apiClient;
    
    public SearchService() {
        this.apiClient = new ApiClient();
    }
    
    public SearchService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }
    
    /**
     * Busca y descarga canciones de forma asíncrona.
     * 
     * @param searchTerms Lista de términos de búsqueda
     * @return Task que se puede vincular a la UI
     */
    public Task<List<Song>> searchAndDownloadAsync(List<String> searchTerms) {
        return new Task<>() {
            @Override
            protected List<Song> call() throws Exception {
                return SearchService.this.searchAndDownload(searchTerms, this::updateProgress, 
                        this::updateMessage);
            }
        };
    }
    
    /**
     * Busca y descarga canciones (versión síncrona).
     * 
     * @param searchTerms Lista de términos de búsqueda
     * @param progressCallback Callback para actualizar progreso
     * @param messageCallback Callback para actualizar mensajes
     * @return Lista de canciones descargadas
     * @throws ApiException si hay error en la API
     */
    public List<Song> searchAndDownload(
            List<String> searchTerms,
            ProgressCallback progressCallback,
            MessageCallback messageCallback) throws ApiException {
        
        validateSearchTerms(searchTerms);
        
        messageCallback.update("Iniciando descarga de " + searchTerms.size() + " canciones...");
        progressCallback.update(0, searchTerms.size());
        
        // Paso 1: Solicitar descargas a la API
        DownloadResponse response = apiClient.requestDownloads(searchTerms);
        
        // Paso 2: Procesar resultados
        List<Song> downloadedSongs = new ArrayList<>();
        int processed = 0;
        
        for (DownloadResponse.DownloadResult result : response.getResults()) {
            progressCallback.update(++processed, searchTerms.size());
            
            if (result.isSuccess()) {
                messageCallback.update("Procesando: " + result.getName());
                
                try {
                    Song song = processDownloadResult(result);
                    if (song != null) {
                        downloadedSongs.add(song);
                        messageCallback.update("✓ Descargado: " + result.getName());
                    }
                } catch (Exception e) {
                    messageCallback.update("✗ Error procesando: " + result.getName());
                }
                
            } else {
                messageCallback.update("✗ " + result.getName() + 
                        " - " + result.getStatus());
            }
        }
        
        messageCallback.update("Completado: " + downloadedSongs.size() + 
                " de " + searchTerms.size() + " canciones");
        
        return downloadedSongs;
    }
    
    /**
     * Carga canciones desde el directorio local.
     * 
     * @return Lista de canciones encontradas
     */
    public List<Song> loadLocalSongs() {
        try {
            Path downloadPath = apiClient.getDownloadDirectory();
            
            if (!Files.exists(downloadPath)) {
                return new ArrayList<>();
            }
            
            return Files.list(downloadPath)
                    .filter(path -> path.toString().endsWith(".mp3"))
                    .map(this::createSongFromPath)
                    .collect(Collectors.toList());
                    
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Lista archivos disponibles en el servidor de forma asíncrona.
     * 
     * @return Task con lista de nombres de archivos
     */
    public Task<List<String>> listServerFilesAsync() {
        return new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                updateMessage("Consultando servidor...");
                return apiClient.listAvailableDownloads();
            }
        };
    }
    
    /**
     * Elimina una canción tanto del servidor como localmente.
     * 
     * @param song Canción a eliminar
     * @return Task que ejecuta la eliminación
     */
    public Task<Boolean> deleteSongAsync(Song song) {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return deleteSong(song);
            }
        };
    }
    
    /**
     * Verifica la disponibilidad de la API.
     * 
     * @return Task que verifica la conexión
     */
    public Task<Boolean> checkApiConnectionAsync() {
        return new Task<>() {
            @Override
            protected Boolean call() {
                updateMessage("Verificando conexión...");
                boolean available = apiClient.isApiAvailable();
                updateMessage(available ? "Conectado" : "Sin conexión");
                return available;
            }
        };
    }
    
    // ===== MÉTODOS PRIVADOS =====
    
    private Song processDownloadResult(DownloadResponse.DownloadResult result) 
            throws ApiException, InterruptedException {
        
        // Esperar a que el archivo esté disponible en el servidor
        String fileName = findMatchingFile(result.getName());
        if (fileName == null) {
            throw new ApiException("Archivo no encontrado en servidor");
        }
        
        // Descargar archivo al directorio local
        Path localPath = apiClient.downloadFile(fileName);
        
        // Crear objeto Song
        return createSongFromPath(localPath);
    }
    
    private String findMatchingFile(String searchTerm) 
            throws ApiException, InterruptedException {
        
        // Polling: esperar hasta que el archivo aparezca en el servidor
        for (int i = 0; i < MAX_RETRIES; i++) {
            List<String> files = apiClient.listAvailableDownloads();
            
            for (String file : files) {
                if (file.contains(searchTerm) || searchTerm.contains(file)) {
                    return file;
                }
            }
            
            Thread.sleep(POLL_INTERVAL_MS);
        }
        
        return null;
    }
    
    private Song createSongFromPath(Path filePath) {
        String fileName = filePath.getFileName().toString();
        String nameWithoutExt = fileName.replace(".mp3", "");
        
        Song song = new Song();
        
        // Intentar extraer artista y título
        if (nameWithoutExt.contains("-")) {
            String[] parts = nameWithoutExt.split("-", 2);
            song.setArtist(parts[0].trim());
            song.setTitle(parts[1].trim());
        } else {
            song.setTitle(nameWithoutExt);
            song.setArtist("Desconocido");
        }
        
        song.setFilePath(filePath);
        
        // Estimar duración basándose en tamaño
        try {
            long bytes = Files.size(filePath);
            song.setDurationSeconds(estimateDurationSeconds(bytes));
        } catch (IOException e) {
            song.setDurationSeconds(0);
        }
        
        return song;
    }
    
    private boolean deleteSong(Song song) throws ApiException, IOException {
        if (song == null || song.getFilePath() == null) {
            return false;
        }
        
        Path localPath = song.getFilePath();
        String fileName = localPath.getFileName().toString();
        
        // Eliminar del servidor
        boolean serverDeleted = apiClient.deleteFile(fileName);
        
        // Eliminar archivo local
        if (Files.exists(localPath)) {
            Files.delete(localPath);
        }
        
        return serverDeleted;
    }
    
    private int estimateDurationSeconds(long bytes) {
        // MP3 a 192kbps ≈ 1.44 MB por minuto
        double mb = bytes / (1024.0 * 1024.0);
        return (int) ((mb / 1.44) * 60);
    }
    
    private void validateSearchTerms(List<String> searchTerms) throws ApiException {
        if (searchTerms == null || searchTerms.isEmpty()) {
            throw new ApiException("La lista de búsqueda no puede estar vacía");
        }
    }
    
    // ===== GETTERS =====
    
    public ApiClient getApiClient() {
        return apiClient;
    }
    
    // ===== INTERFACES PARA CALLBACKS =====
    
    @FunctionalInterface
    public interface ProgressCallback {
        void update(long workDone, long max);
    }
    
    @FunctionalInterface
    public interface MessageCallback {
        void update(String message);
    }
}
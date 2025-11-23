package reproductor.com.musica.api;

import javafx.concurrent.Task;
import reproductor.com.musica.api.dto.DownloadResponse;
import reproductor.com.musica.api.dto.SearchResponse;
import reproductor.com.musica.api.dto.SearchResult;
import reproductor.com.musica.model.Song;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class SearchService {

    private final ApiClient apiClient;

    public SearchService() {
        this.apiClient = new ApiClient();
    }

    public SearchService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }


    /**
     * Busca canciones en YouTube SIN descargarlas.
     * 
     * @param query Término de búsqueda
     * @return Task con lista de resultados
     */
    public Task<List<SearchResult>> searchOnlyAsync(String query) {
        return new Task<>() {
            @Override
            protected List<SearchResult> call() throws Exception {
                updateMessage("Buscando: " + query);
                
                SearchResponse response = apiClient.search(query, 10);
                
                updateMessage("Encontrados: " + response.getCount() + " resultados");
                
                return response.getResultados();
            }
        };
    }
    
    /**
     * Descarga canciones seleccionadas por el usuario.
     * 
     * @param searchResults Lista de SearchResult seleccionados
     * @return Task con lista de Songs descargados
     */
    public Task<List<Song>> downloadSelectedAsync(List<SearchResult> searchResults) {
        return new Task<>() {
            @Override
            protected List<Song> call() throws Exception {
                return SearchService.this.downloadSelected(
                        searchResults,
                        this::updateProgress,
                        this::updateMessage
                );
            }
        };
    }
    
   
    public List<Song> downloadSelected(
            List<SearchResult> searchResults,
            ProgressCallback progressCallback,
            MessageCallback messageCallback) throws ApiException {
        
        if (searchResults == null || searchResults.isEmpty()) {
            throw new ApiException("No hay resultados seleccionados");
        }
        
        messageCallback.update("Descargando " + searchResults.size() + " canciones...");
        progressCallback.update(0, searchResults.size());
        
        List<String> videoIds = searchResults.stream()
                .map(SearchResult::getVideoId)
                .collect(Collectors.toList());
        
        DownloadResponse response = apiClient.downloadByVideoIds(videoIds);
        
        List<Song> downloadedSongs = new ArrayList<>();
        int processed = 0;
        
        for (DownloadResponse.DownloadResult result : response.getResults()) {
            progressCallback.update(++processed, searchResults.size());
            
            if (!result.isSuccess()) {
                messageCallback.update("✗ Error: " + result.getName());
                continue;
            }
            
            String fileName = result.getFileName();
            if (fileName == null || fileName.isBlank()) {
                messageCallback.update("✗ Sin archivo para: " + result.getName());
                continue;
            }
            
            messageCallback.update("Descargando: " + fileName);
            
            try {
                Path localPath = apiClient.downloadFile(fileName);
                Song song = createSongFromPath(localPath);
                
                if (song != null) {
                    SearchResult original = findOriginal(searchResults, result.getName());
                    if (original != null) {
                        song.setArtist(original.getArtista());
                        song.setTitle(original.getTitulo());
                        song.setDurationSeconds(original.getDuracion());
                    }
                    
                    downloadedSongs.add(song);
                    messageCallback.update("✓ Descargado: " + song.getTitle());
                }
            } catch (Exception e) {
                messageCallback.update("✗ Error procesando: " + fileName);
                e.printStackTrace();
            }
        }
        
        messageCallback.update("Completado: " + downloadedSongs.size() + 
                " de " + searchResults.size() + " canciones");
        
        return downloadedSongs;
    }


    public Task<List<Song>> searchAndDownloadAsync(List<String> searchTerms) {
        return new Task<>() {
            @Override
            protected List<Song> call() throws Exception {
                return SearchService.this.searchAndDownload(
                        searchTerms,
                        this::updateProgress,
                        this::updateMessage
                );
            }
        };
    }

    public List<Song> searchAndDownload(
            List<String> searchTerms,
            ProgressCallback progressCallback,
            MessageCallback messageCallback) throws ApiException {

        validateSearchTerms(searchTerms);

        messageCallback.update("Iniciando descarga de " + searchTerms.size() + " canciones...");
        progressCallback.update(0, searchTerms.size());

        DownloadResponse response = apiClient.requestDownloads(searchTerms);

        List<Song> downloadedSongs = new ArrayList<>();
        int processed = 0;

        for (DownloadResponse.DownloadResult result : response.getResults()) {
            progressCallback.update(++processed, searchTerms.size());

            if (!result.isSuccess()) {
                messageCallback.update("✗ " + result.getName() + " - " + result.getStatus());
                continue;
            }

            String fileName = result.getFileName();
            
            if (fileName == null || fileName.isBlank()) {
                messageCallback.update("✗ Respuesta sin archivo para: " + result.getName());
                continue;
            }

            messageCallback.update("Procesando: " + fileName);

            try {
                Path localPath = apiClient.downloadFile(fileName);
                Song song = createSongFromPath(localPath);
                
                if (song != null) {
                    downloadedSongs.add(song);
                    messageCallback.update("✓ Descargado: " + song.getTitle());
                }
            } catch (Exception e) {
                messageCallback.update("✗ Error procesando: " + fileName);
                e.printStackTrace();
            }
        }

        messageCallback.update("Completado: " + downloadedSongs.size() +
                " de " + searchTerms.size() + " canciones");

        return downloadedSongs;
    }


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

    public Task<List<String>> listServerFilesAsync() {
        return new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                updateMessage("Consultando servidor...");
                return apiClient.listAvailableDownloads();
            }
        };
    }

    public Task<Boolean> deleteSongAsync(Song song) {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return deleteSong(song);
            }
        };
    }

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

    private Song createSongFromPath(Path filePath) {
        if (filePath == null) return null;

        String fileName = filePath.getFileName().toString();
        String nameWithoutExt = fileName.replace(".mp3", "");

        Song song = new Song(nameWithoutExt, filePath.toString(), 0);

        if (nameWithoutExt.contains("-")) {
            String[] parts = nameWithoutExt.split("-", 2);
            song.setArtist(parts[0].trim());
            song.setTitle(parts[1].trim());
        } else {
            song.setTitle(nameWithoutExt);
            song.setArtist("Desconocido");
        }

        try {
            long bytes = Files.size(filePath);
            song.setDurationSeconds(estimateDurationSeconds(bytes));
        } catch (IOException e) {
            song.setDurationSeconds(0);
        }

        return song;
    }
    
    private SearchResult findOriginal(List<SearchResult> results, String videoId) {
        return results.stream()
                .filter(r -> r.getVideoId().equals(videoId))
                .findFirst()
                .orElse(null);
    }

    private boolean deleteSong(Song song) throws ApiException, IOException {
        if (song == null || song.getFilePath() == null) {
            return false;
        }

        Path localPath = song.getFilePath();
        String fileName = localPath.getFileName().toString();

        boolean serverDeleted = apiClient.deleteFile(fileName);

        if (Files.exists(localPath)) {
            Files.delete(localPath);
        }

        return serverDeleted;
    }

    private int estimateDurationSeconds(long bytes) {
        double mb = bytes / (1024.0 * 1024.0);
        return (int) ((mb / 1.44) * 60);
    }

    private void validateSearchTerms(List<String> searchTerms) throws ApiException {
        if (searchTerms == null || searchTerms.isEmpty()) {
            throw new ApiException("La lista de búsqueda no puede estar vacía");
        }
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    @FunctionalInterface
    public interface ProgressCallback {
        void update(long workDone, long max);
    }

    @FunctionalInterface
    public interface MessageCallback {
        void update(String message);
    }
}
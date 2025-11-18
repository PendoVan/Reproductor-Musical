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
                return SearchService.this.searchAndDownload(
                        searchTerms,
                        this::updateProgress,
                        this::updateMessage
                );
            }
        };
    }

    /**
     * Busca y descarga canciones (versión síncrona).
     *
     * @param searchTerms      Lista de términos de búsqueda
     * @param progressCallback Callback para actualizar progreso
     * @param messageCallback  Callback para actualizar mensajes
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

        // Paso 1: solicitar descargas a la API
        DownloadResponse response = apiClient.requestDownloads(searchTerms);

        // Paso 2: procesar resultados
        List<Song> downloadedSongs = new ArrayList<>();
        int processed = 0;

        for (DownloadResponse.DownloadResult result : response.getResults()) {
            progressCallback.update(++processed, searchTerms.size());

            if (!result.isSuccess()) {
                messageCallback.update("✗ " + result.getName() + " - " + result.getStatus());
                continue;
            }

            // Usar SIEMPRE el nombre de archivo que viene de la API
            String fileName = result.getName();
            if (fileName == null || fileName.isBlank()) {
                messageCallback.update("✗ Respuesta sin archivo para: " + result.getName());
                continue;
            }

            messageCallback.update("Procesando: " + result.getName());

            try {
                // Descargar el archivo al directorio local configurado en ApiClient
                Path localPath = apiClient.downloadFile(fileName);

                // Crear Song a partir del MP3 local
                Song song = createSongFromPath(localPath);
                if (song != null) {
                    downloadedSongs.add(song);
                    messageCallback.update("✓ Descargado: " + song.getTitle());
                }
            } catch (Exception e) {
                messageCallback.update("✗ Error procesando: " + result.getName());
                e.printStackTrace();
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

    private Song createSongFromPath(Path filePath) {
        if (filePath == null) return null;

        String fileName = filePath.getFileName().toString();
        String nameWithoutExt = fileName.replace(".mp3", "");

        Song song = new Song(nameWithoutExt, nameWithoutExt, filePath);

        // Intentar extraer artista y título si viene en formato "Artista - Título"
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

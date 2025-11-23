package reproductor.com.musica.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import reproductor.com.musica.api.dto.DownloadRequest;
import reproductor.com.musica.api.dto.DownloadResponse;
import reproductor.com.musica.api.dto.SearchResponse;
import reproductor.com.musica.util.Config;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Cliente HTTP para comunicarse con la API de descarga de música.
 */
public class ApiClient {
    
    private static final String ENDPOINT_SEARCH = "/buscar";
    private static final String ENDPOINT_DOWNLOAD = "/descargar";
    private static final String ENDPOINT_DOWNLOAD_BY_ID = "/descargar_por_id";
    private static final String ENDPOINT_LIST = "/descargas";
    private static final int TIMEOUT_SECONDS = 300;
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final Path downloadDirectory;
    
    public ApiClient() {
        this(Config.API_BASE_URL, getDefaultDownloadPath());
    }
    
    public ApiClient(String baseUrl, Path downloadDirectory) {
    	this.httpClient = HttpClient.newBuilder()
    	        .version(Version.HTTP_1_1)
    	        .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
    	        .build();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = baseUrl;
        this.downloadDirectory = downloadDirectory;
        
        ensureDownloadDirectoryExists();
    }
    
    // ===== NUEVO: BÚSQUEDA SIN DESCARGA =====
    
    /**
     * Busca canciones en YouTube SIN descargarlas.
     * 
     * @param query Término de búsqueda
     * @param maxResults Máximo de resultados
     * @return Lista de resultados de búsqueda
     * @throws ApiException si hay error
     */
    public SearchResponse search(String query, int maxResults) throws ApiException {
        if (query == null || query.isBlank()) {
            throw new ApiException("El término de búsqueda no puede estar vacío");
        }
        
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String endpoint = ENDPOINT_SEARCH + "?q=" + encodedQuery + 
                            "&max_results=" + maxResults;
            
            HttpRequest request = buildGetRequest(endpoint);
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            return handleSearchResponse(response);
            
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Error en búsqueda: " + e.getMessage(), e);
        }
    }
    
    public SearchResponse search(String query) throws ApiException {
        return search(query, 10);
    }
    
    // ===== NUEVO: DESCARGA POR VIDEO_ID =====
    
    /**
     * Descarga canciones usando sus video_id de YouTube.
     * 
     * @param videoIds Lista de IDs de videos a descargar
     * @return Respuesta con estado de cada descarga
     * @throws ApiException si hay error
     */
    public DownloadResponse downloadByVideoIds(List<String> videoIds) throws ApiException {
        if (videoIds == null || videoIds.isEmpty()) {
            throw new ApiException("La lista de video IDs no puede estar vacía");
        }
        
        try {
            String jsonBody = objectMapper.writeValueAsString(
                    Map.of("video_ids", videoIds)
            );
            
            HttpRequest httpRequest = buildPostRequest(ENDPOINT_DOWNLOAD_BY_ID, jsonBody);
            HttpResponse<String> response = httpClient.send(httpRequest, 
                    HttpResponse.BodyHandlers.ofString());
            
            return handleDownloadResponse(response);
            
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Error al descargar por ID: " + e.getMessage(), e);
        }
    }
    
    // ===== MÉTODOS EXISTENTES =====
    
    public DownloadResponse requestDownloads(List<String> songNames) throws ApiException {
        validateSongNames(songNames);
        
        try {
            DownloadRequest request = new DownloadRequest(songNames);
            String jsonBody = objectMapper.writeValueAsString(request);
            
            HttpRequest httpRequest = buildPostRequest(ENDPOINT_DOWNLOAD, jsonBody);
            HttpResponse<String> response = httpClient.send(httpRequest, 
                    HttpResponse.BodyHandlers.ofString());
            
            System.out.println("➡ Enviando JSON:");
            System.out.println(jsonBody);
            System.out.println("➡ URL: " + (baseUrl + ENDPOINT_DOWNLOAD));

            
            return handleDownloadResponse(response);
            
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Error al solicitar descargas: " + e.getMessage(), e);
        }
    }
    
    public List<String> listAvailableDownloads() throws ApiException {
        try {
            HttpRequest request = buildGetRequest(ENDPOINT_LIST);
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            return handleListResponse(response);
            
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Error al listar descargas: " + e.getMessage(), e);
        }
    }
    
    public Path downloadFile(String fileName) throws ApiException {
        validateFileName(fileName);
        
        try {
        	String encodedName = encodeFileName(fileName);
            String endpoint = ENDPOINT_LIST + "/" + encodedName;
            
            HttpRequest request = buildGetRequest(endpoint);
            Path outputPath = downloadDirectory.resolve(fileName);
            
            HttpResponse<Path> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofFile(outputPath));
            
            return handleFileDownloadResponse(response, outputPath);
            
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Error al descargar archivo: " + e.getMessage(), e);
        }
    }
    
    public boolean deleteFile(String fileName) throws ApiException {
        validateFileName(fileName);
        
        try {
            String encodedName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
            String endpoint = ENDPOINT_LIST + "/" + encodedName;
            
            HttpRequest request = buildDeleteRequest(endpoint);
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            return response.statusCode() == 200;
            
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Error al eliminar archivo: " + e.getMessage(), e);
        }
    }
    
    public boolean isApiAvailable() {
        try {
            HttpRequest request = buildGetRequest(ENDPOINT_LIST);
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
    
    // ===== MÉTODOS PRIVADOS - BUILDERS =====
    
    private HttpRequest buildPostRequest(String endpoint, String body) {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
    }
    
    private HttpRequest buildGetRequest(String endpoint) {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .GET()
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
    }
    
    private HttpRequest buildDeleteRequest(String endpoint) {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .DELETE()
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
    }
    
    // ===== MÉTODOS PRIVADOS - HANDLERS =====
    
    private SearchResponse handleSearchResponse(HttpResponse<String> response) 
            throws ApiException {
        if (response.statusCode() != 200) {
            throw new ApiException("Error HTTP " + response.statusCode() + 
                    ": " + response.body());
        }
        
        try {
            return objectMapper.readValue(response.body(), SearchResponse.class);
        } catch (IOException e) {
            throw new ApiException("Error al parsear búsqueda: " + e.getMessage(), e);
        }
    }
    
    private DownloadResponse handleDownloadResponse(HttpResponse<String> response) 
            throws ApiException {
        if (response.statusCode() != 200) {
            throw new ApiException("Error HTTP " + response.statusCode() + 
                    ": " + response.body());
        }
        
        try {
            return objectMapper.readValue(response.body(), DownloadResponse.class);
        } catch (IOException e) {
            throw new ApiException("Error al parsear respuesta: " + e.getMessage(), e);
        }
    }
    
    private String encodeFileName(String name) {
        return name.replace(" ", "%20")
                   .replace("(", "%28")
                   .replace(")", "%29");
    }

    @SuppressWarnings("unchecked")
    private List<String> handleListResponse(HttpResponse<String> response) 
            throws ApiException {
        if (response.statusCode() != 200) {
            throw new ApiException("Error HTTP " + response.statusCode());
        }
        
        try {
            Map<String, List<String>> result = objectMapper.readValue(
                    response.body(), Map.class);
            return result.get("archivos");
        } catch (IOException e) {
            throw new ApiException("Error al parsear lista: " + e.getMessage(), e);
        }
    }
    
    private Path handleFileDownloadResponse(HttpResponse<Path> response, Path outputPath) 
            throws ApiException {
        if (response.statusCode() != 200) {
            throw new ApiException("Error HTTP " + response.statusCode());
        }
        return response.body();
    }
    
    // ===== MÉTODOS PRIVADOS - VALIDACIÓN =====
    
    private void validateSongNames(List<String> songNames) throws ApiException {
        if (songNames == null || songNames.isEmpty()) {
            throw new ApiException("La lista de canciones no puede estar vacía");
        }
    }
    
    private void validateFileName(String fileName) throws ApiException {
        if (fileName == null || fileName.isBlank()) {
            throw new ApiException("El nombre del archivo no puede estar vacío");
        }
    }
    
    private void ensureDownloadDirectoryExists() {
        try {
            if (!Files.exists(downloadDirectory)) {
                Files.createDirectories(downloadDirectory);
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    "No se pudo crear el directorio de descargas", e);
        }
    }
    
    // ===== GETTERS =====
    
    public Path getDownloadDirectory() {
        return downloadDirectory;
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }
    
    private static Path getDefaultDownloadPath() {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, "Music", "Reproductor");
    }
}
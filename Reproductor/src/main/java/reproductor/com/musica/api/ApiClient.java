package reproductor.com.musica.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import reproductor.com.musica.api.dto.DownloadRequest;
import reproductor.com.musica.api.dto.DownloadResponse;
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
    
    private static final String ENDPOINT_DOWNLOAD = "/descargar";
    private static final String ENDPOINT_LIST = "/descargas";
    private static final int TIMEOUT_SECONDS = 300;
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final Path downloadDirectory;
    
    /**
     * Constructor con configuración por defecto.
     */
    public ApiClient() {
        this(Config.API_BASE_URL, getDefaultDownloadPath());
    }
    
    /**
     * Constructor con inyección de dependencias para testing.
     * 
     * @param baseUrl URL base de la API
     * @param downloadDirectory Directorio de descargas local
     */
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
    
    /**
     * Solicita la descarga de canciones a la API.
     * 
     * @param songNames Lista de nombres de canciones a buscar
     * @return Respuesta con el estado de cada descarga
     * @throws ApiException si hay error en la comunicación
     */
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
    
    /**
     * Lista los archivos disponibles en el servidor.
     * 
     * @return Lista de nombres de archivos MP3
     * @throws ApiException si hay error en la comunicación
     */
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
    
    /**
     * Descarga un archivo específico del servidor al directorio local.
     * 
     * @param fileName Nombre del archivo a descargar
     * @return Path del archivo descargado
     * @throws ApiException si hay error en la descarga
     */
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
    
    /**
     * Elimina un archivo del servidor.
     * 
     * @param fileName Nombre del archivo a eliminar
     * @return true si se eliminó correctamente
     * @throws ApiException si hay error en la eliminación
     */
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
    
    /**
     * Verifica si la API está disponible.
     * 
     * @return true si la API responde correctamente
     */
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
    
    // ===== HELPERS ESTÁTICOS =====
    
    private static Path getDefaultDownloadPath() {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, "Music", "Reproductor");
    }
}
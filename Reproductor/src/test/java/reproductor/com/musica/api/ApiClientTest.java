package reproductor.com.musica.api;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import reproductor.com.musica.api.dto.DownloadResponse;

import static org.junit.jupiter.api.Assertions.*;

class ApiClientTest {

    private static HttpServer server;
    private static int port;
    private ApiClient apiClient;

    @BeforeAll
    static void startServer() throws Exception {
        server = HttpServer.create(new InetSocketAddress(0), 0); // 0 → puerto aleatorio
        port = server.getAddress().getPort();

        // Endpoint: GET /descargas
        server.createContext("/descargas", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                String json = "{\"archivos\":[\"a.mp3\",\"b.mp3\"]}";
                exchange.sendResponseHeaders(200, json.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(json.getBytes());
                }
            } else if ("POST".equals(exchange.getRequestMethod())) {
                // Respuesta simulada de descarga
                String json = "{\"estadoDescargas\":{\"Song A\":\"OK\"}}";
                exchange.sendResponseHeaders(200, json.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(json.getBytes());
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        });

        // Endpoint: GET /descargas/{archivo}
        server.createContext("/descargas/file.mp3", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                byte[] content = "MP3DATA".getBytes();
                exchange.sendResponseHeaders(200, content.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(content);
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        });

        server.start();
    }

    @AfterAll
    static void stopServer() {
        server.stop(0);
    }

    @BeforeEach
    void setup() {
        String baseUrl = "http://localhost:" + port;
        Path downloadDir = Path.of("target/test-downloads");
        apiClient = new ApiClient(baseUrl, downloadDir);
    }

    @Test
    void listAvailableDownloads_success() throws Exception {
        List<String> lista = apiClient.listAvailableDownloads();

        assertNotNull(lista);
        assertEquals(2, lista.size());
        assertEquals("a.mp3", lista.get(0));
    }
    
    @Test
    void downloadFile_success() throws Exception {
        Path file = apiClient.downloadFile("file.mp3");

        assertTrue(Files.exists(file));
        assertTrue(Files.size(file) > 0);
    }

    @Test
    void deleteFile_returnsFalseSinceNotImplementedOnServer() throws Exception {
        // Nuestro servidor no implementa DELETE → 405
        boolean result = apiClient.deleteFile("file.mp3");

        assertFalse(result);
    }

    @Test
    void requestDownloads_empty_throws() {
        assertThrows(ApiException.class, () -> apiClient.requestDownloads(List.of()));
    }

    @Test
    void downloadFile_blank_throws() {
        assertThrows(ApiException.class, () -> apiClient.downloadFile(""));
    }
}
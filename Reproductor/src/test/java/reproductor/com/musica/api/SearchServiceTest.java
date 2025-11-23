package reproductor.com.musica.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import reproductor.com.musica.api.dto.DownloadResponse;
import reproductor.com.musica.api.dto.SearchResult;
import reproductor.com.musica.model.Song;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void testDownloadSelectedThrowsExceptionOnEmptyList() {
        SearchService service = new SearchService();
        List<SearchResult> listaVacia = new ArrayList<>();

        ApiException exception = assertThrows(ApiException.class, () -> {
            service.downloadSelected(
                listaVacia, 
                (done, total) -> {}, 
                (msg) -> {}
            );
        });

        assertEquals("No hay resultados seleccionados", exception.getMessage());
    }

    @Test
    void testSearchAndDownloadThrowsExceptionOnNullList() {
        SearchService service = new SearchService();

        ApiException exception = assertThrows(ApiException.class, () -> {
            service.searchAndDownload(
                null, 
                (done, total) -> {}, 
                (msg) -> {}
            );
        });

        assertEquals("La lista de búsqueda no puede estar vacía", exception.getMessage());
    }
}
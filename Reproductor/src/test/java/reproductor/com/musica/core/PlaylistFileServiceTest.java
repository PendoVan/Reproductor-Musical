package reproductor.com.musica.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import reproductor.com.musica.model.Song;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlaylistFileServiceTest {

    @TempDir
    Path tempDir;

    private String originalUserDir;
    private PlaylistFileService service;

    @BeforeEach
    void setup() {
        originalUserDir = System.getProperty("user.dir");

        System.setProperty("user.dir", tempDir.toAbsolutePath().toString());

        service = new PlaylistFileService();
    }

    @AfterEach
    void tearDown() {
        if (originalUserDir != null) {
            System.setProperty("user.dir", originalUserDir);
        }
    }

    @Test
    void testDeletePlaylist() throws IOException {
        String nombre = "ParaBorrar";
        service.savePlaylist(nombre, List.of());

        assertTrue(service.playlistExists(nombre));

        boolean eliminado = service.deletePlaylist(nombre);

        assertTrue(eliminado);
        assertFalse(service.playlistExists(nombre));
    }

    @Test
    void testLoadNonExistentPlaylistThrows() {
        assertThrows(IOException.class, () -> {
            service.loadPlaylist("NoExiste");
        });
    }
    
    @Test
    void testListPlaylists() throws IOException {
        service.savePlaylist("Rock", List.of());
        service.savePlaylist("Pop", List.of());
        
        List<String> playlists = service.listPlaylists();
        
        assertTrue(playlists.contains("Rock"));
        assertTrue(playlists.contains("Pop"));
        assertTrue(playlists.size() >= 2);
    }
}
package reproductor.com.musica.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import reproductor.com.musica.model.PlaybackMode;
import reproductor.com.musica.model.Song;

class PlaylistServiceTest {

    private PlaylistService playlist;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    void setup() {
        playlist = new PlaylistService();
    }

    private Song song(String title, double duration) {
        return new Song(title, "/tmp/" + title, duration);
    }

    @Test
    void testAddSongUpdatesDuration() {
        Song s1 = song("A", 10);
        Song s2 = song("B", 20);

        playlist.addSong(s1);
        playlist.addSong(s2);

        assertEquals(2, playlist.getSongs().size());
        assertEquals(30, playlist.totalDurationProperty().get(), 0.001);
    }

    @Test
    void testAddSongs() {
        List<Song> list = List.of(song("A", 5), song("B", 15));

        playlist.addSongs(list);

        assertEquals(2, playlist.getSongs().size());
        assertEquals(20, playlist.totalDurationProperty().get(), 0.001);
    }

    @Test
    void testSetCurrentSong() {
        Song s1 = song("A", 3);
        Song s2 = song("B", 4);
        playlist.addSongs(List.of(s1, s2));

        playlist.setCurrentSong(s2);

        assertEquals(1, playlist.currentIndexProperty().get());
        assertEquals("B", playlist.getCurrentSong().getTitle());
    }

    @Test
    void testCurrentSongOrFirstWhenEmpty() {
        assertNull(playlist.getCurrentSongOrFirst());
    }

    @Test
    void testCurrentSongOrFirstAssignsFirst() {
        Song s = song("X", 8);
        playlist.addSong(s);

        assertEquals(s, playlist.getCurrentSongOrFirst());
        assertEquals(0, playlist.currentIndexProperty().get());
    }

    @Test
    void testNextNormalMode() {
        playlist.addSongs(List.of(
                song("A", 2),
                song("B", 2),
                song("C", 2)
        ));

        playlist.setCurrentSong(playlist.getSongs().get(0));

        Song next = playlist.getNextSong();
        assertEquals("B", next.getTitle());

        next = playlist.getNextSong();
        assertEquals("C", next.getTitle());

        assertNull(playlist.getNextSong());
    }

    @Test
    void testNextRepeatAll() {
        playlist.setPlaybackMode(PlaybackMode.REPEAT_ALL);
        playlist.addSongs(List.of(
                song("A", 2),
                song("B", 2)
        ));

        playlist.setCurrentSong(playlist.getSongs().get(1)); // B

        Song next = playlist.getNextSong(); // vuelve a A
        assertEquals("A", next.getTitle());
        assertEquals(0, playlist.currentIndexProperty().get());
    }

    @Test
    void testNextRepeatOne() {
        playlist.setPlaybackMode(PlaybackMode.REPEAT_ONE);
        playlist.addSongs(List.of(song("A", 2), song("B", 2)));

        playlist.setCurrentSong(playlist.getSongs().get(0));

        Song next = playlist.getNextSong();
        assertEquals("A", next.getTitle());
        assertEquals(0, playlist.currentIndexProperty().get());
    }

    @Test
    void testNextShuffle() {
        playlist.setPlaybackMode(PlaybackMode.SHUFFLE);
        playlist.addSongs(List.of(
                song("A", 2),
                song("B", 2),
                song("C", 2)
        ));

        playlist.setCurrentSong(playlist.getSongs().get(0));

        Song next = playlist.getNextSong();

        assertNotNull(next);
        assertTrue(List.of("A", "B", "C").contains(next.getTitle()));
    }


    @Test
    void testPreviousNormalMode() {
        playlist.addSongs(List.of(
                song("A", 2),
                song("B", 2),
                song("C", 2)
        ));

        playlist.setCurrentSong(playlist.getSongs().get(2));

        Song prev = playlist.getPreviousSong();
        assertEquals("B", prev.getTitle());

        prev = playlist.getPreviousSong();
        assertEquals("A", prev.getTitle());


        prev = playlist.getPreviousSong();
        assertEquals("A", prev.getTitle());
    }

    @Test
    void testPreviousShuffle() {
        playlist.setPlaybackMode(PlaybackMode.SHUFFLE);

        playlist.addSongs(List.of(
                song("A", 2),
                song("B", 2),
                song("C", 2)
        ));

        playlist.setCurrentSong(playlist.getSongs().get(1));

        Song prev = playlist.getPreviousSong();

        assertNotNull(prev);
        assertTrue(List.of("A", "B", "C").contains(prev.getTitle()));
    }



    @Test
    void testClearPlaylist() {
        playlist.addSongs(List.of(song("A", 2), song("B", 2)));
        playlist.setCurrentSong(playlist.getSongs().get(1));

        playlist.clearCurrentPlaylist();

        assertEquals(0, playlist.getSongs().size());
        assertEquals(-1, playlist.currentIndexProperty().get());
        assertEquals(0, playlist.totalDurationProperty().get(), 0.0001);
    }
    
    @Test
    void testSaveAndLoadPlaylist() throws IOException {
        // 1. Configurar el servicio para usar la carpeta temporal (Necesitarás modificar tu constructor 
        // o usar reflexión, pero para este ejemplo asumiremos que puedes setear la ruta)
        // NOTA: Como tu clase usa rutas fijas, lo ideal es refactorizar PlaylistFileService 
        // para que acepte la ruta en el constructor.
        
        // Simulación de la lógica:
        PlaylistFileService service = new PlaylistFileService(); 
        
        String nombreLista = "TestPlaylist";
        Song cancion = new Song("Titulo", "Artista", "/tmp/test.mp3");
        cancion.setDurationSeconds(120);
        
        service.savePlaylist(nombreLista, List.of(cancion));
        
        assertTrue(service.playlistExists(nombreLista));
        
        List<Song> cargadas = service.loadPlaylist(nombreLista);
        assertEquals(1, cargadas.size());
        assertEquals("Titulo", cargadas.get(0).getTitle());
        assertEquals(120, cargadas.get(0).getDurationSeconds());
    }
    
    @Test
    void testDeletePlaylist() throws IOException {
        PlaylistFileService service = new PlaylistFileService();
        String nombre = "ParaBorrar";
        service.savePlaylist(nombre, List.of());
        
        assertTrue(service.playlistExists(nombre));
        
        service.deletePlaylist(nombre);
        
        assertFalse(service.playlistExists(nombre));
    }
    
    @Test
    void testRemoveSongWhenEmptyDoesNotThrow() {
        Song s = new Song("A", "A", "path");
        assertDoesNotThrow(() -> playlist.removeSong(s));
    }

    @Test
    void testNextSongAtEndOfListNormalMode() {
        Song s1 = new Song("1", "1", "p1");
        Song s2 = new Song("2", "2", "p2");
        playlist.addSongs(java.util.List.of(s1, s2));
        
        playlist.setCurrentSong(s2);
        
        assertNull(playlist.getNextSong());
        
        assertEquals(1, playlist.currentIndexProperty().get());
    }
}
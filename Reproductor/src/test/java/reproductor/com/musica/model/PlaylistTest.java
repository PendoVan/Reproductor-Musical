package reproductor.com.musica.model;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PlaylistTest {

    @BeforeAll
    static void initToolkit() {
        // Necesario para que JavaFX inicialice el toolkit de ObservableList
        try {
            javafx.application.Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // JavaFX ya estaba inicializado, no pasa nada
        }
    }

    @Test
    void testDefaultConstructor() {
        Playlist playlist = new Playlist();

        assertEquals("", playlist.getName());
        assertNotNull(playlist.getSongs());
        assertTrue(playlist.getSongs().isEmpty());
    }

    @Test
    void testConstructorWithName() {
        Playlist playlist = new Playlist("Mis canciones");

        assertEquals("Mis canciones", playlist.getName());
        assertTrue(playlist.getSongs().isEmpty());
    }

    @Test
    void testConstructorWithInitialSongs() {
        Song s1 = new Song("A", "X", Paths.get("a.mp3"));
        Song s2 = new Song("B", "Y", Paths.get("b.mp3"));
        List<Song> initial = Arrays.asList(s1, s2);

        Playlist playlist = new Playlist("Lista", initial);

        assertEquals("Lista", playlist.getName());
        assertEquals(2, playlist.getSongs().size());
        assertTrue(playlist.getSongs().containsAll(initial));
    }

    @Test
    void testSetAndGetName() {
        Playlist playlist = new Playlist();
        playlist.setName("Nueva lista");

        assertEquals("Nueva lista", playlist.getName());
    }

    @Test
    void testGetNameNullSafe() {
        Playlist playlist = new Playlist();
        playlist.setName(null);

        assertEquals("", playlist.getName()); // comportamiento definido en getName()
    }

    @Test
    void testAddSong() {
        Playlist playlist = new Playlist();
        Song s = new Song("Test", "Autor", Paths.get("test.mp3"));

        playlist.addSong(s);

        assertEquals(1, playlist.getSongs().size());
        assertTrue(playlist.getSongs().contains(s));
    }

    @Test
    void testAddNullSongDoesNothing() {
        Playlist playlist = new Playlist();

        playlist.addSong(null);

        assertTrue(playlist.getSongs().isEmpty());
    }

    @Test
    void testAddDuplicateSongDoesNothing() {
        Playlist playlist = new Playlist();
        Song s = new Song("Test", "Autor", Paths.get("test.mp3"));

        playlist.addSong(s);
        playlist.addSong(s); // no debe duplicarse

        assertEquals(1, playlist.getSongs().size());
    }

    @Test
    void testRemoveSong() {
        Playlist playlist = new Playlist();
        Song s = new Song("Test", "Autor", Paths.get("test.mp3"));

        playlist.addSong(s);
        playlist.removeSong(s);

        assertFalse(playlist.getSongs().contains(s));
    }

    @Test
    void testRemoveSongNotInList() {
        Playlist playlist = new Playlist();
        Song s = new Song("NoExiste", "Autor", Paths.get("none.mp3"));

        assertDoesNotThrow(() -> playlist.removeSong(s));
        assertTrue(playlist.getSongs().isEmpty());
    }

    @Test
    void testClear() {
        Playlist playlist = new Playlist();
        playlist.addSong(new Song("A", "X", Paths.get("a.mp3")));
        playlist.addSong(new Song("B", "Y", Paths.get("b.mp3")));

        playlist.clear();

        assertTrue(playlist.getSongs().isEmpty());
    }

    @Test
    void testGetSongsReturnsObservableList() {
        Playlist playlist = new Playlist();
        ObservableList<Song> songs = playlist.getSongs();

        assertNotNull(songs);
        assertTrue(songs.isEmpty());
    }

    @Test
    void testToStringReturnsName() {
        Playlist playlist = new Playlist("MiLista");
        assertEquals("MiLista", playlist.toString());
    }

    @Test
    void testToStringWithNullName() {
        Playlist playlist = new Playlist();
        playlist.setName(null);

        assertEquals("", playlist.toString()); // porque getName() evita null
    }
}

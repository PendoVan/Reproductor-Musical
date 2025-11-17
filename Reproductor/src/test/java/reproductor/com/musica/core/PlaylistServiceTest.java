package reproductor.com.musica.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reproductor.com.musica.model.PlaybackMode;
import reproductor.com.musica.model.Song;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlaylistServiceTest {

    private PlaylistService playlist;

    @BeforeEach
    void setup() {
        playlist = new PlaylistService();
    }

    private Song song(String title, double duration) {
        return new Song(title, "/tmp/" + title, duration);
    }

    // ---------------------------------------------------------------
    // --------------------- TESTS BÁSICOS ---------------------------
    // ---------------------------------------------------------------

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

    // ---------------------------------------------------------------
    // ----------------------- TEST NEXT() ---------------------------
    // ---------------------------------------------------------------

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

        // Final → NORMAL devuelve null
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

    // ---------------------------------------------------------------
    // --------------------- TEST PREVIOUS() -------------------------
    // ---------------------------------------------------------------

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

        // no baja más de 0
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

    // ---------------------------------------------------------------
    // ----------------------- CLEAR --------------------------------
    // ---------------------------------------------------------------

    @Test
    void testClearPlaylist() {
        playlist.addSongs(List.of(song("A", 2), song("B", 2)));
        playlist.setCurrentSong(playlist.getSongs().get(1));

        playlist.clearCurrentPlaylist();

        assertEquals(0, playlist.getSongs().size());
        assertEquals(-1, playlist.currentIndexProperty().get());
        assertEquals(0, playlist.totalDurationProperty().get(), 0.0001);
    }
}
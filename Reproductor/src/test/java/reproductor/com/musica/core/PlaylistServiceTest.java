package reproductor.com.musica.core;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reproductor.com.musica.model.PlaybackMode;
import reproductor.com.musica.model.Song;

class PlaylistServiceTest {

    private PlaylistService playlist;

    @BeforeEach
    void setUp() {
        playlist = new PlaylistService();
    }

    // ------------------------------------------------------------
    // Inicialización
    // ------------------------------------------------------------

    @Test
    void testInitialState() {
        assertEquals(-1, playlist.getCurrentIndex());
        assertEquals(0, playlist.getSongs().size());
        assertEquals(PlaybackMode.NORMAL, playlist.getPlaybackMode());
    }

    // ------------------------------------------------------------
    // setCurrentIndex
    // ------------------------------------------------------------

    @Test
    void testSetCurrentIndex_Valid() {
        Song s = new Song("A", "", Path.of("A.mp3"));
        playlist.addSong(s);

        playlist.setCurrentIndex(0);
        assertEquals(0, playlist.getCurrentIndex());
    }

    @Test
    void testSetCurrentIndex_InvalidNegative() {
        playlist.setCurrentIndex(-5);
        assertEquals(-1, playlist.getCurrentIndex());
    }

    @Test
    void testSetCurrentIndex_OutOfBounds() {
        Song s = new Song("A", "", Path.of("A.mp3"));
        playlist.addSong(s);

        playlist.setCurrentIndex(99);
        assertEquals(-1, playlist.getCurrentIndex());
    }

    // ------------------------------------------------------------
    // addSong
    // ------------------------------------------------------------

    @Test
    void testAddSong_Normal() {
        Song s = new Song("A", "", Path.of("A.mp3"));
        playlist.addSong(s);

        assertEquals(1, playlist.getSongs().size());
        assertEquals(0, playlist.getCurrentIndex());
        assertEquals(s, playlist.getCurrentSong());
    }

    @Test
    void testAddSong_NullIgnored() {
        playlist.addSong(null);
        assertTrue(playlist.getSongs().isEmpty());
        assertEquals(-1, playlist.getCurrentIndex());
    }

    @Test
    void testAddSong_AfterFirstCurrentIndexRemains() {
        Song s1 = new Song("A", "", Path.of("A.mp3"));
        Song s2 = new Song("B", "", Path.of("B.mp3"));

        playlist.addSong(s1);
        playlist.addSong(s2);

        assertEquals(0, playlist.getCurrentIndex());
    }

    // ------------------------------------------------------------
    // add(Path)
    // ------------------------------------------------------------

    @Test
    void testAddPath() {
        Path p = Path.of("song.mp3");
        playlist.add(p);

        assertEquals(1, playlist.getSongs().size());
        assertEquals("song.mp3", playlist.getSongs().get(0).getTitle());
    }

    @Test
    void testAddPath_NullDoesNothing() {
        playlist.add(null);
        assertTrue(playlist.getSongs().isEmpty());
    }

    // ------------------------------------------------------------
    // addSongs(Collection)
    // ------------------------------------------------------------

    @Test
    void testAddSongs_Normal() {
        Song s1 = new Song("A", "", Path.of("A.mp3"));
        Song s2 = new Song("B", "", Path.of("B.mp3"));

        playlist.addSongs(List.of(s1, s2));

        assertEquals(2, playlist.getSongs().size());
    }

    @Test
    void testAddSongs_NullCollection() {
        playlist.addSongs(null);
        assertTrue(playlist.getSongs().isEmpty());
    }

    // ------------------------------------------------------------
    // removeSong
    // ------------------------------------------------------------

    @Test
    void testRemoveSong_Normal() {
        Song s1 = new Song("A", "", Path.of("A.mp3"));
        Song s2 = new Song("B", "", Path.of("B.mp3"));

        playlist.addSong(s1);
        playlist.addSong(s2);
        playlist.setCurrentIndex(1);

        playlist.removeSong(s2);

        assertEquals(1, playlist.getSongs().size());
        assertEquals(0, playlist.getCurrentIndex());
        assertEquals(s1, playlist.getCurrentSong());
    }

    @Test
    void testRemoveSong_NullDoesNothing() {
        Song s = new Song("A", "", Path.of("A.mp3"));
        playlist.addSong(s);
        playlist.removeSong(null);

        assertEquals(1, playlist.getSongs().size());
    }

    @Test
    void testRemoveSong_NotInListDoesNothing() {
        Song s1 = new Song("A", "", Path.of("A.mp3"));
        Song s2 = new Song("B", "", Path.of("B.mp3"));

        playlist.addSong(s1);
        playlist.removeSong(s2);

        assertEquals(1, playlist.getSongs().size());
    }

    @Test
    void testRemoveSong_LeavesListEmpty() {
        Song s1 = new Song("A", "", Path.of("A.mp3"));
        playlist.addSong(s1);

        playlist.removeSong(s1);

        assertTrue(playlist.getSongs().isEmpty());
        assertEquals(-1, playlist.getCurrentIndex());
    }

    @Test
    void testRemoveSong_BeforeCurrentMovesIndexDown() {
        Song s1 = new Song("A", "", Path.of("A.mp3"));
        Song s2 = new Song("B", "", Path.of("B.mp3"));
        Song s3 = new Song("C", "", Path.of("C.mp3"));

        playlist.addSongs(List.of(s1, s2, s3));
        playlist.setCurrentIndex(2);

        playlist.removeSong(s2);

        assertEquals(1, playlist.getCurrentIndex());
    }

    // ------------------------------------------------------------
    // clear
    // ------------------------------------------------------------

    @Test
    void testClear() {
        Song s1 = new Song("A", "", Path.of("A.mp3"));
        playlist.addSong(s1);

        playlist.clear();

        assertTrue(playlist.getSongs().isEmpty());
        assertEquals(-1, playlist.getCurrentIndex());
    }

    // ------------------------------------------------------------
    // loadFromPaths
    // ------------------------------------------------------------

    @Test
    void testLoadFromPaths_Normal() {
        Path p1 = Path.of("A.mp3");
        Path p2 = Path.of("B.mp3");

        playlist.loadFromPaths(List.of(p1, p2));

        assertEquals(2, playlist.getSongs().size());
        assertEquals(0, playlist.getCurrentIndex());
    }

    @Test
    void testLoadFromPaths_Null() {
        playlist.loadFromPaths(null);

        assertTrue(playlist.getSongs().isEmpty());
        assertEquals(-1, playlist.getCurrentIndex());
    }

    @Test
    void testLoadFromPaths_EmptyList() {
        playlist.loadFromPaths(List.of());

        assertTrue(playlist.getSongs().isEmpty());
        assertEquals(-1, playlist.getCurrentIndex());
    }

    // ------------------------------------------------------------
    // hasNext
    // ------------------------------------------------------------

    @Test
    void testHasNext_Normal() {
        playlist.addSongs(List.of(
                new Song("A", "", Path.of("A")), 
                new Song("B", "", Path.of("B"))
        ));

        playlist.setCurrentIndex(0);
        assertTrue(playlist.hasNext());

        playlist.setCurrentIndex(1);
        assertFalse(playlist.hasNext());
    }

    @Test
    void testHasNext_RepeatAll() {
        playlist.setPlaybackMode(PlaybackMode.REPEAT_ALL);
        playlist.addSong(new Song("A", "", Path.of("A")));

        playlist.setCurrentIndex(0);
        assertTrue(playlist.hasNext());
    }

    @Test
    void testHasNext_RepeatOne() {
        playlist.setPlaybackMode(PlaybackMode.REPEAT_ONE);
        playlist.addSong(new Song("A", "", Path.of("A")));

        assertTrue(playlist.hasNext());
    }

    @Test
    void testHasNext_Shuffle() {
        playlist.setPlaybackMode(PlaybackMode.SHUFFLE);
        playlist.addSongs(List.of(
                new Song("A", "", Path.of("A")), 
                new Song("B", "", Path.of("B"))
        ));

        assertTrue(playlist.hasNext());

        // Con solo 1 → false
        PlaylistService p2 = new PlaylistService();
        p2.setPlaybackMode(PlaybackMode.SHUFFLE);
        p2.addSong(new Song("A", "", Path.of("A")));
        assertFalse(p2.hasNext());
    }

    // ------------------------------------------------------------
    // hasPrevious
    // ------------------------------------------------------------

    @Test
    void testHasPrevious_Normal() {
        playlist.addSongs(List.of(
                new Song("A", "", Path.of("A")), 
                new Song("B", "", Path.of("B"))
        ));

        playlist.setCurrentIndex(1);
        assertTrue(playlist.hasPrevious());

        playlist.setCurrentIndex(0);
        assertFalse(playlist.hasPrevious());
    }

    @Test
    void testHasPrevious_RepeatOne() {
        playlist.setPlaybackMode(PlaybackMode.REPEAT_ONE);
        playlist.addSong(new Song("A", "", Path.of("A")));

        assertTrue(playlist.hasPrevious());
    }

    @Test
    void testHasPrevious_RepeatAll() {
        playlist.setPlaybackMode(PlaybackMode.REPEAT_ALL);
        playlist.addSong(new Song("A", "", Path.of("A")));

        assertTrue(playlist.hasPrevious());
    }

    @Test
    void testHasPrevious_Shuffle() {
        playlist.setPlaybackMode(PlaybackMode.SHUFFLE);
        playlist.addSongs(List.of(
                new Song("A", "", Path.of("A")), 
                new Song("B", "", Path.of("B"))
        ));

        assertTrue(playlist.hasPrevious());
    }

    // ------------------------------------------------------------
    // next()
    // ------------------------------------------------------------

    @Test
    void testNext_Normal() {
        playlist.addSongs(List.of(
                new Song("A", "", Path.of("A")), 
                new Song("B", "", Path.of("B"))
        ));

        playlist.setCurrentIndex(0);
        Song s2 = playlist.next();
        assertEquals("B", s2.getTitle());

        // final → no avanza más
        Song sLast = playlist.next();
        assertEquals("B", sLast.getTitle());
    }

    @Test
    void testNext_RepeatAll() {
        playlist.setPlaybackMode(PlaybackMode.REPEAT_ALL);
        playlist.addSongs(List.of(
                new Song("A", "", Path.of("A")), 
                new Song("B", "", Path.of("B"))
        ));

        playlist.setCurrentIndex(1);
        Song s = playlist.next();
        assertEquals("A", s.getTitle());
    }

    @Test
    void testNext_RepeatOne() {
        playlist.setPlaybackMode(PlaybackMode.REPEAT_ONE);
        playlist.addSong(new Song("A", "", Path.of("A")));

        playlist.setCurrentIndex(0);
        Song s = playlist.next();
        assertEquals("A", s.getTitle());
    }

    @Test
    void testNext_EmptyListReturnsNull() {
        assertNull(playlist.next());
    }

    // ------------------------------------------------------------
    // prev()
    // ------------------------------------------------------------

    @Test
    void testPrev_Normal() {
        playlist.addSongs(List.of(
                new Song("A", "", Path.of("A")), 
                new Song("B", "", Path.of("B"))
        ));

        playlist.setCurrentIndex(1);
        Song s = playlist.prev();
        assertEquals("A", s.getTitle());

        playlist.setCurrentIndex(0);
        Song s2 = playlist.prev();
        assertEquals("A", s2.getTitle());
    }

    @Test
    void testPrev_RepeatAll() {
        playlist.setPlaybackMode(PlaybackMode.REPEAT_ALL);
        playlist.addSongs(List.of(
                new Song("A", "", Path.of("A")), 
                new Song("B", "", Path.of("B"))
        ));

        playlist.setCurrentIndex(0);
        Song s = playlist.prev();
        assertEquals("B", s.getTitle());
    }

    @Test
    void testPrev_RepeatOne() {
        playlist.setPlaybackMode(PlaybackMode.REPEAT_ONE);
        playlist.addSong(new Song("A", "", Path.of("A")));

        Song s = playlist.prev();
        assertEquals("A", s.getTitle());
    }

    @Test
    void testPrev_EmptyListReturnsNull() {
        assertNull(playlist.prev());
    }
}

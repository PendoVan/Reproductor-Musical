package reproductor.com.musica.model;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class SongTest {

    @Test
    void testIsLocalWhenFilePathIsSet() {
        Song song = new Song("Title", "Artist", Path.of("music.mp3"));
        assertTrue(song.isLocal());
        assertFalse(song.isRemote());
    }

    @Test
    void testIsRemoteWhenStreamUrlIsSet() {
        Song song = new Song("Title", "Artist", "http://example.com/stream");
        assertTrue(song.isRemote());
        assertFalse(song.isLocal());
    }

    @Test
    void testIsRemoteWhenStreamUrlIsBlank() {
        Song song = new Song();
        song.setStreamUrl("   ");
        assertFalse(song.isRemote());
    }

    @Test
    void testToStringWithArtist() {
        Song song = new Song("Imagine", "John Lennon", (Path) null);
        assertEquals("Imagine - John Lennon", song.toString());
    }

    @Test
    void testToStringWithoutArtist() {
        Song song = new Song("Imagine", null, (Path) null);
        assertEquals("Imagine", song.toString());
    }

    @Test
    void testEqualsWhenIdsArePresent() {
        Song s1 = new Song();
        Song s2 = new Song();
        s1.setId(10);
        s2.setId(10);

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    void testEqualsWithoutIds() {
        Song s1 = new Song("A", "B", Path.of("x.mp3"));
        Song s2 = new Song("A", "B", Path.of("x.mp3"));

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    void testNotEqualsDifferentAttributes() {
        Song s1 = new Song("A", "B", Path.of("x.mp3"));
        Song s2 = new Song("A", "B", Path.of("y.mp3"));

        assertNotEquals(s1, s2);
    }

    @Test
    void testEqualsWithDifferentIds() {
        Song s1 = new Song();
        Song s2 = new Song();
        s1.setId(1);
        s2.setId(2);

        assertNotEquals(s1, s2);
    }

    @Test
    void testGetTitleReturnsEmptyWhenNull() {
        Song s = new Song();
        s.setTitle(null);
        assertEquals("", s.getTitle());
    }

    @Test
    void testGetArtistReturnsEmptyWhenNull() {
        Song s = new Song();
        s.setArtist(null);
        assertEquals("", s.getArtist());
    }
}

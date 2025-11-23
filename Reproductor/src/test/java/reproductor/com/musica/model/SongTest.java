package reproductor.com.musica.model;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SongTest {

    @Test
    void testConstructorWithPath() {
        Path p = Path.of("/music/test.mp3");
        Song s = new Song("Titulo", "Artista", p);

        assertEquals("Titulo", s.getTitle());
        assertEquals("Artista", s.getArtist());
        assertEquals(p, s.getFilePath());
        assertNull(s.getStreamUrl());
        assertTrue(s.isLocal());
        assertFalse(s.isRemote());
    }


    @Test
    void testConstructorWithStreamUrl() {
        Song s = new Song("Song", "Artist", "http://example.com/stream");

        assertEquals("Song", s.getTitle());
        assertEquals("Artist", s.getArtist());
        assertNull(s.getFilePath());
        assertEquals("http://example.com/stream", s.getStreamUrl());
        assertTrue(s.isRemote());
        assertFalse(s.isLocal());
    }


    @Test
    void testConstructorSimple() {
        Song s = new Song("Titulo", "/tmp/a.mp3", 12.7);

        assertEquals("Titulo", s.getTitle());
        assertEquals("Desconocido", s.getArtist());
        assertEquals(Path.of("/tmp/a.mp3"), s.getFilePath());
        assertEquals(12, s.getDurationSeconds());
        assertTrue(s.isLocal());
        assertFalse(s.isRemote());
    }


    @Test
    void testConstructorSimpleWithArtistExtraction() {
        Song s = new Song("Metallica - Nothing Else Matters", "/tmp/m.mp3", 300);

        assertEquals("Nothing Else Matters", s.getTitle());
        assertEquals("Metallica", s.getArtist());
    }


    @Test
    void testToStringWithArtist() {
        Song s = new Song("Artist - Track", "/tmp/x.mp3", 50);

        assertEquals("Artist - Track", s.toString());
    }

    @Test
    void testToStringWithoutArtist() {
        Song s = new Song("Titulo", "/tmp/y.mp3", 50);

        assertEquals("Titulo", s.toString());
    }


    @Test
    void testLocalVsRemoteFlags() {
        Song local = new Song("A", "/tmp/a.mp3", 20);
        Song remote = new Song("B", "Artist", "https://example.com/stream");

        assertTrue(local.isLocal());
        assertFalse(local.isRemote());

        assertTrue(remote.isRemote());
        assertFalse(remote.isLocal());
    }


    @Test
    void testEqualsWithoutId() {
        Song s1 = new Song("A", "/tmp/a.mp3", 20);
        Song s2 = new Song("A", "/tmp/a.mp3", 20);

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }


    @Test
    void testEqualsWithId() {
        Song s1 = new Song("A", "/tmp/a.mp3", 20);
        Song s2 = new Song("A", "/tmp/a.mp3", 20);

        s1.setId(5);
        s2.setId(5);

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    void testEqualsWithDifferentId() {
        Song s1 = new Song("A", "/tmp/a.mp3", 20);
        Song s2 = new Song("A", "/tmp/a.mp3", 20);

        s1.setId(1);
        s2.setId(2);

        assertNotEquals(s1, s2);
    }

    @Test
    void testGetFilePathStringNull() {
        Song s = new Song("A", "Artist", "http://example.com");

        assertNull(s.getFilePathString());
    }
    
    @Test
    void testConstructorHandlesNullValues() {
        Song s = new Song(null, null, (String)null);
        
        assertEquals("", s.getTitle(), "GetTitle debe devolver cadena vacía si es null");
        assertEquals("Desconocido", s.getArtist(), "GetArtist debe devolver 'Desconocido' si es null");
    }

    @Test
    void testParsingComplexFilenames() {
        Song s = new Song("Artista - Titulo - Remix", "/path.mp3", 100);
        
        assertEquals("Artista", s.getArtist());
        assertEquals("Titulo - Remix", s.getTitle());
    }
    
    @Test
    void testParsingFilenameWithoutDash() {
        Song s = new Song("SoloTitulo", "/path.mp3", 100);
        
        assertEquals("Desconocido", s.getArtist());
        assertEquals("SoloTitulo", s.getTitle());
    }
}
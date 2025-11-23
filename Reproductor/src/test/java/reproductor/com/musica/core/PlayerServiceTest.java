package reproductor.com.musica.core;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reproductor.com.musica.model.Song;

import static org.junit.jupiter.api.Assertions.*;

class PlayerServiceTest {

    private PlayerService player;

    @BeforeAll
    static void initToolkit() {
        try {
            javafx.application.Platform.startup(() -> {});
        } catch (IllegalStateException e) {
        	
        }
    }

    @BeforeEach
    void setup() {
        player = new PlayerService();
    }

    @Test
    void testSetVolumePropertyUpdates() {
        
        player.setVolume(1.5);
        assertEquals(1.5, player.getVolume(), 0.001, "La propiedad debe guardar el valor asignado (1.5)");
        
        player.setVolume(-0.5);
        assertEquals(-0.5, player.getVolume(), 0.001, "La propiedad debe guardar el valor asignado (-0.5)");
    }

    @Test
    void testMuteToggle() {
        assertFalse(player.isMuted());
        player.setMuted(true);
        assertTrue(player.isMuted());
        player.setMuted(false);
        assertFalse(player.isMuted());
    }

    @Test
    void testCurrentSongProperty() {
        Song s = new Song("Titulo", "Artista", "ruta/falsa.mp3");
        
        assertNull(player.getCurrentSong());
        
        player.setCurrentSong(s);
        
        assertNotNull(player.getCurrentSong());
        assertEquals("Titulo", player.getCurrentSong().getTitle());
        assertEquals(s, player.currentSongProperty().get());
    }
    
    @Test
    void testPlaySongHandlingNull() {
        assertDoesNotThrow(() -> player.playSong(null));
    }
}
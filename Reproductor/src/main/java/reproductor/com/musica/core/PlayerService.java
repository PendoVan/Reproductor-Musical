package reproductor.com.musica.core;

import java.io.File;
import javafx.beans.property.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import reproductor.com.musica.model.Song;

/**
 * Encapsula el uso de MediaPlayer de JavaFX y expone
 * propiedades simples para que el controlador las enlace.
 */
public class PlayerService {

    private final ObjectProperty<Song> currentSong = new SimpleObjectProperty<>(null);
    private final DoubleProperty currentTimeSeconds = new SimpleDoubleProperty(0);
    private final DoubleProperty totalDurationSeconds = new SimpleDoubleProperty(0);
    private final BooleanProperty playing = new SimpleBooleanProperty(false);
    private final DoubleProperty volume = new SimpleDoubleProperty(0.7);
    private final BooleanProperty muted = new SimpleBooleanProperty(false);

    private MediaPlayer mediaPlayer;
    private boolean stoppedByEndOfMedia = false;

    // ========= PROPIEDADES EXPUESTAS =========

    public ObjectProperty<Song> currentSongProperty() {
        return currentSong;
    }

    public Song getCurrentSong() {
        return currentSong.get();
    }

    public void setCurrentSong(Song song) {
        currentSong.set(song);
    }

    public DoubleProperty currentTimeSecondsProperty() {
        return currentTimeSeconds;
    }

    public DoubleProperty totalDurationSecondsProperty() {
        return totalDurationSeconds;
    }

    public BooleanProperty playingProperty() {
        return playing;
    }

    public boolean isPlaying() {
        return playing.get();
    }

    public double getTotalDurationSeconds() {
        return totalDurationSeconds.get();
    }

    public double getVolume() {
        return volume.get();
    }

    public void setVolume(double v) {
        volume.set(v);
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(clamp(v, 0.0, 1.0));
        }
    }

    public void setMuted(boolean mute) {
        muted.set(mute);
        if (mediaPlayer != null) {
            mediaPlayer.setMute(mute);
        }
    }

    public boolean isMuted() {
        return muted.get();
    }

    public boolean isStoppedByEndOfMedia() {
        return stoppedByEndOfMedia;
    }

    // ========= CONTROL DE REPRODUCCIÓN =========

    public void playSong(Song song) {
        if (song == null) {
            System.err.println("[PlayerService] Song es null");
            return;
        }

        // Si ya está ese mismo song y solo estaba pausado, reanudar
        if (song.equals(currentSong.get()) && mediaPlayer != null) {
            mediaPlayer.play();
            playing.set(true);
            return;
        }

        disposePlayer();

        currentSong.set(song);
        String uri = resolveMediaUri(song);
        
        if (uri == null) {
            System.err.println("[PlayerService] No se pudo obtener la ruta de la canción: " + song.getTitle());
            return;
        }

        try {
            Media media = new Media(uri);
            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setOnReady(() -> {
                Duration dur = mediaPlayer.getTotalDuration();
                totalDurationSeconds.set(dur.toSeconds());
                System.out.println("[PlayerService] Canción lista: " + song.getTitle() + 
                                 " - Duración: " + dur.toSeconds() + "s");
            });

            mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) ->
                    currentTimeSeconds.set(newTime.toSeconds()));

            mediaPlayer.setOnPlaying(() -> {
                playing.set(true);
                stoppedByEndOfMedia = false;
                System.out.println("[PlayerService] Reproduciendo: " + song.getTitle());
            });

            mediaPlayer.setOnPaused(() -> {
                playing.set(false);
                System.out.println("[PlayerService] Pausado");
            });

            mediaPlayer.setOnStopped(() -> {
                playing.set(false);
                System.out.println("[PlayerService] Detenido");
            });

            mediaPlayer.setOnEndOfMedia(() -> {
                playing.set(false);
                stoppedByEndOfMedia = true;
                System.out.println("[PlayerService] Fin del medio");
            });

            mediaPlayer.setOnError(() -> {
                System.err.println("[PlayerService] Error: " + mediaPlayer.getError().getMessage());
            });

            mediaPlayer.setVolume(clamp(volume.get(), 0.0, 1.0));
            mediaPlayer.setMute(muted.get());

            mediaPlayer.play();
            
        } catch (Exception e) {
            System.err.println("[PlayerService] Error al crear Media/MediaPlayer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void stop() {
        stoppedByEndOfMedia = false;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    /**
     * Mueve la reproducción a una fracción de la duración total.
     */
    public void seekToFraction(double fraction) {
        if (mediaPlayer == null) return;

        fraction = clamp(fraction, 0.0, 1.0);
        double total = totalDurationSeconds.get();
        if (total <= 0) return;

        double targetSeconds = total * fraction;
        mediaPlayer.seek(Duration.seconds(targetSeconds));
    }

    // ========= LIMPIEZA =========

    private void disposePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        currentTimeSeconds.set(0);
        totalDurationSeconds.set(0);
        playing.set(false);
        stoppedByEndOfMedia = false;
    }

    // ========= HELPERS =========

    private String resolveMediaUri(Song song) {
        if (song == null) return null;

        // CORRECCIÓN: Usar getFilePathString() en lugar de getFilePath()
        String filePathString = song.getFilePathString();

        if (filePathString == null || filePathString.isEmpty()) {
            System.err.println("[PlayerService] La ruta del archivo es null o vacía");
            return null;
        }

        File file = new File(filePathString);
        
        if (!file.exists()) {
            System.err.println("[PlayerService] El archivo no existe: " + filePathString);
            return null;
        }

        String uri = file.toURI().toString();
        System.out.println("[PlayerService] URI resuelto: " + uri);
        return uri;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
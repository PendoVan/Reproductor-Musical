package reproductor.com.musica.core;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.function.Consumer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import reproductor.com.musica.model.Song;

/**
 * Servicio encargado de reproducir audio usando JavaFX MediaPlayer.
 * No gestiona la lista de canciones, solo la reproducción de la canción actual.
 */
public class PlayerService {

    private MediaPlayer mediaPlayer;

    private final ObjectProperty<Song> currentSong = new SimpleObjectProperty<>();
    private final DoubleProperty currentTimeSeconds = new SimpleDoubleProperty(0);
    private final DoubleProperty totalDurationSeconds = new SimpleDoubleProperty(0);
    private final DoubleProperty volume = new SimpleDoubleProperty(0.5);
    private final BooleanProperty playing = new SimpleBooleanProperty(false);

    private double previousVolume = 0.5;

    // Listeners de compatibilidad con el código antiguo
    private Consumer<Update> updateListener;
    private Consumer<String> errorListener;

    /** Pequeño DTO para reportar estado al controlador antiguo. */
    public static class Update {
        private final double current;
        private final double total;

        public Update(double current, double total) {
            this.current = current;
            this.total = total;
        }

        public double current() { return current; }
        public double total() { return total; }
        public double ratio() {
            if (total <= 0) return 0.0;
            return current / total;
        }
    }

    public PlayerService() {
        // volumen por defecto al 50%
        volume.addListener((obs, oldV, newV) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newV.doubleValue());
            }
        });
    }

    // -------------------------
    // Propiedades para la UI
    // -------------------------

    public ReadOnlyObjectProperty<Song> currentSongProperty() {
        return currentSong;
    }

    public Song getCurrentSong() {
        return currentSong.get();
    }

    public ReadOnlyDoubleProperty currentTimeSecondsProperty() {
        return currentTimeSeconds;
    }

    public double getCurrentTimeSeconds() {
        return currentTimeSeconds.get();
    }

    public ReadOnlyDoubleProperty totalDurationSecondsProperty() {
        return totalDurationSeconds;
    }

    public double getTotalDurationSeconds() {
        return totalDurationSeconds.get();
    }

    public DoubleProperty volumeProperty() {
        return volume;
    }

    public double getVolume() {
        return volume.get();
    }

    public void setVolume(double value) {
        volume.set(clamp(value, 0.0, 1.0));
    }

    public ReadOnlyBooleanProperty playingProperty() {
        return playing;
    }

    public boolean isPlaying() {
        return playing.get();
    }

    // -------------------------
    // Control de reproducción
    // -------------------------

    // Carga y reproduce la canción indicada.
    public void playSong(Song song) {
        if (song == null) return;

        disposeMediaPlayer();

        String source = buildMediaSource(song);
        Media media = new Media(source);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(volume.get());

        // listeners para tiempo y estado
        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            currentTimeSeconds.set(newTime.toSeconds());
            if (updateListener != null) {
                updateListener.accept(new Update(currentTimeSeconds.get(), totalDurationSeconds.get()));
            }
        });

        mediaPlayer.setOnReady(() -> {
            Duration total = mediaPlayer.getMedia().getDuration();
            totalDurationSeconds.set(total.toSeconds());
            if (updateListener != null) {
                updateListener.accept(new Update(currentTimeSeconds.get(), totalDurationSeconds.get()));
            }
        });

        mediaPlayer.setOnPlaying(() -> playing.set(true));
        mediaPlayer.setOnPaused(() -> playing.set(false));
        mediaPlayer.setOnStopped(() -> playing.set(false));
        mediaPlayer.setOnEndOfMedia(() -> playing.set(false));

        mediaPlayer.setOnError(() -> {
            String msg = "Error al reproducir la pista";
            if (mediaPlayer.getError() != null) {
                msg = mediaPlayer.getError().getMessage();
            }
            if (errorListener != null) {
                errorListener.accept(msg);
            }
        });

        currentSong.set(song);
        mediaPlayer.play();
    }

    // Reanuda la reproducción si hay una canción cargada.
    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    // Pausa la reproducción actual.
    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    // Detiene la reproducción actual y reinicia el tiempo a 0.
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    // Salta a un punto de la canción según un progreso 0.0–1.0.
    public void seek(double progress) {
        if (mediaPlayer == null || totalDurationSeconds.get() <= 0) return;
        progress = clamp(progress, 0.0, 1.0);
        double targetSeconds = totalDurationSeconds.get() * progress;
        mediaPlayer.seek(Duration.seconds(targetSeconds));
    }

    // ---- Métodos de compatibilidad con PlayerController antiguo ----
    public void seekByRatio(double ratio) {
        seek(ratio);
    }

    public void setMute(boolean mute) {
        if (mute) {
            previousVolume = getVolume();
            setVolume(0.0);
        } else {
            setVolume(previousVolume);
        }
    }

    public void open(Path path) {
        if (path == null) return;
        Song s = new Song(path.getFileName().toString(), "", path);
        playSong(s);
    }

    public void onUpdate(Consumer<Update> listener) {
        this.updateListener = listener;
    }

    public void onError(Consumer<String> listener) {
        this.errorListener = listener;
    }

    // Libera recursos del MediaPlayer actual.
    public void dispose() {
        disposeMediaPlayer();
    }

    // Helpers internos
    private void disposeMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (Exception ignored) {
            }
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        currentTimeSeconds.set(0);
        totalDurationSeconds.set(0);
        playing.set(false);
    }

    private String buildMediaSource(Song song) {
        try {
            if (song.isLocal() && song.getFilePath() != null) {
                return song.getFilePath().toUri().toURL().toExternalForm();
            }
        } catch (MalformedURLException e) {
            // si falla, intentaremos streamUrl
        }

        if (song.isRemote() && song.getStreamUrl() != null) {
            return song.getStreamUrl();
        }

        throw new IllegalArgumentException("La canción no tiene una fuente válida (filePath o streamUrl).");
    }

    private double clamp(double value, double min, double max) {
        if (value < min) return min;
        return Math.min(value, max);
    }
}

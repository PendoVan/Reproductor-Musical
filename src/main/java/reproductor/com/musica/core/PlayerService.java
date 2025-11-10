package reproductor.com.musica.core;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.function.Consumer;

public class PlayerService {

    private MediaPlayer player;
    private String currentTitle;

    private Consumer<Update> onUpdate = update -> {};
    private Consumer<String> onError = message -> {};

    /** Registro inmutable que contiene la información de progreso actual del reproductor. */
    public record Update(java.time.Duration current, java.time.Duration total, double ratio, String title) {}

    // === Event Handlers ===
    public void onUpdate(Consumer<Update> handler) {
        this.onUpdate = Objects.requireNonNull(handler);
    }

    public void onError(Consumer<String> handler) {
        this.onError = Objects.requireNonNull(handler);
    }

    // === Core Methods ===
    public void open(Path path) {
        stop();
        try {
            Media media = new Media(path.toUri().toURL().toExternalForm());
            player = new MediaPlayer(media);

            configurePlayer(media, path);
            currentTitle = path.getFileName().toString();

        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Ruta inválida: " + path, e);
        }
    }

    private void configurePlayer(Media media, Path path) {
        player.setOnReady(this::notifyUpdate);
        player.currentTimeProperty().addListener((obs, oldVal, newVal) -> notifyUpdate());
        player.setOnEndOfMedia(this::notifyUpdate);

        player.setOnError(() -> onError.accept("Error de reproducción: " + player.getError()));
        media.setOnError(() -> onError.accept("Archivo no soportado o corrupto: " + path.getFileName()));
    }

    public void play() {
        if (player != null) player.play();
    }

    public void pause() {
        if (player != null) player.pause();
    }

    public void stop() {
        if (player != null) {
            player.stop();
            player.dispose();
            player = null;
        }
    }

    public void setVolume(double volume) {
        if (player != null) player.setVolume(volume);
    }

    public void setMute(boolean mute) {
        if (player != null) player.setMute(mute);
    }

    public boolean isPlaying() {
        return player != null && player.getStatus() == MediaPlayer.Status.PLAYING;
    }

    // === Seek & Progress ===
    public void seekByRatio(double ratio) {
        if (player == null) return;

        ratio = clamp(ratio, 0.0, 1.0);
        Duration total = player.getTotalDuration();

        if (total == null || total.isUnknown()) return;
        player.seek(total.multiply(ratio));
    }

    private void notifyUpdate() {
        if (player == null) {
            onUpdate.accept(new Update(null, null, 0, null));
            return;
        }

        Duration currentFx = player.getCurrentTime();
        Duration totalFx = player.getTotalDuration();
        double ratio = calculateRatio(currentFx, totalFx);

        onUpdate.accept(new Update(
                toJavaDuration(currentFx),
                totalFx == null || totalFx.isUnknown() ? null : toJavaDuration(totalFx),
                ratio,
                currentTitle
        ));
    }

    // === Utility Methods ===
    private double calculateRatio(Duration current, Duration total) {
        if (total == null || total.isUnknown() || total.toMillis() == 0) return 0;
        return current.toMillis() / total.toMillis();
    }

    private java.time.Duration toJavaDuration(Duration fxDuration) {
        return java.time.Duration.of((long) fxDuration.toMillis(), ChronoUnit.MILLIS);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}


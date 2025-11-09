package reproductor.com.musica.core;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class PlayerService {
	private MediaPlayer player;
	private String currentTitle;
	private java.util.function.Consumer<Update> onUpdate = u -> {};
	
	public record Update(java.time.Duration current, java.time.Duration total, double ratio, String title) {}
	
	public void onUpdate(java.util.function.Consumer<Update> handler) {
		this.onUpdate = Objects.requireNonNull(handler);
	}
	
	public void open(Path path) {
		stop();
		try {
			Media media = new Media(path.toUri().toURL().toExternalForm());
			player = new MediaPlayer(media);
			player.setOnReady(this::notifyUpdate);
			player.currentTimeProperty().addListener((obs, o, n) -> notifyUpdate());
			player.setOnEndOfMedia(this::notifyUpdate);
			currentTitle = path.getFileName().toString();
			player.setOnError(() -> onError.accept("Error de reproducción: " + player.getError()));
			media.setOnError(() -> onError.accept("Archivo no soportado o corrupto: " + path.getFileName()));
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Ruta inválida: " + path, e);
		}
	}
	
	public void play()	{ if (player != null) player.play(); }
	public void pause()	{ if (player != null) player.pause(); }
	public void stop()	{ if (player != null) { player.stop(); player.dispose(); player = null; } }
	
	public void setVolume(double v) { if (player != null) player.setVolume(v); }
	public void setMute(boolean m) { if (player != null) player.setMute(m); }
	
	public void seekByRatio(double ratio) {
		ratio = Math.max(0, Math.min(1, ratio));
		if (player == null) return;
		Duration total = player.getTotalDuration();
		if (total == null || total.isUnknown()) return;
		player.seek(total.multiply(ratio));
	}
	
	private void notifyUpdate() {
		if (player == null) { onUpdate.accept(new Update(null, null, 0, null)); return; }
		Duration cur = player.getCurrentTime();
		Duration tot = player.getTotalDuration();
		double ratio = (tot == null || tot.isUnknown() || tot.toMillis() == 0) ? 0 : cur.toMillis() / tot.toMillis();
		String title = currentTitle;
		
		onUpdate.accept(new Update(
			java.time.Duration.of((long) cur.toMillis(), ChronoUnit.MILLIS),
			(tot == null || tot.isUnknown()) ? null : java.time.Duration.of((long) tot.toMillis(), ChronoUnit.MILLIS),
			ratio, title));
	}
	
	public boolean isPlaying() { return player != null && player.getStatus() == MediaPlayer.Status.PLAYING; }
	private java.util.function.Consumer<String> onError = msg -> {};
	public void onError(java.util.function.Consumer<String> h) { this.onError = h; }

}

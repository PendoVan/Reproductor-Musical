package reproductor.com.musica.core;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import reproductor.com.musica.model.PlaybackMode;
import reproductor.com.musica.model.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Maneja la lista de reproducción actual y la lógica
 * de navegación (next/prev/aleatorio/repetir).
 */
public class PlaylistService {

    private final ObservableList<Song> songs = FXCollections.observableArrayList();
    private final IntegerProperty currentIndex = new SimpleIntegerProperty(-1);
    private final DoubleProperty totalDuration = new SimpleDoubleProperty(0);

    private PlaybackMode playbackMode = PlaybackMode.NORMAL;
    private final Random random = new Random();

    public PlaylistService() {
    }

    // ========= PROPIEDADES BÁSICAS =========

    public ObservableList<Song> getSongs() {
        return songs;
    }

    public ReadOnlyIntegerProperty currentIndexProperty() {
        return currentIndex;
    }

    public DoubleProperty totalDurationProperty() {
        return totalDuration;
    }

    public PlaybackMode getPlaybackMode() {
        return playbackMode;
    }

    public void setPlaybackMode(PlaybackMode mode) {
        if (mode != null) {
            this.playbackMode = mode;
        }
    }

    // ========= MANEJO DE CANCIONES =========

    public void setCurrentSong(Song song) {
        int idx = songs.indexOf(song);
        if (idx >= 0) {
            currentIndex.set(idx);
        }
    }

    public Song getCurrentSong() {
        int idx = currentIndex.get();
        if (idx >= 0 && idx < songs.size()) {
            return songs.get(idx);
        }
        return null;
    }

    public Song getCurrentSongOrFirst() {
        if (songs.isEmpty()) {
            return null;
        }
        Song current = getCurrentSong();
        if (current != null) {
            return current;
        }
        currentIndex.set(0);
        return songs.get(0);
    }

    /**
     * Devuelve la siguiente canción según el modo de reproducción.
     */
    public Song getNextSong() {
        if (songs.isEmpty()) {
            return null;
        }

        int size = songs.size();
        int idx = currentIndex.get();

        switch (playbackMode) {
            case NORMAL -> {
                if (idx + 1 < size) {
                    currentIndex.set(idx + 1);
                } else {
                    return null; // fin de la lista
                }
            }
            case SHUFFLE -> currentIndex.set(random.nextInt(size));
            case REPEAT_ALL -> currentIndex.set((idx + 1) % size);
            case REPEAT_ONE -> {
                // índice se mantiene
            }
        }
        return getCurrentSong();
    }

    /**
     * Devuelve la canción anterior (modo simple).
     */
    public Song getPreviousSong() {
        if (songs.isEmpty()) {
            return null;
        }

        int size = songs.size();
        int idx = currentIndex.get();

        switch (playbackMode) {
            case SHUFFLE -> currentIndex.set(random.nextInt(size));
            default -> {
                if (idx > 0) {
                    currentIndex.set(idx - 1);
                } else {
                    currentIndex.set(0);
                }
            }
        }
        return getCurrentSong();
    }

    public void clearCurrentPlaylist() {
        songs.clear();
        currentIndex.set(-1);
        totalDuration.set(0);
    }

    public void addSong(Song song) {
        if (song != null) {
            songs.add(song);
            recalcTotalDuration();
        }
    }

    public void addSongs(List<Song> list) {
        if (list != null && !list.isEmpty()) {
            songs.addAll(list);
            recalcTotalDuration();
        }
    }

    /**
     * Agrega archivos locales y devuelve la lista de Songs creadas.
     * Ajusta el constructor de Song a tu propio modelo.
     */
    public List<Song> addFiles(List<File> files) {
        List<Song> added = new ArrayList<>();
        if (files == null) return added;

        for (File f : files) {
            if (f == null || !f.exists()) continue;

            // Crear canción con duración inicial de 0
            Song song = new Song(f.getName(), f.getAbsolutePath(), 0);
            
            // Intentar obtener la duración del archivo de audio
            try {
                javafx.scene.media.Media media = new javafx.scene.media.Media(f.toURI().toString());
                
                // Si la duración ya está disponible
                if (media.getDuration() != null && !media.getDuration().isUnknown()) {
                    song.setDurationSeconds((int) media.getDuration().toSeconds());
                    System.out.println("[PlaylistService] Duración cargada: " + f.getName() + " = " + song.getDurationSeconds() + "s");
                }
                
                // Listener por si la duración se carga después
                media.durationProperty().addListener((obs, oldDuration, newDuration) -> {
                    if (newDuration != null && !newDuration.isUnknown()) {
                        int seconds = (int) newDuration.toSeconds();
                        song.setDurationSeconds(seconds);
                        System.out.println("[PlaylistService] Duración actualizada: " + f.getName() + " = " + seconds + "s");
                        javafx.application.Platform.runLater(() -> recalcTotalDuration());
                    }
                });
            } catch (Exception e) {
                System.err.println("[PlaylistService] Error al obtener duración para: " + f.getName() + " - " + e.getMessage());
            }

            songs.add(song);
            added.add(song);
        }
        recalcTotalDuration();
        return added;
    }

    /**
     * Guarda la playlist actual. Aquí te dejo un stub;
     * puedes implementar escritura a JSON, texto, etc.
     */
    public void saveCurrentPlaylist() {
        // TODO: Implementar persistencia real si el curso lo requiere.
        System.out.println("[PlaylistService] saveCurrentPlaylist(): todavía no implementado");
    }

    // ========= UTILIDADES =========

    private void recalcTotalDuration() {
        double sum = 0;
        for (Song s : songs) {
            // TODO: ajusta si tu Song maneja la duración de forma distinta
            sum += s.getDurationSeconds();
        }
        totalDuration.set(sum);
    }
}

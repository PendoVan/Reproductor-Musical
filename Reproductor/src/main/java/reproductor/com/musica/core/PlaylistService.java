package reproductor.com.musica.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import reproductor.com.musica.model.PlaybackMode;
import reproductor.com.musica.model.Song;


public class PlaylistService {

    private final ObservableList<Song> songs = FXCollections.observableArrayList();
    private final IntegerProperty currentIndex = new SimpleIntegerProperty(-1);
    private final DoubleProperty totalDuration = new SimpleDoubleProperty(0);

    private PlaybackMode playbackMode = PlaybackMode.NORMAL;
    private final Random random = new Random();

    public PlaylistService() {
    }


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
                    return null; 
                }
            }
            case SHUFFLE -> currentIndex.set(random.nextInt(size));
            case REPEAT_ALL -> currentIndex.set((idx + 1) % size);
            case REPEAT_ONE -> {

            }
        }
        return getCurrentSong();
    }


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


    public List<Song> addFiles(List<File> files) {
        List<Song> added = new ArrayList<>();
        if (files == null) return added;

        for (File f : files) {
            if (f == null || !f.exists()) continue;


            int duracionEstimada = estimateDurationFromFileSize(f);
            Song song = new Song(f.getName(), f.getAbsolutePath(), duracionEstimada);
            

            try {
                javafx.scene.media.Media media = new javafx.scene.media.Media(f.toURI().toString());
                

                if (media.getDuration() != null && !media.getDuration().isUnknown()) {
                    int seconds = (int) media.getDuration().toSeconds();
                    song.setDurationSeconds(seconds);
                    System.out.println("[PlaylistService] ✓ Duración cargada: " + f.getName() + " = " + seconds + "s");
                } else {

                    media.durationProperty().addListener((obs, oldDuration, newDuration) -> {
                        if (newDuration != null && !newDuration.isUnknown()) {
                            int seconds = (int) newDuration.toSeconds();
                            song.setDurationSeconds(seconds);
                            System.out.println("[PlaylistService] ⏱ Duración actualizada: " + f.getName() + " = " + seconds + "s");
                            javafx.application.Platform.runLater(() -> recalcTotalDuration());
                        }
                    });
                    System.out.println("[PlaylistService] ⌛ Duración pendiente para: " + f.getName() + 
                                     " (usando estimación: " + duracionEstimada + "s)");
                }
            } catch (Exception e) {
                System.err.println("[PlaylistService] ⚠ Error al obtener duración para: " + f.getName() + " - " + e.getMessage());

            }

            songs.add(song);
            added.add(song);
        }
        
        recalcTotalDuration();
        return added;
    }
    
    /**
     * Estima la duración de un archivo MP3 basándose en su tamaño.
     * Asume bitrate promedio de 192 kbps.
     * 
     * @param file Archivo MP3
     * @return Duración estimada en segundos
     */
    private int estimateDurationFromFileSize(File file) {
        try {
            long bytes = file.length();
            double mb = bytes / (1024.0 * 1024.0);

            int segundos = (int) (bytes / 24000.0);
            return segundos > 0 ? segundos : 180; 
        } catch (Exception e) {
            return 180; 
        }
    }
    
    /**
     * Elimina una canción de la playlist.
     * 
     * @param song Canción a eliminar
     */
    public void removeSong(Song song) {
        if (song != null && songs.contains(song)) {
            songs.remove(song);
            

            if (getCurrentSong() != null && getCurrentSong().equals(song)) {
                currentIndex.set(-1);
            } else {

                int idx = currentIndex.get();
                int removedIdx = songs.indexOf(song);
                if (idx > removedIdx) {
                    currentIndex.set(idx - 1);
                }
            }
            
            recalcTotalDuration();
            
            System.out.println("[PlaylistService] 🗑️ Canción eliminada: " + song.getTitle());
        }
    }

    /**
     * Elimina múltiples canciones de la playlist.
     * 
     * @param songsToRemove Lista de canciones a eliminar
     */
    public void removeSongs(java.util.List<Song> songsToRemove) {
        if (songsToRemove == null || songsToRemove.isEmpty()) return;
        
        for (Song song : songsToRemove) {
            removeSong(song);
        }
        
        System.out.println("[PlaylistService] 🗑️ Eliminadas " + songsToRemove.size() + " canciones");
    }


    public void saveCurrentPlaylist() {
        // TODO: Implementar persistencia real si el curso lo requiere.
        System.out.println("[PlaylistService] saveCurrentPlaylist(): todavía no implementado");
    }



    private void recalcTotalDuration() {
        double sum = 0;
        for (Song s : songs) {
            // TODO: ajusta si tu Song maneja la duración de forma distinta
            sum += s.getDurationSeconds();
        }
        totalDuration.set(sum);
    }
}

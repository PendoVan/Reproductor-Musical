package reproductor.com.musica.core;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import reproductor.com.musica.model.PlaybackMode;
import reproductor.com.musica.model.Song;

public class PlaylistService {

    private final ObservableList<Song> songs = FXCollections.observableArrayList();
    private int currentIndex = -1;
    private PlaybackMode playbackMode = PlaybackMode.NORMAL;
    private final Random random = new Random();

    public ObservableList<Song> getSongs() {
        return songs;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int index) {
        if (index < 0 || index >= songs.size()) {
            currentIndex = -1;
        } else {
            currentIndex = index;
        }
    }

    public Song getCurrentSong() {
        if (currentIndex < 0 || currentIndex >= songs.size()) {
            return null;
        }
        return songs.get(currentIndex);
    }

    public PlaybackMode getPlaybackMode() {
        return playbackMode;
    }

    public void setPlaybackMode(PlaybackMode playbackMode) {
        if (playbackMode != null) {
            this.playbackMode = playbackMode;
        }
    }

    // Manejo de canciones
    public void addSong(Song song) {
        if (song != null) {
            songs.add(song);
            if (currentIndex == -1) {
                currentIndex = 0;
            }
        }
    }

    public void addSongs(Collection<Song> newSongs) {
        if (newSongs == null) return;
        for (Song s : newSongs) {
            addSong(s);
        }
    }

    public void removeSong(Song song) {
        if (song == null) return;
        int index = songs.indexOf(song);
        if (index == -1) return;

        songs.remove(index);

        if (songs.isEmpty()) {
            currentIndex = -1;
            return;
        }

        if (index < currentIndex) {
            currentIndex--;
        } else if (index == currentIndex) {
            // si quitamos la actual, ajustar índice
            if (currentIndex >= songs.size()) {
                currentIndex = songs.size() - 1;
            }
        }
    }

    public void clear() {
        songs.clear();
        currentIndex = -1;
    }

    // Carga rápida desde archivos locales
    public void loadFromPaths(List<Path> paths) {
        clear();
        if (paths == null || paths.isEmpty()) {
            return;
        }
        for (Path path : paths) {
            if (path != null) {
                Song s = new Song(path.getFileName().toString(), "", path);
                addSong(s);
            }
        }
        if (!songs.isEmpty()) {
            currentIndex = 0;
        }
    }

    // Navegación (next/prev)
    public boolean hasNext() {
        if (songs.isEmpty()) return false;
        if (playbackMode == PlaybackMode.SHUFFLE) return songs.size() > 1;
        if (playbackMode == PlaybackMode.REPEAT_ONE) return true;
        return currentIndex < songs.size() - 1 || playbackMode == PlaybackMode.REPEAT_ALL;
    }

    public boolean hasPrevious() {
        if (songs.isEmpty()) return false;
        if (playbackMode == PlaybackMode.SHUFFLE) return songs.size() > 1;
        if (playbackMode == PlaybackMode.REPEAT_ONE) return true;
        return currentIndex > 0 || playbackMode == PlaybackMode.REPEAT_ALL;
    }

    public Song next() {
        if (songs.isEmpty()) return null;

        switch (playbackMode) {
            case SHUFFLE:
                int newIndex;
                if (songs.size() == 1) {
                    newIndex = 0;
                } else {
                    do {
                        newIndex = random.nextInt(songs.size());
                    } while (newIndex == currentIndex);
                }
                currentIndex = newIndex;
                break;

            case REPEAT_ONE:
                // no cambiamos currentIndex
                break;

            case REPEAT_ALL:
                currentIndex++;
                if (currentIndex >= songs.size()) {
                    currentIndex = 0;
                }
                break;

            case NORMAL:
            default:
                if (currentIndex < songs.size() - 1) {
                    currentIndex++;
                }
                break;
        }

        return getCurrentSong();
    }

    public Song previous() {
        if (songs.isEmpty()) return null;

        switch (playbackMode) {
            case SHUFFLE:
                int newIndex;
                if (songs.size() == 1) {
                    newIndex = 0;
                } else {
                    do {
                        newIndex = random.nextInt(songs.size());
                    } while (newIndex == currentIndex);
                }
                currentIndex = newIndex;
                break;

            case REPEAT_ONE:
                // no cambiamos currentIndex
                break;

            case REPEAT_ALL:
                currentIndex--;
                if (currentIndex < 0) {
                    currentIndex = songs.size() - 1;
                }
                break;

            case NORMAL:
            default:
                if (currentIndex > 0) {
                    currentIndex--;
                }
                break;
        }

        return getCurrentSong();
    }
}

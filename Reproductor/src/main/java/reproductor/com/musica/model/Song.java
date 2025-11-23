package reproductor.com.musica.model;

import java.nio.file.Path;
import java.util.Objects;

public class Song {
    
    private Integer id;
    private String title;
    private String artist;
    private int durationSeconds;
    private Path filePath;  
    private String streamUrl;
    

    public Song(String title, String artist, Path filePath) {
        this.title = title;
        this.artist = artist;
        this.filePath = filePath;
    }
    

    public Song(String title, String artist, String streamUrl) {
        this.title = title;
        this.artist = artist;
        this.streamUrl = streamUrl;
    }
    

    public Song(String title, String filePath, double durationSeconds) {
        this.title = title;
        this.filePath = Path.of(filePath);
        this.durationSeconds = (int) durationSeconds;
        

        if (title.contains(" - ")) {
            String[] parts = title.split(" - ", 2);
            this.artist = parts[0].trim();
            this.title = parts[1].trim();
        } else {
            this.artist = "Desconocido";
        }
    }
    

    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title != null ? title : "";
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getArtist() {
        return artist != null ? artist : "Desconocido";
    }
    
    public void setArtist(String artist) {
        this.artist = artist;
    }
    
    public int getDurationSeconds() {
        return durationSeconds;
    }
    
    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }
    
    public Path getFilePath() {
        return filePath;
    }
    

    public String getFilePathString() {
        return filePath != null ? filePath.toString() : null;
    }
    
    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }
    
    public String getStreamUrl() {
        return streamUrl;
    }
    
    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }
    

    public boolean isLocal() {
        return filePath != null;
    }
    

    public boolean isRemote() {
        return streamUrl != null && !streamUrl.isBlank();
    }
    
    @Override
    public String toString() {
        if (!getArtist().isBlank() && !getArtist().equals("Desconocido")) {
            return getArtist() + " - " + getTitle();
        }
        return getTitle();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song)) return false;
        Song song = (Song) o;
        
        if (id != null && song.id != null) {
            return Objects.equals(id, song.id);
        }
        
        return Objects.equals(title, song.title)
                && Objects.equals(artist, song.artist)
                && Objects.equals(filePath, song.filePath)
                && Objects.equals(streamUrl, song.streamUrl);
    }
    
    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(title, artist, filePath, streamUrl);
    }
}
package reproductor.com.musica.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


public class SearchResult {
    
    @JsonProperty("video_id")
    private String videoId;
    
    @JsonProperty("titulo")
    private String titulo;
    
    @JsonProperty("artista")
    private String artista;
    
    @JsonProperty("duracion")
    private int duracion; // en segundos
    
    @JsonProperty("thumbnail")
    private String thumbnail;
    
    @JsonProperty("url")
    private String url;
    
    
    
    public SearchResult() {}
    
    public SearchResult(String videoId, String titulo, String artista, 
                       int duracion, String thumbnail, String url) {
        this.videoId = videoId;
        this.titulo = titulo;
        this.artista = artista;
        this.duracion = duracion;
        this.thumbnail = thumbnail;
        this.url = url;
    }
    
   
    
    public String getVideoId() {
        return videoId;
    }
    
    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getArtista() {
        return artista != null ? artista : "Desconocido";
    }
    
    public void setArtista(String artista) {
        this.artista = artista;
    }
    
    public int getDuracion() {
        return duracion;
    }
    
    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }
    
    public String getThumbnail() {
        return thumbnail;
    }
    
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    
    public String getFormattedDuration() {
        int minutos = duracion / 60;
        int segundos = duracion % 60;
        return String.format("%d:%02d", minutos, segundos);
    }
    
    @Override
    public String toString() {
        return "SearchResult{" +
                "videoId='" + videoId + '\'' +
                ", titulo='" + titulo + '\'' +
                ", artista='" + artista + '\'' +
                ", duracion=" + getFormattedDuration() +
                '}';
    }
}
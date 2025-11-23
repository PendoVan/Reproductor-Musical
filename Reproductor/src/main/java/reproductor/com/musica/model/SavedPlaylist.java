package reproductor.com.musica.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class SavedPlaylist {
    
    @JsonProperty("nombre")
    private String nombre;
    
    @JsonProperty("fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @JsonProperty("fecha_modificacion")
    private LocalDateTime fechaModificacion;
    
    @JsonProperty("canciones")
    private List<SongReference> canciones;
    

    
    public SavedPlaylist() {
        this.canciones = new ArrayList<>();
        this.fechaCreacion = LocalDateTime.now();
        this.fechaModificacion = LocalDateTime.now();
    }
    
    public SavedPlaylist(String nombre) {
        this();
        this.nombre = nombre;
    }
    

    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }
    
    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }
    
    public List<SongReference> getCanciones() {
        return canciones;
    }
    
    public void setCanciones(List<SongReference> canciones) {
        this.canciones = canciones;
    }
    

    public static class SongReference {
        
        @JsonProperty("titulo")
        private String titulo;
        
        @JsonProperty("artista")
        private String artista;
        
        @JsonProperty("nombre_archivo")
        private String nombreArchivo;
        
        @JsonProperty("duracion_segundos")
        private int duracionSegundos;
        
        public SongReference() {}
        
        public SongReference(String titulo, String artista, String nombreArchivo, int duracionSegundos) {
            this.titulo = titulo;
            this.artista = artista;
            this.nombreArchivo = nombreArchivo;
            this.duracionSegundos = duracionSegundos;
        }
        

        public static SongReference fromSong(Song song) {
            String nombreArchivo = "";
            if (song.getFilePath() != null) {
                nombreArchivo = song.getFilePath().getFileName().toString();
            }
            
            return new SongReference(
                song.getTitle(),
                song.getArtist(),
                nombreArchivo,
                song.getDurationSeconds()
            );
        }
        

        
        public String getTitulo() {
            return titulo;
        }
        
        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }
        
        public String getArtista() {
            return artista;
        }
        
        public void setArtista(String artista) {
            this.artista = artista;
        }
        
        public String getNombreArchivo() {
            return nombreArchivo;
        }
        
        public void setNombreArchivo(String nombreArchivo) {
            this.nombreArchivo = nombreArchivo;
        }
        
        public int getDuracionSegundos() {
            return duracionSegundos;
        }
        
        public void setDuracionSegundos(int duracionSegundos) {
            this.duracionSegundos = duracionSegundos;
        }
    }
}
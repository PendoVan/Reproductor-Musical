package reproductor.com.musica.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import reproductor.com.musica.model.SavedPlaylist;
import reproductor.com.musica.model.Song;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para guardar y cargar playlists desde archivos JSON.
 * Las playlists se guardan en la carpeta: user.home/.reproductor/playlists/
 */
public class PlaylistFileService {
    
    private static final String PLAYLISTS_FOLDER = "yt-backend/playlists";
    private static final String BACKEND_DOWNLOADS = "yt-backend/downloads";
    
    private final Path playlistsDirectory;
    private final Path downloadsDirectory;
    private final ObjectMapper objectMapper;
    
    public PlaylistFileService() {
        // Configurar directorios
    	String projectRoot = System.getProperty("user.dir");
        Path parentDir = Paths.get(projectRoot).getParent();
        if (parentDir != null) {
            this.playlistsDirectory = parentDir.resolve(PLAYLISTS_FOLDER);
        } else {
            this.playlistsDirectory = Paths.get(projectRoot).resolve(BACKEND_DOWNLOADS);
        }
        
        if (parentDir != null) {
            this.downloadsDirectory = parentDir.resolve(BACKEND_DOWNLOADS);
        } else {
            this.downloadsDirectory = Paths.get(projectRoot).resolve(BACKEND_DOWNLOADS);
        }
        
        // Configurar Jackson para manejar LocalDateTime
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        // Crear carpeta si no existe
        ensurePlaylistsDirectoryExists();
        
        System.out.println("[PlaylistFileService] Inicializado");
        System.out.println("  Playlists: " + playlistsDirectory);
        System.out.println("  Downloads: " + downloadsDirectory);
    }
    
    /**
     * Guarda la playlist actual en un archivo JSON.
     * 
     * @param nombre Nombre de la playlist
     * @param songs Lista de canciones actuales
     * @throws IOException si hay error al escribir
     */
    public void savePlaylist(String nombre, List<Song> songs) throws IOException {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la playlist no puede estar vacío");
        }
        
        // Crear el objeto SavedPlaylist
        SavedPlaylist savedPlaylist = new SavedPlaylist(nombre);
        savedPlaylist.setFechaModificacion(LocalDateTime.now());
        
        // Convertir cada Song a SongReference
        List<SavedPlaylist.SongReference> referencias = songs.stream()
                .map(SavedPlaylist.SongReference::fromSong)
                .collect(Collectors.toList());
        
        savedPlaylist.setCanciones(referencias);
        
        // Guardar como JSON
        Path filePath = getPlaylistPath(nombre);
        objectMapper.writeValue(filePath.toFile(), savedPlaylist);
        
        System.out.println("[PlaylistFileService] ✅ Playlist guardada: " + filePath);
    }
    
    /**
     * Carga una playlist desde un archivo JSON.
     * 
     * @param nombre Nombre de la playlist a cargar
     * @return Lista de Songs reconstruidas
     * @throws IOException si hay error al leer
     */
    public List<Song> loadPlaylist(String nombre) throws IOException {
        Path filePath = getPlaylistPath(nombre);
        
        if (!Files.exists(filePath)) {
            throw new IOException("La playlist '" + nombre + "' no existe");
        }
        
        // Leer el JSON
        SavedPlaylist savedPlaylist = objectMapper.readValue(filePath.toFile(), SavedPlaylist.class);
        
        // Reconstruir Songs desde las referencias
        List<Song> songs = new ArrayList<>();
        
        for (SavedPlaylist.SongReference ref : savedPlaylist.getCanciones()) {
            try {
                Song song = reconstructSongFromReference(ref);
                if (song != null) {
                    songs.add(song);
                } else {
                    System.err.println("[PlaylistFileService] ⚠ No se pudo reconstruir: " + ref.getNombreArchivo());
                }
            } catch (Exception e) {
                System.err.println("[PlaylistFileService] ❌ Error al reconstruir " + 
                                 ref.getNombreArchivo() + ": " + e.getMessage());
            }
        }
        
        System.out.println("[PlaylistFileService] ✅ Playlist cargada: " + nombre + 
                         " (" + songs.size() + " canciones)");
        
        return songs;
    }
    
    /**
     * Lista todas las playlists guardadas.
     * 
     * @return Lista de nombres de playlists
     */
    public List<String> listPlaylists() {
        ensurePlaylistsDirectoryExists();
        
        try {
            return Files.list(playlistsDirectory)
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(path -> path.getFileName().toString().replace(".json", ""))
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("[PlaylistFileService] Error al listar playlists: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Elimina una playlist guardada.
     * 
     * @param nombre Nombre de la playlist a eliminar
     * @return true si se eliminó correctamente
     */
    public boolean deletePlaylist(String nombre) {
        try {
            Path filePath = getPlaylistPath(nombre);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("[PlaylistFileService] 🗑️ Playlist eliminada: " + nombre);
                return true;
            }
            return false;
        } catch (IOException e) {
            System.err.println("[PlaylistFileService] Error al eliminar playlist: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifica si una playlist existe.
     * 
     * @param nombre Nombre de la playlist
     * @return true si existe
     */
    public boolean playlistExists(String nombre) {
        return Files.exists(getPlaylistPath(nombre));
    }
    
    // ===== MÉTODOS PRIVADOS =====
    
    /**
     * Reconstruye un Song desde una SongReference.
     * Busca el archivo MP3 en yt-backend/downloads/.
     */
    private Song reconstructSongFromReference(SavedPlaylist.SongReference ref) {
        // Buscar el archivo en downloads
        Path mp3Path = downloadsDirectory.resolve(ref.getNombreArchivo());
        
        if (!Files.exists(mp3Path)) {
            System.err.println("[PlaylistFileService] ⚠ Archivo no encontrado: " + mp3Path);
            return null;
        }
        
        // Crear Song con la información guardada
        Song song = new Song(ref.getTitulo(), mp3Path.toString(), ref.getDuracionSegundos());
        song.setArtist(ref.getArtista());
        song.setTitle(ref.getTitulo());
        
        // CRÍTICO: Asegurar que la duración se preserve correctamente
        int duracion = ref.getDuracionSegundos();
        if (duracion > 0) {
            song.setDurationSeconds(duracion);
        } else {
            // Si no hay duración guardada, intentar obtenerla del archivo
            try {
                long bytes = Files.size(mp3Path);
                song.setDurationSeconds(estimateDurationFromFileSize(bytes));
            } catch (Exception e) {
                System.err.println("[PlaylistFileService] No se pudo obtener duración para: " + ref.getNombreArchivo());
                song.setDurationSeconds(0);
            }
        }
        
        System.out.println("[PlaylistFileService] ✓ Song reconstruido: " + ref.getTitulo() + 
                         " (" + song.getDurationSeconds() + "s)");
        
        return song;
    }
    
    /**
     * Estima la duración en segundos basándose en el tamaño del archivo.
     * Asume MP3 a 192kbps en promedio.
     */
    private int estimateDurationFromFileSize(long bytes) {
        double mb = bytes / (1024.0 * 1024.0);
        return (int) ((mb / 1.44) * 60);
    }
    
    /**
     * Obtiene la ruta completa del archivo JSON de una playlist.
     */
    private Path getPlaylistPath(String nombre) {
        String safeName = nombre.replaceAll("[^a-zA-Z0-9_\\-]", "_");
        return playlistsDirectory.resolve(safeName + ".json");
    }
    
    /**
     * Crea el directorio de playlists si no existe.
     */
    private void ensurePlaylistsDirectoryExists() {
        try {
            if (!Files.exists(playlistsDirectory)) {
                Files.createDirectories(playlistsDirectory);
                System.out.println("[PlaylistFileService] 📁 Carpeta creada: " + playlistsDirectory);
            }
        } catch (IOException e) {
            System.err.println("[PlaylistFileService] ❌ Error al crear carpeta: " + e.getMessage());
            throw new RuntimeException("No se pudo crear el directorio de playlists", e);
        }
    }
    
    // ===== GETTERS =====
    
    public Path getPlaylistsDirectory() {
        return playlistsDirectory;
    }
    
    public Path getDownloadsDirectory() {
        return downloadsDirectory;
    }
}
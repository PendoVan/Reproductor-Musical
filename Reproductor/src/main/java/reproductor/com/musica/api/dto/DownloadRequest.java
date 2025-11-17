
package reproductor.com.musica.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO para solicitar descargas a la API.
 */
public class DownloadRequest {
    
    @JsonProperty("canciones")
    private List<String> songNames;
    
    public DownloadRequest() {
    }
    
    public DownloadRequest(List<String> songNames) {
        this.songNames = songNames;
    }
    
    public List<String> getSongNames() {
        return songNames;
    }
    
    public void setSongNames(List<String> songNames) {
        this.songNames = songNames;
    }
}

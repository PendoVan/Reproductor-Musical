package reproductor.com.musica.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class DownloadResponse {
    
    @JsonProperty("resultados")
    private List<DownloadResult> results;
    
    public DownloadResponse() {
    }
    
    public List<DownloadResult> getResults() {
        return results;
    }
    
    public void setResults(List<DownloadResult> results) {
        this.results = results;
    }
    
    /**
     * Resultado individual de una descarga.
     */
    public static class DownloadResult {
        
        @JsonProperty("nombre")
        private String name;
        
        @JsonProperty("url")
        private String url;
        
        @JsonProperty("archivo")
        private String fileName;
        
        @JsonProperty("estado")
        private String status;
        
        public DownloadResult() {
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getUrl() {
            return url;
        }
        
        public void setUrl(String url) {
            this.url = url;
        }
        
        public String getFileName() {
            return fileName;
        }
        
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public boolean isSuccess() {
            return "descargado".equalsIgnoreCase(status);
        }
        
        public boolean isError() {
            return status != null && status.toLowerCase().contains("error");
        }
    }
}
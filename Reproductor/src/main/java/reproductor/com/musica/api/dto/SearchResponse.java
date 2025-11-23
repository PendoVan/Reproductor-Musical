package reproductor.com.musica.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;


public class SearchResponse {
    
    @JsonProperty("resultados")
    private List<SearchResult> resultados;
    
    public SearchResponse() {}
    
    public SearchResponse(List<SearchResult> resultados) {
        this.resultados = resultados;
    }
    
    public List<SearchResult> getResultados() {
        return resultados;
    }
    
    public void setResultados(List<SearchResult> resultados) {
        this.resultados = resultados;
    }
    
    public int getCount() {
        return resultados != null ? resultados.size() : 0;
    }
}
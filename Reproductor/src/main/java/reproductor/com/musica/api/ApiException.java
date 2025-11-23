package reproductor.com.musica.api;


public class ApiException extends Exception {
    
	private static final long serialVersionUID = 1L;
	private final int statusCode;
    
    public ApiException(String message) {
        super(message);
        this.statusCode = -1;
    }
    
    public ApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
    }
    
    public ApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
    public boolean hasStatusCode() {
        return statusCode > 0;
    }
}
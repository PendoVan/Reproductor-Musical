package reproductor.com.musica;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import reproductor.com.musica.core.BackendManager;

public class App extends Application {
    
    private BackendManager backendManager;
    
    @Override
    public void init() throws Exception {
        super.init();
        
        System.out.println("=".repeat(60));
        System.out.println("🎵 REPRODUCTOR MUSICAL - Grupo 3");
        System.out.println("=".repeat(60));
        
        // Iniciar backend automáticamente
        backendManager = new BackendManager();
        
        System.out.println("\n🚀 Iniciando backend FastAPI...");
        boolean backendStarted = backendManager.start();
        
        if (!backendStarted) {
            System.err.println("❌ ERROR: No se pudo iniciar el backend");
            System.err.println("La aplicación funcionará en modo limitado");
            System.err.println("(no podrás buscar ni descargar música online)");
        } else {
            System.out.println("✅ Backend listo para usar\n");
        }
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        // Cargar interfaz
        Parent root = FXMLLoader.load(
            getClass().getResource("/reproductor/com/musica/view/MainView.fxml")
        );
        
        Scene scene = new Scene(root, 900, 560);
        stage.setTitle("Reproductor Musical - Grupo 3");
        stage.setScene(scene);
        
        // Manejar cierre de ventana
        stage.setOnCloseRequest(event -> {
            System.out.println("\n👋 Cerrando aplicación...");
            cleanup();
            Platform.exit();
            System.exit(0);
        });
        
        stage.show();
        
        // Mostrar advertencia si el backend no está disponible
        if (backendManager != null && !backendManager.isRunning()) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Advertencia");
                alert.setHeaderText("Backend no disponible");
                alert.setContentText(
                    "El servidor backend no pudo iniciarse.\n\n" +
                    "Podrás reproducir música local, pero no podrás:\n" +
                    "• Buscar música online\n" +
                    "• Descargar desde YouTube\n\n" +
                    "Verifica los logs en la consola."
                );
                alert.showAndWait();
            });
        }
    }
    
    @Override
    public void stop() throws Exception {
        cleanup();
        super.stop();
    }
    
    /**
     * Limpia recursos al cerrar la aplicación.
     */
    private void cleanup() {
        if (backendManager != null) {
            System.out.println("🛑 Deteniendo backend...");
            backendManager.stop();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
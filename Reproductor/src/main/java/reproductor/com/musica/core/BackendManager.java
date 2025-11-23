package reproductor.com.musica.core;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * Gestiona el ciclo de vida completo del backend FastAPI.
 * 
 * Responsabilidades:
 * - Detectar/crear entorno virtual Python
 * - Instalar dependencias automáticamente
 * - Levantar servidor FastAPI como proceso hijo
 * - Verificar disponibilidad del backend
 * - Cerrar el proceso al salir
 */
public class BackendManager {
    
    private static final String BACKEND_DIR = "yt-backend";
    private static final String VENV_DIR = "venv";
    private static final String PYTHON_EMBEDDED_DIR = "python-embedded";
    private static final String REQUIREMENTS_FILE = "requirements.txt";
    private static final int BACKEND_PORT = 8000;
    private static final String BACKEND_HOST = "127.0.0.1";
    private static final int STARTUP_TIMEOUT_SECONDS = 30;
    
    private Process backendProcess;
    private final Path projectRoot;
    private final Path backendPath;
    private final Path venvPath;
    private final Path pythonEmbeddedPath;
    
    public BackendManager() {
        // Detectar raíz del proyecto
        this.projectRoot = detectProjectRoot();
        this.backendPath = projectRoot.resolve(BACKEND_DIR);
        this.venvPath = backendPath.resolve(VENV_DIR);
        this.pythonEmbeddedPath = projectRoot.resolve(PYTHON_EMBEDDED_DIR);
        
        log("📁 Rutas detectadas:");
        log("  Proyecto: " + projectRoot);
        log("  Backend: " + backendPath);
        log("  Venv: " + venvPath);
        log("  Python: " + pythonEmbeddedPath);
    }
    
    /**
     * Inicia el backend completo: setup + inicio del servidor.
     * 
     * @return true si el backend está listo para usar
     */
    public boolean start() {
        try {
            log("🚀 Iniciando backend...");
            
            // 1. Verificar que existe el directorio backend
            if (!Files.exists(backendPath)) {
                logError("❌ No se encontró el directorio: " + backendPath);
                return false;
            }
            
            // 2. Setup del entorno virtual
            if (!setupVirtualEnvironment()) {
                logError("❌ Error al configurar entorno virtual");
                return false;
            }
            
            // 3. Instalar dependencias
            if (!installDependencies()) {
                logError("❌ Error al instalar dependencias");
                return false;
            }
            
            // 4. Levantar servidor FastAPI
            if (!startFastAPIServer()) {
                logError("❌ Error al iniciar servidor FastAPI");
                return false;
            }
            
            // 5. Esperar a que el backend esté listo
            if (!waitForBackend()) {
                logError("❌ El backend no respondió a tiempo");
                stopBackend();
                return false;
            }
            
            log("✅ Backend iniciado correctamente en http://" + BACKEND_HOST + ":" + BACKEND_PORT);
            return true;
            
        } catch (Exception e) {
            logError("❌ Error fatal al iniciar backend: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Detiene el servidor backend de forma limpia.
     */
    public void stop() {
        stopBackend();
    }
    
    /**
     * Verifica si el backend está corriendo.
     */
    public boolean isRunning() {
        return backendProcess != null && backendProcess.isAlive() && checkBackendHealth();
    }
    
    // ==================== MÉTODOS PRIVADOS ====================
    
    /**
     * Configura el entorno virtual de Python.
     */
    private boolean setupVirtualEnvironment() {
        try {
            // Si ya existe el venv, no hacer nada
            if (Files.exists(venvPath) && Files.exists(getPythonExecutable())) {
                log("✓ Entorno virtual ya existe");
                return true;
            }
            
            log("📦 Creando entorno virtual...");
            
            // Obtener ejecutable de Python
            Path pythonExe = findPythonExecutable();
            if (pythonExe == null) {
                logError("❌ No se encontró Python. Instala Python o coloca python-embedded/");
                return false;
            }
            
            // Crear venv
            ProcessBuilder pb = new ProcessBuilder(
                pythonExe.toString(),
                "-m", "venv",
                venvPath.toString()
            );
            pb.directory(backendPath.toFile());
            pb.redirectErrorStream(true);
            
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode != 0) {
                logError("❌ Error al crear venv (código: " + exitCode + ")");
                printProcessOutput(process);
                return false;
            }
            
            log("✅ Entorno virtual creado");
            return true;
            
        } catch (Exception e) {
            logError("❌ Error en setupVirtualEnvironment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Instala las dependencias desde requirements.txt.
     */
    private boolean installDependencies() {
        try {
            Path requirementsPath = backendPath.resolve(REQUIREMENTS_FILE);
            
            if (!Files.exists(requirementsPath)) {
                log("⚠ No se encontró requirements.txt, saltando instalación");
                return true;
            }
            
            log("📦 Instalando dependencias...");
            
            Path pipExe = getPipExecutable();
            
            ProcessBuilder pb = new ProcessBuilder(
                pipExe.toString(),
                "install",
                "-r", REQUIREMENTS_FILE,
                "--quiet"
            );
            pb.directory(backendPath.toFile());
            pb.redirectErrorStream(true);
            
            Process process = pb.start();
            
            // Mostrar progreso
            showProcessOutput(process);
            
            int exitCode = process.waitFor();
            
            if (exitCode != 0) {
                logError("❌ Error al instalar dependencias (código: " + exitCode + ")");
                return false;
            }
            
            log("✅ Dependencias instaladas");
            return true;
            
        } catch (Exception e) {
            logError("❌ Error en installDependencies: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Inicia el servidor FastAPI usando uvicorn.
     */
    private boolean startFastAPIServer() {
        try {
            log("🌐 Iniciando servidor FastAPI...");
            
            Path pythonExe = getPythonExecutable();
            
            ProcessBuilder pb = new ProcessBuilder(
                pythonExe.toString(),
                "-m", "uvicorn",
                "app.main:app",
                "--host", BACKEND_HOST,
                "--port", String.valueOf(BACKEND_PORT),
                "--log-level", "info"
            );
            
            pb.directory(backendPath.toFile());
            
            // CRÍTICO: Ocultar ventana de consola en Windows
            if (isWindows()) {
                try {
                    // Usar ProcessBuilder para Windows sin ventana
                    pb.redirectOutput(ProcessBuilder.Redirect.PIPE);
                    pb.redirectError(ProcessBuilder.Redirect.PIPE);
                } catch (Exception e) {
                    log("⚠ No se pudo ocultar consola: " + e.getMessage());
                }
            }
            
            backendProcess = pb.start();
            
            // Capturar logs en background
            startLogCapture(backendProcess);
            
            // Dar tiempo para que inicie
            Thread.sleep(2000);
            
            if (!backendProcess.isAlive()) {
                logError("❌ El proceso del backend murió inmediatamente");
                printProcessOutput(backendProcess);
                return false;
            }
            
            log("✓ Proceso FastAPI iniciado (PID: " + backendProcess.pid() + ")");
            return true;
            
        } catch (Exception e) {
            logError("❌ Error en startFastAPIServer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Espera hasta que el backend responda correctamente.
     */
    private boolean waitForBackend() {
        log("⏳ Esperando a que el backend esté listo...");
        
        for (int i = 0; i < STARTUP_TIMEOUT_SECONDS; i++) {
            if (checkBackendHealth()) {
                log("✅ Backend respondió correctamente");
                return true;
            }
            
            try {
                Thread.sleep(1000);
                if (i % 5 == 0) {
                    log("  ... esperando (" + i + "s)");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        
        return false;
    }
    
    /**
     * Verifica si el backend está respondiendo.
     */
    private boolean checkBackendHealth() {
        try {
            URL url = new URL("http://" + BACKEND_HOST + ":" + BACKEND_PORT + "/ping");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);
            
            int responseCode = conn.getResponseCode();
            conn.disconnect();
            
            return responseCode == 200;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Detiene el proceso del backend.
     */
    private void stopBackend() {
        if (backendProcess != null && backendProcess.isAlive()) {
            log("🛑 Deteniendo backend...");
            
            backendProcess.destroy();
            
            try {
                boolean exited = backendProcess.waitFor(5, TimeUnit.SECONDS);
                if (!exited) {
                    log("⚠ Forzando cierre del backend...");
                    backendProcess.destroyForcibly();
                    backendProcess.waitFor(2, TimeUnit.SECONDS);
                }
                log("✅ Backend detenido");
            } catch (InterruptedException e) {
                log("⚠ Interrupción al detener backend");
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Busca el ejecutable de Python (embedded o del sistema).
     */
    private Path findPythonExecutable() {
        // 1. Buscar Python embedded
        if (Files.exists(pythonEmbeddedPath)) {
            Path embeddedPython = pythonEmbeddedPath.resolve(isWindows() ? "python.exe" : "bin/python3");
            if (Files.exists(embeddedPython)) {
                log("✓ Usando Python embedded: " + embeddedPython);
                return embeddedPython;
            }
        }
        
        // 2. Buscar Python del sistema
        String[] pythonCommands = isWindows() 
            ? new String[]{"python", "python3", "py"}
            : new String[]{"python3", "python"};
        
        for (String cmd : pythonCommands) {
            try {
                Process p = new ProcessBuilder(cmd, "--version").start();
                if (p.waitFor() == 0) {
                    log("✓ Usando Python del sistema: " + cmd);
                    return Paths.get(cmd);
                }
            } catch (Exception ignored) {}
        }
        
        return null;
    }
    
    /**
     * Obtiene la ruta al ejecutable de Python del venv.
     */
    private Path getPythonExecutable() {
        if (isWindows()) {
            return venvPath.resolve("Scripts").resolve("python.exe");
        } else {
            return venvPath.resolve("bin").resolve("python");
        }
    }
    
    /**
     * Obtiene la ruta al ejecutable pip del venv.
     */
    private Path getPipExecutable() {
        if (isWindows()) {
            return venvPath.resolve("Scripts").resolve("pip.exe");
        } else {
            return venvPath.resolve("bin").resolve("pip");
        }
    }
    
    /**
     * Detecta la raíz del proyecto.
     */
    private Path detectProjectRoot() {
        // Desde el .jar o desde el IDE
        Path current = Paths.get(System.getProperty("user.dir"));
        
        // Si estamos en Reproductor/, subir un nivel
        if (current.endsWith("Reproductor")) {
            return current.getParent();
        }
        
        return current;
    }
    
    /**
     * Captura los logs del proceso en background.
     */
    private void startLogCapture(Process process) {
        Thread outputThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("Uvicorn running") || line.contains("Application startup complete")) {
                        log("[Backend] " + line);
                    }
                }
            } catch (IOException ignored) {}
        });
        outputThread.setDaemon(true);
        outputThread.start();
    }
    
    /**
     * Muestra la salida de un proceso en tiempo real.
     */
    private void showProcessOutput(Process process) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("  " + line);
            }
        }
    }
    
    /**
     * Imprime la salida de un proceso después de ejecutarse.
     */
    private void printProcessOutput(Process process) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logError("  " + line);
            }
        } catch (IOException ignored) {}
    }
    
    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
    
    private void log(String message) {
        System.out.println("[BackendManager] " + message);
    }
    
    private void logError(String message) {
        System.err.println("[BackendManager] " + message);
    }
}
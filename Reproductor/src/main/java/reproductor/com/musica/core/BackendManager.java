package reproductor.com.musica.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class BackendManager {
    
    private static final String BACKEND_DIR = "yt-backend";
    private static final String VENV_DIR = "venv";
    private static final String PYTHON_EMBEDDED_DIR = "python-embedded";
    private static final String REQUIREMENTS_FILE = "requirements.txt";
    private static final int BACKEND_PORT = 8000;
    private static final String BACKEND_HOST = "127.0.0.1";
    private static final int STARTUP_TIMEOUT_SECONDS = 30;
    private static final String FFMPEG_DIR = "ffmpeg";
    
    private Process backendProcess;
    private final Path projectRoot;
    private final Path backendPath;
    private final Path venvPath;
    private final Path pythonEmbeddedPath;
    
    public BackendManager() {
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
            
            if (!Files.exists(backendPath)) {
                logError("❌ No se encontró el directorio: " + backendPath);
                return false;
            }
            
            if (!setupVirtualEnvironment()) {
                logError("❌ Error al configurar entorno virtual");
                return false;
            }
            
            if (!installDependencies()) {
                logError("❌ Error al instalar dependencias");
                return false;
            }
            
            if (!startFastAPIServer()) {
                logError("❌ Error al iniciar servidor FastAPI");
                return false;
            }
            
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
    
    public void stop() {
        stopBackend();
    }
    
    public boolean isRunning() {
        return backendProcess != null && backendProcess.isAlive() && checkBackendHealth();
    }
    
    private boolean setupVirtualEnvironment() {
        try {

            if (Files.exists(venvPath) && Files.exists(getPythonExecutable())) {
                log("✓ Entorno virtual ya existe");
                return true;
            }
            
            log("📦 Creando entorno virtual...");
            

            Path pythonExe = findPythonExecutable();
            if (pythonExe == null) {
                logError("❌ No se encontró Python. Instala Python o coloca python-embedded/");
                return false;
            }
            

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
    

    private boolean startFastAPIServer() {
        try {
            log("🌐 Iniciando servidor FastAPI...");
            
            Path pythonExe = getPythonExecutable();
            

            Path ffmpegPath = getFfmpegPath();
            
            ProcessBuilder pb = new ProcessBuilder(
                pythonExe.toString(),
                "-m", "uvicorn",
                "app.main:app",
                "--host", BACKEND_HOST,
                "--port", String.valueOf(BACKEND_PORT),
                "--log-level", "info"
            );
            
            pb.directory(backendPath.toFile());
            

            if (ffmpegPath != null) {
                Map<String, String> env = pb.environment();
                String currentPath = env.getOrDefault("PATH", "");
                String newPath = ffmpegPath.toString() + File.pathSeparator + currentPath;
                env.put("PATH", newPath);
                log("✓ FFmpeg agregado al PATH: " + ffmpegPath);
            } else {
                log("⚠ FFmpeg no encontrado en el proyecto");
                log("  Los archivos se descargarán como .webm");
                log("  Instala FFmpeg o inclúyelo en: " + projectRoot.resolve(FFMPEG_DIR));
            }
            

            if (isWindows()) {
                try {
                    pb.redirectOutput(ProcessBuilder.Redirect.PIPE);
                    pb.redirectError(ProcessBuilder.Redirect.PIPE);
                } catch (Exception e) {
                    log("⚠ No se pudo ocultar consola: " + e.getMessage());
                }
            }
            
            backendProcess = pb.start();
            
            startLogCapture(backendProcess);
            
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
     * Busca FFmpeg en el proyecto o en el sistema.
     * 
     * @return Path al directorio de FFmpeg, o null si no se encuentra
     */
    private Path getFfmpegPath() {
        Path ffmpegDir = projectRoot.resolve(FFMPEG_DIR);
        

        if (isWindows()) {
            Path ffmpegExe = ffmpegDir.resolve("ffmpeg.exe");
            if (Files.exists(ffmpegExe)) {
                log("✓ FFmpeg encontrado en el proyecto");
                return ffmpegDir;
            }
        } 

        else {
            Path ffmpegBin = ffmpegDir.resolve("bin");
            Path ffmpegExe = ffmpegBin.resolve("ffmpeg");
            if (Files.exists(ffmpegExe)) {
                log("✓ FFmpeg encontrado en el proyecto");
                return ffmpegBin;
            }
        }
        

        if (isFFmpegInSystem()) {
            log("✓ Usando FFmpeg del sistema");
            return null; // El PATH del sistema ya lo tiene
        }
        
        return null;
    }


    private boolean isFFmpegInSystem() {
        try {
            String command = isWindows() ? "where ffmpeg" : "which ffmpeg";
            Process p = new ProcessBuilder(command.split(" ")).start();
            return p.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }
    

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
    

    private Path findPythonExecutable() {
        // 1. Buscar Python embedded
        if (Files.exists(pythonEmbeddedPath)) {
            Path embeddedPython = pythonEmbeddedPath.resolve(isWindows() ? "python.exe" : "bin/python3");
            if (Files.exists(embeddedPython)) {
                log("✓ Usando Python embedded: " + embeddedPython);
                return embeddedPython;
            }
        }
        

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
    

    private Path getPythonExecutable() {
        if (isWindows()) {
            return venvPath.resolve("Scripts").resolve("python.exe");
        } else {
            return venvPath.resolve("bin").resolve("python");
        }
    }
    

    private Path getPipExecutable() {
        if (isWindows()) {
            return venvPath.resolve("Scripts").resolve("pip.exe");
        } else {
            return venvPath.resolve("bin").resolve("pip");
        }
    }
    

    private Path detectProjectRoot() {

        Path current = Paths.get(System.getProperty("user.dir"));
        

        if (current.endsWith("Reproductor")) {
            return current.getParent();
        }
        
        return current;
    }
    

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
    

    private void showProcessOutput(Process process) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("  " + line);
            }
        }
    }
    

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
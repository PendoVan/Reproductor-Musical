@echo off
echo ==================================
echo  REPRODUCTOR MUSICAL - FRONTEND TEST
echo ==================================
echo.

cd /d "c:\Users\Katherine\Reproductor-Musical\Reproductor"

echo Compilando aplicacion...

javac -d target/classes ^
  --module-path "%JAVA_HOME%\lib" ^
  --add-modules javafx.controls,javafx.fxml,javafx.media ^
  -cp "target/classes" ^
  "src/main/java/reproductor/com/musica/App.java" ^
  "src/main/java/reproductor/com/musica/controller/PlayerController.java" ^
  "src/main/java/reproductor/com/musica/core/PlayerService.java" ^
  "src/main/java/reproductor/com/musica/core/PlaylistService.java" ^
  "src/main/java/reproductor/com/musica/model/PlaybackMode.java" ^
  2>nul

if %ERRORLEVEL% EQU 0 (
    echo Compilacion exitosa!
    echo.
    echo Ejecutando aplicacion...
    echo.
    
    java --module-path "%JAVA_HOME%\lib" ^
         --add-modules javafx.controls,javafx.fxml,javafx.media ^
         -cp "target/classes;src/main/resources" ^
         reproductor.com.musica.App
) else (
    echo Error en la compilacion.
    echo Intenta ejecutar desde VS Code con F5
)

echo.
echo Presiona cualquier tecla para salir...
pause >nul
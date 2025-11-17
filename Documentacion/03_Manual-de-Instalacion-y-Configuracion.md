

# **03. Manual de Instalación y Configuración**

---

## 1. Introducción

El presente manual describe el procedimiento de instalación, configuración y ejecución del sistema **Reproductor Musical**, desarrollado en **Java** utilizando **JavaFX** como tecnología principal para la interfaz gráfica.

El objetivo es brindar una guía clara y detallada para que cualquier usuario o evaluador pueda instalar y ejecutar el sistema correctamente en su equipo local, garantizando el funcionamiento adecuado del prototipo.

El manual está dirigido a estudiantes, docentes y desarrolladores que requieran ejecutar el proyecto con fines académicos.

---

## 2. Requisitos del sistema

### 2.1 Requisitos de hardware

* Procesador de arquitectura x64.
* Memoria RAM mínima: 4 GB (recomendado 8 GB).
* Espacio disponible en disco: 200 MB como mínimo.
* Monitor con resolución mínima de 1280 x 720.

### 2.2 Requisitos de software

* Sistema operativo: **Windows 10 o superior**.
* **Java Development Kit (JDK)** versión 17, 19, 21 o superior.
* **JavaFX SDK** compatible con la versión del JDK utilizado.
* IDE recomendado: **Eclipse IDE** (o cualquier IDE compatible, como IntelliJ IDEA o VS Code).
* Biblioteca estándar de JavaFX (incluida mediante SDK o módulo externo).
* Acceso a internet opcional (solo necesario para actualizaciones o futuras integraciones).

---

## 3. Instalación del entorno

### 3.1 Instalación del JDK

1. Acceder a la página oficial de Oracle o Temurin (Adoptium).
2. Descargar el instalador del **JDK 21** (o versión compatible).
3. Ejecutar el instalador y seguir las instrucciones.
4. Verificar la instalación mediante consola con el comando:

   ```
   java -version
   ```

   Si la instalación fue exitosa, la consola mostrará la versión del JDK instalado.

### 3.2 Instalación del JavaFX SDK

1. Ingresar al sitio oficial de Gluon:
   [https://gluonhq.com/products/javafx/](https://gluonhq.com/products/javafx/)
2. Descargar el paquete **JavaFX SDK** correspondiente a la versión del JDK instalado y al sistema operativo.
3. Extraer el archivo ZIP en una ubicación accesible, por ejemplo:

   ```
   C:\javafx-sdk\
   ```

### 3.3 Instalación del IDE Eclipse

1. Descargar Eclipse IDE desde [https://www.eclipse.org/downloads/](https://www.eclipse.org/downloads/).
2. Instalar la versión "Eclipse IDE for Java Developers".
3. Abrir el IDE e importar el proyecto.

---

## 4. Configuración del proyecto en Eclipse

### 4.1 Importar el proyecto

1. Abrir Eclipse IDE.
2. Seleccionar la opción:

   ```
   File > Import > Existing Projects into Workspace
   ```
3. Elegir la carpeta del proyecto `Reproductor-Musical`.
4. Verificar que Eclipse detecte los paquetes y archivos fuente.
5. Finalizar la importación.

### 4.2 Configurar las librerías JavaFX

Debido a que JavaFX no viene incluido por defecto en los JDK actuales, es necesario configurar manualmente los módulos.

1. Hacer clic derecho sobre el proyecto.
2. Seleccionar:

   ```
   Properties > Java Build Path > Libraries
   ```
3. Agregar los archivos JAR desde:

   ```
   C:\javafx-sdk\lib\
   ```
4. Asegurar que todos los módulos de JavaFX se incluyan correctamente.

### 4.3 Configuración del runtime

Para ejecutar el proyecto desde Eclipse:

1. Hacer clic derecho en el proyecto.

2. Seleccionar:

   ```
   Run As > Run Configurations
   ```

3. En la pestaña "Arguments", agregar lo siguiente en "VM Arguments":

   ```
   --module-path "C:\javafx-sdk\lib" --add-modules javafx.controls,javafx.fxml,javafx.media
   ```

4. Guardar la configuración y ejecutar.

---

## 5. Estructura del proyecto

El proyecto se organiza de la siguiente manera:

* **src/main/java/**
  Contiene los paquetes del sistema: controladores, servicios y modelos.

* **src/main/resources/**
  Contiene vistas FXML, archivos CSS y recursos adicionales.

* **PlayerService.java**
  Control central del reproductor.

* **PlaylistService.java**
  Gestión de playlists internas.

* **View FXML**:

  * MainView.fxml
  * NowPlayingView.fxml
  * SearchView.fxml
  * SettingsView.fxml

* **Archivos CSS**:

  * dark-theme.css

Esta estructura permite una separación clara entre lógica, modelos y recursos de presentación.

---

## 6. Ejecución del proyecto

### 6.1 Ejecución desde el IDE

Una vez configurados los módulos JavaFX, se puede ejecutar:

1. Abrir la clase principal del proyecto (normalmente ubicada en un paquete "main" o "launcher").
2. Hacer clic derecho y seleccionar:

   ```
   Run As > Java Application
   ```
3. Verificar que la ventana principal del reproductor se abra correctamente.

### 6.2 Ejecución desde consola (opcional)

También es posible ejecutar el proyecto desde la terminal, utilizando:

```
java --module-path "C:\javafx-sdk\lib" --add-modules javafx.controls,javafx.fxml,javafx.media -jar Reproductor-Musical.jar
```

Para ello, el proyecto debe ser previamente exportado a un archivo JAR ejecutable.

---

## 7. Exportación del archivo ejecutable (JAR)

### 7.1 Exportación en Eclipse

1. Clic derecho sobre el proyecto.
2. Seleccionar:

   ```
   Export > Runnable JAR File
   ```
3. Marcar la opción:

   ```
   Package required libraries into generated JAR
   ```
4. Definir la ruta de destino para el archivo.
5. Confirmar y generar el JAR.

### 7.2 Consideraciones del JAR

Debido al uso de JavaFX, el archivo JAR requiere el uso de argumentos adicionales para ejecutarse, por ejemplo:

```
java --module-path "C:\javafx-sdk\lib" --add-modules javafx.controls,javafx.fxml,javafx.media -jar Reproductor-Musical.jar
```

En versiones avanzadas se puede empaquetar mediante **jlink** o **jpackage**, pero esto queda fuera del alcance del presente proyecto académico.

---

## 8. Errores comunes y soluciones

### Error: "JavaFX runtime components are missing"

Causa: JavaFX no ha sido agregado al módulo de ejecución.
Solución: Añadir los argumentos de VM indicados anteriormente.

### Error: "ClassNotFoundException: javafx.application.Application"

Causa: El SDK de JavaFX no está añadido al Build Path.
Solución: Verificar en `Properties > Java Build Path > Libraries`.

### Error: "Cannot load media"

Causa: El archivo de audio no está en formato compatible o la ruta no es válida.
Solución: Utilizar archivos `.mp3` o `.wav` y verificar la ruta absoluta.

### Error: Eclipse no reconoce los archivos FXML

Causa: Mal mapeo de rutas dentro del proyecto.
Solución: Colocar archivos FXML dentro de `src/main/resources`.

---

## 9. Conclusiones

Este manual proporciona los pasos necesarios para instalar, configurar y ejecutar el sistema Reproductor Musical en un entorno Windows utilizando Java y JavaFX.

La correcta instalación del JDK, el SDK de JavaFX y la configuración en el IDE son fundamentales para garantizar que la aplicación funcione sin errores.
El uso de una arquitectura clara y la separación adecuada de módulos facilita el despliegue y la ejecución del proyecto.

---



# **04. Manual de Usuario**

---

## 1. Introducción

El presente manual tiene como propósito orientar al usuario en el uso del sistema **Reproductor Musical**, una aplicación de escritorio desarrollada en Java y JavaFX.
Aquí se describen las funcionalidades principales, el funcionamiento de la interfaz, la interacción con las listas de reproducción y los controles del reproductor.

Este documento está dirigido a usuarios que emplean la aplicación por primera vez, así como evaluadores y docentes que necesiten comprender su funcionamiento general.

---

## 2. Requisitos previos para el uso del sistema

Para utilizar correctamente el Reproductor Musical, se requiere lo siguiente:

* Dispositivo con sistema operativo Windows.
* Java instalado correctamente en el sistema.
* Archivos de audio en formato compatible (mp3 o wav).
* Espacio suficiente para almacenar playlists y archivos de música.

El usuario no necesita conocimientos técnicos avanzados; basta con conocer el uso básico de aplicaciones de escritorio.

---

## 3. Pantalla principal

Al iniciar la aplicación, se muestra la **ventana principal**, diseñada para ofrecer acceso rápido a las funcionalidades del reproductor.

Los elementos principales que la componen son:

1. **Menú o barra lateral**
   Permite acceder a:

   * Biblioteca local
   * Playlists
   * Búsqueda de canciones
   * Ajustes del sistema

2. **Área central de contenido**
   Muestra información según la sección seleccionada:

   * Listas de canciones
   * Resultados de búsqueda
   * Detalles de reproducción

3. **Controles de reproducción**
   Ubicados generalmente en la parte inferior de la ventana:

   * Botón Play
   * Botón Pausa
   * Botón Detener
   * Botón Siguiente
   * Botón Anterior
   * Barra de progreso
   * Control de volumen
   * Botón de silencio (Mute)

4. **Información de la canción en reproducción**
   Muestra:

   * Nombre de la canción
   * Duración actual y total
   * Fuente del archivo

---

## 4. Gestión de canciones locales

El usuario puede agregar canciones desde el almacenamiento local de su equipo.

### 4.1 Agregar canciones

1. Acceder a la sección de **Biblioteca**.
2. Seleccionar la opción de **Agregar archivo**.
3. Elegir uno o varios archivos de audio desde el explorador de Windows.
4. Confirmar la selección.
5. Las canciones aparecerán en la lista de la biblioteca.

### 4.2 Visualizar canciones agregadas

La ventana de biblioteca muestra una tabla o listado que incluye:

* Título de la canción
* Ruta o ubicación
* Duración aproximada

### 4.3 Eliminar canciones

1. Seleccionar la canción deseada.
2. Presionar la opción **Eliminar**.
3. Confirmar la acción.
4. La canción se retira de la lista de biblioteca (no se elimina del disco local).

---

## 5. Reproducción de canciones

### 5.1 Seleccionar una canción

1. Desde la biblioteca o una playlist, seleccionar la canción.
2. Pulsar el botón **Play** o hacer doble clic en la canción.

### 5.2 Controles del reproductor

El reproductor permite las siguientes acciones:

* **Play:** iniciar la reproducción.
* **Pausa:** detener temporalmente la reproducción.
* **Stop:** detener completamente la canción.
* **Siguiente:** pasar a la canción siguiente en la lista.
* **Anterior:** volver a la canción precedente.
* **Barra de progreso:** permite adelantar o retroceder arrastrando el control.
* **Volumen:** ajustar el nivel de sonido.
* **Mute:** silenciar el audio de forma instantánea.

### 5.3 Información presentada

Durante la reproducción se muestran:

* Nombre de la canción
* Tiempo transcurrido
* Tiempo total
* Indicador visual de progreso

---

## 6. Gestión de playlists

El sistema permite crear, modificar y eliminar listas de reproducción personalizadas.

### 6.1 Crear una nueva playlist

1. Acceder a la sección **Playlists**.
2. Seleccionar la opción **Nueva Playlist**.
3. Asignar un nombre.
4. Confirmar la creación.

### 6.2 Agregar canciones a una playlist

1. Seleccionar una playlist existente.
2. Abrir la biblioteca local.
3. Seleccionar una canción.
4. Presionar la opción **Agregar a playlist**.
5. Elegir la playlist de destino.

### 6.3 Eliminar canciones de una playlist

1. Abrir la playlist.
2. Seleccionar la canción a eliminar.
3. Presionar **Eliminar**.
4. Confirmar.

### 6.4 Cambiar el orden de las canciones

Dependiendo de la implementación, se ofrecen una o ambas opciones:

* Botones para subir o bajar la canción dentro de la lista.
* Arrastrar y soltar para reorganizar (si la interfaz lo permite).

### 6.5 Eliminar una playlist

1. Seleccionar la playlist.
2. Presionar **Eliminar Playlist**.
3. Confirmar la acción.

La playlist será removida del sistema (sin eliminar los archivos de música del disco).

---

## 7. Búsqueda de canciones

El sistema cuenta con una sección de **búsqueda**, orientada inicialmente para futuras integraciones con servicios externos. Actualmente, la interfaz permite:

1. Acceder a la sección de búsqueda.
2. Escribir texto en el cuadro de búsqueda.
3. Visualizar resultados compatibles internos (si los hubiera).

En posteriores iteraciones del proyecto se puede integrar un servicio de búsqueda en línea.

---

## 8. Ajustes del sistema

En la sección de **Configuración** o **Ajustes**, el usuario puede:

* Cambiar temas de la interfaz (si están disponibles).
* Ajustar el volumen general.
* Modificar comportamientos predeterminados del reproductor.
* Consultar información sobre el sistema.

Estas opciones pueden ampliarse según futuras versiones.

---

## 9. Cierre del programa

Para cerrar la aplicación, el usuario puede:

* Seleccionar la opción **Salir** desde el menú.
* Cerrar la ventana principal mediante el botón estándar de Windows.

La aplicación finalizará la reproducción en curso y liberará los recursos del reproductor.

---

## 10. Recomendaciones de uso

* Evitar abrir archivos en formatos no compatibles.
* Mantener las rutas de las canciones sin mover o renombrar archivos externos.
* Cerrar la aplicación correctamente para evitar pérdida de playlists temporales.
* Ejecutar la aplicación con la versión correcta del JDK y JavaFX.

---

## 11. Conclusiones

El presente manual proporciona las instrucciones necesarias para que cualquier usuario pueda emplear de manera adecuada el Reproductor Musical. Las funciones descritas permiten gestionar canciones locales, reproducir audio, trabajar con playlists y personalizar ciertos aspectos del sistema.

La interfaz ha sido diseñada para ser intuitiva y accesible, asegurando que usuarios sin conocimientos técnicos puedan utilizar el sistema sin dificultad.

---



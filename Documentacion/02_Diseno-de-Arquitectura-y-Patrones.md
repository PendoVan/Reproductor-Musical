

**02_Diseño-de-Arquitectura-y-Patrones.md**

---

# **02. Diseño de Arquitectura y Patrones**

## Proyecto: *Reproductor Musical*

---

## **1. Introducción**

El presente documento detalla la **arquitectura de software** y los **patrones de diseño** empleados en el desarrollo del sistema *Reproductor Musical*, prototipo académico implementado en Java utilizando JavaFX para la interfaz gráfica.

El objetivo es proporcionar una visión clara, estructurada y justificable sobre la organización interna del sistema, las capas que lo conforman, los flujos de información, los patrones aplicados y las decisiones técnicas adoptadas durante el desarrollo.

Este documento complementa a los requisitos del sistema y sirve como referencia para mantenimiento, ampliación y evaluación técnica del proyecto.

---

## **2. Arquitectura general del sistema**

El sistema emplea una **arquitectura por capas**, separando responsabilidades en distintos niveles para mejorar la organización, el mantenimiento y la escalabilidad del software.

Las capas principales son:

1. **Capa de Presentación (GUI – JavaFX)**
2. **Capa de Lógica de Negocio (Servicios)**
3. **Capa de Datos (Modelos y Persistencia básica)**

La interacción entre capas ocurre de manera **unidireccional**, siguiendo el principio de dependencia hacia capas inferiores.

---

## **3. Descripción de las capas**

### **3.1 Capa de Presentación (GUI)**

Corresponde a todas las interfaces visibles para el usuario.
Está conformada por:

* Archivos **FXML** que definen la estructura visual:

  * `MainView.fxml`
  * `NowPlayingView.fxml`
  * `SearchView.fxml`
  * `SettingsView.fxml`

* Hojas de estilo (**CSS**), como por ejemplo:

  * `dark-theme.css`

* Controladores JavaFX:

  * `MainController`
  * `SearchController`
  * `NowPlayingController`
  * `SettingsController`

**Responsabilidades:**

* Mostrar información al usuario.
* Capturar eventos (botones, sliders, selecciones).
* Solicitar acciones a la capa de servicios.
* Actualizar la vista según cambios en la lógica.

La GUI **no** implementa lógica de reproducción ni manipulación de playlists; se limita a delegarla.

---

### **3.2 Capa de Lógica de Negocio**

Esta capa implementa las reglas del sistema y coordina las operaciones principales:

* Reproducción de canciones.
* Gestión de listas de reproducción.
* Control de los estados del reproductor.
* Validación de operaciones sobre modelos.

Clases principales:

#### **PlayerService**

Encargado de:

* Controlar el `MediaPlayer` de JavaFX.
* Reproducir, pausar, detener, avanzar y retroceder.
* Ajustar volumen y mute.
* Gestionar modos:

  * Repetición de pista
  * Repetición de lista
  * Aleatorio

También lleva el registro de:

* Canción actual.
* Estado de reproducción.
* Progreso del tiempo.

#### **PlaylistService**

Encargado de:

* Crear, renombrar y eliminar playlists.
* Añadir y eliminar canciones de una lista.
* Obtener la siguiente o anterior canción según el modo de reproducción.
* Persistir y cargar playlists de forma básica (archivos o memoria temporal).

Esta capa es **independiente de la GUI**, promoviendo una mayor modularidad.

---

### **3.3 Capa de Datos (Modelos)**

Incluye estructuras orientadas a objetos que representan los elementos fundamentales del sistema:

#### **Song**

Atributos típicos:

* Nombre
* Ruta del archivo
* Duración
* Autor o fuente

#### **Playlist**

* Lista de objetos `Song`
* Nombre de la lista
* Métodos para agregar, eliminar, ordenar canciones

#### **PlaybackMode**

Enumeración con modos:

* NORMAL
* SHUFFLE
* REPEAT_ONE
* REPEAT_ALL

Estas clases permiten almacenar la información que utiliza la capa de servicios y actúan como puente entre lógica y presentación.

---

## **4. Flujo general del sistema**

A continuación se describe el flujo típico cuando el usuario interactúa:

1. **El usuario ejecuta una acción en la GUI**, por ejemplo:

   * Botón *Play*
   * Seleccionar una canción
   * Crear playlist

2. El **Controller** correspondiente recibe el evento.

3. El Controller **invoca un método** del servicio adecuado:

   * `playerService.play(song)`
   * `playlistService.createPlaylist(name)`
   * `playlistService.addSong(playlist, song)`

4. El **Servicio ejecuta la operación**, actualizando estados internos o modelos.

5. La GUI se **actualiza** según el resultado:

   * Cambia el texto del botón
   * Actualiza listas visibles
   * Actualiza barra de progreso

Este flujo mantiene una separación nítida entre la vista y la lógica.

---

## **5. Patrón arquitectónico: MVVM / MVC simplificado**

El proyecto adopta un enfoque **MVVM simplificado**, inspirado también en MVC, donde:

* **Modelo (Model):**
  Clases `Song`, `Playlist`, `PlaybackMode`.

* **Vista (View):**
  Archivos FXML + CSS.

* **ViewModel / Controlador (Controller):**
  Manejan la lógica de la vista y comunican eventos a los servicios.

Aunque no se implementa MVVM completo (con *bindings* avanzados), sí se sigue su filosofía:

* Separar interfaz de lógica.
* Mantener datos y estados dentro de la capa de servicios.
* Evitar que la vista tenga lógica compleja.

Este enfoque es adecuado para proyectos educativos con JavaFX.

---

## **6. Patrones de diseño utilizados**

### ### **6.1 Patrón Singleton (PlayerService)**

El reproductor requiere que exista **un único control centralizado de reproducción**.
Por ello, la clase `PlayerService` se implementa como **Singleton**, permitiendo:

* Un único `MediaPlayer`.
* Control global desde cualquier parte de la aplicación.
* Evitar múltiples reproductores simultáneos.

Características:

* Constructor privado.
* Instancia única accesible desde un método estático.
* Control centralizado de estados de reproducción.

Este patrón es el más apropiado para objetos que representan recursos únicos en el sistema.

---

### **6.2 Patrón Observer / Listener**

JavaFX incorpora eventos y listeners que funcionan como un **Observer Pattern**, permitiendo:

* Actualizar barras de progreso en tiempo real.
* Detectar cambios en la reproducción.
* Escuchar cambios en el volumen o mute.
* Actualizar la vista según el estado de reproducción.

Ejemplos comunes:

* `mediaPlayer.currentTimeProperty().addListener(...)`
* `volumeSlider.valueProperty().addListener(...)`

El uso de este patrón permite una interfaz reactiva y adaptable.

---

### **6.3 Patrón Strategy (implícito)**

El modo de reproducción (`PlaybackMode`) representa diferentes **estrategias**:

* Normal
* Aleatorio
* Repetición de canción
* Repetición de lista

Cada estrategia determina cuál será la siguiente canción.
Aunque no está implementado explícitamente como clases separadas, **su representación enum cumple el rol conceptual del patrón Strategy**.

---

### **6.4 Patrones propios de JavaFX**

El proyecto hace uso natural de:

* **Inyección de controladores** con `@FXML`
* **Binding de propiedades** para sliders y labels
* **Patrón de Componentes GUI** modularizado en vistas FXML

Estos patrones ayudan a mantener un código claro y estructurado.

---

## **7. Escalabilidad y extensibilidad**

La arquitectura fue diseñada para permitir mejoras futuras:

* **Integración con API externa**
  Aunque planificada, no fue implementada en esta versión.
  Sin embargo, la separación en capas facilita añadir un módulo `ApiService` sin modificar la GUI.

* **Persistencia avanzada**
  Se puede extender la capa de datos con:

  * JSON
  * XML
  * SQLite
  * Archivos binarios

* **Soporte de más formatos de audio**

* **Módulos adicionales** (ecualizador, favoritos, estadísticas de reproducción)

La arquitectura actual soporta estas extensiones sin rediseños drásticos.

---

## **8. Consideraciones sobre la API externa**

De acuerdo con los requisitos iniciales, el sistema debía incorporar un módulo de integración con una API de música en línea.

Sin embargo, durante el desarrollo:

> **La integración con API permanece como un requisito planificado pero fuera de alcance en esta versión.**

Justificación académica:

* Limitaciones de tiempo.
* Complejidad técnica de las API multimediales.
* Restricciones del curso de Algorítmica II.
* Diferencias entre streaming de audio y carga local.

La arquitectura, no obstante, está preparada para ello:

* La capa de servicios puede extenderse con un módulo `OnlineSongService`.
* La GUI ya contempla una vista de búsqueda (`SearchView`).

Esto garantiza coherencia entre requisitos y diseño sin falsear la implementación real.

---

## **9. Conclusiones**

La arquitectura del proyecto *Reproductor Musical* se sustenta en una estructura por capas clara y coherente, basada en principios de separación de responsabilidades y uso adecuado de patrones de diseño.

Los patrones aplicados (Singleton, Observer, Strategy conceptual y MVC/MVVM simplificado) permiten un sistema organizado, mantenible y con bases sólidas para futuras extensiones.

El diseño adoptado facilita:

* Mantenimiento del código
* Escalabilidad del sistema
* Independencia entre interfaz, lógica y datos
* Comprensión por parte de nuevos desarrolladores

Este documento permite comprender cómo está construido el sistema y cómo evolucionará en futuras iteraciones académicas.

---


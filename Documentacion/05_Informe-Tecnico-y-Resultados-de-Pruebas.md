

# **05. Informe Técnico y Resultados de Pruebas**

---

## 1. Introducción

El presente informe documenta los aspectos técnicos más relevantes del sistema *Reproductor Musical* y los resultados de las pruebas realizadas durante su desarrollo.
El propósito de este documento es demostrar la solidez de la implementación, validar el funcionamiento esperado de las funcionalidades principales y registrar los procedimientos seguidos durante la verificación del software.

Este informe está dirigido a evaluadores, docentes y desarrolladores que requieran comprender el comportamiento interno del sistema y la calidad alcanzada en esta versión del prototipo.

---

## 2. Descripción técnica del sistema

### 2.1 Lenguaje y entorno de desarrollo

El sistema fue desarrollado utilizando:

* Lenguaje de programación: **Java** (JDK 21)
* Interfaz gráfica: **JavaFX**
* IDE: **Eclipse IDE**
* Patrón arquitectónico: **MVC/MVVM simplificado**
* Patrones de diseño: **Singleton**, **Observer**, **Strategy conceptual**

### 2.2 Módulos principales

El sistema se estructura en tres capas:

1. **Presentación (JavaFX):**
   Conformada por vistas FXML y controladores responsables de recibir interacciones del usuario.

2. **Lógica de negocio:**
   Implementada mediante servicios internos:

   * `PlayerService` (control del reproductor).
   * `PlaylistService` (gestión de listas de reproducción).

3. **Datos (Modelos):**
   Incluye representaciones de canciones, playlists y modos de reproducción.

### 2.3 Reproducción de audio

La reproducción está basada en:

* Clase `Media` de JavaFX para cargar la fuente de audio.
* Clase `MediaPlayer` para controlar:

  * Play
  * Pausa
  * Stop
  * Avance y retroceso
  * Control de volumen
  * Estado de la reproducción

El sistema admite archivos locales en formatos compatibles (.mp3, .wav).

---

## 3. Objetivos de las pruebas

Las pruebas realizadas tienen como propósito validar:

1. La operación adecuada del reproductor bajo distintos escenarios.
2. La gestión correcta de las playlists (crear, agregar, eliminar).
3. La estabilidad general de la interfaz y ausencia de errores críticos.
4. La correcta interacción entre controladores, servicios y modelos.
5. El cumplimiento de los requisitos funcionales establecidos en el documento de requisitos.

---

## 4. Tipos de pruebas realizadas

### 4.1 Pruebas funcionales

Validan que los requisitos funcionales (RF) se cumplan de manera completa.

Ejemplos de funciones verificadas:

* Reproducción de archivos locales.
* Control de volumen.
* Avanzar y retroceder entre canciones.
* Crear y eliminar playlists.
* Agregar y quitar canciones de una playlist.

### 4.2 Pruebas unitarias conceptuales

Aunque el prototipo no incluye un conjunto formal de pruebas automatizadas (JUnit), se realizaron pruebas unitarias manuales sobre métodos clave, tales como:

* Métodos de selección de la siguiente canción.
* Métodos de manipulación de listas en `PlaylistService`.
* Encapsulación y obtención de datos en los modelos.

### 4.3 Pruebas de integración

Se verificó la interacción entre:

* Controladores JavaFX y PlayerService.
* Vistas FXML y controladores.
* PlaylistService y modelos.

Estas pruebas permiten asegurar que los cambios en una capa no afectan la estabilidad del sistema completo.

### 4.4 Pruebas de interfaz de usuario

El objetivo fue validar:

* Comportamiento visual coherente.
* Respuestas adecuadas a clics, desplazamientos y eventos.
* Actualización correcta de información durante la reproducción.

---

## 5. Casos de prueba

A continuación se incluye una tabla con los principales casos de prueba ejecutados.

### 5.1 Tabla de casos de prueba

| ID    | Descripción del caso          | Datos de entrada               | Resultado esperado                                 | Estado   |
| ----- | ----------------------------- | ------------------------------ | -------------------------------------------------- | -------- |
| CP-01 | Reproducción de canción local | Archivo .mp3 existente         | El reproductor inicia la reproducción sin errores  | Correcto |
| CP-02 | Pausar canción                | Canción en reproducción        | La reproducción se detiene temporalmente           | Correcto |
| CP-03 | Detener reproducción          | Canción en reproducción        | La canción vuelve al inicio                        | Correcto |
| CP-04 | Siguiente canción             | Playlist con varias canciones  | Se reproduce la siguiente canción de la lista      | Correcto |
| CP-05 | Canción anterior              | Playlist con varias canciones  | Se reproduce la canción previa                     | Correcto |
| CP-06 | Crear playlist                | Nombre de playlist válido      | Se crea una nueva lista de reproducción            | Correcto |
| CP-07 | Agregar canción a playlist    | Canción seleccionada           | La canción se agrega correctamente                 | Correcto |
| CP-08 | Eliminar canción de playlist  | Canción dentro de una lista    | La canción es removida de la playlist              | Correcto |
| CP-09 | Eliminar playlist             | Playlist seleccionada          | La lista es eliminada sin afectar archivos locales | Correcto |
| CP-10 | Ajustar volumen               | Nivel de volumen entre 0 y 100 | El volumen del reproductor varía correctamente     | Correcto |
| CP-11 | Silenciar volumen             | Opción Mute seleccionada       | El volumen se silenció y puede restaurarse         | Correcto |
| CP-12 | Barra de progreso             | Canción en reproducción        | El usuario puede adelantar o retroceder            | Correcto |
| CP-13 | Cargar vista principal        | Inicio del sistema             | Las vistas FXML se cargan sin errores              | Correcto |

---

## 6. Resultados generales de las pruebas

Los resultados obtenidos fueron los siguientes:

* Todas las pruebas funcionales fueron exitosas.
* Las interacciones entre vistas, controladores y servicios funcionan correctamente.
* El sistema se comporta de manera estable durante la reproducción prolongada.
* No se detectaron pérdidas de memoria ni cierres inesperados.
* El sistema responde adecuadamente a la manipulación simultánea de controles (por ejemplo, cambiar volumen durante la reproducción).
* Las playlists mantienen integridad en su estructura durante operaciones múltiples de inserción o eliminación.

---

## 7. Problemas encontrados y soluciones aplicadas

Durante la etapa de pruebas se identificaron los siguientes inconvenientes:

### 7.1 Rutas de archivos incorrectas

Algunas canciones no podían reproducirse debido a rutas inválidas.
Solución aplicada: Validación previa del archivo mediante la clase `File`.

### 7.2 Congelamiento ocasional de la interfaz

Se presentó un pequeño retraso al cargar ciertos archivos.
Solución aplicada: Uso adecuado del módulo `Media` y optimización del llamado a eventos gráficos.

### 7.3 Falta de sincronización de la barra de progreso

La barra de progreso no reflejaba correctamente el tiempo transcurrido.
Solución aplicada: Agregar un listener al `currentTimeProperty` del `MediaPlayer`.

---

## 8. Evaluación del cumplimiento de requisitos

El análisis final indica lo siguiente:

* La mayoría de los **requisitos funcionales** fueron implementados correctamente.
* La funcionalidad de integración con API externa, si bien fue incluida en los requisitos, **no fue implementada en esta versión**, quedando como un objetivo para versiones futuras.
* Los requisitos no funcionales relacionados con usabilidad, rendimiento y estabilidad se cumplieron adecuadamente.
* La arquitectura propuesta (MVC/MVVM) puede ampliarse sin dificultades técnicas.

---

## 9. Conclusiones

Las pruebas realizadas permiten concluir que el sistema **Reproductor Musical** cumple satisfactoriamente su propósito académico.
El reproductor es capaz de gestionar canciones locales, reproducir audio de manera estable, administrar playlists y ofrecer una interfaz amigable para el usuario.

El análisis técnico evidencia:

* Correcto funcionamiento de los servicios internos.
* Interacción fluida entre interfaz y lógica del sistema.
* Bajo índice de errores.
* Código estructurado y fácilmente ampliable.

Aunque la integración con API externa queda pendiente, el prototipo cumple las funciones esenciales propias de un reproductor musical de escritorio.

Este informe proporciona evidencia suficiente del correcto funcionamiento del sistema y del proceso de verificación seguido durante el desarrollo.

---

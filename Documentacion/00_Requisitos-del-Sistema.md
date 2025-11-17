# 00 Requisitos del Sistema

## Proyecto: “Reproductor Musical”

## 1. Introducción

### 1.1 Propósito del documento

El presente documento describe los requisitos del sistema **Reproductor Musical**, desarrollado como proyecto final del curso **Algorítmica II**.  
Su objetivo es definir de forma clara las funcionalidades, restricciones y características de calidad que debe cumplir el sistema, de manera que sirva como guía para el desarrollo, pruebas y evaluación del proyecto.

### 1.2 Alcance del sistema

El **Reproductor Musical** es una aplicación de escritorio para **Windows** que permite a usuarios de PC reproducir música de forma gratuita, tanto desde archivos locales como desde canciones obtenidas a través de una API (por ejemplo, YouTube u otros servicios en línea).

El sistema permitirá:

- Gestionar canciones locales.
- Reproducir audio con controles completos de reproducción.
- Buscar y reproducir canciones en línea.
- Gestionar playlists personalizadas.
- Guardar playlists para reproducirlas posteriormente (incluso sin conexión, si las canciones están disponibles).

### 1.3 Público objetivo

El sistema está orientado a **usuarios promedio de PC** que desean escuchar música de manera sencilla, sin necesidad de configuraciones avanzadas ni registro de cuentas.

### 1.4 Definiciones, siglas y abreviaturas

- **Java**: Lenguaje de programación utilizado para el desarrollo del sistema.
- **JavaFX**: Biblioteca gráfica en Java para construir interfaces de usuario.
- **MVC/MVVM**: Patrones de arquitectura de software. En este proyecto se utiliza una variante basada en **MVVM**.
- **API**: Interfaz de programación de aplicaciones, utilizada para acceder a servicios de música en línea (por ejemplo, YouTube).

### 1.5 Referencias

- Enunciado del proyecto de curso de Algorítmica II.  
- Apuntes y material del curso relacionados con estructuras de datos, arquitectura de software y uso de Java/JavaFX.

---

## 2. Descripción general

### 2.1 Perspectiva del producto

El **Reproductor Musical** se presenta como un **prototipo de aplicación de escritorio** independiente.  
Se compone de:

- Una **interfaz gráfica** desarrollada en JavaFX.
- Una capa de **lógica de negocio** basada en servicios (gestión de reproducción, playlists, búsqueda, etc.).
- Una capa de **acceso a datos** que maneja archivos locales y la integración con una API externa para obtener canciones en línea.

### 2.2 Funcionalidades generales

De manera general, el sistema permitirá:

- **Canciones locales**:
  - Agregar canciones locales a la biblioteca o playlist.
  - Eliminar canciones locales de la lista.
  - Reproducir canciones locales mediante el reproductor integrado.
  - Controlar la reproducción mediante botones y barras de control.

- **Canciones desde YouTube/API**:
  - Buscar canciones en línea mediante un cuadro de búsqueda.
  - Mostrar resultados de búsqueda provenientes de la API.
  - Reproducir canciones en streaming.
  - Agregar canciones obtenidas por API a una playlist.
  - Volver a reproducir playlists previamente guardadas (sin internet, siempre que las canciones estén disponibles localmente o según lo definido en el prototipo).

- **Playlists**:
  - Crear nuevas playlists.
  - Editar playlists (agregar canciones, eliminar canciones, cambiar el nombre).
  - Cambiar el orden de las canciones dentro de una playlist.
  - Eliminar playlists completas.

- **Controles del reproductor**:
  - Play, Pause, Stop.
  - Siguiente (Next) y Anterior (Prev).
  - Control de volumen mediante barra.
  - Mute / Unmute.
  - Repetir canción.
  - Repetir playlist completa.
  - Modo aleatorio (shuffle) y modo normal.
  - Barra de progreso de la canción para adelantar o retroceder manualmente.

### 2.3 Características de los usuarios

Los usuarios objetivo:

- Poseen conocimientos básicos de uso de una computadora y aplicaciones de escritorio.
- No requieren conocimientos técnicos de programación.
- Esperan una interfaz clara, con botones y textos intuitivos.

### 2.4 Suposiciones y dependencias

- El usuario dispone de un equipo con **sistema operativo Windows**.
- El usuario tiene instalado **Java** (o un ejecutable que incluya el runtime necesario).
- Para la búsqueda y reproducción de canciones desde la API (por ejemplo, YouTube), se asume que el usuario cuenta con conexión a internet.
- Se utilizarán **librerías externas** para el manejo de la API de música en línea (según lo permita el proyecto académico).

---

## 3. Requisitos funcionales

> Nota: Los requisitos funcionales se identifican como **RF-xx**.

### 3.1 Gestión de canciones locales

- **RF-01**: El sistema debe permitir **agregar canciones locales** a la aplicación seleccionando archivos de audio desde el sistema de archivos de Windows.
- **RF-02**: El sistema debe permitir **listar las canciones locales agregadas** en una tabla o lista dentro de la interfaz.
- **RF-03**: El sistema debe permitir **eliminar canciones locales** de la lista o playlist seleccionada.
- **RF-04**: El sistema debe permitir **reproducir una canción local** seleccionada por el usuario.

### 3.2 Búsqueda y gestión de canciones en línea (API)

- **RF-05**: El sistema debe permitir al usuario **buscar canciones en línea** mediante un cuadro de búsqueda de texto.
- **RF-06**: El sistema debe mostrar una **lista de resultados de búsqueda** provenientes de la API.
- **RF-07**: El sistema debe permitir **reproducir una canción en línea** seleccionada de la lista de resultados.
- **RF-08**: El sistema debe permitir **agregar canciones en línea** (de la API) a una playlist existente o a una nueva playlist.
- **RF-09**: El sistema debe permitir **reproducir playlists que incluyan canciones obtenidas de la API**, siempre que la conexión a internet esté disponible o según lo definido en el prototipo.

### 3.3 Gestión de playlists

- **RF-10**: El sistema debe permitir al usuario **crear una nueva playlist**, asignándole un nombre.
- **RF-11**: El sistema debe permitir **renombrar una playlist** existente.
- **RF-12**: El sistema debe permitir **eliminar una playlist** completa.
- **RF-13**: El sistema debe permitir **agregar canciones** (locales o en línea) a una playlist.
- **RF-14**: El sistema debe permitir **eliminar canciones** de una playlist.
- **RF-15**: El sistema debe permitir **cambiar el orden de las canciones** dentro de una playlist (por ejemplo, mediante botones de subir/bajar o drag & drop, según implementación).

### 3.4 Controles de reproducción

- **RF-16**: El sistema debe permitir **iniciar la reproducción** de una canción (botón Play).
- **RF-17**: El sistema debe permitir **pausar la reproducción** (botón Pause).
- **RF-18**: El sistema debe permitir **detener completamente la reproducción** (botón Stop).
- **RF-19**: El sistema debe permitir **pasar a la siguiente canción** de la lista o playlist (botón Next).
- **RF-20**: El sistema debe permitir **volver a la canción anterior** de la lista o playlist (botón Prev).
- **RF-21**: El sistema debe mostrar y permitir manipular una **barra de progreso** de la canción en reproducción para adelantar o retroceder.
- **RF-22**: El sistema debe permitir **ajustar el volumen** mediante una barra deslizante.
- **RF-23**: El sistema debe permitir **silenciar el audio (Mute)** y restaurarlo.
- **RF-24**: El sistema debe permitir activar el modo **repetir canción**.
- **RF-25**: El sistema debe permitir activar el modo **repetir playlist completa**.
- **RF-26**: El sistema debe permitir activar el modo **aleatorio (shuffle)** y el modo **normal** de reproducción.

### 3.5 Persistencia básica

- **RF-27**: El sistema debe permitir **guardar las playlists** creadas por el usuario para que estén disponibles al reiniciar la aplicación.
- **RF-28**: El sistema debe permitir **cargar las playlists guardadas** al iniciar la aplicación.

---

## 4. Requisitos no funcionales

> Nota: Los requisitos no funcionales se identifican como **RNF-xx**.

### 4.1 Rendimiento

- **RNF-01**: El sistema debe **responder de forma fluida** al reproducir canciones, evitando que la interfaz se congele durante la reproducción.
- **RNF-02**: El tiempo de respuesta al **cambiar de canción (Next/Prev)** no debe ser excesivo, de forma que el usuario perciba el cambio casi inmediato.
- **RNF-03**: La búsqueda de canciones en la API debe realizarse en un tiempo razonable, considerando la latencia de la red (a nivel de prototipo).

### 4.2 Usabilidad

- **RNF-04**: La interfaz gráfica debe ser **intuitiva y fácil de usar**, con botones claramente identificados (Play, Pause, Stop, Next, Prev, etc.).
- **RNF-05**: Los iconos y textos de los controles deben ser **claros y legibles** para un usuario promedio.
- **RNF-06**: Las acciones básicas (reproducir, pausar, cambiar de canción, ajustar volumen) deben ser accesibles en **uno o dos clics**.

### 4.3 Confiabilidad

- **RNF-07**: El sistema debe **evitar errores inesperados** que detengan la ejecución de la aplicación durante la reproducción o la búsqueda de canciones.
- **RNF-08**: Ante un fallo de la API o la falta de conexión a internet, el sistema debe **notificar al usuario** sin cerrarse, y permitir seguir utilizando las canciones locales.

### 4.4 Portabilidad e instalación

- **RNF-09**: El sistema debe ejecutarse en **Windows**.
- **RNF-10**: Se recomienda que el sistema se distribuya como un **ejecutable sencillo** (por ejemplo, un `.jar` ejecutable o instalador) para facilitar su instalación y uso por parte del usuario final.

### 4.5 Requisitos tecnológicos

- **RNF-11**: El sistema debe ser desarrollado en **Java**, utilizando **JavaFX** para la interfaz gráfica.
- **RNF-12**: La arquitectura del sistema debe respetar un patrón **MVC/MVVM**, separando la lógica de presentación de la lógica de negocio y los datos.
- **RNF-13**: Se permite el uso de **librerías externas** para el manejo de la API de música en línea, siempre que sean compatibles con Java y las restricciones del curso.

---

## 5. Restricciones

- **RC-01**: El sistema está limitado al entorno de **proyecto académico** del curso Algorítmica II.
- **RC-02**: El sistema será desarrollado dentro del tiempo establecido por el curso (un ciclo académico).
- **RC-03**: La aplicación estará orientada a **Windows** y no se garantiza compatibilidad con otros sistemas operativos.
- **RC-04**: La funcionalidad de reproducción en línea depende de la **conectividad a internet** y del correcto funcionamiento de la API utilizada.
- **RC-05**: La parte local (canciones almacenadas en el equipo y playlists guardadas) debe poder **funcionar sin conexión a internet**.

---

## 6. Requisitos fuera de alcance (No objetivos)

Los siguientes puntos **no forman parte del alcance** del presente proyecto:

- **FO-01**: El sistema **no gestionará cuentas de usuario** ni autenticación. No habrá registro ni inicio de sesión.
- **FO-02**: El sistema **no descargará música desde YouTube** u otras plataformas; su objetivo es la reproducción (streaming) y la gestión de playlists a nivel de prototipo.
- **FO-03**: El sistema **no reproducirá video**, solo audio.
- **FO-04**: El sistema **no sincronizará datos** con aplicaciones móviles u otros dispositivos.
- **FO-05**: El sistema **no integrará sistemas de recomendación avanzada** (por ejemplo, sugerencias automáticas basadas en gustos del usuario).
- **FO-06**: El sistema **no contempla integración con servicios de pago**, publicidad ni modelos de negocio.

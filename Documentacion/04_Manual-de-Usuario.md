# Manual de Usuario – Reproductor Musical (JavaFX)

## Tabla de Contenidos

- [1. Introducción](#1-introducción)
- [2. Objetivo del Manual](#2-objetivo-del-manual)
- [3. A Quién Está Dirigido](#3-a-quién-está-dirigido)
- [4. Descripción General de la Interfaz](#4-descripción-general-de-la-interfaz)
- [5. Primeros Pasos](#5-primeros-pasos)
- [6. Uso del Reproductor Local](#6-uso-del-reproductor-local)
- [7. Controles de Reproducción](#7-controles-de-reproducción)
- [8. Uso de la Búsqueda (API/YouTube)](#8-uso-de-la-búsqueda-apiyoutube)
- [9. Mensajes y Errores Comunes](#9-mensajes-y-errores-comunes)
- [10. Preguntas Frecuentes (FAQ)](#10-preguntas-frecuentes-faq)

---

## 1. Introducción

El **Reproductor Musical** es una aplicación desarrollada en **JavaFX** que permite gestionar playlists, reproducir canciones locales y utilizar un buscador externo para encontrar y agregar nuevas pistas.

Este manual explica cómo utilizar cada módulo de manera clara y sencilla.

## 2. Objetivo del Manual

Brindar al usuario una guía completa sobre el funcionamiento del reproductor, explicando los pasos necesarios para:

- ✅ Reproducir música desde el equipo
- ✅ Crear y gestionar playlists
- ✅ Buscar canciones mediante API
- ✅ Controlar la reproducción (pausa, siguiente, anterior, volumen, etc.)

## 3. A Quién Está Dirigido

Este manual está pensado para:

- **Usuarios finales** que utilizarán el reproductor desde su computadora
- **Estudiantes o docentes** que deseen probar la aplicación como parte de un proyecto académico
- **Integrantes del equipo** que necesiten conocer el funcionamiento general

## 4. Descripción General de la Interfaz

### 4.1 Pantalla Principal

La pantalla principal del reproductor muestra:

- 🔝 **Barra superior** con botones de control
- 📋 **Panel izquierdo** con la lista de playlists
- 🎵 **Panel central** donde se muestra la playlist seleccionada
- 🎛️ **Panel inferior** con los controles de reproducción (Play, Pause, Stop, Next, Previous, Volumen)

### 4.2 Menús y Barras de Herramientas

Generalmente incluye:

**📁 Archivo**
- Cargar canción
- Salir

**📋 Playlist**
- Crear nueva playlist
- Eliminar playlist
- Editar playlist (agregar o quitar canciones)

**❓ Ayuda**
- Ver Manual de Usuario
- Información sobre la app

### 4.3 Panel de Playlist

Aquí se muestran todas las playlists almacenadas.

**Funciones disponibles:**
- ✅ Crear playlist
- ✅ Seleccionar una playlist
- ✅ Eliminar playlist
- ✅ Ver canciones que contiene

### 4.4 Panel de Búsqueda

Permite buscar canciones usando un servicio externo (API/YouTube).

**Incluye:**
- 🔍 Cuadro de texto para ingresar la búsqueda
- 🔘 Botón de "Buscar"
- 📜 Resultados mostrados en una lista
- ➕ Botón para agregar resultado a la playlist seleccionada

## 5. Primeros Pasos

### 5.1 Iniciar la Aplicación

1. **Ejecutar** desde Maven: `mvn clean javafx:run`
2. **O ejecutar** desde Eclipse/IntelliJ IDEA/VS Code según indicación del docente
3. Al iniciar, aparecerá la **pantalla principal** del reproductor

### 5.2 Configuración Inicial

La aplicación no requiere configuración avanzada.

**Opcionalmente puedes:**
- 📝 Crear tu primera playlist
- 🎵 Cargar canciones locales
- 🌐 Conectarte a internet si deseas usar la API de búsqueda

## 6. Uso del Reproductor Local

### 6.1 Cargar Canciones desde el Equipo

1. **Abrir** el menú `Archivo`
2. **Seleccionar** `Cargar Canción`
3. **Buscar** un archivo de audio compatible (MP3, WAV)
4. **Aceptar**
5. La canción aparecerá en la **playlist activa**

### 6.2 Crear y Gestionar Playlists

#### Crear Nueva Playlist

1. **Panel izquierdo** → botón `Nueva Playlist`
2. **Asignar** un nombre
3. **Confirmar**

#### Editar Playlist

- ➕ Agregar canciones
- ➖ Eliminar canciones
- ✏️ Cambiar nombre de playlist

#### Eliminar Playlist

1. **Seleccionar** playlist
2. **Clic** en `Eliminar`
3. **Confirmar**

## 7. Controles de Reproducción

| Control | Función |
|---------|---------|
| ▶️ **Play** | Reproduce la canción seleccionada |
| ⏸️ **Pause** | Pausa la reproducción |
| ⏹️ **Stop** | Detiene la canción |
| ⏭️ **Next** | Pasa a la siguiente canción |
| ⏮️ **Prev** | Regresa a la canción anterior |
| 🔊 **Volumen** | Ajusta el volumen |
| 🔁 **Modo Normal** | Reproduce en orden |
| 🔀 **Modo Aleatorio** | Reproduce canciones al azar |

## 8. Uso de la Búsqueda (API/YouTube)

### 8.1 Realizar una Búsqueda

1. **Ir** al panel de búsqueda
2. **Escribir** el nombre de una canción, artista o álbum
3. **Presionar** `Buscar`
4. **Esperar** los resultados (requiere conexión a internet)

### 8.2 Ver Resultados

Los resultados aparecen en una lista con:

- 🎵 **Título**
- 👤 **Artista**
- ⏱️ **Duración**
- 🖼️ **Miniatura**

### 8.3 Agregar Canciones de la Búsqueda a la Playlist

1. **Seleccionar** una playlist
2. **Elegir** una canción de los resultados
3. **Presionar** `Agregar a playlist`

### 8.4 Reproducir Canciones Obtenidas por la API

Las canciones añadidas desde la API se agregan como URLs o streams.

**Para reproducirlas:**
1. **Seleccionar** la playlist
2. **Elegir** la canción agregada
3. **Pulsar** Play

## 9. Mensajes y Errores Comunes

### 9.1 Errores de Conexión a la API

**Mensajes:**
- ❌ "No se pudo conectar al servidor"
- ❌ "Búsqueda no disponible"

**Solución:** Verifica conexión a internet o que la API esté activa

### 9.2 Archivos No Válidos

**Mensajes:**
- ❌ "Formato no compatible"
- ❌ "El archivo no contiene audio"

**Solución:** Cargar solo archivos MP3 o WAV

### 9.3 Otros Mensajes

- ⚠️ "Playlist vacía"
- ⚠️ "Debe seleccionar una playlist"
- ⚠️ "No hay canciones para reproducir"
- ⚠️ "No se encontró ningún resultado para la búsqueda"

## 10. Preguntas Frecuentes (FAQ)

### ¿Puedo reproducir canciones desde internet?
**R:** Sí, si fueron agregadas desde la API.

### ¿Puedo cargar carpetas completas?
**R:** Actualmente no; debes cargar canciones una por una.

### ¿Qué formatos soporta el reproductor?
**R:** MP3 y WAV.

### ¿Cómo puedo editar una playlist?
**R:** Seleccionándola en el panel izquierdo y usando los botones de agregar/eliminar canciones.

### ¿Necesito internet para usarlo?
**R:** Solo para buscar canciones mediante la API. El reproductor local no necesita internet.

### ¿La aplicación guarda mis playlists?
**R:** Sí, las playlists se guardan automáticamente.

---

## Información del Documento

- **Proyecto:** Reproductor Musical JavaFX
- **Versión:** 1.0.0
- **Fecha:** Noviembre 2025
- **Responsable:** Integrante 4 - UI/UX Developer

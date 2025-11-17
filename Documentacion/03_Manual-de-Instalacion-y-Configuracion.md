# Manual de Instalación y Configuración – Reproductor Musical (JavaFX)

## Tabla de Contenidos

- [1. Introducción](#1-introducción)
- [2. Objetivo del Manual](#2-objetivo-del-manual)
- [3. Público Objetivo](#3-público-objetivo)
- [4. Requisitos Previos](#4-requisitos-previos)
- [5. Instalación del Backend / API](#5-instalación-del-backend--api)
- [6. Instalación del Frontend (Reproductor Musical)](#6-instalación-del-frontend-reproductor-musical)
- [7. Verificación de la Instalación](#7-verificación-de-la-instalación)
- [8. Solución de Problemas Comunes](#8-solución-de-problemas-comunes)

---

## 1. Introducción

Este documento describe el proceso de **instalación**, **configuración** y **puesta en marcha** del Reproductor Musical desarrollado en JavaFX.

Incluye los pasos necesarios para preparar el entorno, configurar el backend/API y ejecutar correctamente la aplicación.

## 2. Objetivo del Manual

- 🎯 **Guiar** al usuario paso a paso en la instalación del sistema
- ✅ **Asegurar** que el backend/API y el reproductor JavaFX funcionen correctamente
- 📋 **Detallar** configuraciones, dependencias y pruebas de verificación

## 3. Público Objetivo

Este manual está dirigido a:

- **👨‍🎓 Estudiantes** del curso
- **👨‍🏫 Docentes** que evaluarán el proyecto
- **👥 Integrantes del equipo** que necesiten instalar la aplicación en sus computadoras
- **🔬 Usuarios** que deseen ejecutar la aplicación como demostración académica

## 4. Requisitos Previos

### 4.1 Requisitos de Hardware (Mínimos)

| Componente | Requerimiento |
|------------|---------------|
| **Procesador** | Intel i3 / AMD Ryzen 3 o superior |
| **RAM** | 4 GB (8 GB recomendado) |
| **Almacenamiento** | 500 MB libres |
| **Tarjeta de video** | Integrada suficiente para JavaFX |

### 4.2 Requisitos de Software

| Software | Versión Mínima | Descripción |
|----------|----------------|-------------|
| **Sistema Operativo** | Windows 10/11, Linux o macOS | Compatible con Java y JavaFX |
| **Java JDK** | Versión 17 o superior | Necesario para ejecutar JavaFX |
| **JavaFX SDK** | 17 o superior | Librería para interfaz gráfica |
| **Maven** | 3.6+ | Gestor de dependencias |
| **IDE** | Eclipse, IntelliJ IDEA o VS Code | Para compilar y ejecutar |
| **SceneBuilder** | Opcional | Para editar las interfaces FXML |
| **Backend/API** | Python Flask | Para búsquedas externas |
| **Conexión a internet** | Opcional | Solo si usas la API externa |

## 5. Instalación del Backend / API

> 🎵 **YT Music Downloader API:** API REST construida con FastAPI que permite buscar y descargar música desde YouTube.

### 5.1 Navegación al Directorio Backend

1. **Abrir** terminal o línea de comandos
2. **Navegar** al directorio del backend:
   ```bash
   cd BackendApi
   ```

### 5.2 Instalación de Python y Dependencias

#### **Requisitos previos:**
- **Python 3.8** o superior
- **pip** (gestor de paquetes de Python)

#### **Verificar instalación:**
```bash
python --version
pip --version
```

#### **Instalar dependencias:**
```bash
pip install -r requirements.txt
```

**Las dependencias incluyen:**
- `fastapi` - Framework web moderno
- `uvicorn` - Servidor ASGI
- `yt-dlp` - Descargador de YouTube
- `python-dotenv` - Variables de entorno

### 5.3 Instalación de FFMPEG

> ⚠️ **Importante:** FFMPEG es requerido para conversión de audio a MP3.

#### **Windows:**
1. **Descargar** desde: [https://ffmpeg.org/download.html](https://ffmpeg.org/download.html)
2. **Extraer** en `C:\ffmpeg`
3. **Agregar** `C:\ffmpeg\bin` al PATH del sistema

#### **Verificar instalación:**
```bash
ffmpeg -version
```

### 5.4 Configuración de Variables de Entorno

**Crear** archivo `.env` en la carpeta `BackendApi`:

```env
YT_QUALITY=192
DOWNLOAD_DIR=./downloads
API_PORT=8000
API_URL=http://localhost:8000
```

### 5.5 Ejecución del Servidor API

#### **Método 1: Uvicorn (Recomendado)**
```bash
uvicorn app.main:app --reload --port 8000
```

#### **Método 2: Python directo**
```bash
python -m uvicorn app.main:app --reload
```

**Debería mostrarse algo como:**
```
INFO:     Uvicorn running on http://127.0.0.1:8000 (Press CTRL+C to quit)
INFO:     Started reloader process
INFO:     Started server process
```

### 5.6 Verificación del API

**Probar** en el navegador:
- **Documentación Swagger:** [http://localhost:8000/docs](http://localhost:8000/docs)
- **Health check:** [http://localhost:8000/](http://localhost:8000/)

**Endpoints disponibles:**
- `POST /descargar` - Descargar canciones desde YouTube
- `GET /descargas` - Listar archivos MP3 disponibles
- `GET /descargas/{nombre_archivo}` - Obtener archivo específico
- `DELETE /descargas/{nombre_archivo}` - Eliminar archivo específico

## 6. Instalación del Frontend (Reproductor Musical)

### 6.1 Descarga del Proyecto

1. **Ir** al repositorio o carpeta compartida
2. **Descargar** el ZIP del reproductor JavaFX
3. **Extraer** en:
   ```
   C:\ReproductorMusical\frontend\
   ```

### 6.2 Importación en el IDE

#### **En Eclipse:**
1. **Abrir** Eclipse
2. **Ir a** `File → Import → Maven → Existing Maven Project`
3. **Seleccionar** la carpeta del proyecto
4. **Esperar** a que se descarguen las dependencias

#### **En IntelliJ IDEA:**
1. **Open Project**
2. **Seleccionar** la carpeta del frontend
3. **Ejecutar** Maven import

#### **En VS Code:**
1. **Abrir** la carpeta del proyecto
2. **Instalar** extensiones:
   - Extension Pack for Java
   - Maven for Java

### 6.3 Configuración de la URL de la API

**Buscar** en tu código la constante o archivo de configuración donde se define la URL, por ejemplo:

```java
private static final String API_URL = "http://localhost:8000/descargar";
```

**Configuraciones disponibles:**
- ✔️ **API local:** `http://localhost:8000/descargar`
- ✔️ **API remota:** `https://tu-servidor.com/descargar`
- ✔️ **Modo demo:** Sin API (solo archivos locales)

**Endpoints del BackendApi:**
- **Descargar:** `POST http://localhost:8000/descargar`
- **Listar archivos:** `GET http://localhost:8000/descargas`
- **Obtener archivo:** `GET http://localhost:8000/descargas/{nombre}`

### 6.4 Ejecución de la Aplicación

#### **Método 1: Maven (Recomendado)**
```bash
mvn clean javafx:run
```

#### **Método 2: IDE**
**En Eclipse:**
1. **Seleccionar** la clase principal: `App.java`
2. **Clic derecho** → `Run As → Java Application`

**En IntelliJ:**
1. **Presionar** `Shift + F10`

**Si JavaFX está correctamente configurado, se abrirá la ventana del reproductor.**

## 7. Verificación de la Instalación

### 7.1 Checklist de Prueba Rápida

| Paso | Resultado Esperado |
|------|-------------------|
| ✅ **Abrir el reproductor** | La ventana aparece sin errores |
| ✅ **Cargar una canción local** | Se añade a la playlist |
| ✅ **Reproducir canción** | Se escucha correctamente |
| ✅ **Modificar volumen** | Cambia sin errores |
| ✅ **Crear playlist** | Se crea y aparece en el panel |
| ✅ **Buscar canción (API)** | Muestra resultados (si hay internet) |
| ✅ **Agregar desde API** | Se agrega a la playlist seleccionada |

### 7.2 Verificación del Backend

**Probar** en el navegador:
```
http://localhost:8000/
```

**Respuesta esperada:**
```json
{
  "status": "OK",
  "message": "API funcionando correctamente"
}
```

## 8. Solución de Problemas Comunes

| Problema | Causa | Solución |
|----------|-------|----------|
| ❌ **La app no abre** | JavaFX no configurado | Revisar ruta del SDK |
| ❌ **Error "Module not found"** | Faltan dependencias | Reimportar Maven |
| ❌ **No se reproduce sonido** | Archivo no compatible | Usar MP3/WAV |
| ❌ **No funciona la API** | Sin internet o servidor apagado | Revisar puerto y conexión |
| ❌ **Ventana se cierra de inmediato** | Error en la ruta del archivo | Verificar rutas absolutas |
| ❌ **Error Maven** | JDK incorrecto | Configurar JAVA_HOME |
| ❌ **CSS no se aplica** | Ruta incorrecta | Verificar recursos |

### 8.1 Comandos de Diagnóstico

**Verificar Java:**
```bash
java -version
mvn -version
```

**Limpiar Maven:**
```bash
mvn clean install
```

**Verificar JavaFX:**
```bash
mvn javafx:run
```

---

## Información del Documento

- **Proyecto:** Reproductor Musical JavaFX
- **Versión:** 1.0.0
- **Fecha:** Noviembre 2025
- **Responsable:** Equipo de Desarrollo

---

> 💡 **Nota:** Este manual describe la instalación completa del sistema funcional.

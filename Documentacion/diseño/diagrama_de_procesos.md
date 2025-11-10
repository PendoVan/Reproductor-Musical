
---

## Objetivo del diagrama

Representar los **procesos clave** que realiza el sistema, según los **RF-001 al RF-041**, destacando:

* Flujo de datos (entradas / salidas)
* Secuencia de acciones
* Interacción entre usuario, sistema y API externa

---

## Estructura general de procesos

### **Procesos principales:**

1. **Gestión de usuarios**

   * RF-001 Registro
   * RF-002 Login
2. **Gestión de música**

   * RF-010 Búsqueda local
   * RF-011 Conexión API (YouTube)
   * RF-012 Descarga
3. **Gestión de playlists**

   * RF-020 Crear playlist
   * RF-021 Eliminar playlist
   * RF-022 Añadir canción
4. **Reproducción**

   * RF-030 Play / Pause / Next / Prev
   * RF-032 Modo Aleatorio
5. **Interfaz**

   * RF-040 Control de volumen
   * RF-041 Barra de progreso

---

## Descripción textual del flujo (para documentación)

1. **Inicio**

   * El usuario abre la aplicación.
   * El sistema muestra las opciones de **Login o Registro**.

2. **Gestión de acceso**

   * Si el usuario no está registrado → completa formulario → datos se guardan localmente.
   * Si está registrado → ingresa credenciales → el sistema valida.
   * Si es correcto, accede al menú principal.

3. **Gestión de música**

   * El usuario usa el buscador.
   * El sistema consulta primero la base local.
   * Si no encuentra resultados, se conecta a la **API de YouTube**.
   * Muestra la lista de coincidencias.
   * El usuario puede descargar canciones (RF-012).

4. **Gestión de playlists**

   * El usuario puede crear una nueva playlist.
   * Añade canciones descargadas.
   * Puede eliminar o editar playlists existentes.

5. **Reproducción**

   * El usuario selecciona una canción o playlist.
   * El sistema inicia el **GestorReproducción (Singleton)**.
   * El usuario controla la reproducción (Play, Pause, Next, Prev).
   * Puede activar **modo aleatorio** (RF-032).

6. **Interfaz**

   * Se muestran barras de **volumen (RF-040)** y **progreso (RF-041)**.
   * Los cambios visuales se actualizan en tiempo real.

7. **Fin**

   * El usuario cierra la sesión o la aplicación.
   * El sistema guarda los datos actualizados (playlists, historial, configuración).

---

## Elementos del diagrama

**Actores:**

* Usuario
* Sistema Reproductor
* API Externa (YouTube)

**Procesos internos:**

* Validar usuario
* Buscar canción
* Descargar archivo
* Crear/Añadir playlist
* Reproducir canción
* Controlar volumen/progreso

---
<img width="1536" height="1024" alt="Diagrama de procesos UML- BPMN" src="https://github.com/user-attachments/assets/2206a73a-a3d1-40a2-80dd-2135db80cf4e" />

---

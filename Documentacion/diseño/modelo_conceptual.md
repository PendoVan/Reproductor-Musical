
---

## **Entidades principales (según los RF)**

| Entidad          | Descripción                                                        | Principales atributos                                  |
| ---------------- | ------------------------------------------------------------------ | ------------------------------------------------------ |
| **Usuario**      | Representa a la persona que usa el sistema.                        | idUsuario, nombreUsuario, correo, contraseña           |
| **Cancion**      | Contiene los datos de cada canción descargada o reproducida.       | idCancion, titulo, artista, duracion, rutaArchivo      |
| **Playlist**     | Agrupa canciones según preferencia del usuario.                    | idPlaylist, nombre, fechaCreacion                      |
| **Descarga**     | Registra las descargas de canciones.                               | idDescarga, fechaDescarga, formato, rutaArchivo        |
| **Reproduccion** | Controla la interacción de reproducción (Play, Pause, Next, Prev). | idReproduccion, fechaInicio, estado, volumen, progreso |
| **APIExterna**   | Fuente de datos externos (YouTube).                                | idAPI, nombreAPI, urlBase, estadoConexion              |

---

## **Relaciones conceptuales**

| Relación                   | Descripción                                             | Cardinalidad |
| -------------------------- | ------------------------------------------------------- | ------------ |
| **Usuario – Playlist**     | Un usuario puede tener muchas playlists.                | 1 ⟶ N        |
| **Playlist – Cancion**     | Una playlist puede contener muchas canciones.           | 1 ⟶ N        |
| **Usuario – Descarga**     | Un usuario puede descargar muchas canciones.            | 1 ⟶ N        |
| **Descarga – Cancion**     | Una descarga corresponde a una canción específica.      | N ⟶ 1        |
| **Reproduccion – Cancion** | Cada reproducción hace referencia a una canción.        | 1 ⟶ 1        |
| **Sistema – APIExterna**   | El sistema se conecta a la API para obtener resultados. | 1 ⟶ 1        |

---

## **Modelo Conceptual (versión textual UML)**

```textlist
+----------------+
|    Usuario     |
+----------------+
| - idUsuario    |
| - nombreUsuario|
| - correo       |
| - contraseña   |
+----------------+
         |
         | 1..*
         |
+----------------+
|   Playlist     |
+----------------+
| - idPlaylist   |
| - nombre       |
| - fechaCreacion|
+----------------+
         |
         | 1..*
         |
+----------------+
|   Cancion      |
+----------------+
| - idCancion    |
| - titulo       |
| - artista      |
| - duracion     |
| - rutaArchivo  |
+----------------+

+----------------+
|   Descarga     |
+----------------+
| - idDescarga   |
| - fechaDescarga|
| - formato      |
| - rutaArchivo  |
+----------------+
   | N..1
   | 
   └──> Cancion

+----------------+
|  Reproduccion  |
+----------------+
| - idReproduccion |
| - fechaInicio    |
| - estado         |
| - volumen        |
| - progreso       |
+----------------+
   | 1..1
   └──> Cancion

+----------------+
|   APIExterna   |
+----------------+
| - idAPI        |
| - nombreAPI    |
| - urlBase      |
| - estadoConexion |
+----------------+
```

---
<img width="1536" height="1024" alt="Diagrama E-R" src="https://github.com/user-attachments/assets/632c74bf-3c36-41d8-bc8a-9bd93b87df4b" />

---

## **Explicación conceptual resumida**

* **Usuario**: núcleo principal del sistema. Gestiona sus credenciales, playlists y descargas.
* **Playlist**: estructura lógica que agrupa canciones. Está vinculada a un solo usuario.
* **Cancion**: elemento central de reproducción; puede pertenecer a varias playlists y descargas.
* **Descarga**: entidad puente entre Usuario y Cancion, registra cada descarga realizada.
* **Reproduccion**: gestiona los controles de audio (play, pausa, volumen, progreso).
* **APIExterna**: fuente externa (YouTube API) simulada o real para búsqueda de canciones.

---

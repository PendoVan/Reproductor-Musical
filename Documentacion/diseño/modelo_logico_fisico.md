# Modelo lógico (tablas y relaciones)

El modelo lógico transforma las entidades y relaciones del modelo conceptual en tablas relacionales con claves primarias (PK) y foráneas (FK).

## Tabla de usuario

| Campo         | Tipo de dato | Clave | Descripción                     |
| ------------- | ------------ | ----- | ------------------------------- |
| idUsuario     | INT          | PK    | Identificador único del usuario |
| nombreUsuario | VARCHAR(50)  |       | Nombre de usuario               |
| correo        | VARCHAR(100) |       | Correo del usuario              |
| contraseña    | VARCHAR(100) |       | Contraseña cifrada              |

---

## Tabla de playlist

| Campo         | Tipo de dato | Clave                   | Descripción                        |
| ------------- | ------------ | ----------------------- | ---------------------------------- |
| idPlaylist    | INT          | PK                      | Identificador único de la playlist |
| nombre        | VARCHAR(100) |                         | Nombre de la playlist              |
| fechaCreacion | DATE         |                         | Fecha de creación                  |
| idUsuario     | INT          | FK → Usuario(idUsuario) | Usuario propietario                |

---

## Tabla de cancion

| Campo       | Tipo de dato | Clave | Descripción                       |
| ----------- | ------------ | ----- | --------------------------------- |
| idCancion   | INT          | PK    | Identificador único de la canción |
| titulo      | VARCHAR(100) |       | Título de la canción              |
| artista     | VARCHAR(100) |       | Artista o grupo                   |
| duracion    | DECIMAL(5,2) |       | Duración en minutos               |
| rutaArchivo | VARCHAR(255) |       | Ubicación local del archivo       |

---

## Tabla Playlist_Cancion (relación N:N entre Playlist y Cancion)

| Campo      | Tipo de dato | Clave                         | Descripción       |
| ---------- | ------------ | ----------------------------- | ----------------- |
| idPlaylist | INT          | PK, FK → Playlist(idPlaylist) | Playlist asociada |
| idCancion  | INT          | PK, FK → Cancion(idCancion)   | Canción asociada  |

---

## Tabla de descarga

| Campo         | Tipo de dato | Clave                   | Descripción                          |
| ------------- | ------------ | ----------------------- | ------------------------------------ |
| idDescarga    | INT          | PK                      | Identificador de descarga            |
| fechaDescarga | DATETIME     |                         | Fecha y hora de la descarga          |
| formato       | VARCHAR(10)  |                         | Formato del archivo (mp3, wav, etc.) |
| rutaArchivo   | VARCHAR(255) |                         | Ruta donde se guardó                 |
| idUsuario     | INT          | FK → Usuario(idUsuario) | Usuario que descargó                 |
| idCancion     | INT          | FK → Cancion(idCancion) | Canción descargada                   |

---

## Tabla de reproduccion

| Campo          | Tipo de dato | Clave                   | Descripción                      |
| -------------- | ------------ | ----------------------- | -------------------------------- |
| idReproduccion | INT          | PK                      | Identificador de la reproducción |
| fechaInicio    | DATETIME     |                         | Fecha de inicio                  |
| estado         | VARCHAR(20)  |                         | Estado (Play, Pause, Stop)       |
| volumen        | INT          |                         | Nivel de volumen (0–100)         |
| progreso       | DECIMAL(5,2) |                         | Progreso actual de reproducción  |
| idCancion      | INT          | FK → Cancion(idCancion) | Canción reproducida              |

---

## Tabla de API externa

| Campo          | Tipo de dato | Clave | Descripción                   |
| -------------- | ------------ | ----- | ----------------------------- |
| idAPI          | INT          | PK    | Identificador del API         |
| nombreAPI      | VARCHAR(50)  |       | Nombre del servicio (YouTube) |
| urlBase        | VARCHAR(200) |       | URL base de conexión          |
| estadoConexion | VARCHAR(20)  |       | Estado actual de conexión     |

---

## Diagrama lógico UML

```scss
Usuario (1)───(N) Playlist
Usuario (1)───(N) Descarga───(1) Cancion
Playlist (N)───(N) Cancion
Cancion (1)───(1) Reproduccion
APIExterna (1)───(1) Cancion
```
---

## Diagrama fisico SQL

```sql
CREATE TABLE Usuario (
  idUsuario INT AUTO_INCREMENT PRIMARY KEY,
  nombreUsuario VARCHAR(50) NOT NULL,
  correo VARCHAR(100) NOT NULL,
  contraseña VARCHAR(100) NOT NULL
);

CREATE TABLE Playlist (
  idPlaylist INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  fechaCreacion DATE,
  idUsuario INT,
  FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
);

CREATE TABLE Cancion (
  idCancion INT AUTO_INCREMENT PRIMARY KEY,
  titulo VARCHAR(100) NOT NULL,
  artista VARCHAR(100),
  duracion DECIMAL(5,2),
  rutaArchivo VARCHAR(255)
);

CREATE TABLE Playlist_Cancion (
  idPlaylist INT,
  idCancion INT,
  PRIMARY KEY (idPlaylist, idCancion),
  FOREIGN KEY (idPlaylist) REFERENCES Playlist(idPlaylist),
  FOREIGN KEY (idCancion) REFERENCES Cancion(idCancion)
);

CREATE TABLE Descarga (
  idDescarga INT AUTO_INCREMENT PRIMARY KEY,
  fechaDescarga DATETIME,
  formato VARCHAR(10),
  rutaArchivo VARCHAR(255),
  idUsuario INT,
  idCancion INT,
  FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario),
  FOREIGN KEY (idCancion) REFERENCES Cancion(idCancion)
);

CREATE TABLE Reproduccion (
  idReproduccion INT AUTO_INCREMENT PRIMARY KEY,
  fechaInicio DATETIME,
  estado VARCHAR(20),
  volumen INT,
  progreso DECIMAL(5,2),
  idCancion INT,
  FOREIGN KEY (idCancion) REFERENCES Cancion(idCancion)
);

CREATE TABLE APIExterna (
  idAPI INT AUTO_INCREMENT PRIMARY KEY,
  nombreAPI VARCHAR(50),
  urlBase VARCHAR(200),
  estadoConexion VARCHAR(20)
);

```
---

## Interpretacion:

* Cada Usuario puede crear múltiples Playlists y realizar múltiples Descargas.
* Las Playlists se vinculan con varias Canciones mediante una tabla intermedia.
* Cada Reproducción está asociada a una sola Canción.
* La APIExterna (YouTube) es una entidad referenciada para las búsquedas y descargas.
  
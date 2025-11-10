
---

# 🎧 **Casos de Uso del Reproductor de Música**

## **Actores principales**

| Actor                     | Descripción                                                                                                                    |
| :------------------------ | :----------------------------------------------------------------------------------------------------------------------------- |
| **Usuario**               | Persona que interactúa con el reproductor: se registra, inicia sesión, busca canciones, crea playlists, reproduce música, etc. |
| **Sistema Reproductor**   | Software que gestiona la reproducción, persistencia y GUI del reproductor de música.                                           |
| **API Externa (YouTube)** | Fuente simulada o real de resultados de búsqueda de música en línea.                                                           |

---

## **Lista de Casos de Uso (CU)**

| ID        | Nombre del Caso de Uso                               | Actor Principal | Requisitos Relacionados |
| :-------- | :--------------------------------------------------- | :-------------- | :---------------------- |
| **CU-01** | Registrar nuevo usuario                              | Usuario         | RF-001                  |
| **CU-02** | Iniciar sesión                                       | Usuario         | RF-002                  |
| **CU-03** | Buscar canciones                                     | Usuario         | RF-010, RF-011          |
| **CU-04** | Descargar canción                                    | Usuario         | RF-012                  |
| **CU-05** | Crear playlist                                       | Usuario         | RF-020                  |
| **CU-06** | Eliminar playlist                                    | Usuario         | RF-021                  |
| **CU-07** | Añadir canción a playlist                            | Usuario         | RF-022                  |
| **CU-08** | Reproducir canción                                   | Usuario         | RF-030                  |
| **CU-09** | Controlar reproducción (pausar, siguiente, anterior) | Usuario         | RF-030                  |
| **CU-10** | Activar modo aleatorio                               | Usuario         | RF-032                  |
| **CU-11** | Ajustar volumen                                      | Usuario         | RF-040                  |
| **CU-12** | Mover barra de progreso                              | Usuario         | RF-041                  |

---

## **Desarrollo de casos de uso detallados**

---

### **CU-01: Registrar nuevo usuario**

**Actor principal:** Usuario
**Descripción:** Permite que un nuevo usuario cree una cuenta ingresando sus datos básicos.
**Precondición:** El usuario no debe estar registrado previamente.
**Flujo principal:**

1. El usuario selecciona la opción *“Registrarse”* en la interfaz.
2. El sistema muestra el formulario de registro.
3. El usuario ingresa su nombre, correo y contraseña.
4. El sistema valida los datos.
5. El sistema guarda la información en un archivo (`usuarios.dat`).
6. Se muestra el mensaje *“Registro exitoso”*.
   **Postcondición:** El usuario queda registrado en el sistema.
   **Excepciones:**

* [E1] Datos vacíos o inválidos.
* [E2] Usuario ya registrado.

---

### **CU-02: Iniciar sesión**

**Actor principal:** Usuario
**Descripción:** Permite al usuario autenticarse en el sistema.
**Precondición:** El usuario debe estar registrado.
**Flujo principal:**

1. El usuario ingresa su nombre y contraseña.
2. El sistema valida las credenciales.
3. Si los datos son correctos, carga su información y playlists.
   **Postcondición:** El usuario accede al reproductor.
   **Excepciones:**

* [E1] Contraseña incorrecta.
* [E2] Usuario inexistente.

---

### **CU-03: Buscar canciones**

**Actor principal:** Usuario
**Descripción:** Permite al usuario buscar canciones en la base local o mediante conexión simulada a la API de YouTube.
**Precondición:** El usuario debe estar autenticado.
**Flujo principal:**

1. El usuario introduce un término de búsqueda.
2. El sistema busca coincidencias en la base local.
3. Si no hay resultados, consulta a la API externa.
4. Muestra una lista con resultados.
   **Postcondición:** Se muestran resultados filtrados.
   **Excepciones:**

* [E1] No hay conexión a la API.
* [E2] No se encontraron coincidencias.

---

### **CU-04: Descargar canción**

**Actor principal:** Usuario
**Descripción:** Permite descargar una canción seleccionada desde los resultados de búsqueda.
**Flujo principal:**

1. El usuario elige una canción y pulsa *“Descargar”*.
2. El sistema inicia la descarga y guarda el archivo en `/music/`.
3. Muestra una notificación de éxito.
   **Postcondición:** El archivo queda disponible para reproducir.
   **Excepciones:**

* [E1] Error de conexión.
* [E2] Archivo corrupto.

---

### **CU-05: Crear playlist**

**Actor principal:** Usuario
**Descripción:** Permite crear una nueva lista de reproducción.
**Flujo principal:**

1. El usuario selecciona *“Nueva Playlist”*.
2. El sistema solicita un nombre.
3. Crea la lista vacía y la asocia al usuario.
   **Postcondición:** Se registra una nueva playlist.
   **Excepciones:**

* [E1] Nombre vacío o duplicado.

---

### **CU-06: Eliminar playlist**

**Actor principal:** Usuario
**Descripción:** Permite eliminar una playlist creada.
**Precondición:** Debe existir al menos una playlist creada.
**Flujo principal:**

1. El usuario selecciona la playlist a eliminar.
2. El sistema pide confirmación.
3. La elimina de la base y actualiza los datos del usuario.
   **Postcondición:** La playlist se elimina del sistema.

---

### **CU-07: Añadir canción a playlist**

**Actor principal:** Usuario
**Descripción:** Permite añadir canciones descargadas a una lista específica.
**Flujo principal:**

1. El usuario selecciona una canción.
2. Elige una playlist de destino.
3. El sistema agrega la canción a la lista.
   **Postcondición:** La canción aparece dentro de la playlist seleccionada.

---

### **CU-08: Reproducir canción**

**Actor principal:** Usuario
**Descripción:** Permite reproducir una canción seleccionada.
**Flujo principal:**

1. El usuario selecciona una canción y pulsa *“Play”*.
2. El sistema inicia la reproducción mediante `javax.sound.sampled`.
3. Muestra la barra de progreso.
   **Postcondición:** La canción se está reproduciendo.
   **Excepciones:**

* [E1] Archivo no encontrado.
* [E2] Error de formato.

---

### **CU-09: Controlar reproducción**

**Actor principal:** Usuario
**Descripción:** Permite pausar, reanudar o cambiar de pista.
**Flujo principal:**

1. El usuario interactúa con los botones *Play*, *Pause*, *Next*, *Prev*.
2. El sistema ejecuta la acción correspondiente.
   **Postcondición:** Se actualiza la reproducción actual.

---

### **CU-10: Activar modo aleatorio**

**Actor principal:** Usuario
**Descripción:** Permite reproducir canciones en orden aleatorio.
**Flujo principal:**

1. El usuario activa el modo *Shuffle*.
2. El sistema cambia el orden de la lista actual.
3. Reproduce una canción al azar.
   **Postcondición:** La reproducción sigue en orden aleatorio.

---

### **CU-11: Ajustar volumen**

**Actor principal:** Usuario
**Descripción:** Controla el nivel de volumen de reproducción.
**Flujo principal:**

1. El usuario mueve el control deslizante (`JSlider`).
2. El sistema ajusta el volumen mediante el mezclador de audio.
   **Postcondición:** Se modifica el nivel de volumen.

---

### **CU-12: Mover barra de progreso**

**Actor principal:** Usuario
**Descripción:** Permite avanzar o retroceder en la canción.
**Flujo principal:**

1. El usuario mueve la barra de progreso (`JProgressBar`).
2. El sistema actualiza la posición del audio.
   **Postcondición:** La reproducción continúa desde el nuevo punto.

---




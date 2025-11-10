# Patrón de Diseño: MVC + Singleton  

---

## Patrón recomendado: MVC + Singleton

El patrón más adecuado y que además puedes defender fácilmente en la exposición es una **combinación de MVC (Model-View-Controller)** y **Singleton**.  

---

## Patrón 1: MVC (Model–View–Controller)

### Propósito
Separar la **lógica de negocio (Model)**, la **interfaz gráfica (View)** y el **control de eventos (Controller)**.  
Permite que cada módulo evolucione sin afectar a los demás.

---

### Implementación 

```plaintext
src/
 ├── model/
 │    ├── Cancion.java
 │    ├── Playlist.java
 │    └── Usuario.java
 │
 ├── controller/
 │    ├── ReproductorController.java
 │    └── GestorPlaylists.java
 │
 ├── ui/
 │    ├── ReproductorView.java
 │    └── LoginView.java
 │
 └── main/
      └── App.java
```

---
# Relación entre capas
 
Usuario --> View --> Controller --> Model --> Presistence

```java
package controller;

import model.GestorReproduccion;
import ui.ReproductorView;

public class ReproductorController {
    private ReproductorView view;
    private GestorReproduccion modelo;

    public ReproductorController(ReproductorView view, GestorReproduccion modelo) {
        this.view = view;
        this.modelo = modelo;
        this.view.addPlayListener(e -> reproducir());
    }

    public void reproducir() {
        modelo.play();
        view.mostrarMensaje("Reproduciendo: " + modelo.getCancionActual());
    }
}
```

---
# Patrón 2: Singleton (para el Gestor de Reproducción)

## Proposito 
Garantizar que solo exista una instancia del reproductor en toda la aplicación.
Esto evita que dos canciones se reproduzcan simultáneamente o que se creen varios controladores.

## Ejemplo de implementacion

```java
package model;

public class GestorReproduccion {
    private static GestorReproduccion instancia;
    private Cancion cancionActual;

    // Constructor privado para impedir múltiples instancias
    private GestorReproduccion() {
    }

    // Devuelve la única instancia disponible
    public static GestorReproduccion getInstancia() {
        if (instancia == null)
            instancia = new GestorReproduccion();
        return instancia;
    }

    public void play(Cancion c) {
        this.cancionActual = c;
        System.out.println("Reproduciendo: " + c.getTitulo());
    }

    public Cancion getCancionActual() {
        return cancionActual;
    }
}

```
---
## Uso de la GUI o controlador

```java
GestorReproduccion gestor = GestorReproduccion.getInstancia();
gestor.play(cancionSeleccionada);

```
---
# Diagrama UML (MVC + Singleton):

## Descripcion
* Model: contiene las clases del dominio (Cancion, Playlist, Usuario,  GestorReproduccion como Singleton).

* Controller: maneja los eventos (ReproductorController, GestorPlaylists).

* View: interfaz gráfica (ReproductorView, LoginView).

* Las flechas representan dependencias entre capas:

  * View → Controller → Model

  * Model → notifica cambios → View


---

# Conclusion:
El uso combinado de los patrones MVC y Singleton garantiza una estructura sólida, escalable y fácilmente defendible académicamente.
Esta arquitectura permite implementar todos los Requisitos Funcionales (RF) del reproductor, favoreciendo la claridad del código, la modularidad y el cumplimiento del paradigma de la Programación Orientada a Objetos.


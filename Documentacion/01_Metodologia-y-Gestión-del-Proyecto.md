

# 01 Metodología y Gestión del Proyecto 

## **1. Introducción**

### **1.1 Propósito del documento**

El presente documento define la **metodología de trabajo**, la **planificación**, la **organización del equipo**, el **desglose de actividades**, el **modelo iterativo**, los **artefactos ágiles generados** y la **gestión de riesgos** del proyecto **Reproductor Musical**, desarrollado como proyecto final del curso **Algorítmica II**.

Este documento sirve para:

* Planificar y coordinar el trabajo del equipo.
* Controlar el avance del proyecto.
* Mostrar evidencia real de la metodología aplicada.
* Justificar las fases, entregables y decisiones tomadas durante el desarrollo.

---

### **1.2 Descripción general del proyecto**

El **Reproductor Musical** es una aplicación de escritorio desarrollada en *Java + JavaFX*, enfocada en la reproducción de música local y funcionalidades adicionales como:

* Reproducción de archivos de audio locales (.mp3, .wav).
* Controles multimedia: **play, pause, stop, siguiente, anterior, volumen, mute**.
* Gestión de playlists: crear, renombrar, eliminar, reordenar canciones.
* Integración con una **API de música online** (p. ej. YouTube) para buscar canciones.
* Arquitectura basada en **MVC/MVVM** para separar interfaz, lógica y datos.

El sistema se concibe como un prototipo académico con potencial para ampliaciones futuras.

---

### **1.3 Objetivos de planificación**

1. Definir la metodología y el proceso de trabajo.
2. Establecer roles y responsabilidades del equipo.
3. Desglosar el sistema en tareas organizadas (WBS).
4. Crear un cronograma basado en iteraciones (sprints).
5. Identificar y gestionar riesgos.
6. Documentar herramientas y repositorio del proyecto.
7. Registrar artefactos y entregables de la metodología Scrum adaptada.

---

## **2. Metodología de desarrollo**

### **2.1 Enfoque general**

El desarrollo se plantea como un proceso **iterativo e incremental**, siguiendo estos ciclos:

* Análisis
* Diseño
* Implementación
* Pruebas
* Integración
* Retroalimentación

El enfoque iterativo permite entregar avances funcionales por etapas, ajustar el trabajo según resultados y mejorar progresivamente el sistema.

---

### **2.2 Metodología “ligera tipo Scrum”**

Aunque no se implementó Scrum en su totalidad, se adoptaron los elementos esenciales:

#### **Elementos incluidos:**

* **Product Backlog**
* **Sprint Backlog**
* **Historias de Usuario**
* **Tablero Kanban** (To Do – In Progress – Done)
* **Incrementos por Sprint**
* **Pruebas por iteración**
* **Reuniones breves semanales (tipo Daily)**
* **Sprint Review** (presentación del avance)
* **Retrospectiva** (lecciones aprendidas)

#### **Ventajas obtenidas:**

* Mejor organización del trabajo.
* Control de versiones y avances por semana.
* Entregables parciales funcionales.
* Flexibilidad ante cambios.

---

## **2.3 Artefactos Scrum**

### **2.3.1 Product Backlog**

| ID    | Funcionalidad / Historia       | Prioridad | Estado      |
| ----- | ------------------------------ | --------- | ----------- |
| PB-01 | Reproducción de música local   | Alta      | Completado  |
| PB-02 | Controles multimedia           | Alta      | Completado  |
| PB-03 | Sistema de playlists           | Alta      | Completado  |
| PB-04 | GUI en JavaFX                  | Media     | Completado  |
| PB-05 | Búsqueda de canciones locales  | Media     | Pendiente   |
| PB-06 | Integración API externa        | Media     | En progreso |
| PB-07 | Ecualizador / mejoras visuales | Baja      | Pendiente   |

---

### **2.3.2 Historias de Usuario**

**HU-01 – Reproducción de música local**
*Como usuario, quiero reproducir archivos MP3 para escuchar mi música almacenada.*

**HU-02 – Control multimedia**
*Como usuario, quiero controlar la reproducción (play/pause/stop) para gestionar las canciones.*

**HU-03 – Playlists**
*Como usuario, quiero crear y organizar playlists para agrupar mis canciones favoritas.*

**HU-04 – Búsqueda local**
*Como usuario, quiero buscar canciones dentro de mi PC para encontrarlas rápidamente.*

**HU-05 – API online**
*Como usuario, quiero buscar canciones por internet para ampliarlas opciones de música disponible.*

---

### **2.3.3 Sprints del proyecto**

#### **Sprint 1 (Semana 1–2)**

**Objetivo:** reproducción local + controles básicos.
**Historias:** HU-01, HU-02.
**Entrega:**

* Módulo de reproducción con MediaPlayer.
* Controles (play, pause, stop, volumen).
* GUI básica operativa.

---

#### **Sprint 2 (Semana 3–4)**

**Objetivo:** playlists y mejoras de interfaz.
**Historias:** HU-03, HU-04.
**Entrega:**

* CRUD de playlists.
* Integración GUI–controladores.
* Navegación fluida en JavaFX.

---

#### **Sprint 3 (Semana 5–6)**

**Objetivo:** API + pruebas + documentación.
**Historias:** HU-05.
**Entrega:**

* Módulo de integración API externa.
* Pruebas unitarias y funcionales.
* Documentación final del proyecto.

---

## **2.4 Ciclo de vida del proyecto**


1. **Análisis de requisitos**
2. **Diseño arquitectónico y visual (UML + GUI)**
3. **Implementación modular por sprints**
4. **Pruebas por iteración y pruebas finales**
5. **Integración completa del sistema**
6. **Entrega final y presentación**

---

## **3. Organización del equipo**

### 3.1 Estructura del equipo

El equipo de desarrollo está organizado en los siguientes roles principales:

- **Jefe de proyecto:** coordinación general y seguimiento del avance.
- **Analista de requisitos:** levantamiento y documentación de requisitos.
- **Desarrolladores (frontend/backend):** implementación de la lógica de negocio y la interfaz.
- **Tester:** diseño y ejecución de pruebas.

## 3.2 Asignación de roles

| Rol                     | Nombre                                  | Responsabilidades principales                                                                 |
|-------------------------|-----------------------------------------|-----------------------------------------------------------------------------------------------|
| **Jefe de Proyecto**    | Steveen Maque Espinoza                  | Coordinar tareas, comunicación con el docente, organizar reuniones y controlar el cronograma. |
| **Analista de Requisitos** | Luis Alessandro Gutierrez Flores  | Recopilar y documentar requisitos, casos de uso y alcance del sistema.                       |
| **Desarrollador (Backend)** | Edwin Piero Badillo Castillo       | Implementar lógica interna del reproductor, control de medios, servicios y modelos.          |
| **Desarrollador (Frontend)** | Katherine Siesquen Torres   | Diseñar la interfaz gráfica (FXML/Scene Builder) y conectar la GUI con los controladores.    |
| **Tester**              | Rodrigo Alonso Rodríguez Pérez          | Diseñar y ejecutar pruebas, registrar errores y validar la estabilidad del sistema.          |

> Nota: de forma práctica, los integrantes colaboran también en tareas cruzadas (por ejemplo, pruebas y documentación), según las necesidades de cada fase.


---

### **3.3 Dinámica de trabajo**

* Reuniones semanales de avance.
* Coordinación mediante WhatsApp/Discord.
* Uso de GitHub para control de versiones y manejo de ramas.
* Tablero Kanban en GitHub Projects/Trello.

---

## **4. Desglose del trabajo (WBS)**

### 4.1 Estructura de Desglose de Trabajo

La **Estructura de Desglose de Trabajo (EDT/WBS)** se organiza en cinco grandes bloques:

1. **Análisis**
   - Recopilar requisitos del sistema.
   - Documentar casos de uso.
   - Definir alcance de la versión con playlists y API.
2. **Diseño**
   - Modelo de datos (canciones, playlists).
   - Diseño de la interfaz en Scene Builder.
   - Definición de arquitectura MVC/MVVM.
3. **Implementación**
   - Backend: servicios de reproducción, playlists, API.
   - Frontend: vistas FXML y controladores JavaFX.
   - Configuración de librerías JavaFX y API en el IDE.
4. **Pruebas**
   - Pruebas unitarias de métodos clave.
   - Pruebas de integración GUI + lógica.
   - Registro y corrección de errores.
5. **Entrega**
   - Documentación final (requisitos, planificación, manual).
   - Preparación de la presentación y demo.

### 4.2 Ejemplo de WBS con responsables

| ID  | Tarea                          | Entregable                          | Responsable                     |
|-----|--------------------------------|--------------------------------------|----------------------------------|
| 1.1 | Recopilar requisitos           | Documento de requisitos       | Steveen Maque Espinoza   |
| 2.1 | Modelo de datos e interfaz     | Diagrama/Md de datos y prototipo GUI | Luis Alessandro Gutierrez Flores |
| 3.1 | Backend                        | Código funcional Java (servicios)    | Edwin Piero Badillo Castillo |
| 3.2 | Frontend                       | Interfaz operativa FXML/JavaFX       |  Katherine Siesquen Torres       |
| 4.1 | Pruebas e integración          | Informe de validación                | Rodrigo Alonso Rodríguez Pérez   |
| 5.1 | Documentación y presentación   | Manual, informe y demo               | Todo el equipo                   |


### 4.3 Relación WBS ↔ Historias ↔ Sprints

| WBS | Historias   | Sprint       |
| --- | ----------- | ------------ |
| 1.1 | HU-01–HU-05 | Sprint 1     |
| 2.1 | HU-01–HU-03 | Sprint 1–2   |
| 3.1 | HU-01–HU-02 | Sprint 1     |
| 3.2 | HU-03       | Sprint 2     |
| 3.3 | HU-05       | Sprint 3     |
| 4.1 | Todas       | Sprint 3     |
| 5.1 | N/A         | Sprint Final |

---

## **5. Planificación temporal**

### **5.1 Fases estimadas**

A nivel académico, el proyecto se distribuye en varias semanas, por ejemplo:

| Fase                            | Duración estimada |
|---------------------------------|-------------------|
| Análisis y requisitos           | 1 semana          |
| Diseño de interfaz y modelo     | 1 semana          |
| Implementación funcional        | 3 semanas         |
| Pruebas e integración           | 1 semana          |
| Documentación y entrega         | 1 semana          |

Estas estimaciones sirven como guía; en la práctica, pueden solaparse tareas (por ejemplo, empezar a codificar mientras se termina el diseño).

### **5.2 Cronograma por iteraciones**

Un ejemplo de cronograma por “sprints” podría ser:

- **Semana 1 – Análisis**
  - Refinar requisitos y alcance final.
  - Acordar arquitectura (MVVM) y tecnologías definitivas.
- **Semana 2 – Diseño**
  - Maquetar pantallas principales en Scene Builder.
  - Definir modelo de datos para canciones y playlists.
- **Semana 3 – Implementación (Módulo local)**
  - Reproducción de canciones locales.
  - Controles básicos (play, pause, stop, next, prev, volumen).
- **Semana 4 – Implementación (Playlists + API)**
  - Gestión de playlists (crear, editar, eliminar, reordenar).
  - Integración básica con API (búsqueda y reproducción).
- **Semana 5 – Pruebas e Integración**
  - Pruebas de flujo completo: buscar → agregar a playlist → reproducir.
  - Corrección de errores y ajustes de interfaz.
- **Semana 6 – Documentación y Presentación**
  - Redacción de informes (requisitos, planificación, manual).
  - Preparación de diapositivas y demo final.

### **5.3 Burndown Chart**

Se puede agregar como imagen o gráfico.

---

## **6. Herramientas y gestión de configuración**

### 6.1 Tecnologías principales

- **Lenguaje:** Java (JDK 21).
- **Framework/UI:** JavaFX.
- **Diseño GUI:** Scene Builder.
- **IDE:** Eclipse IDE.
- **Control de versiones:** Git + GitHub.

### 6.2 Estrategia de ramas en Git

- **rama principal** (`master`): contiene la versión estable del proyecto.
- **ramas de funcionalidad** (`feature/…`):
  - `feature/player-local`
  - `feature/playlists`
  - `feature/api-youtube`
  - `feature/ui-mejoras`, etc.
- Flujo recomendado:
  1. Crear rama desde `main`.
  2. Desarrollar y hacer commits pequeños y descriptivos.
  3. Realizar **pull request** a `main` cuando la funcionalidad esté lista.
  4. Revisar código (al menos por otro integrante) antes de fusionar.


---

## **7. Gestión de riesgos**

### 7.1 Identificación de riesgos

Se consideran riesgos de tipo:

- **Técnicos:** problemas con versiones de Java/JavaFX, librerías, API externa.
- **Organizativos:** falta de tiempo, mala coordinación, retrasos en tareas.
- **Externos:** fallos de energía, problemas de internet, pérdida de datos.

### 7.2 Tabla de riesgos y mitigación

| ID  | Riesgo                                                            | Prob. | Impacto | Mitigación                                                                                  |
|-----|-------------------------------------------------------------------|-------|---------|---------------------------------------------------------------------------------------------|
| R1  | Retraso en entrega de módulos por sobrecarga académica           | Alta  | Medio   | Dividir tareas en hitos semanales y hacer revisiones periódicas en GitHub.                 |
| R2  | Incompatibilidad entre versiones de Java y JavaFX                | Media | Alta    | Usar una versión estándar y documentar la configuración del entorno.                       |
| R3  | Falta de experiencia con Scene Builder                           | Media | Media   | Capacitación rápida con guías oficiales y práctica en pantallas simples.                   |
| R4  | Errores de reproducción o formatos de audio no soportados       | Media | Alta    | Limitar formatos a .mp3 y .wav, validar archivos antes de reproducir.                      |
| R5  | Problemas de sincronización al trabajar con ramas en el repositorio | Media | Medio | Definir convención de commits y usar *pull requests* revisados antes de cada merge.       |
| R6  | Pérdida de archivos locales o del repositorio                    | Baja  | Alto    | Copias de seguridad periódicas en GitHub y almacenamiento en la nube.                      |

---

## **8. Seguimiento y control**
Para asegurar que el proyecto avance según lo planificado:

- Se registran tareas y estado (Pendiente / En progreso / Completado) en un tablero tipo Kanban (GitHub Projects, Trello, etc.).
- Se revisa el avance al menos una vez por semana.
- Se documentan:
  - Funcionalidades implementadas.
  - Problemas encontrados.
  - Decisiones de diseño relevantes.
  
* Seguimiento por sprint.
* Revisión de incrementos.
* Validación del avance en cada fase.

---

## **9. Conclusiones**

Este documento de **Metodología y Planificación** establece la forma de trabajo, la estructura del equipo, el desglose de tareas, el cronograma aproximado y la gestión de riesgos del proyecto **Reproductor Musical**.

Su objetivo es que el desarrollo no sea solo “programar hasta que funcione”, sino seguir un proceso ordenado y justificable académicamente, dejando evidencia de:

- Cómo se organizó el equipo.
- Qué fases siguió el proyecto.
- Qué riesgos se consideraron.
- Qué herramientas se utilizaron para coordinar el trabajo.


# Documento de Metodología y Planificación  

## Proyecto: “Reproductor Musical”

## 1. Introducción

### 1.1 Propósito del documento

El presente documento define la **metodología de trabajo**, la **organización del equipo**, el **desglose de actividades**, la **planificación temporal** y la **gestión de riesgos** del proyecto **Reproductor Musical**, desarrollado como proyecto final del curso **Algorítmica II**.

Sirve como guía para:

- Planificar y coordinar el trabajo de los integrantes.
- Controlar el avance del proyecto.
- Documentar el proceso seguido durante el desarrollo del prototipo.

### 1.2 Descripción general del proyecto

El **Reproductor Musical** es una aplicación de escritorio desarrollada en **Java** y **JavaFX**, diseñada inicialmente para reproducir archivos de audio locales y extendida, en la versión actual del proyecto, con:

- Reproducción de archivos de audio locales (.mp3, .wav).
- Controles multimedia (play, pause, stop, siguiente, anterior, volumen, mute).
- Gestión de playlists (crear, renombrar, eliminar, reordenar canciones).
- Integración con una **API de música en línea** (por ejemplo, YouTube) para buscar y reproducir canciones mediante internet.
- Uso de un patrón arquitectónico basado en **MVC/MVVM** para separar interfaz, lógica y datos.

El sistema se concibe como un **prototipo académico**, pero con una base suficientemente sólida para futuras ampliaciones (persistencia avanzada, login, más servicios online).

### 1.3 Objetivos de planificación

1. Establecer una **metodología de desarrollo clara** (iterativa e incremental).
2. Definir los **roles y responsabilidades** de cada integrante.
3. Desglosar el trabajo en **fases, actividades y tareas** (WBS).
4. Proponer un **cronograma aproximado** de ejecución.
5. Identificar **riesgos** y definir estrategias de **mitigación**.
6. Documentar el uso de **herramientas** (IDE, control de versiones, gestión de tareas).

---

## 2. Metodología de desarrollo

### 2.1 Enfoque general

El proyecto se desarrolla con un enfoque **iterativo e incremental**:

- El sistema se construye en **módulos** (reproducción local, playlists, conexión a API).
- Cada iteración incluye:  
  **análisis → diseño → implementación → pruebas → retroalimentación**.
- La planificación se organiza en **fases cortas** (tipo “sprints” académicos) de 1 a 2 semanas.

Este enfoque permite:

- Ajustar el alcance según el tiempo disponible.
- Detectar errores temprano.
- Ir integrando poco a poco la interfaz con la lógica de negocio.

### 2.2 Metodología “ligera tipo Scrum”

Sin aplicar Scrum de forma estricta, se adoptan algunas de sus ideas:

- **Backlog de tareas:** lista de funcionalidades y tareas técnicas pendientes (requisitos, diseño, código, pruebas, documentación).
- **Iteraciones semanales:** se asignan tareas a cada integrante al inicio de la semana (por ejemplo, mediante GitHub Projects o tableros tipo Kanban).
- **Revisiones periódicas:**
  - Revisión del avance al cierre de cada fase (análisis, diseño, implementación, pruebas, documentación).
  - Integración del código en la rama principal solo tras pruebas básicas.
- **Responsable de coordinación:** el Jefe de Proyecto supervisa que las tareas se completen y que no haya bloqueos.

### 2.3 Ciclo de vida del proyecto

Las fases principales del ciclo de vida del proyecto son:

1. **Análisis**
   - Recopilar requisitos funcionales y no funcionales.
   - Definir el alcance y las versiones (local + API).
2. **Diseño**
   - Modelo de datos (canciones, playlists).
   - Diseño de interfaz con **Scene Builder**.
   - Definición de la arquitectura MVC/MVVM.
3. **Implementación**
   - Módulo de reproducción (MediaPlayer).
   - Módulo de playlists.
   - Integración con API.
   - Conexión de controladores JavaFX con la interfaz FXML.
4. **Pruebas**
   - Pruebas unitarias de métodos críticos (play, pause, next, gestión de listas).
   - Pruebas de integración (flujo completo desde la interfaz).
5. **Entrega**
   - Documentación final (requisitos, planificación, manual de usuario).
   - Preparación de la presentación y demostración del sistema.

---

## 3. Organización del equipo

### 3.1 Estructura del equipo

El equipo de desarrollo está organizado en los siguientes roles principales:

- **Jefe de proyecto:** coordinación general y seguimiento del avance.
- **Analista de requisitos:** levantamiento y documentación de requisitos.
- **Desarrolladores (frontend/backend):** implementación de la lógica de negocio y la interfaz.
- **Tester:** diseño y ejecución de pruebas.

### 3.2 Asignación de roles

| Rol                     | Nombre                                  | Responsabilidades principales                                                                 |
|-------------------------|-----------------------------------------|-----------------------------------------------------------------------------------------------|
| **Jefe de Proyecto**    | Steveen Maque Espinoza                  | Coordinar tareas, comunicación con el docente, organizar reuniones y controlar el cronograma. |
| **Analista de Requisitos** | Luis Alessandro Gutierrez Flores  | Recopilar y documentar requisitos, casos de uso y alcance del sistema.                       |
| **Desarrollador (Backend)** | Edwin Piero Badillo Castillo       | Implementar lógica interna del reproductor, control de medios, servicios y modelos.          |
| **Desarrollador (Frontend)** | Katherine Siesquen Torres   | Diseñar la interfaz gráfica (FXML/Scene Builder) y conectar la GUI con los controladores.    |
| **Tester**              | Rodrigo Alonso Rodríguez Pérez          | Diseñar y ejecutar pruebas, registrar errores y validar la estabilidad del sistema.          |

> Nota: de forma práctica, los integrantes colaboran también en tareas cruzadas (por ejemplo, pruebas y documentación), según las necesidades de cada fase.

### 3.3 Dinámica de trabajo

- Reuniones (presenciales o virtuales) al inicio y fin de cada fase.
- Comunicación continua por medios acordados (WhatsApp, Discord, etc.).
- Uso de **GitHub** para:
  - Control de versiones (ramas, commits).
  - Revisión de código.
  - Registro de issues y tareas pendientes.

---

## 4. Desglose del trabajo (WBS)

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

---

## 5. Planificación temporal

### 5.1 Fases y estimaciones

A nivel académico, el proyecto se distribuye en varias semanas, por ejemplo:

| Fase                            | Duración estimada |
|---------------------------------|-------------------|
| Análisis y requisitos           | 1 semana          |
| Diseño de interfaz y modelo     | 1 semana          |
| Implementación funcional        | 3 semanas         |
| Pruebas e integración           | 1 semana          |
| Documentación y entrega         | 1 semana          |

Estas estimaciones sirven como guía; en la práctica, pueden solaparse tareas (por ejemplo, empezar a codificar mientras se termina el diseño).

### 5.2 Cronograma simplificado por iteraciones

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

---

## 6. Herramientas y gestión de configuración

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

## 7. Gestión de riesgos

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

### 7.3 Seguimiento de riesgos

- Los riesgos se revisan al cierre de cada fase.
- El **Jefe de Proyecto** lleva un registro de incidencias y acuerdos de mitigación.
- Los integrantes reportan problemas técnicos u organizativos tan pronto como aparezcan.

---

## 8. Seguimiento y control del proyecto

Para asegurar que el proyecto avance según lo planificado:

- Se registran tareas y estado (Pendiente / En progreso / Completado) en un tablero tipo Kanban (GitHub Projects, Trello, etc.).
- Se revisa el avance al menos una vez por semana.
- Se documentan:
  - Funcionalidades implementadas.
  - Problemas encontrados.
  - Decisiones de diseño relevantes.

---

## 9. Conclusiones

Este documento de **Metodología y Planificación** establece la forma de trabajo, la estructura del equipo, el desglose de tareas, el cronograma aproximado y la gestión de riesgos del proyecto **Reproductor Musical**.

Su objetivo es que el desarrollo no sea solo “programar hasta que funcione”, sino seguir un proceso ordenado y justificable académicamente, dejando evidencia de:

- Cómo se organizó el equipo.
- Qué fases siguió el proyecto.
- Qué riesgos se consideraron.
- Qué herramientas se utilizaron para coordinar el trabajo.

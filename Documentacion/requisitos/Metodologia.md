# 🔄 Metodología de Desarrollo

Para el desarrollo del proyecto **Reproductor de Música de Escritorio**, se ha seleccionado la metodología ágil **Scrum**. Esta metodología proporciona un marco de trabajo iterativo e incremental, ideal para gestionar los requisitos del proyecto y asegurar entregas funcionales constantes a lo largo del ciclo de vida del desarrollo.

## 1. Justificación de la Metodología

Se eligió **Scrum** debido a sus características clave que se alinean con las necesidades del proyecto:
* **Adaptabilidad y Flexibilidad:** Permite incorporar cambios en los requisitos (como nuevas funcionalidades descubiertas durante el desarrollo) sin alterar significativamente la planificación general.
* **Entregas Frecuentes e Incrementales:** Facilita la obtención de versiones funcionales del *software* al final de cada ciclo corto (*Sprint*), permitiendo pruebas y retroalimentación continua.
* **Fomento del Trabajo en Equipo:** Promueve la comunicación constante y la transparencia entre todos los miembros del equipo de desarrollo.
* **Enfoque en el Valor:** Prioriza el desarrollo de las funcionalidades más importantes para el usuario final desde el inicio.

## 2. Roles del Proyecto

Adaptando los roles estándar de Scrum al tamaño y contexto del equipo de desarrollo:

| Rol Scrum | Asignación en el Proyecto | Responsabilidades Principales |
| :--- | :--- | :--- |
| **Product Owner** (Dueño del Producto) | [Nombre del Responsable/Profesor] | Define la visión del producto, gestiona y prioriza el *Product Backlog* (lista de requisitos RF/RNF). Decide *qué* se construye y en qué orden. |
| **Scrum Master** (Facilitador) | [Miembro del Equipo] | Asegura que el equipo siga los principios y prácticas de Scrum. Elimina impedimentos que bloqueen el avance del equipo. |
| **Equipo de Desarrollo** | [Todos los integrantes] | Grupo autoorganizado encargado de analizar, diseñar, implementar, probar y documentar el *software*. |

## 3. Artefactos de Scrum

Los artefactos son documentos vivos que garantizan la transparencia y el registro del progreso.

### 3.1. Product Backlog (Pila del Producto)
Es una lista ordenada y priorizada de todo lo que se podría necesitar en el producto. En nuestro caso, está compuesta por todos los **Requisitos Funcionales (RF)** y **Requisitos No Funcionales (RNF)** identificados y diversificados.

### 3.2. Sprint Backlog (Pila del Sprint)
Es el subconjunto de elementos del *Product Backlog* seleccionados para ser desarrollados durante un *Sprint* específico, junto con el plan para entregarlos.

### 3.3. Incremento
Es la suma de todos los elementos del *Product Backlog* completados durante un *Sprint* y el valor de los incrementos de todos los *Sprints* anteriores. Es una versión funcional y verificable del *software* (ej. un archivo `.jar` ejecutable).

## 4. Eventos (Flujo de Trabajo)

El desarrollo se estructura en ciclos temporales fijos llamados **Sprints**, con una duración recomendada de **1 a 2 semanas**.

1.  **Sprint Planning (Planificación del Sprint):**
    * Al inicio de cada Sprint, el equipo selecciona qué requisitos del *Product Backlog* abordará.
    * Se definen las tareas técnicas necesarias (ej. "Diseñar interfaz de login en SceneBuilder", "Crear tabla de usuarios en MySQL").

2.  **Desarrollo y Daily Scrum (Reunión Diaria):**
    * El equipo trabaja en las tareas planificadas.
    * Se realizan breves reuniones diarias (opcional, según disponibilidad) para sincronizar el trabajo y reportar bloqueos.

3.  **Sprint Review (Revisión del Sprint):**
    * Al final del Sprint, el equipo presenta el **Incremento** (el *software* funcionando) a los interesados para recibir *feedback*.

4.  **Sprint Retrospective (Retrospectiva del Sprint):**
    * Reunión interna del equipo para analizar qué funcionó bien durante el Sprint, qué problemas surgieron y cómo mejorar los procesos para el siguiente ciclo.

## 5. Herramientas de Soporte

Para la implementación efectiva de Scrum se utilizan las siguientes herramientas:

* **Gestión del Backlog y Tareas:** Trello o Jira (Tableros Kanban para visualizar el flujo de trabajo: *To Do*, *In Progress*, *Done*).
* **Control de Versiones Colaborativo:** Git y GitHub/GitLab (Para la integración continua del código desarrollado por cada miembro).
* **Comunicación:** Plataformas de mensajería (Discord, Slack, WhatsApp) para mantener la comunicación fluida y rápida.

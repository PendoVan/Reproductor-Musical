# Riesgos

El presente documento identifica los **riesgos potenciales** asociados al desarrollo del proyecto **Reproductor de Música (JavaFX)** y las **estrategias de mitigación** propuestas por el equipo de desarrollo.  
Su objetivo es anticipar posibles problemas que puedan afectar el cronograma, la calidad del software o la coordinación entre los integrantes.

---

## Tipos de Riesgos

- **Riesgos técnicos:** relacionados con fallas de software, librerías o compatibilidad de versiones.  
- **Riesgos organizativos:** derivados de la gestión del tiempo, comunicación o coordinación del equipo.  
- **Riesgos externos:** factores fuera del control directo del equipo (fallas de energía, problemas de conexión, etc.).

---

## Tabla de Riesgos

| ID  | Riesgo | Probabilidad | Impacto | Mitigación |
|-----|--------|--------------|----------|-------------|
| **R1** | Retraso en la entrega de módulos por sobrecarga académica | Alta | Medio | Dividir tareas en hitos semanales y realizar revisiones periódicas en GitHub. |
| **R2** | Incompatibilidad entre versiones de Java y JavaFX | Media | Alta | Usar una versión estándar (JDK 17 + JavaFX 25) y documentar la configuración del entorno. |
| **R3** | Falta de experiencia inicial con Scene Builder | Media | Media | Capacitación interna mediante guías oficiales y práctica en diseño FXML. |
| **R4** | Errores en reproducción de audio o formatos no soportados | Media | Alta | Limitar el soporte inicial a archivos .mp3 y .wav y validar las rutas antes de cargar archivos. |
| **R5** | Problemas de sincronización al trabajar en ramas del repositorio | Media | Medio | Definir una convención de commits y usar *pull requests* revisados antes de cada fusión. |
| **R6** | Pérdida de archivos locales o del repositorio | Baja | Alto | Copias de seguridad periódicas en GitHub y almacenamiento en la nube. |

---

## Seguimiento y Control

Los riesgos serán evaluados de forma continua durante el desarrollo del proyecto.  
Cada integrante del equipo reportará los incidentes o desviaciones detectadas y el **Jefe de Proyecto** tomará las acciones necesarias para su corrección o mitigación.

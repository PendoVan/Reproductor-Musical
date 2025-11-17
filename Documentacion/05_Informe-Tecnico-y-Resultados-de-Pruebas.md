

# **05. Informe Técnico y Resultados de Pruebas**

**Servicios de integración con la API**

- **Propósito y alcance**

Los servicios de integración encapsulan la comunicación entre la aplicación de escritorio y la API remota que provee búsquedas y descargas de audio. Su objetivo es:

- Aislar la lógica HTTP en capas reutilizables.
- Serializar / deserializar modelos JSON hacia objetos Java.
- Centralizar manejo de errores y tiempo de espera.
- Facilitar pruebas unitarias e integración.

Las clases principales implicadas son ApiClient, AuthService (y su implementación), SearchService (y su implementación), y DTOs como DownloadRequest y DownloadResponse.

- **Contratos y endpoints**

Los endpoints consumidos por la aplicación son, como ejemplo en ApiClient:

- POST /descargar — Solicita la descarga de una o varias canciones. Cuerpo: JSON con lista de nombres.
- GET /descargas — Lista de archivos disponibles en el servidor. Respuesta: JSON con clave archivos.
- GET /descargas/{fileName} — Descarga un archivo específico (flujo binario).
- DELETE /descargas/{fileName} — Elimina un archivo.

Estos endpoints se consumen construyendo URIs sobre la baseUrl configurada en ApiClient, concatenando el path y codificando parámetros cuando corresponde (uso de URLEncoder).

- **Formato JSON (ejemplos)**

Los contratos JSON relevantes son:

**Request** — POST /descargar (ejemplo)

{

`  `"nombres": ["songA", "songB"]

}

(En el código se utiliza DownloadRequest que contiene la lista de nombres.)

**Response** — POST /descargar (ejemplo)

{

`  `"estadoDescargas": {

`    `"songA": "OK",

`    `"songB": "NOT\_FOUND"

`  `}

}

(En el código se utiliza DownloadResponse que mapea estadoDescargas a un Map<String,String>.)

**Response** — GET /descargas

{

`  `"archivos": ["a.mp3", "b.mp3", "c.mp3"]

}

**Descarga binaria** — GET /descargas/{fileName}\
Respuesta: Body binario (application/octet-stream o audio/mpeg). ApiClient usa HttpResponse.BodyHandlers.ofFile(...) para escribir directamente a disco.

**Serialización y librerías**
- Se emplea Jackson (com.fasterxml.jackson.databind.ObjectMapper) para serializar objetos Java a JSON y deserializar respuestas JSON a clases DTO.
- Cuando se parsean estructuras genéricas (por ejemplo Map<String,List<String>>), prestar atención a tipos y advertencias de unchecked. En casos que requieran tipos parametrizados, usar TypeReference<>.

Ejemplo:

Map<String, List<String>> result = objectMapper.readValue(json, new TypeReference<Map<String,List<String>>>(){});

**Manejo de errores y excepciones**
- Todos los errores de comunicación o parseo se normalizan en ApiException, que encapsula mensaje y causa:
  - Errores de validación de entrada— ApiException lanzada antes de invocar HTTP.
  - Códigos HTTP distintos de 200 — ApiException con mensaje que incluye statusCode y cuerpo cuando proceda.
  - Errores de red / timeout / IO — ApiException con la causa original.
  - Errores de parseo JSON — ApiException con la causa IOException o JsonProcessingException.
- ApiClient captura InterruptedException y reestablece el flag de interrupción del hilo (Thread.currentThread().interrupt()), luego lanza ApiException.
- Para descargas de archivos se revisa response.statusCode() y se lanza ApiException si no es 200.
  
**Estrategia de reintento y timeouts**
- Tiempo de espera configurable en ApiClient (por ejemplo TIMEOUT\_SECONDS).
- Política de reintento no incluida por defecto; si se desea, proponer un decorador que implemente reintentos exponiendo parámetros: número máximo de reintentos, backoff exponencial y manejo de idempotencia.
  
**Seguridad y autenticación**
- AuthService centraliza login/refresh/logout y devuelve tokens (Optional<String>) para ser almacenados por SessionManager.
- ApiClient debe permitir inyección de token en el header Authorization: Bearer {token} en peticiones que requieran autenticación.
- Nunca persistir tokens en texto plano fuera del directorio de usuario protegido. Para pruebas usar tokens dummy.
  
**Consideraciones operacionales**
- Directorio de descargas: ApiClient crea el directorio si no existe (Files.createDirectories(...)) y lanza IllegalStateException sólo en caso de fallo al crear la carpeta.
- Validar espacio disponible y permisos de escritura en despliegue real.
- Logs: registrar solicitudes y respuestas (solo metadatos, no payloads sensibles) con nivel DEBUG para diagnóstico; registrar errores con stack trace en nivel ERROR.

**Anexo técnico de pruebas**

**Objetivo del anexo**

Documentar qué pruebas existen, qué cubren y cómo ejecutarlas en local y en CI. Incluir comandos, rutas de reportes y criterios de aceptación.

**Qué se prueba (resumen)**

- **Modelos**: Song, Playlist — pruebas de getters/setters, equals, hashCode, toString, validaciones (nulos, blanks).
- **Servicios de negocio**: PlaylistService — agregar/eliminar canciones, navegación (next/prev/hasNext/hasPrevious), carga desde paths, límites, modos de reproducción.
- **Clientes HTTP**: ApiClient — validaciones de entrada, manejo de directorio de descargas, respuesta correcta en escenarios controlados (tests con servidor embebido o con inyección de HttpClient para pruebas unitarias).
- **Utilidades**: SessionManager, Config — manejo de tokens y configuración.

Pruebas que no se realizan en esta suite por decisión de alcance:

- Tests que requieren GUI de JavaFX (controladores) salvo pruebas unitarias sin inicialización UI.
- Tests de integración a la API en entornos externos (esto se realiza con pipelines de integración separados si se necesita).

**Estructura de los tests en el proyecto**

Ubicación:

Reproductor/src/test/java/reproductor/com/musica/...

Convenciones:

- Cada clase X tiene una clase de test XTest.
- Tests que requieren JavaFX deben inicializar el toolkit (Platform.startup) en @BeforeAll si manipulan ObservableList o propiedades.
- Tests que interactúan con HTTP usan:
  - servidor embebido (com.sun.net.httpserver.HttpServer) para pruebas reales de ApiClient, o
  - constructor package-private en ApiClient para inyectar HttpClient y ObjectMapper mockeados en pruebas unitarias.

**Dependencias de test (Maven)**

Agregar en pom.xml:

<!-- JUnit 5 -->

<dependency>

`  `<groupId>org.junit.jupiter</groupId>

`  `<artifactId>junit-jupiter</artifactId>

`  `<version>5.10.0</version>

`  `<scope>test</scope>

</dependency>

**Criterios de aceptación para tests**
- Todos los tests unitarios deben pasar en el pipeline principal.
- Cobertura mínima requerida (sugerida): 60% líneas en el módulo Reproductor; objetivo 80% para clases críticas (modelos y servicios).
- No admitir tests frágiles que dependan de entornos externos; todo acceso a red se simula o se aísla en pruebas de integración separadas.
  
**Buenas prácticas de pruebas**
- Tests unitarios deben ser deterministas y rápidos (< 200 ms por test idealmente).
- Separar pruebas unitarias de pruebas de integración (naming: \*IT para integración).
- Usar datos de prueba pequeños y claros; evitar grandes fixtures.
- Para pruebas que involucran archivo, usar el directorio target/test-... y limpiarlo en @AfterEach o @AfterAll.
- Documentar cualquier dependencia global (por ejemplo, variable de entorno que cambie URLs).

**Carencia de errores y warnings; tests automatizados; clean code**

**Correcciones prioritarias**
  1. Tipos genéricos: reemplazar raw types (List, Map sin parámetros) con parámetros concretos (List<Song>).
  1. @Override: añadir a todos los métodos que sobrescriben contratos.
  1. Eliminar import .\* y reemplazarlos por imports explícitos.
  1. Eliminar código muerto (métodos no referenciados); si se preserva por compatibilidad, añadir comentario justificativo.
     
**Formato y codificación**
  1. Forzar codificación UTF-8 en pom.xml y en la configuración del IDE:
     1. Maven compiler plugin:
      <plugin>
      `  `<groupId>org.apache.maven.plugins</groupId>
      `  `<artifactId>maven-compiler-plugin</artifactId>
      `  `<configuration>
      `    `<encoding>UTF-8</encoding>
      `  `</configuration>
      </plugin>
**Refactors y organización**
  1. Organización de paquetes clara y coherente:
  1. reproductor.com.musica.api
  1. reproductor.com.musica.model
  1. reproductor.com.musica.core
  1. reproductor.com.musica.controller
  1. reproductor.com.musica.util
  1. Separar DTOs (api.dto) de excepciones (api.exceptions).
  1. Mantener clases con responsabilidad única (Single Responsibility Principle).
  1. Extraer clases helper para evitar métodos muy largos.
     
**Checklist pre-merge**
  1. Código compila sin warnings críticos.
  1. Tests unitarios pasan en local.
  1. Cobertura no inferior al umbral acordado.
  1. No hay System.out.println en código productivo.
  1. No hay TODO o FIXME sin issue asociado.

Mantener documentación técnica (este informe) dentro de Documentacion/ y actualizarla cuando cambie la API.



Este informe proporciona evidencia suficiente del correcto funcionamiento del sistema y del proceso de verificación seguido durante el desarrollo.

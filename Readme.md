# Backend dev technical test

Queremos ofrecer una nueva funcionalidad a nuestros clientes que muestre productos similares al que están viendo actualmente. Para ello, acordamos con
nuestras aplicaciones de front-end crear una nueva operación de la API REST que les proporcionará el detalle del producto de los productos similares a
uno determinado.

## Arquitectura del proyecto

El proyecto se ha implementado siguiendo la arquitectura ports and adapters o arquitectura hexagonal donde tenemos

- Vertical Slicing: Para aislar los distintos módulos que componen la aplicación por concepto de negocio y no por responsabilidad técnica.
- Dentro de cada slice se definen los siguientes directorios agrupando las distintas responsabilidades:
    - Domain: Contiene la definición de las clases que forman parte del dominio del módulo así como los distintos ports que expone el mismo.
    - Infrastructure: Las implementaciones de los ports definidos en el dominio y los controladores web que exponen la API REST.
    - Application: Los distintos casos de uso disponibles para los controladores. Se encargan de orquestar los distintos ports definidos en el
      dominio.

- Se ha identificado un único vertical slice al que llamaremos _product_.

### Domain

Se define una entidad de dominio llamada ProductDetail.

- Esta entidad está acompañada por tres ValueObject que modelan los distintos atributos de la entidad. Se emplean para introducir alguna mínima
  validación en el constructor de los mismos y evitar que pueda haber objetos mal inicializados en el dominio; como efecto colateral se minimiza el
  primitive obsession.
- Se define un port llamado SimilarProductService que declara las operaciones para obtener los ids de productos similares a un producto dado, el
  detalle de un producto a partir de un id, y la lista de detalles de producto a partir de una lista de ids.
- También se declaran excepciones de dominio que se emplearán para convertir los errores de la API externa en excepciones del dominio.

### Application

En aplicación se define un único caso de uso que podrá emplear el controlador web para exponer la funcionalidad vía API REST. El caso de uso se llama
GetSimilarProducts y recibe un objeto query con el id del producto del que se quiere obtener los productos similares y devuelve una lista de productos
similares.

### Infrastructure

En infraestructura tenemos

- El controlador web que expone la API REST.
- La implementación de los ports definidos en el dominio junto a sus distintos helpers
    - Una implementación base del port llamada SimilarProductsServiceImpl que usa un cliente web para acceder a la API externa.
    - Una implementación que decora la anterior y que añade caché en memoria para la operación de obtención de un detalle de producto por id

## Detalles de implementación

A continuación se describen los detalles de implementación más relevantes de la infraestructura a la hora de conseguir resiliencia y mejoras en el
rendimiento.

### Open Feign

Como cliente web se ha utilizado Open Feign por su funcionalidad de implementación a partir de interfaces al estilo Spring Data y su arquitectura
plugabble donde resulta relativamente sencillo añadir componentes como retriers o circuit breakers.

Se configura OpenFeign con un pool de conexiones basado en HTTPClient para mitigar el coste de crear y cerrar conexiones al vuelo

### Caching

Dado que el escenario a abordar es un entorno de consulta de una api externa y alta concurrencia la primera optimización es eliminar al máximo el
tráfico de red puesto que será allí donde obtendremos la principal ganancia de rendimiento. Se usa una implementación de cache en memoria con
Caffeine para almacenar los detalles de los productos similares. Se guardará en cache el detalle de los productos encontrados así como un producto
vacio para el caso de producto no encotrado en el upstream server que lo consideraremos un resultado válido de cara a almacenarlo en cache.

Cualquier otra condición excepcional, error 500 en el upstream server, timeouts, etc. se considerará un resultado inválido no metiendo nada en caché
aunque devolviendo un resultado vacio a las capas superiores para obtener un fallo graceful.

### Circuit Breaker

Dada la alta concurrencia del escenario se ha implementado un circuit breaker basado en resilience4j para evitar ir a la red ante errores recurrentes
y obtener un resultado rápido del fallback configurado.

### Parallel Execution

Se ha agregado un pequeño pool de virtual threads para ejecutar las llamadas a la API externa para obtener los detalles de los productos por id en
paralelo

### Background Fetching

Como última mejora, intentado mejorar la calidad de los datos almacenados en cache, se ha implementado un mecanismo de background fetching que
hace un último intento de obtener el detalle de un producto por id desde el upstream server si ha fallado la obtención del mismo por timeout de la
conexión. Se intenta con un timeout mayor y una única vez. Este cliente web es más sencillo, está implementado con rest template y no tiene retriers
ni
circuit breakers.

## Testing

Distinguimos tres tipos de testing en el proyecto:

- **Tests de integración (infraestructura)**: Con la ayuda de Wiremock, definimos un catálogo de stubs del servidor externo para que
  nuestros tests no tengan dependencias externas y verificamos que la implementación de los ports de dominio hace lo que esperamos de ellos
- **Tests unitarios (dominio/applicación)**: Mockeando las dependencias de los casos de uso, verificamos que los casos de uso colaboran con los
  servicios
  de dominio tal y como esperamos.
- **Tests de integración end to end** : Nuevamente con Wiremock, y Spring Mock MVC verificamos el comportamiento de los controladores web.
- **Tests de Aceptación** : Con Rest Assured levantamos un servidor real por el puerto 5000 y verificamos el correcto funcionamiento de nuestra
  aplicación

Configuramos maven para que el plugin Surefire se encargue de los tests de integración y unitarios en la fase de test y el plugin Failsafe ejecute los
test de aceptación en la fase verify

**Nota** Los test de aceptación requieren de la infraestrura real levantada con docker compose

## Ejecución del proyecto

El proyecto tiene como dependencias runtime java 25 y maven 3.8.5. Se han definido dos modos de ejecución

### Modo standalone

Si el host dispone de java 25 y maven 3.8.5 se puede lanzar el script el servidor web con el siguiente comando:

```shell
./run-standalone.sh
```

También se puede lanzar la suite completa de tests con el siguiente comando:

```shell
./test-standalone.sh
```

### Modo Docker

Si se desea evitar instalar Java 25 y Maven 3.8.5 en el host, y se dispone de Docker, se puede ejecutar el proyecto mediante Docker. Para ello, basta
con ejecutar el script `run-docker.sh` que se encuentra en la raíz del proyecto:

Este script se encarga de construir la imagen Docker y lanzarla.

La imagen, es una build multistep que se encarga de pasar los test de integración y unitarios y de lanzar el servidor web. Si alguno de los test falla
no se construirá la imagen de Docker de la aplicación.

El contenedor docker se intentará añadir a la red docker _backenddevtest-main_default_ definida en el fichero docker compose de la prueba. Por tanto
debe estar corriendo este docker compose antes de hacer uso de este script.

```shell
./run-docker.sh
```

## Marco tecnológico

El proyecto se ha implementado utilizando las siguientes tecnologías:

- Java 25
- Spring Boot
- Spring Cloud
- OpenFeign
- Resilience4J

Para testing se confía en las siguientes librerías

- Spring Test
- WireMock
- Junit
- AssertJ
- Rest Assured

El entorno de desarrollo utilizado ha sido:

- IntelliJ IDEA Ultimate
- SDK Man
- Maven 3.8.5
- Ghostty
- OrbStack



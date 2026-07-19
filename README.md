# API de gestión de tiendas

API REST para la gestión de tiendas, trabajadores, aptitudes y turnos de una cadena de supermercados.

## Requisitos

Para ejecutar el proyecto mediante contenedores solamente es necesario tener instalado:

- Docker.
- Docker Compose, incluido actualmente en Docker Desktop y disponible mediante el comando `docker compose`.

No es necesario instalar Java, Maven ni Oracle localmente, ya que Docker se encarga de proporcionar estas dependencias.

## Ejecución con Docker

El archivo [`compose.yaml`](compose.yaml) levanta dos servicios:

- `database`: base de datos Oracle Free.
- `application`: API Spring Boot.

Desde el directorio raíz del proyecto, se construyen las imágenes y se arrancan ambos servicios en segundo plano con:

```bash
docker compose up --build -d
```

Durante el primer arranque, Docker crea el volumen de Oracle y ejecuta el script [`init.sql`](src/main/resources/database/init.sql), que genera las tablas y carga los datos de ejemplo. La aplicación no arranca hasta que la base de datos está disponible.

Una vez iniciada, se puede acceder a:

- API: [http://localhost:8081](http://localhost:8081)
- Swagger UI: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- Especificación OpenAPI: [http://localhost:8081/openapi.yaml](http://localhost:8081/openapi.yaml)
- Colección de Postman: [MercadonaIT.postman_collection.json](MercadonaIT.postman_collection.json)
- Oracle: `localhost:1521/FREEPDB1`



## Detener la aplicación


Para detener y eliminar los contenedores y la red creada por Docker Compose, conservando los datos de Oracle:

```bash
docker compose down
```

## Detener la aplicación y reiniciar la base de datos

Para detener los servicios y eliminar también sus volúmenes:

```bash
docker compose down -v
```

Este comando elimina el volumen `oracle-data` y, por tanto, todos los datos almacenados en la base de datos del proyecto. En el siguiente arranque,
Docker creará un volumen nuevo y volverá a ejecutar [`init.sql`](src/main/resources/database/init.sql), restaurando el esquema y los datos iniciales.

## Información técnica

| Componente | Tecnología y versión                                    |
| --- |---------------------------------------------------------|
| Lenguaje | Java 21                                                 |
| Framework | Spring Boot 4.1.0                                       |
| API web | Spring Web MVC                                          |
| Persistencia | Spring Data JPA y Hibernate                             |
| Base de datos | Oracle Free 23 (`gvenzl/oracle-free:23-slim-faststart`) |
| Driver de base de datos | Oracle JDBC (`ojdbc11`)                                 |
| Construcción | Maven 3.9.11 en la imagen Docker                        |
| Contrato de la API | OpenAPI 3.0.3                                           |
| Generación de API y DTO | OpenAPI Generator 7.23.0                                |
| Documentación interactiva | Springdoc OpenAPI 3.0.3 / Swagger UI                    |
| Mapeo de DTO y entidades | MapStruct 1.6.3                                         |
| Generación de PDF | OpenPDF 3.0.5                                           |
| Contenedores | Docker y Docker Compose                                 |


## Base de datos

El esquema incluye tiendas, secciones, aptitudes, trabajadores y las relaciones necesarias para asignar aptitudes y turnos.

- [Diagrama de entidades y relaciones](src/main/resources/database/MercadonaDiagram.mmd)
- [Script de creación y datos iniciales](src/main/resources/database/init.sql)

## Datos iniciales

El script inicial insertará en las tablas los siguientes datos iniciales:
1. `seccion`: Las 5 secciones que se listan en el ejercicio 1 junto con sus correspondientes horas mínimas necesarias.
2. `aptitud`: Las 8 aptitudes que se listan en el ejercicio 4 que son necesarias para operar las distintas secciones.
3. `seccion_aptitud`: Relaciona estas dos anteriores, siguiendo el esquema del ejercicio 4.
4. `tienda`: Tres tiendas con IDs del 1 al 3.
5. `tienda_seccion`: Para la tienda 1, se le asignan los 5 tipos de secciones, para la 2, solo Horno, Cajas y Pescadería y para la 3 ninguna sección.
6. `trabajador`: 9 trabajadores con DNI 11111111A, 22222222B y así hasta el 99999999I, todos ellos asignados a la tienda 1 y con 8 horas de trabajo.
7. `trabajador_aptitud`: A cada trabajador se le asignan las aptitudes necesarias para crearles turnos en el siguiente punto.
8. `trabajador_seccion`: A cada trabajador se le asignan dos turnos en dos secciones distintas siguiendo las aptitudes que se les da en el insert anterior.


## Consideraciones del ejercicio

A continuación se listan una serie de consideraciones personales que hice durante el ejercicio:
1. Los datos en las tablas `seccion` y `aptitud` son inmutables a través de la API.
2. Al crear una nueva tienda, se le asignan automáticamente todas las secciones que se encuentran en la tabla `seccion`.
3. Al generar informes, si el id de la tienda no devuelve resultado de la API externa, se devuelven interrogantes para imprimir la dirección.
4. De cara a la generación del informe de secciones de la tienda no cubiertos, no tener ninguna sección asignada a la 
tienda (caso de la tienda 3) se considera como tener todas las secciones cubiertas.

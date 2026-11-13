# base_product — Microservicio CRUD de productos (MongoDB)

Microservicio REST en Spring Boot 4.1.2 (Java 21) que gestiona productos en MongoDB, con validaciones,
manejo global de errores, pruebas unitarias y de integración, cobertura con JaCoCo y análisis con SonarQube.

## Requisitos

- Java 21 y Maven 3.9 (o el wrapper `mvnw` incluido)
- MongoDB en `localhost:27017` sin autenticación (la base `products_db` se crea sola con el primer registro)
- Docker Desktop, solo para levantar SonarQube

## Ejecutar

```bash
mvn spring-boot:run
```

El servicio queda en `http://127.0.0.1:8080`.

## Endpoints

| Método | Ruta | Respuesta correcta | Errores |
|---|---|---|---|
| GET | `/product` | 200 con la lista de productos | — |
| GET | `/product/{id}` | 200 con el producto | 404 |
| POST | `/product` | 201 con el producto creado | 400, 409 |
| PUT | `/product/{id}` | 200 con el producto actualizado | 400, 404, 409 |
| DELETE | `/product/{id}` | 204 sin contenido | 404 |

Cuerpo de POST y PUT:

```json
{ "name": "Keyboard", "sku": "KEY-001", "price": 499.9, "stock": 10 }
```

Validaciones: `name` y `sku` obligatorios, `price` mayor a 0 y `stock` 0 o mayor. El SKU no se puede repetir.

Formato de los errores:

```json
{ "message": "Product not found", "httpStatus": "404 NOT_FOUND", "timestamp": 1794599869034 }
```

## Pruebas y cobertura (JaCoCo)

```bash
mvn clean verify
```

- Unitarias: `ProductServiceTest` (Mockito, sin base de datos).
- Integración: `ProductControllerTest` (MockMvc + MongoDB real en la base `products_test`).
- Reporte HTML de cobertura: `target/site/jacoco/index.html`.
- El build falla si la cobertura de líneas o de ramas baja de 80%.

## Análisis estático (SonarQube)

1. Levantar SonarQube local:

   ```bash
   docker run -d --name sonarqube -p 127.0.0.1:9000:9000 sonarqube:community
   ```

2. Entrar a `http://127.0.0.1:9000` (usuario y contraseña iniciales `admin` / `admin`), cambiar la
   contraseña y generar un token en **My Account → Security**.
3. Analizar el proyecto (la cobertura de JaCoCo se envía automáticamente):

   ```bash
   mvn clean verify sonar:sonar -Dsonar.host.url=http://127.0.0.1:9000 -Dsonar.token=<TOKEN>
   ```

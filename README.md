# Servicio de Notificaciones — Prueba Técnica Sagant

Servicio REST que recibe notificaciones, las valida, persiste con estado `PENDING` y las despacha de forma asíncrona por los canales `LOG` (siempre presente) y `EMAIL` vía SMTP (MailHog en desarrollo).

## 1. Cómo levantar el proyecto

Desde cero, en una máquina con las precondiciones instaladas:

```bash
git clone https://github.com/julian-larrosa/sagant-prueba.git
cd sagant-prueba

# Levanta PostgreSQL y MailHog (el archivo .env ya viene commiteado en el repo)
docker compose up -d

# Arranca la app (Linux/macOS):
./mvnw spring-boot:run

# Arranca la app (Windows):
.\mvnw.cmd spring-boot:run
```

Para correr los tests:

```bash
# Linux/macOS:
./mvnw test

# Windows:
.\mvnw.cmd test
```

La app queda escuchando en `http://localhost:8080`. La documentación de la API (OpenAPI) queda en `http://localhost:8080/swagger-ui.html`.

## 2. Precondiciones

- **JDK 21 o superior** (el proyecto compila a `release 21` en `pom.xml`).
- **Docker + Docker Desktop** corriendo (para PostgreSQL y MailHog).
- **Git** y **Maven** (Maven wrapper `mvnw` incluido, no hace falta instalar Maven).
- **Windows**: usar `.\mvnw.cmd` en lugar de `./mvnw`.

## 3. Credenciales de prueba

- **API Key**: `key` (header `X-API-KEY: key`).
- **PostgreSQL**: `localhost:5432`, base `notifications_db`, usuario `postgres`, contraseña `HOLA123`.
- **MailHog UI**: `http://localhost:8025` (SMTP en `localhost:1025`).

Ejemplo de request:

```bash
curl -X POST http://localhost:8080/api/notifications \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: key" \
  -d '{
    "recipient": "cliente@empresa.com",
    "channel": "EMAIL",
    "subject": "Bienvenido",
    "body": "Tu cuenta fue creada exitosamente."
  }'
```

## 4. Decisiones de diseño

- **Canales**: elegí **Email vía SMTP (MailHog)** por sobre "Entrega HTTP a otro servicio". No conocía Mailhog, quería probarlo, y para el tiempo acotado me pareció la opción más eficiente de demostrar un canal "real" con reintentos.
- **Autenticación**: usé un `OncePerRequestFilter` (`ApiKeyAuthFilter`) registrado como `@Component` con `@Order(1)`, de modo que las peticiones a `/api/**` se validan **antes** de llegar al controlador. No agregué Spring Security porque el alcance (una API key por header) no lo justificaba y preferí no meter complejidad que no pudiera explicar del todo.
- **Despacho asíncrono**: `@Async("notificationExecutor")` con un `ThreadPoolTaskExecutor` propio (5/10 hilos, cola de 100). El request responde `202 Accepted` con el id y el estado inicial `PENDING` sin esperar el envío.
- **Estrategia de canales (Strategy)**: `NotificationSender` es la interfaz que implementan `LogNotificationSender` y `EmailNotificationSender`. Agregar un canal nuevo es solo implementar la interfaz y registrarlo.
- **DTOs desacoplados**: las entidades JPA nunca se exponen en el controlador; se mapean con `NotificationMapper` a `NotificationRequest`/`NotificationResponse`.
- **Resiliencia**: el canal EMAIL reintenta `max-retries` veces (por defecto 2, es decir intento inicial + 2 reintentos) antes de marcar la notificación como `FAILED` con el mensaje de error y el contador de reintentos.

## 5. Trade-offs y limitaciones

- No usé **H2** porque no lo conocía y ya tenía experiencia con PostgreSQL vía Docker; por eso elegí Postgres + docker-compose.
- Los **logs** son claros por evento pero no estructurados en JSON (el enunciado lo dejaba como plus).
- Quedó pendiente profundizar en **Mockito** y en buenas prácticas de **Spring Security** (lo dejé explícito en el código/README para ser honesto con lo que hice y lo que no).
- La autenticación es una API key fija por configuración: suficiente para la prueba, no es un sistema de credenciales rotables.
- El despacho por canal `LOG` no pasa por el mecanismo de reintentos: un log local prácticamente no falla, y el alcance de los reintentos se concentró donde hay una dependencia real (SMTP).

## 6. Reflexión de escalabilidad

Si corrieran **dos instancias** del servicio al mismo tiempo:

- **Duplicados en el despacho**: si ambas instancias leyeran la misma notificación `PENDING`, podrían enviar el mismo email dos veces. Haría falta un lock por registro (optimistic locking con `@Version`, o un estado `PROCESSING` transitorio reclamado con atomicidad) para que solo una instancia tome el trabajo.
- **Carrera al actualizar estado**: la actualización `PENDING -> SENT/FAILED` sin control de concurrencia puede pisar el estado (`lost update`). Un `WHERE status = PENDING` en el update (update condicional) o paquete de "scheduler distribuido" (ej. Quartz cluster / Spring Batch remote partioning) resolvería parte del problema.
- **Reintentos simultáneos**: si el reintento lo disparan dos workers a la vez, el contador de reintentos podría aumentar de más.
- No es necesario resolverlo en la prueba; lo dejo identificado como trabajo futuro.

## 7. Estado de la entrega

El proyecto cubre los puntos requeridos del enunciado: endpoint REST protegido por API key, validación con Bean Validation y mensajes descriptivos, persistencia con estado `PENDING`, despacho asíncrono, reintentos, estados `SENT`/`FAILED` y tests (unitario de despacho + integración con MockMvc). Si algo no quedó completo, está indicado en la sección de limitaciones.
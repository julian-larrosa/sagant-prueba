# AGENTS.md

Servicio asíncrono de notificaciones con Spring Boot 3.3.4. Java 25, Maven wrapper.
Contrato y requerimientos de la API: `specs/api-contract.yaml`, `specs/requirements.md`, `specs/architecture-and-flow.md`.

## Comandos
- Build/test/ejecución vía wrapper: `.\mvnw.cmd compile | test | package | spring-boot:run` — requiere JDK 25.
- Infra: `docker compose up -d` (PostgreSQL 16 + MailHog; SMTP :1025, UI :8025).
- Los tests REQUIEREN Postgres levantado: el test `@SpringBootTest` contextLoads inicia JPA (`ddl-auto: update`) contra localhost:5432. No hay DB embebida.

## Peculiaridad de configuración
- `application.yml` tiene credenciales de DB y la API key hardcodeadas como literales. Importa `.env` vía `spring.config.import`, pero el bloque datasource NO usa `${DB_*}`. Editar `.env` no afecta a la app — mantener `.env` y `application.yml` sincronizados manualmente. (`.env` está commiteado.)

## Estado actual (verificar antes de asumir que una feature existe)
- Implementado: entidad `Notification` + repositorio, DTOs, `GlobalExceptionHandler`, enums de canal/estado.
- NO implementado: controller de `POST /api/v1/notifications`, service, despacho `@Async`, lógica de reintentos (existe config `app.dispatch.max-retries` / `retry-delay-ms` sin usar), emisores LOG/EMAIL, `@EnableAsync`.
- RF-09 menciona un estado `PROCESSING`; el enum solo define `PENDING`/`SENT`/`FAILED`.

## Gotchas
- `config/ApiKeyAuthFilter` (chequeo de X-API-KEY en `/api/*`) NO tiene extensión `.java`, así que Maven nunca lo compila — la auth es actualmente código muerto (no hay `config/` en `target/classes`). Si lo arreglas, agrega también `.java`.
- Component scan: la clase `@SpringBootApplication` es `com.sagant.prueba.notification_project`, y todos los demás paquetes (`config`, `dto`, `exception`, `model`, `repository`) son hermanos FUERA del scan por defecto. Los componentes nuevos ahí no se registran sin `@ComponentScan(basePackages = "com.sagant.prueba")`. La migración planeada (ver Roadmap) la mueve a `com.sagant.prueba` y resuelve esto.
- No quitar los `annotationProcessorPaths` de `maven-compiler-plugin` (Lombok en Java 25).
- Convención: mensajes de validación, errores, commits y specs están en español — respetar eso en código nuevo.

## Estructura objetivo (roadmap)
- Migración planeada: mover la clase principal de `com.sagant.prueba.notification_project` a `com.sagant.prueba` y crear paquetes `config/`, `controller/`, `service/` (+ `service/channel/`), `dto/`, `exception/`, `model/`, `repository/`. Con eso el component scan cubre todo y se elimina el gotcha actual.
- `config/` planeado: `ApiKeyAuthFilter.java` (con `.java`), `AsyncConfig.java` (pool `@Async`), `FilterConfig.java` (registro del filtro).
- `service/` planeado: `NotificationService`, `NotificationDispatcherService` (reintentos), `channel/` con patrón Strategy (interfaz `NotificationSender` + `LogNotificationSender` + `EmailNotificationSender`).
- Tests planeados: `controller/NotificationControllerIntegrationTest` (MockMvc: 202/400/401), `service/NotificationDispatcherServiceTest` (reintentos).
- Docs pendientes: `README.md` (documentación central) y `.env.example` (plantilla).
- Nota: los specs reales viven en `specs/` como `requirements.md`, `api-contract.yaml`, `architecture-and-flow.md` (sin prefijo numérico).

## Ramas (workflow)
- Historial ya mergeado en `main` vía tópicos `feat/*`: `feat/specs`, `feat/dominio`, `feat/validation`, `feat/security`.
- Próximas (crear sobre `main`, en este orden):
  1. `feature/async-dispatcher`      (service, channel/, reintentos, `@EnableAsync`)
  2. `feature/rest-controller`       (`NotificationController` + `POST /api/v1/notifications`)
  3. `feature/tests`                 (integración MockMvc + unitarios de reintentos)
  4. `docs/readme-and-documentation` (`README.md`, `.env.example`)
## CONTEXT0 Y FLUJO
Servicio REST simple que reciba notificaciones y las despache. El flujo básico es
el siguiente:

- Un cliente externo envía una notificación vía HTTP POST.
- El servicio valida la solicitud y la persiste con estado PENDING.
- La notificación se procesa de forma asíncrona (no es necesario un broker de mensajería externo — alcanza con @Async de Spring o un ExecutorService).
- Un componente de despacho envía la notificación por al menos un canal adicional al log, y actualiza su estado (SENT o FAILED).
- Si el despacho falla, se reintenta al menos una vez antes de marcarla como fallida.

## Requerimientos Funcionales (RF)
- RF-01 (Endpoint de Recepción): 
    Exponer un endpoint `POST /api/v1/notifications` para recibir solicitudes de notificación.

- RF-02 (Autenticación):
    El endpoint debe requerir el header `X-API-KEY`. Si es ausente o inválido, retornar `401 Unauthorized`.

- RF-03 (Validación de Datos):
    Validar campos obligatorios (`recipient`, `channel`, `subject`, `body`). Si la validación falla, retornar `400 Bad Request` con el detalle de los errores.

- RF-04 (Persistencia Inicial):
    Toda notificación válida debe persistirse en PostgreSQL con estado inicial `PENDING` antes de su despacho.

- RF-05 (Respuesta Inmediata / Desacople): 
    Responder de inmediato al cliente con código `202 Accepted` y el identificador de la notificación, sin esperar a que ocurra el despacho real.

- RF-06 (Despacho Asíncrono): 
    El envío se realiza en segundo plano mediante un pool de hilos (`@Async`).

- RF-07 (Canales Soportados):
  - `LOG`: Log estructurado siempre presente con los datos del mensaje.
  - `EMAIL`: Envío de correo electrónico vía SMTP (MailHog en desarrollo/pruebas).

- RF-08 (Resiliencia y Reintentos):
    En caso de error en el canal de despacho, reintentar al menos 1 vez antes de marcar la notificación como `FAILED`. Si el envío es exitoso, marcar como `SENT`.

- RF-09 (Trazabilidad):
    Emitir logs en consola que registren cada transición de estado: `PENDING` -> `PROCESSING` -> `SENT` / `FAILED`.
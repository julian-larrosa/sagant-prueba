# 03 - Arquitectura y Flujo de Estados

## 1. Máquina de Estados de la Notificación

```text
       [ Request HTTP POST ]
                 │
                 ▼
        ( Persistencia DB )
                 │
                 ▼
          ┌─────────────┐
          │   PENDING   │ ──► [ Retorna 202 Accepted al cliente ]
          └─────────────┘
                 │
        ( @Async Worker )
                 │
                 ▼
           [ Despacho ]
          ┌──────┴──────┐
          │             │
     (Exitoso)       (Fallo)
          │             │
          │      [ Reintento >= 1 ]
          │      ┌──────┴──────┐
          │      │             │
          │  (Exitoso)      (Fallo)
          │      │             │
          ▼      ▼             ▼
       ┌───────────┐     ┌───────────┐
       │   SENT    │     │  FAILED   │
       └───────────┘     └───────────┘
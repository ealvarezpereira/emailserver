# 🇪🇸 📧 Email Server Microservices

Sistema de gestión y envío de emails basado en microservicios con Spring Boot, PostgreSQL y RabbitMQ.

---

## 🏗️ Arquitectura

| Servicio | Puerto | Descripción |
|---|---|---|
| email-manager | 8080 | API REST principal |
| email-sender | 8081 | Consumidor de emails |
| PostgreSQL | 5432 | Base de datos |
| RabbitMQ Management | 15672 | Panel de colas |
| pgAdmin | 5050 | Panel de base de datos |
| Dozzle (logs) | 2002 | Visor de logs Docker |

---

## ⚙️ Configuración inicial

1. Copia el fichero de variables de entorno:
   ```bash
   cp .env.example .env
   ```

2. Ajusta las credenciales en `.env` si es necesario (las por defecto funcionan sin cambios).

---

## 🚀 Arranque

```bash
docker compose up -d
```

Al arrancar, Flyway ejecuta automáticamente las migraciones:
- Crea las tablas `tusers` y `temails`
- Inserta 5 usuarios de ejemplo

Comprueba que los servicios están listos:
- http://localhost:8080/actuator/health
- http://localhost:8081/actuator/health

---

## 📋 Estados de un email

| Código | Estado | Descripción |
|---|---|---|
| 1 | Enviado | Despachado vía Feign + RabbitMQ |
| 2 | Borrador | Solo los borradores pueden editarse |
| 3 | Eliminado | Borrado lógico (no se elimina de BD) |
| 4 | Spam | Marcado como spam |

---

## 📬 Endpoints

### Usuarios — `/users`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/users` | Listar todos los usuarios |
| GET | `/users/{id}` | Obtener usuario por ID |
| GET | `/users/{id}/emails` | Emails enviados por el usuario |
| POST | `/users` | Crear usuario |
| PUT | `/users/{id}` | Actualizar usuario |
| DELETE | `/users/{id}` | Eliminar usuario |

### Emails — `/emails`

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/emails` | Insertar emails (masivo) |
| GET | `/emails` | Listar todos los emails |
| GET | `/emails?state={n}` | Filtrar por estado |
| GET | `/emails/{id}` | Obtener email por ID |
| POST | `/emails/{id}/send` | Enviar un borrador (cambia estado a Enviado y lo despacha) |
| PUT | `/emails/{id}` | Actualizar email (solo Borrador) |
| DELETE | `/emails` | Eliminar emails (masivo, body: lista de IDs) |

---

## 🧪 Ejemplo de uso completo

Este ejemplo cubre el flujo completo paso a paso partiendo de cero.

### 1. Crear un usuario nuevo

Los emails solo pueden ser enviados o recibidos por usuarios registrados en el sistema.

**Request:**
```http
POST http://localhost:8080/users
Content-Type: application/json

{
  "userEmail": "marcus@gbtec.es",
  "userName": "Marcus",
  "userSurname": "Smith"
}
```

**Response `201 Created`:**
```json
{
  "userId": 6,
  "userEmail": "marcus@gbtec.es",
  "userName": "Marcus",
  "userSurname": "Smith"
}
```

---

### 2. Insertar emails en borrador (masivo)

Insertamos dos emails como borradores. El emisor y los destinatarios deben existir en `tusers`.

**Request:**
```http
POST http://localhost:8080/emails
Content-Type: application/json

{
  "emails": [
    {
      "emailFrom": "marcus@gbtec.es",
      "emailTo": [
        {"email": "javier.garcia@example.com"},
        {"email": "maria.lopez@example.com"}
      ],
      "emailCC": [{"email": "carl@gbtec.es"}],
      "emailSubject": "Reunion de equipo",
      "emailBody": "Recordad la reunion del lunes a las 10h.",
      "state": 2
    },
    {
      "emailFrom": "marcus@gbtec.es",
      "emailTo": [{"email": "admin@proyect.es"}],
      "emailSubject": "Informe semanal",
      "emailBody": "Adjunto el informe de esta semana.",
      "state": 2
    }
  ]
}
```

**Response `201 Created`:**
```json
[
  {
    "emailId": 1,
    "emailFrom": "marcus@gbtec.es",
    "emailTo": [
      {"email": "javier.garcia@example.com"},
      {"email": "maria.lopez@example.com"}
    ],
    "emailCC": [{"email": "carl@gbtec.es"}],
    "emailSubject": "Reunion de equipo",
    "emailBody": "Recordad la reunion del lunes a las 10h.",
    "state": 2,
    "emailFromUser": {
      "userName": "Marcus",
      "userSurname": "Smith"
    }
  },
  {
    "emailId": 2,
    "emailFrom": "marcus@gbtec.es",
    "emailTo": [{"email": "admin@proyect.es"}],
    "emailCC": null,
    "emailSubject": "Informe semanal",
    "emailBody": "Adjunto el informe de esta semana.",
    "state": 2,
    "emailFromUser": {
      "userName": "Marcus",
      "userSurname": "Smith"
    }
  }
]
```

> ℹ️ El campo `emailFromUser` se rellena automáticamente con los datos del emisor desde `tusers`.

---

### 3. Actualizar un borrador

Solo los emails en estado `Borrador (2)` pueden modificarse.

**Request:**
```http
PUT http://localhost:8080/emails/1
Content-Type: application/json

{
  "emailTo": [{"email": "soporte@tecnico.com"}],
  "emailCC": [],
  "emailSubject": "Reunion de equipo - ACTUALIZADA",
  "emailBody": "La reunion ha sido cambiada al martes."
}
```

**Response `200 OK`:**
```json
{
  "emailId": 1,
  "emailFrom": "marcus@gbtec.es",
  "emailTo": [{"email": "soporte@tecnico.com"}],
  "emailCC": [],
  "emailSubject": "Reunion de equipo - ACTUALIZADA",
  "emailBody": "La reunion ha sido cambiada al martes.",
  "state": 2,
  "emailFromUser": {
    "userName": "Marcus",
    "userSurname": "Smith"
  }
}
```

---

### 4. Enviar un borrador

Una vez el email está listo, se envía con un endpoint dedicado. El sistema cambia el estado a Enviado y lo despacha por dos canales en paralelo:
- **Feign:** llamada REST síncrona directa al `email-sender`
- **RabbitMQ:** publicación en la cola para procesamiento asíncrono

**Request:**
```http
POST http://localhost:8080/emails/1/send
```

**Response `200 OK`:**
```json
{
  "emailId": 1,
  "emailFrom": "marcus@gbtec.es",
  "emailTo": [{"email": "soporte@tecnico.com"}],
  "emailCC": [],
  "emailSubject": "Reunion de equipo - ACTUALIZADA",
  "emailBody": "La reunion ha sido cambiada al martes.",
  "state": 1,
  "emailFromUser": {
    "userName": "Marcus",
    "userSurname": "Smith"
  }
}
```

En los logs del `email-sender` verás:
```
[Feign]    Received email to send: emailId=1
[RabbitMQ] Received email from queue: emailId=1
[MockMailService] Simulating email delivery: emailId=1  From: marcus@gbtec.es  To: ...
```

> ℹ️ Insertar directamente con `state: 1` sigue funcionando si no necesitas el paso de borrador.

---

### 5. Consultar los emails de un usuario

**Request:**
```http
GET http://localhost:8080/users/4/emails
```

_(El usuario con ID 4 es `carl@gbtec.es` según la migración inicial)_

**Response `200 OK`:**
```json
[
  {
    "emailId": 3,
    "emailFrom": "carl@gbtec.es",
    "emailTo": [{"email": "soporte@tecnico.com"}],
    "emailSubject": "Incidencia en produccion",
    "emailBody": "Hay un fallo en el entorno de produccion. Revisad urgente.",
    "state": 1,
    "emailFromUser": {
      "userName": "Carl",
      "userSurname": "Johnson"
    }
  }
]
```

---

### 6. Filtrar emails por estado

**Request:**
```http
GET http://localhost:8080/emails?state=2
```

Devuelve todos los emails en estado `Borrador`. Valores posibles: `1` Enviado, `2` Borrador, `3` Eliminado, `4` Spam.

---

### 7. Eliminar emails (masivo)

El borrado es lógico: cambia el estado a `Eliminado (3)`, no elimina el registro.

**Request:**
```http
DELETE http://localhost:8080/emails
Content-Type: application/json

[1, 2]
```

**Response `204 No Content`**

---

### 8. Errores comunes

**400 — Emisor o destinatario no registrado:**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Email address not registered: desconocido@noexiste.com"
}
```

**404 — Email o usuario no encontrado:**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Email not found: 99"
}
```

**422 — Intentar editar un email que no es borrador:**
```json
{
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Only emails in DRAFT state can be updated"
}
```

---

## ⏰ Tarea programada

Todos los días a las **10:00 AM**, el sistema marca automáticamente como **Spam (4)** todos los emails cuyo emisor sea `carl@gbtec.es`, independientemente de su estado actual.

---

## 🔍 Paneles de control

| Panel | URL | Credenciales |
|---|---|---|
| RabbitMQ Management | http://localhost:15672 | Ver `.env` (por defecto `rabbitmq / rabbitmq`) |
| pgAdmin | http://localhost:5050 | Ver `.env` (por defecto `pgadmin4@pgadmin.org / admin`) |
| Dozzle (logs) | http://localhost:2002 | Sin autenticación |
| Health Manager | http://localhost:8080/actuator/health | — |
| Health Sender | http://localhost:8081/actuator/health | — |
| Logs Manager | http://localhost:8080/actuator/logfile | — |
| Logs Sender | http://localhost:8081/actuator/logfile | — |

---

## 👥 Usuarios de ejemplo (precargados)

| ID | Email | Nombre |
|---|---|---|
| 1 | javier.garcia@example.com | Javier García |
| 2 | maria.lopez@example.com | María López |
| 3 | admin@proyect.es | Admin Sistema |
| 4 | carl@gbtec.es | Carl Johnson |
| 5 | soporte@tecnico.com | Soporte General |

---
---

# 🇬🇧 📧 Email Server Microservices

Microservices-based email management and delivery system built with Spring Boot, PostgreSQL and RabbitMQ.

---

## 🏗️ Architecture

| Service | Port | Description |
|---|---|---|
| email-manager | 8080 | Main REST API |
| email-sender | 8081 | Email consumer |
| PostgreSQL | 5432 | Database |
| RabbitMQ Management | 15672 | Queue panel |
| pgAdmin | 5050 | Database panel |
| Dozzle (logs) | 2002 | Docker log viewer |

---

## ⚙️ Initial Setup

1. Copy the environment variables file:
   ```bash
   cp .env.example .env
   ```

2. Adjust credentials in `.env` if needed (the defaults work out of the box).

---

## 🚀 Starting Up

```bash
docker compose up -d
```

On startup, Flyway automatically runs the migrations:
- Creates the `tusers` and `temails` tables
- Inserts 5 sample users

Check that services are ready:
- http://localhost:8080/actuator/health
- http://localhost:8081/actuator/health

---

## 📋 Email States

| Code | State | Description |
|---|---|---|
| 1 | Sent | Dispatched via Feign + RabbitMQ |
| 2 | Draft | Only drafts can be edited |
| 3 | Deleted | Logical deletion (record kept in DB) |
| 4 | Spam | Marked as spam |

---

## 📬 Endpoints

### Users — `/users`

| Method | Path | Description |
|---|---|---|
| GET | `/users` | List all users |
| GET | `/users/{id}` | Get user by ID |
| GET | `/users/{id}/emails` | Emails sent by the user |
| POST | `/users` | Create user |
| PUT | `/users/{id}` | Update user |
| DELETE | `/users/{id}` | Delete user |

### Emails — `/emails`

| Method | Path | Description |
|---|---|---|
| POST | `/emails` | Insert emails (bulk) |
| GET | `/emails` | List all emails |
| GET | `/emails?state={n}` | Filter by state |
| GET | `/emails/{id}` | Get email by ID |
| POST | `/emails/{id}/send` | Send a draft (changes state to Sent and dispatches it) |
| PUT | `/emails/{id}` | Update email (Draft only) |
| DELETE | `/emails` | Delete emails (bulk, body: list of IDs) |

---

## 🧪 Full Usage Example

This example covers the complete flow step by step from scratch.

### 1. Create a new user

Emails can only be sent or received by users registered in the system.

**Request:**
```http
POST http://localhost:8080/users
Content-Type: application/json

{
  "userEmail": "marcus@gbtec.es",
  "userName": "Marcus",
  "userSurname": "Smith"
}
```

**Response `201 Created`:**
```json
{
  "userId": 6,
  "userEmail": "marcus@gbtec.es",
  "userName": "Marcus",
  "userSurname": "Smith"
}
```

---

### 2. Insert emails as drafts (bulk)

We insert two emails as drafts. The sender and all recipients must exist in `tusers`.

**Request:**
```http
POST http://localhost:8080/emails
Content-Type: application/json

{
  "emails": [
    {
      "emailFrom": "marcus@gbtec.es",
      "emailTo": [
        {"email": "javier.garcia@example.com"},
        {"email": "maria.lopez@example.com"}
      ],
      "emailCC": [{"email": "carl@gbtec.es"}],
      "emailSubject": "Team meeting",
      "emailBody": "Remember the meeting on Monday at 10am.",
      "state": 2
    },
    {
      "emailFrom": "marcus@gbtec.es",
      "emailTo": [{"email": "admin@proyect.es"}],
      "emailSubject": "Weekly report",
      "emailBody": "Please find the weekly report attached.",
      "state": 2
    }
  ]
}
```

**Response `201 Created`:**
```json
[
  {
    "emailId": 1,
    "emailFrom": "marcus@gbtec.es",
    "emailTo": [
      {"email": "javier.garcia@example.com"},
      {"email": "maria.lopez@example.com"}
    ],
    "emailCC": [{"email": "carl@gbtec.es"}],
    "emailSubject": "Team meeting",
    "emailBody": "Remember the meeting on Monday at 10am.",
    "state": 2,
    "emailFromUser": {
      "userName": "Marcus",
      "userSurname": "Smith"
    }
  },
  {
    "emailId": 2,
    "emailFrom": "marcus@gbtec.es",
    "emailTo": [{"email": "admin@proyect.es"}],
    "emailCC": null,
    "emailSubject": "Weekly report",
    "emailBody": "Please find the weekly report attached.",
    "state": 2,
    "emailFromUser": {
      "userName": "Marcus",
      "userSurname": "Smith"
    }
  }
]
```

> ℹ️ The `emailFromUser` field is automatically populated with the sender's data from `tusers`.

---

### 3. Update a draft

Only emails in `Draft (2)` state can be modified.

**Request:**
```http
PUT http://localhost:8080/emails/1
Content-Type: application/json

{
  "emailTo": [{"email": "soporte@tecnico.com"}],
  "emailCC": [],
  "emailSubject": "Team meeting - UPDATED",
  "emailBody": "The meeting has been moved to Tuesday."
}
```

**Response `200 OK`:**
```json
{
  "emailId": 1,
  "emailFrom": "marcus@gbtec.es",
  "emailTo": [{"email": "soporte@tecnico.com"}],
  "emailCC": [],
  "emailSubject": "Team meeting - UPDATED",
  "emailBody": "The meeting has been moved to Tuesday.",
  "state": 2,
  "emailFromUser": {
    "userName": "Marcus",
    "userSurname": "Smith"
  }
}
```

---

### 4. Send a draft

Once the email is ready, send it with a dedicated endpoint. The system changes the state to Sent and dispatches it through two parallel channels:
- **Feign:** synchronous REST call directly to `email-sender`
- **RabbitMQ:** published to the queue for asynchronous processing

**Request:**
```http
POST http://localhost:8080/emails/1/send
```

**Response `200 OK`:**
```json
{
  "emailId": 1,
  "emailFrom": "marcus@gbtec.es",
  "emailTo": [{"email": "soporte@tecnico.com"}],
  "emailCC": [],
  "emailSubject": "Team meeting - UPDATED",
  "emailBody": "The meeting has been moved to Tuesday.",
  "state": 1,
  "emailFromUser": {
    "userName": "Marcus",
    "userSurname": "Smith"
  }
}
```

In the `email-sender` logs you will see:
```
[Feign]    Received email to send: emailId=1
[RabbitMQ] Received email from queue: emailId=1
[MockMailService] Simulating email delivery: emailId=1  From: marcus@gbtec.es  To: ...
```

> ℹ️ Inserting directly with `state: 1` still works if you don't need the draft step.

---

### 5. Get all emails from a user

**Request:**
```http
GET http://localhost:8080/users/4/emails
```

_(User ID 4 is `carl@gbtec.es` as per the initial migration)_

**Response `200 OK`:**
```json
[
  {
    "emailId": 3,
    "emailFrom": "carl@gbtec.es",
    "emailTo": [{"email": "soporte@tecnico.com"}],
    "emailSubject": "Production incident",
    "emailBody": "There is a failure in the production environment. Please check urgently.",
    "state": 1,
    "emailFromUser": {
      "userName": "Carl",
      "userSurname": "Johnson"
    }
  }
]
```

---

### 6. Filter emails by state

**Request:**
```http
GET http://localhost:8080/emails?state=2
```

Returns all emails in `Draft` state. Possible values: `1` Sent, `2` Draft, `3` Deleted, `4` Spam.

---

### 7. Delete emails (bulk)

Deletion is logical: changes the state to `Deleted (3)`, the record is not removed from the database.

**Request:**
```http
DELETE http://localhost:8080/emails
Content-Type: application/json

[1, 2]
```

**Response `204 No Content`**

---

### 8. Common errors

**400 — Sender or recipient not registered:**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Email address not registered: unknown@notexists.com"
}
```

**404 — Email or user not found:**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Email not found: 99"
}
```

**422 — Attempting to edit a non-draft email:**
```json
{
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Only emails in DRAFT state can be updated"
}
```

---

## ⏰ Scheduled Task

Every day at **10:00 AM**, the system automatically marks as **Spam (4)** all emails whose sender is `carl@gbtec.es`, regardless of their current state.

---

## 🔍 Control Panels

| Panel | URL | Credentials |
|---|---|---|
| RabbitMQ Management | http://localhost:15672 | See `.env` (default: `rabbitmq / rabbitmq`) |
| pgAdmin | http://localhost:5050 | See `.env` (default: `pgadmin4@pgadmin.org / admin`) |
| Dozzle (logs) | http://localhost:2002 | No authentication |
| Health Manager | http://localhost:8080/actuator/health | — |
| Health Sender | http://localhost:8081/actuator/health | — |
| Logs Manager | http://localhost:8080/actuator/logfile | — |
| Logs Sender | http://localhost:8081/actuator/logfile | — |

---

## 👥 Sample Users (preloaded)

| ID | Email | Name |
|---|---|---|
| 1 | javier.garcia@example.com | Javier García |
| 2 | maria.lopez@example.com | María López |
| 3 | admin@proyect.es | Admin Sistema |
| 4 | carl@gbtec.es | Carl Johnson |
| 5 | soporte@tecnico.com | Soporte General |

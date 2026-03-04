# 📧 Email Server Microservices

---

## 🇪🇸
#### Este proyecto es un sistema de gestión y envío de correos electrónicos basado en microservicios utilizando Spring Boot, PostgreSQL y RabbitMQ.

### 🚀 Arquitectura del Sistema
* **Manager Service (Puerto 8080):** Lógica de negocio y persistencia.
* **Sender Service (Puerto 8081):** Envío físico de emails.
* **PostgreSQL:** Almacenamiento de datos.
* **RabbitMQ:** Mensajería asíncrona.
* **pgAdmin:** Gestión web de la base de datos (Puerto 5050).

### ⚙️ Configuración Inicial
1. Localiza el archivo ```.env.example``` en la raíz del proyecto.
2. Renómbralo a ```.env``` (quítale la extensión .example):
3. Ajusta las credenciales en el fichero si es necesario.

### 🏃 Lanzamiento
Ejecuta el siguiente comando en la carpeta raíz del proyecto (donde está el docker-compose.yml):
```bash
docker compose up -d
```

### 📊 Paneles de Control
Las credenciales establecidas en el fichero .env (o en su defecto las credenciales por defecto del docker-compose) son las necesarias para iniciar sesión en los siguientes paneles de control:
* **RabbitMQ Management:** http://localhost:15672
* **pgAdmin:** http://localhost:5050

Si quieres comprobar el estado de los servicios, puedes hacerlo a través de este enlace:
* **Health Check Manager:** http://localhost:8080/actuator/health

---

---------------------------------------------------------------------------

---

## 🇺🇸
#### This project is an email management and delivery system based on microservices using Spring Boot, PostgreSQL, and RabbitMQ.

### 🚀 System Architecture
* **Manager Service (Port 8080):** Business logic and persistence.
* **Sender Service (Port 8081):** Physical email delivery.
* **PostgreSQL:** Data storage.
* **RabbitMQ:** Asynchronous messaging.
* **pgAdmin:** Web-based database management (Port 5050).

### ⚙️ Initial Setup
1. Locate the ```.env.example``` file in the project root.
2. Rename it to ```.env``` (remove the .example extension):
3. Adjust the credentials in the file if necessary.

### 🏃 Launching
Run the following command in the root folder of the project (where docker-compose.yml is located):
```bash
docker compose up -d
```

### 📊 Control Panels
The credentials established in the .env file (or, failing that, the default credentials of docker-compose) are those required to log in to the following control panels:
* **RabbitMQ Management:** http://localhost:15672
* **pgAdmin:** http://localhost:5050

If you would like to check the status of services, you can do so via this link:
* **Manager Health Check:** http://localhost:8080/actuator/health
# 📧 Email Server Microservices

---

## 🇪🇸
#### Este proyecto es un sistema de gestión y envío de correos electrónicos basado en microservicios utilizando Spring Boot, PostgreSQL y RabbitMQ.

### 🌐 Arquitectura del Sistema
* **Manager Service:** Lógica de negocio y persistencia.
* **Sender Service:** Envío físico de emails.
* **PostgreSQL:** Almacenamiento de datos.
* **RabbitMQ:** Mensajería asíncrona.
* **pgAdmin:** Gestión web de la base de datos.
* **Dozzle:** Visor de logs de contenedores Docker en tiempo real.

### ⚙️ Configuración Inicial
1. Localiza el archivo ```.env.example``` en la raíz del proyecto.
2. Renómbralo a ```.env``` (quítale la extensión .example):
3. Ajusta las credenciales en el fichero si es necesario.

### 🚀 Lanzamiento
Ejecuta el siguiente comando en la carpeta raíz del proyecto (donde está el docker-compose.yml):
```bash
docker compose up -d
```

### 📊 Paneles de Control
Las credenciales establecidas en el fichero .env (o en su defecto las credenciales por defecto del docker-compose) son las necesarias para iniciar sesión en los siguientes paneles de control:
* **RabbitMQ Management:** http://localhost:15672
* **pgAdmin:** http://localhost:5050

Si quieres comprobar los logs de los contenedores Docker en tiempo real, puedes hacerlo a través de este enlace:
* **Dozzle:** http://localhost:2002

Si quieres comprobar el estado de los servicios, puedes hacerlo a través de estos enlaces:
* **Health Check Manager:** http://localhost:8080/actuator/health
* **Health Check Sender:** http://localhost:8081/actuator/health

Si quieres acceder al fichero de log de cada servicio, puedes hacerlo a través de estos enlaces:
* **Logfile Manager:** http://localhost:8080/actuator/logfile
* **Logfile Sender:** http://localhost:8081/actuator/logfile

---

---------------------------------------------------------------------------

---

## 🇬🇧
#### This project is an email management and delivery system based on microservices using Spring Boot, PostgreSQL, and RabbitMQ.

### 🌐 System Architecture
* **Manager Service:** Business logic and persistence.
* **Sender Service:** Physical email delivery.
* **PostgreSQL:** Data storage.
* **RabbitMQ:** Asynchronous messaging.
* **pgAdmin:** Web-based database management.
* **Dozzle:** Real-time Docker container log viewer.

### ⚙️ Initial Setup
1. Locate the ```.env.example``` file in the project root.
2. Rename it to ```.env``` (remove the .example extension):
3. Adjust the credentials in the file if necessary.

### 🚀 Launching
Run the following command in the root folder of the project (where docker-compose.yml is located):
```bash
docker compose up -d
```

### 📊 Control Panels
The credentials established in the .env file (or, failing that, the default credentials of docker-compose) are those required to log in to the following control panels:
* **RabbitMQ Management:** http://localhost:15672
* **pgAdmin:** http://localhost:5050

If you want to check the logs of Docker containers in real time, you can do so via this link:
* **Dozzle:** http://localhost:2002

If you would like to check the status of services, you can do so via those links:
* **Manager Health Check:** http://localhost:8080/actuator/health
* **Sender Health Check:** http://localhost:8081/actuator/health

If you would like to access the log file of services, you can do so via those links:
* **Logfile Manager:** http://localhost:8080/actuator/logfile
* **Logfile Sender:** http://localhost:8081/actuator/logfile
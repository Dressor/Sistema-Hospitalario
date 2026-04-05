# 🏥 RedNorte — Sistema de Gestión de Listas de Espera Hospitalarias

> **Caso Semestral — Desarrollo Fullstack III (DSY1106) — Duoc UC**  
> Plataforma inteligente para la gestión de listas de espera del Servicio Público de Salud RedNorte.

---

## 📋 Descripción del Proyecto

El **Servicio Público de Salud RedNorte** administra una red de hospitales, centros de atención primaria y clínicas especializadas. Este sistema resuelve los siguientes problemas detectados:

- 🔴 Pacientes que permanecen largos periodos en lista de espera sin información
- 🔴 Cancelaciones de citas que generan pérdida de horas médicas
- 🔴 Falta de herramientas para reasignar citas canceladas
- 🔴 Escasa visibilidad del estado de solicitudes por parte de los pacientes
- 🔴 Dificultad para analizar indicadores de listas de espera

---

## 🏗️ Arquitectura

El sistema está construido como un **microservicio REST** con arquitectura en capas, aplicando los siguientes patrones de diseño:

| Patrón | Implementación | Propósito |
|---|---|---|
| **Repository Pattern** | `*Repository.java` | Separar lógica de acceso a datos |
| **Factory Method** | `NotificacionFactory.java` | Crear notificaciones (Email/SMS) dinámicamente |
| **Circuit Breaker** | `ListaEsperaService.java` | Tolerancia a fallos en cascada |
| **Builder** | Todas las entidades (`@Builder`) | Crear objetos de forma legible |
| **DTO Pattern** | `*DTO.java` | Controlar y validar datos de entrada/salida |

### Diagrama de capas

```
[Cliente / Postman]
        ↓
[API Gateway — Spring Security + JWT]
        ↓
[Controllers] → AuthController, PacienteController,
                ListaEsperaController, CitaMedicaController
        ↓
[Services]    → PacienteService, ListaEsperaService,
                CitaMedicaService, NotificacionFactory
        ↓
[Repositories]→ PacienteRepository, ListaEsperaRepository,
                CitaMedicaRepository, UsuarioRepository
        ↓
[PostgreSQL — rednorte_db]
```

---

## 🛠️ Stack Tecnológico

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 21 (LTS) | Lenguaje base |
| Spring Boot | 3.3.x | Framework principal |
| Spring Security | 6.x | Autenticación y autorización |
| Spring Data JPA | 3.x | ORM y acceso a datos |
| PostgreSQL | 16 | Base de datos relacional |
| JWT (jjwt) | 0.12.3 | Tokens de autenticación |
| Lombok | Latest | Reducción de código boilerplate |
| Resilience4j | 2.1.0 | Circuit Breaker |
| Maven | 3.x | Gestión de dependencias |

---

## 📁 Estructura del Proyecto

```
lista-espera-service/
├── src/main/java/cl/duoc/rednorte/listaespera/
│   ├── ListaEsperaServiceApplication.java   ← Punto de entrada
│   ├── config/
│   │   ├── JwtService.java                  ← Genera y valida tokens JWT
│   │   ├── JwtAuthFilter.java               ← Intercepta cada request HTTP
│   │   └── SecurityConfig.java              ← Reglas de Spring Security
│   ├── controller/
│   │   ├── AuthController.java              ← POST /auth/login, /auth/register
│   │   ├── PacienteController.java          ← CRUD /api/v1/pacientes
│   │   ├── ListaEsperaController.java       ← CRUD /api/v1/lista-espera
│   │   └── CitaMedicaController.java        ← CRUD /api/v1/citas
│   ├── service/
│   │   ├── PacienteService.java
│   │   ├── ListaEsperaService.java          ← Incluye Circuit Breaker
│   │   ├── CitaMedicaService.java           ← Incluye lógica de notificaciones
│   │   └── NotificacionFactory.java         ← Factory Method Pattern
│   ├── repository/
│   │   ├── PacienteRepository.java
│   │   ├── ListaEsperaRepository.java
│   │   ├── CitaMedicaRepository.java
│   │   └── UsuarioRepository.java
│   ├── model/
│   │   ├── Paciente.java
│   │   ├── ListaEspera.java                 ← Enum: PENDIENTE, ASIGNADO, CANCELADO, ATENDIDO
│   │   ├── CitaMedica.java                  ← Enum: PROGRAMADA, CONFIRMADA, REALIZADA, CANCELADA, NO_ASISTIO
│   │   ├── Usuario.java
│   │   ├── Notificacion.java                ← Interface Factory Method
│   │   ├── NotificacionEmail.java
│   │   └── NotificacionSMS.java
│   ├── dto/
│   │   ├── PacienteDTO.java
│   │   ├── ListaEsperaDTO.java
│   │   └── CitaMedicaDTO.java
│   └── exception/
│       └── GlobalExceptionHandler.java      ← Manejo centralizado de errores
├── src/main/resources/
│   └── application.properties
└── pom.xml
```

---

## ⚙️ Configuración e Instalación

### Prerrequisitos

- Java 21 ([Adoptium Temurin](https://adoptium.net/))
- PostgreSQL 16 ([postgresql.org](https://www.postgresql.org/download/))
- Maven 3.x (incluido con el proyecto via `mvnw`)
- VS Code con extensiones: Extension Pack for Java, Spring Boot Extension Pack, Lombok

### 1 — Clonar el repositorio

```bash
git clone https://github.com/TU_USUARIO/lista-espera-service.git
cd lista-espera-service
```

### 2 — Crear la base de datos

```sql
CREATE DATABASE rednorte_db;
```

O desde pgAdmin 4: clic derecho en Databases → Create → Database → nombre: `rednorte_db`.

### 3 — Ejecutar los scripts de datos

Desde pgAdmin 4 o cualquier cliente SQL, ejecuta los scripts en este orden:

```
scripts/
├── 01_schema.sql        ← Crea todas las tablas
├── 02_usuarios.sql      ← Usuarios del sistema
├── 03_pacientes.sql     ← 50 pacientes ficticios
├── 04_lista_espera.sql  ← Solicitudes en distintos estados
└── 05_citas_medicas.sql ← Citas programadas, realizadas, canceladas
```

### 4 — Configurar application.properties

Abre `src/main/resources/application.properties` y cambia:

```properties
spring.datasource.password=TU_PASSWORD_AQUI
```

### 5 — Ejecutar el proyecto

**Opción A — Spring Boot Dashboard en VS Code:**
Panel lateral izquierdo → ícono 🌿 → clic en ▶️ junto a `lista-espera-service`

**Opción B — Terminal:**
```bash
./mvnw spring-boot:run
```

El servidor inicia en `http://localhost:8080`

---

## 🔐 Autenticación

El sistema usa **JWT (JSON Web Tokens)**. Todas las rutas excepto `/api/v1/auth/**` requieren token.

### Flujo de autenticación

```
1. POST /api/v1/auth/register  → Crear usuario
2. POST /api/v1/auth/login     → Obtener token JWT
3. Usar token en header:  Authorization: Bearer <token>
```

### Ejemplo con Postman

**Registro:**
```json
POST /api/v1/auth/register
{
  "email": "medico@rednorte.cl",
  "password": "123456"
}
```

**Login:**
```json
POST /api/v1/auth/login
{
  "email": "medico@rednorte.cl",
  "password": "123456"
}
// Respuesta: { "token": "eyJhbGciOiJIUzI1NiJ9..." }
```

---

## 📡 Endpoints de la API

### Pacientes — `/api/v1/pacientes`

| Método | URL | Descripción |
|---|---|---|
| `GET` | `/api/v1/pacientes` | Listar todos los pacientes |
| `GET` | `/api/v1/pacientes/{id}` | Obtener paciente por ID |
| `POST` | `/api/v1/pacientes` | Crear nuevo paciente |
| `PUT` | `/api/v1/pacientes/{id}` | Actualizar paciente |
| `DELETE` | `/api/v1/pacientes/{id}` | Eliminar paciente |

### Lista de Espera — `/api/v1/lista-espera`

| Método | URL | Descripción |
|---|---|---|
| `GET` | `/api/v1/lista-espera/pendientes` | Solicitudes pendientes ordenadas por prioridad |
| `GET` | `/api/v1/lista-espera/estado/{estado}` | Filtrar por PENDIENTE, ASIGNADO, CANCELADO, ATENDIDO |
| `GET` | `/api/v1/lista-espera/paciente/{id}` | Solicitudes de un paciente |
| `GET` | `/api/v1/lista-espera/{id}` | Obtener solicitud por ID |
| `POST` | `/api/v1/lista-espera` | Registrar nueva solicitud |
| `PUT` | `/api/v1/lista-espera/{id}/asignar` | Asignar hora médica |
| `PUT` | `/api/v1/lista-espera/{id}/atender` | Marcar como atendida |
| `PUT` | `/api/v1/lista-espera/{id}/cancelar` | Cancelar solicitud |
| `DELETE` | `/api/v1/lista-espera/{id}` | Eliminar solicitud |

### Citas Médicas — `/api/v1/citas`

| Método | URL | Descripción |
|---|---|---|
| `POST` | `/api/v1/citas` | Agendar nueva cita |
| `GET` | `/api/v1/citas/{id}` | Obtener cita por ID |
| `GET` | `/api/v1/citas/solicitud/{listaEsperaId}` | Cita vinculada a una solicitud |
| `GET` | `/api/v1/citas/estado/{estado}` | Filtrar por estado |
| `GET` | `/api/v1/citas/reasignacion` | Citas canceladas disponibles para reasignar |
| `PUT` | `/api/v1/citas/{id}/confirmar` | Confirmar asistencia |
| `PUT` | `/api/v1/citas/{id}/realizar` | Marcar como realizada |
| `PUT` | `/api/v1/citas/{id}/cancelar?motivo=...` | Cancelar cita |
| `PUT` | `/api/v1/citas/{id}/no-asistio` | Marcar inasistencia |

---

## 🔄 Flujo de Estados

### ListaEspera
```
PENDIENTE → ASIGNADO → ATENDIDO
    ↓            ↓
 CANCELADO    CANCELADO (vuelve a PENDIENTE para reasignación)
```

### CitaMedica
```
PROGRAMADA → CONFIRMADA → REALIZADA
     ↓             ↓
  CANCELADA     NO_ASISTIO
     ↓
(solicitud vuelve a PENDIENTE → reasignación automática)
```

---

## 🧩 Patrones de Diseño Implementados

### Repository Pattern
```java
// Spring genera el SQL automáticamente por el nombre del método
List<ListaEspera> findByEstado(EstadoSolicitud estado);
List<ListaEspera> findByPacienteId(Long pacienteId);
```

### Factory Method
```java
// Crea el tipo de notificación según el parámetro
Notificacion notif = notificacionFactory.crear("EMAIL");
notif.enviar("paciente@mail.com", "Su cita fue confirmada");

Notificacion sms = notificacionFactory.crear("SMS");
sms.enviar("+56912345678", "Recordatorio de cita");
```

### Circuit Breaker
```java
// Si el 50% de las últimas 10 llamadas fallan → abre el circuito
@CircuitBreaker(name = "listaEsperaService", fallbackMethod = "fallbackPendientes")
public List<ListaEspera> obtenerPendientes() { ... }

// Respuesta de fallback cuando el circuito está abierto
public List<ListaEspera> fallbackPendientes(Exception ex) {
    return Collections.emptyList();
}
```

---

## 🛡️ Seguridad

- **JWT Stateless**: No se almacenan sesiones en el servidor
- **BCrypt**: Las contraseñas nunca se almacenan en texto plano
- **Filtro por request**: Cada petición valida el token antes de procesarse
- **Roles**: `ROLE_ADMIN`, `ROLE_MEDICO`, `ROLE_PACIENTE`
- **CSRF deshabilitado**: Apropiado para APIs REST stateless

---

## ⚡ Circuit Breaker — Configuración

```properties
# Si el 50% de las últimas 10 llamadas fallan → abre el circuito
resilience4j.circuitbreaker.instances.listaEsperaService.slidingWindowSize=10
resilience4j.circuitbreaker.instances.listaEsperaService.failureRateThreshold=50
# Espera 10 segundos antes de volver a intentar (estado HALF_OPEN)
resilience4j.circuitbreaker.instances.listaEsperaService.waitDurationInOpenState=10s
```

| Estado | Descripción |
|---|---|
| **CLOSED** | Funcionamiento normal, solicitudes pasan |
| **OPEN** | Circuito abierto, devuelve fallback inmediatamente |
| **HALF_OPEN** | Probando si el servicio se recuperó |

---

## 👥 Integrantes del Equipo

| Nombre | Rol |
|---|---|
| _(Agregar nombre)_ | Desarrollador/a |
| _(Agregar nombre)_ | Desarrollador/a |
| _(Agregar nombre)_ | Desarrollador/a |

---

## 📚 Referencias

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security + JWT](https://docs.spring.io/spring-security/reference/)
- [Resilience4j Documentation](https://resilience4j.readme.io/docs)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- Sommerville, I. (2016). *Software Engineering* (10th ed.). Pearson.

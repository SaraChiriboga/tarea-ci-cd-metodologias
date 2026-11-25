# To-Do List API REST & CI/CD Pipeline

API REST desarrollada con Spring Boot 3, Java 17 y Gradle. Pipeline automatizado con al menos 5 etapas y despliegue continuo en AWS EC2 vía SSH.

## 👥 Integrantes y Roles
- **Persona 1:** Setup del proyecto, arquitectura y Git Lead
- **Persona 2:** Entidad, Repositorio y Servicio (Capa de datos y lógica de negocio)
- **Persona 3:** Controlador REST, DTOs, Validaciones y Swagger
- **Persona 4:** Pruebas unitarias (JUnit 5, Mockito) y calidad de código (JaCoCo)
- **Persona 5:** Pipeline CI/CD (Etapas Build, Test, Quality, Packing)
- **Persona 6:** Infraestructura AWS EC2, Despliegue (Deploy) y Verificación (Verify)

---

## 🛠️ Tecnologías y Versiones
- **Java:** 17
- **Framework:** Spring Boot 3.3.0
- **Herramienta de compilación:** Gradle
- **Base de datos:** H2 Database (en memoria para dev/test, persistente en prod)
- **Documentación API:** Swagger UI / OpenAPI 3
- **Servidor:** AWS EC2 (Ubuntu)

---

## 📂 Estructura del Proyecto
```text
src/main/java/com/udla/todolist/
│
├── ToDoListApiApplication.java # Entrada principal de Spring Boot
├── model/                     # Persona 2: Entidad Task (@Entity)
├── repository/                # Persona 2: TaskRepository (JpaRepository)
├── service/                   # Persona 2: TaskService y TaskServiceImpl
├── dto/                       # Persona 3: TaskRequest y TaskResponse
├── controller/                # Persona 3: TaskController (@RestController)
└── exception/                 # Persona 2 y 3: TaskNotFoundException y GlobalExceptionHandler
```

---

## 🚀 Cómo Ejecutar y Probar Localmente

### 1. Clonar el repositorio
```bash
git clone <URL_DEL_REPOSITORIO>
cd tarea-ci-cd
```

### 2. Compilar el proyecto
```bash
./gradlew compileJava
```

### 3. Ejecutar pruebas unitarias
```bash
./gradlew test
```

### 4. Generar el empaquetado (JAR)
```bash
./gradlew bootJar
```
*(El archivo generado quedará en `build/libs/to-do-list-api-0.0.1-SNAPSHOT.jar`)*

### 5. Levantar la aplicación localmente
```bash
./gradlew bootRun
```
- API Base: `http://localhost:8080/api/tasks`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Health check (Actuator): `http://localhost:8080/actuator/health`
- Consola H2: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:todolistdb`, User: `sa`, Password: vacía)

---

## 🌿 Convención y Flujo de Trabajo en Git

1. **Ramas principales:**
   - `main`: Código en producción. Protegida. Solo recibe merges desde `develop`.
   - `develop`: Rama de integración donde se unen las funcionalidades del equipo.
2. **Ramas de funcionalidad (`feature/*`):**
   - Cada persona trabaja en su propia rama que parte de `develop`:
     ```bash
     git checkout develop
     git pull origin develop
     git checkout -b feature/nombre-de-la-tarea
     ```
3. **Commits y Pull / Merge Requests:**
   - Realizar commits descriptivos: `feat: agrega entidad Task`, `test: pruebas unitarias del servicio`.
   - Subir la rama y abrir un **Pull Request / Merge Request** hacia `develop`.
   - El Líder Técnico (Persona 1) revisa y aprueba el merge.
4. **Despliegue a Producción:**
   - Cuando `develop` esté probado y estable, se hace merge a `main`, disparando el despliegue automático a EC2.

---

## 🧪 Pruebas y Calidad de Código (Persona 4)

| Comando | Qué hace |
|---|---|
| `./gradlew test` | Ejecuta las pruebas (JUnit 5 + Mockito + MockMvc + DataJpaTest) y genera el reporte JaCoCo |
| `./gradlew check` | Pruebas + Checkstyle + verificación de cobertura mínima (**70 % de líneas**) |
| `./gradlew jacocoTestReport` | Solo regenera el reporte de cobertura |

- Las pruebas usan **H2 en memoria**, no necesitan BD externa.
- Reporte de cobertura HTML: `build/reports/jacoco/test/html/index.html` (XML para GitLab: `build/reports/jacoco/test/jacocoTestReport.xml`).
- Reportes JUnit (para `artifacts: reports: junit`): `build/test-results/test/*.xml`.
- Reporte de Checkstyle: `build/reports/checkstyle/main.html`. Reglas en `config/checkstyle/checkstyle.xml`.

**Pruebas incluidas:** `TaskServiceImplTest` (9), `TaskControllerTest` (13), `TaskRepositoryTest` (2), `ToDoListApiApplicationTests` (1).

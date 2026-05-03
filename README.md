# Spring Boot API Tutorial

**GitHub Repository:** https://github.com/dhtml/springboot_basics

A beginner-friendly Spring Boot REST API project covering everything from basic HTTP concepts to a full CRUD API backed by a database.

---


## What You'll Learn

| Level | Controller | Topics |
|-------|-----------|--------|
| 1 | `BasicController` | GET, POST, PUT, DELETE, path variables, query params, request body, response codes, headers |
| 3 | `StudentController` | Layered architecture, DTOs, validation, pagination, search, H2 database, JPA |

---

## Prerequisites

Before you can run this project you need three things installed on your PC:

### 1. Java Development Kit (JDK) 21 or later

This project requires **Java 21 LTS** as the minimum. Any JDK version 21 or above will work
(the tutor's machine runs **JDK 26**).

1. Go to https://adoptium.net
2. Select **Temurin 21 (LTS)** and download the installer for your OS
3. Run the installer — tick *"Set JAVA_HOME"* and *"Add to PATH"* if prompted
4. Open a **new** terminal and verify:

```
java -version
```

You should see version **21 or higher**:
```
openjdk version "21.0.x" ...   ← or any version ≥ 21
```

### 2. Apache Maven 3.9+

Maven downloads dependencies and builds the project.

1. Go to https://maven.apache.org/download.cgi
2. Download the **Binary zip archive** (e.g. `apache-maven-3.9.14-bin.zip`)
3. Extract it somewhere permanent, e.g. `C:\tools\maven`
4. Add `C:\tools\maven\bin` to your system `PATH`
   - Search *"Edit the system environment variables"* → Environment Variables → Path → New
5. Open a **new** terminal and verify:

```
mvn -version
```

You should see something like (the tutor's machine runs **3.9.14**):
```
Apache Maven 3.9.14 ...
Java version: 21 ...
```

> **Alternative (Windows):** Install both Java and Maven in one step with [Scoop](https://scoop.sh):
> ```
> scoop install temurin21 maven
> ```

### 3. Git

To clone the repository.

Download from https://git-scm.com and run the installer with default settings.

---

## Getting the Project

```bash
git clone https://github.com/dhtml/springboot_basics.git
cd springboot_basics
```

> If you received the project as a ZIP, just extract it and open a terminal in the extracted folder.

---

## Running the Project

```bash
mvn spring-boot:run
```

Maven will download all dependencies on the first run (this may take a minute). Once you see:

```
Started SpringBootApiTutorialApplication in X.XXX seconds
```

the server is live at **http://localhost:8080**.

To stop the server press `Ctrl + C` in the terminal.

---

## Exploring the API

### API Tester — built-in browser tool

A custom test console is bundled with the project itself. Open it at:

```
http://localhost:8080/api-tester.html
```

<img width="1366" height="651" alt="image" src="https://github.com/user-attachments/assets/efff2328-3126-4214-b2cc-57ede43165b7" />


- All endpoints are listed in a **sidebar**, grouped by category
- Click any endpoint to see a **form** with the right input fields auto-generated
- Fill in parameters (path variables, query params, request body) and click **Send**
- The response is shown instantly with **colour-coded JSON**, the HTTP status code, and the response time
- No Postman, no curl, no Swagger account — everything runs in the browser

### Swagger UI — auto-generated API docs

Open your browser and go to:

```
http://localhost:8080/swagger-ui.html
```

Every endpoint is listed here with full schema details. You can fill in parameters and click **Execute** to send a real request.

### Quick test links (open in browser)

| URL | What it does |
|-----|-------------|
| http://localhost:8080/api-tester.html | **Built-in API Tester** — test all endpoints without any extra tools |
| http://localhost:8080/api/basic/hello | Returns a plain "Hello" message |
| http://localhost:8080/api/students | Returns all students as JSON |
| http://localhost:8080/api/students/1 | Returns the student with ID 1 |
| http://localhost:8080/api/students/search?name=alice | Searches students by name |
| http://localhost:8080/api/students/page?page=0&size=3 | Returns the first 3 students (pagination) |
| http://localhost:8080/api/students/stats | Returns aggregated statistics |

### H2 Database Console — browse the database in your browser

```
http://localhost:8080/h2-console
```

Login settings:
| Field | Value |
|-------|-------|
| JDBC URL | `jdbc:h2:mem:tutorialdb` |
| Username | `sa` |
| Password | *(leave blank)* |

---

## Full API Reference

### Basic Endpoints — `/api/basic`

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/basic/hello` | Simple hello message |
| GET | `/api/basic/greet/{name}` | Greet by name (path variable) |
| GET | `/api/basic/search?keyword=foo` | Search with query param |
| POST | `/api/basic/echo` | Echo the JSON body back |
| PUT | `/api/basic/update/{id}` | Simulate updating a resource |
| DELETE | `/api/basic/delete/{id}` | Simulate deleting a resource |

### Student Endpoints — `/api/students`

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/students` | Get all students |
| GET | `/api/students/{id}` | Get student by ID |
| GET | `/api/students/search?name=alice` | Search by name |
| GET | `/api/students/course/{name}` | Filter by course |
| GET | `/api/students/course/{name}/search` | Search within a course |
| GET | `/api/students/page?page=0&size=3` | Paginated list |
| GET | `/api/students/stats` | Aggregated statistics |
| POST | `/api/students` | Create a new student |
| PUT | `/api/students/{id}` | Update a student |
| DELETE | `/api/students/{id}` | Delete a student |

#### Example POST body

```json
{
  "name": "Jane Doe",
  "email": "jane@example.com",
  "course": "Computer Science",
  "grade": "A"
}
```

---

## Project Structure

```
src/
└── main/
    ├── java/com/tutorial/api/
    │   ├── SpringBootApiTutorialApplication.java   ← entry point (@SpringBootApplication)
    │   ├── controller/
    │   │   ├── BasicController.java                ← Level 1: basic HTTP concepts
    │   │   └── StudentController.java              ← Level 3: full CRUD + database
    │   ├── dto/
    │   │   ├── StudentRequestDTO.java              ← what the client sends in
    │   │   └── StudentResponseDTO.java             ← what the API sends back
    │   ├── exception/
    │   │   ├── GlobalExceptionHandler.java         ← catches exceptions, returns JSON errors
    │   │   └── ResourceNotFoundException.java      ← thrown when a record isn't found (→ 404)
    │   ├── model/
    │   │   └── Student.java                        ← JPA entity (maps to the "students" table)
    │   ├── repository/
    │   │   └── StudentRepository.java              ← database queries (extends JpaRepository)
    │   └── service/
    │       └── StudentService.java                 ← business logic lives here
    └── resources/
        ├── application.properties                  ← server port, database settings
        └── data.sql                                ← seed data loaded on every startup
```

---

## Key Dependencies

| Dependency | Purpose |
|-----------|---------|
| `spring-boot-starter-web` | REST API support + embedded Tomcat server |
| `spring-boot-starter-data-jpa` | Database access via JPA / Hibernate |
| `spring-boot-starter-validation` | Request body validation (`@NotBlank`, `@Email`, etc.) |
| `h2` | In-memory database — no installation required |
| `springdoc-openapi-starter-webmvc-ui` | Auto-generates Swagger UI documentation |
| `spring-boot-devtools` | Auto-restarts the server when you save a file |

---

## Seed Data

Seven students are loaded automatically every time the app starts (defined in `data.sql`):

| Name | Course | Grade |
|------|--------|-------|
| Alice Johnson | Computer Science | A |
| Bob Smith | Mathematics | B+ |
| Carol White | Physics | A- |
| David Brown | Chemistry | B |
| Eve Davis | Computer Science | A+ |
| Frank Miller | Mathematics | C+ |
| Grace Wilson | Physics | B- |

> The database is **in-memory** — data resets on every restart. That's intentional for a learning environment.

---

## Common Issues

**Port 8080 is already in use**

Another application is using port 8080. Either stop that application, or change the port in `src/main/resources/application.properties`:

```properties
server.port=9090
```

Then access the app at `http://localhost:9090`.

---

**`mvn` is not recognized**

Maven is not on your PATH. Re-read the Maven installation steps above and make sure you opened a **new** terminal after editing the PATH.

---

**`java` is not recognized**

JDK is not on your PATH. Re-read the JDK installation steps above and make sure you opened a **new** terminal after installation.

---

**Build fails with "cannot find symbol" or "package does not exist"**

Run a clean build to delete stale compiled files:

```bash
mvn clean spring-boot:run
```

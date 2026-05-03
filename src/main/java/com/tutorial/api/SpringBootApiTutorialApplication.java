package com.tutorial.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ============================================================
 * SPRING BOOT API TUTORIAL - MAIN APPLICATION CLASS
 * ============================================================
 *
 * Every Spring Boot application starts here.
 *
 * @SpringBootApplication is a shortcut combining three annotations:
 *
 *   1. @Configuration
 *      Marks this class as a source of Spring bean definitions.
 *
 *   2. @EnableAutoConfiguration
 *      Tells Spring Boot to automatically configure the application
 *      based on the dependencies found in pom.xml.
 *      e.g., "I see spring-boot-starter-web → start Tomcat server"
 *           "I see h2 + spring-data-jpa → set up a database"
 *
 *   3. @ComponentScan
 *      Scans this package (com.tutorial.api) and all sub-packages
 *      for Spring-managed components:
 *        - @RestController (controllers)
 *        - @Service        (business logic)
 *        - @Repository     (database access)
 *        - @Component      (general purpose)
 *
 * ============================================================
 * HOW TO RUN
 * ============================================================
 *
 * Option A - VS Code:
 *   1. Open this file
 *   2. Click the green "Run" button above the main() method
 *   (or press F5 if launch.json is configured)
 *
 * Option B - Terminal:
 *   mvn spring-boot:run
 *
 * Option C - Build then run:
 *   mvn package
 *   java -jar target/api-tutorial-0.0.1-SNAPSHOT.jar
 *
 * ============================================================
 * AFTER STARTUP - WHAT TO DO
 * ============================================================
 *
 * 1. Swagger UI (interactive API docs):
 *    http://localhost:8080/swagger-ui.html
 *
 * 2. Basic API examples (Level 1 - no database):
 *    http://localhost:8080/api/basic/hello
 *    http://localhost:8080/api/basic/info
 *
 * 3. Student API (Level 3 - with database):
 *    http://localhost:8080/api/students
 *
 * 4. H2 Database Console (browse data):
 *    http://localhost:8080/h2-console
 *    JDBC URL: jdbc:h2:mem:tutorialdb  |  User: sa  |  Password: (blank)
 *
 * ============================================================
 */
@SpringBootApplication
public class SpringBootApiTutorialApplication {

    public static void main(String[] args) {
        /*
         * SpringApplication.run() does all the heavy lifting:
         *   - Creates the Spring Application Context (the IoC container)
         *   - Starts the embedded Tomcat web server
         *   - Triggers auto-configuration
         *   - Runs any CommandLineRunner / ApplicationRunner beans
         *   - Logs startup info (port, startup time, etc.)
         */
        SpringApplication.run(SpringBootApiTutorialApplication.class, args);
    }
}

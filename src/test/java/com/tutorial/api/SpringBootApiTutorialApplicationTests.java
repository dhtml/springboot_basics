package com.tutorial.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ============================================================
 * APPLICATION CONTEXT INTEGRATION TEST
 * ============================================================
 *
 * @SpringBootTest loads the COMPLETE Spring Application Context
 *                 (all beans, configuration, database, etc.) just like a real
 *                 startup.
 *
 *                 This test verifies that:
 *                 - All @Bean definitions are valid
 *                 - All @Autowired / constructor injections are satisfied
 *                 - application.properties is correctly configured
 *                 - The database schema is created without errors
 *
 *                 HOW TO RUN TESTS:
 *                 Option A: Terminal
 *                 mvn test
 *
 *                 Option B: VS Code
 *                 Click the green play button next to a test method or class.
 *                 Or open the Testing panel (flask icon in the left sidebar).
 *
 *                 ============================================================
 *                 TESTING PYRAMID (for learning — what to test and how much)
 *                 ============================================================
 *
 *                 /\
 *                 / \ E2E Tests — few, slow, test full system
 *                 / \
 *                 /------\ Integration Tests — some, test multiple layers
 *                 / \
 *                 /──────────\ Unit Tests — many, fast, test one class in
 *                 isolation
 *                 /____________\
 *
 *                 This test is an integration test (loads the full Spring
 *                 context).
 *                 In real projects, you'd also have:
 *                 - @WebMvcTest for controller-layer tests (faster, no DB)
 *                 - @DataJpaTest for repository tests (with in-memory DB)
 *                 - Plain JUnit tests for service/util logic
 */
@SpringBootTest
class SpringBootApiTutorialApplicationTests {

    /**
     * Smoke test — verifies the application starts without errors.
     *
     * If any bean is misconfigured, any SQL is wrong, or any
     * dependency is missing, this test will FAIL with a clear error.
     * It's the first test to run in any CI/CD pipeline.
     */
    @Test
    void contextLoads() {
        // This test has no assertions.
        // It passes if (and only if) the Spring context loads successfully.
        // Failure here means something is fundamentally broken in your config.
    }
}

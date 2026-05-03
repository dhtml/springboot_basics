package com.tutorial.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ============================================================
 * LEVEL 1: BASIC REST API CONCEPTS
 * ============================================================
 *
 * This controller covers the fundamental building blocks of
 * REST APIs in Spring Boot. No database needed here — focus
 * entirely on HTTP mechanics.
 *
 * KEY CONCEPTS TAUGHT:
 *   Level 1A  - Simple GET request
 *   Level 1B  - @PathVariable (dynamic URL segments)
 *   Level 1C  - @RequestParam (query string parameters)
 *   Level 1D  - @RequestBody (receiving JSON via POST)
 *   Level 1E  - ResponseEntity (controlling HTTP status codes)
 *   Level 1F  - PUT, PATCH, DELETE methods
 *   Level 1G  - Reading HTTP headers
 *
 * BASE URL: http://localhost:8080/api/basic
 *
 * HOW TO TEST THESE ENDPOINTS:
 *   - Browser:  works for GET requests
 *   - Swagger:  http://localhost:8080/swagger-ui.html (best for beginners!)
 *   - Postman / Insomnia: works for all methods
 *   - curl:     command-line HTTP client
 *
 * @RestController = @Controller + @ResponseBody
 *   @Controller marks the class as a web controller.
 *   @ResponseBody tells Spring to convert return values to JSON
 *   and write them directly into the HTTP response body.
 *
 * @RequestMapping("/api/basic")
 *   Sets the base URL prefix for ALL endpoints in this class.
 *   So @GetMapping("/hello") resolves to /api/basic/hello.
 *
 * @CrossOrigin(origins = "*")
 *   Allows requests from any domain (useful when testing from browser tools).
 */
@RestController
@RequestMapping("/api/basic")
@CrossOrigin(origins = "*")
public class BasicController {

    // ================================================================
    // LEVEL 1A: SIMPLE GET REQUESTS
    // ================================================================

    /**
     * EXAMPLE 1: The simplest API endpoint.
     *
     * Returns a plain String. Spring (via Jackson) wraps it in JSON.
     *
     * TEST: GET http://localhost:8080/api/basic/hello
     * RETURNS: "Hello, World! Welcome to Spring Boot API Tutorial!"
     */
    @GetMapping("/hello")
    public String helloWorld() {
        return "Hello, World! Welcome to Spring Boot API Tutorial!";
    }

    /**
     * EXAMPLE 2: Returning a JSON object.
     *
     * Returning a Map<String, Object> is automatically serialized by
     * Jackson into a JSON object: { "key": "value", ... }
     *
     * TEST: GET http://localhost:8080/api/basic/info
     * RETURNS: { "appName": "...", "version": "1.0", "timestamp": "..." }
     */
    @GetMapping("/info")
    public Map<String, Object> getAppInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("appName", "Spring Boot API Tutorial");
        info.put("version", "1.0");
        info.put("description", "Beginner-friendly REST API examples");
        info.put("timestamp", LocalDateTime.now().toString());
        info.put("swaggerUrl", "http://localhost:8080/swagger-ui.html");
        info.put("h2ConsoleUrl", "http://localhost:8080/h2-console");
        return info;
    }

    /**
     * EXAMPLE 3: Returning a JSON array.
     *
     * Java List is serialized into a JSON array: ["item1", "item2", ...]
     *
     * TEST: GET http://localhost:8080/api/basic/fruits
     * RETURNS: ["Apple", "Banana", "Cherry", "Date", "Elderberry"]
     */
    @GetMapping("/fruits")
    public List<String> getFruits() {
        // List.of() creates an unmodifiable list — Java 9+ feature
        return List.of("Apple", "Banana", "Cherry", "Date", "Elderberry");
    }

    // ================================================================
    // LEVEL 1B: @PathVariable — Dynamic URL Segments
    // ================================================================

    /**
     * EXAMPLE 4: @PathVariable — reading a value from the URL path.
     *
     * The {name} in the URL is a "path variable" (a placeholder).
     * Spring captures whatever the user puts there and gives it to you.
     *
     * TEST: GET http://localhost:8080/api/basic/hello/Alice
     * TEST: GET http://localhost:8080/api/basic/hello/Bob
     * RETURNS: "Hello, Alice! Great to see you learning Spring Boot!"
     *
     * @param name captured from the URL (e.g., "Alice" from /hello/Alice)
     */
    @GetMapping("/hello/{name}")
    public String greetByName(@PathVariable String name) {
        // @PathVariable binds the {name} URL segment to the 'name' parameter
        return "Hello, " + name + "! Great to see you learning Spring Boot!";
    }

    /**
     * EXAMPLE 5: Multiple path variables in one URL.
     *
     * You can embed multiple {variables} at different positions in the path.
     *
     * TEST: GET http://localhost:8080/api/basic/users/42/orders/7
     * RETURNS: { "userId": 42, "orderId": 7, "message": "..." }
     *
     * @param userId  first path variable
     * @param orderId second path variable
     */
    @GetMapping("/users/{userId}/orders/{orderId}")
    public Map<String, Object> getUserOrder(
            @PathVariable Long userId,
            @PathVariable Long orderId) {

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("orderId", orderId);
        response.put("message", "Showing order " + orderId + " for user " + userId);
        return response;
    }

    // ================================================================
    // LEVEL 1C: @RequestParam — Query String Parameters
    // ================================================================

    /**
     * EXAMPLE 6: @RequestParam — reading query parameters from the URL.
     *
     * Query parameters appear after '?' in the URL: /endpoint?key=value
     * They are used to pass optional filters, search terms, or settings.
     *
     * TEST: GET http://localhost:8080/api/basic/search?keyword=spring
     * TEST: GET http://localhost:8080/api/basic/search?keyword=java&limit=5
     * RETURNS: { "keyword": "spring", "limit": 10, "message": "..." }
     *
     * @param keyword required param — will cause an error if missing
     * @param limit   optional param — defaults to 10 if not provided
     */
    @GetMapping("/search")
    public Map<String, Object> search(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        Map<String, Object> response = new HashMap<>();
        response.put("keyword", keyword);
        response.put("limit", limit);
        response.put("message", "Searching for '" + keyword + "' (max " + limit + " results)");
        return response;
    }

    /**
     * EXAMPLE 7: Combining @PathVariable AND @RequestParam.
     *
     * Real-world APIs often combine both: the path identifies WHAT,
     * while query params control HOW (sorting, filtering, limiting).
     *
     * TEST: GET http://localhost:8080/api/basic/products/electronics?sort=price&limit=5
     * RETURNS: { "category": "electronics", "sort": "price", "limit": 5 }
     *
     * @param category the category from the URL path
     * @param sort     query param for sort field (default: "name")
     * @param limit    query param for max results (default: 10)
     */
    @GetMapping("/products/{category}")
    public Map<String, Object> getProductsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "10") int limit) {

        Map<String, Object> response = new HashMap<>();
        response.put("category", category);
        response.put("sort", sort);
        response.put("limit", limit);
        response.put("message", "Fetching up to " + limit + " " + category + " products, sorted by " + sort);
        return response;
    }

    // ================================================================
    // LEVEL 1D: @RequestBody — Receiving JSON in POST Requests
    // ================================================================

    /**
     * EXAMPLE 8: @RequestBody — Jackson deserializes incoming JSON.
     *
     * POST requests carry data in the request BODY (not the URL).
     * @RequestBody tells Spring to read the request body and convert
     * it from JSON to the Java type you specify (here: Map<String, Object>).
     *
     * TEST: POST http://localhost:8080/api/basic/echo
     * BODY: { "message": "Hello", "from": "Alice" }
     * RETURNS: { "message": "Hello", "from": "Alice", "echoed": true, ... }
     *
     * @param body the JSON request body, converted to a Java Map
     */
    @PostMapping("/echo")
    public Map<String, Object> echo(@RequestBody Map<String, Object> body) {
        // You can read, modify, and return the data
        body.put("echoed", true);
        body.put("receivedAt", LocalDateTime.now().toString());
        body.put("note", "This is your request body, echoed back to you!");
        return body;
    }

    // ================================================================
    // LEVEL 1E: ResponseEntity — Full Control Over HTTP Responses
    // ================================================================

    /**
     * EXAMPLE 9: ResponseEntity<T>
     *
     * ResponseEntity lets you control EVERY part of the HTTP response:
     *   1. Status Code  (200 OK, 201 Created, 404 Not Found, etc.)
     *   2. Headers      (Content-Type, Location, X-Custom-Header, etc.)
     *   3. Body         (the actual data you return)
     *
     * This is BEST PRACTICE for production APIs — always return the
     * correct HTTP status code, not just data.
     *
     * HTTP Status Codes Cheat Sheet:
     *   2xx  Success:    200 OK, 201 Created, 204 No Content
     *   4xx  Client Error: 400 Bad Request, 401 Unauthorized, 404 Not Found, 409 Conflict
     *   5xx  Server Error: 500 Internal Server Error
     *
     * TEST: GET http://localhost:8080/api/basic/demo/200
     * TEST: GET http://localhost:8080/api/basic/demo/404
     * TEST: GET http://localhost:8080/api/basic/demo/201
     *
     * @param code a status code number to demonstrate
     */
    @GetMapping("/demo/{code}")
    public ResponseEntity<Map<String, Object>> demonstrateStatusCodes(@PathVariable int code) {
        Map<String, Object> body = new HashMap<>();
        body.put("requestedCode", code);

        return switch (code) {
            case 200 -> {
                body.put("meaning", "OK — standard successful response");
                yield ResponseEntity.ok(body);                               // 200 OK
            }
            case 201 -> {
                body.put("meaning", "Created — a new resource was created");
                yield ResponseEntity.status(HttpStatus.CREATED).body(body);  // 201 Created
            }
            case 204 -> {
                // 204 No Content has NO body (empty response)
                yield ResponseEntity.noContent().build();                     // 204 No Content
            }
            case 400 -> {
                body.put("meaning", "Bad Request — the client sent invalid data");
                yield ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
            }
            case 404 -> {
                body.put("meaning", "Not Found — the requested resource does not exist");
                yield ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
            }
            case 409 -> {
                body.put("meaning", "Conflict — e.g., duplicate email or username");
                yield ResponseEntity.status(HttpStatus.CONFLICT).body(body);
            }
            case 500 -> {
                body.put("meaning", "Internal Server Error — something went wrong on the server");
                yield ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
            }
            default -> {
                body.put("meaning", "Unknown / not demonstrated here");
                yield ResponseEntity.ok(body);
            }
        };
    }

    /**
     * EXAMPLE 10: POST that returns HTTP 201 Created.
     *
     * Best practice: always return 201 (not 200) when you CREATE a resource.
     * This tells the client: "Yes, the resource was created successfully."
     *
     * TEST: POST http://localhost:8080/api/basic/items
     * BODY: { "name": "Laptop", "price": 999.99 }
     * RETURNS: HTTP 201 with the item + generated ID
     *
     * @param item the item data from the request body
     */
    @PostMapping("/items")
    public ResponseEntity<Map<String, Object>> createItem(
            @RequestBody Map<String, Object> item) {

        // Simulate what a real service would do:
        // Assign an auto-generated ID and record creation time
        item.put("id", System.currentTimeMillis());   // Fake ID (real apps use DB auto-increment)
        item.put("createdAt", LocalDateTime.now().toString());

        // Return 201 Created status — this is the correct code for resource creation
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    // ================================================================
    // LEVEL 1F: PUT, PATCH, DELETE HTTP Methods
    // ================================================================

    /**
     * EXAMPLE 11: PUT — Replace an entire resource.
     *
     * PUT means: "Replace everything about this resource with what I send."
     * All fields must be included in the request body.
     *
     * PUT vs PATCH:
     *   PUT   → Full replacement (send all fields, even unchanged ones)
     *   PATCH → Partial update  (send only the fields you want to change)
     *
     * TEST: PUT http://localhost:8080/api/basic/items/1
     * BODY: { "name": "Updated Laptop", "price": 1199.99 }
     * RETURNS: The updated item
     *
     * @param id   the item's ID from the URL
     * @param item the complete new data for this item
     */
    @PutMapping("/items/{id}")
    public ResponseEntity<Map<String, Object>> updateItem(
            @PathVariable Long id,
            @RequestBody Map<String, Object> item) {

        item.put("id", id);
        item.put("updatedAt", LocalDateTime.now().toString());
        item.put("operation", "FULL UPDATE via PUT");
        return ResponseEntity.ok(item);
    }

    /**
     * EXAMPLE 12: PATCH — Partial update.
     *
     * PATCH means: "Only change the specific fields I'm sending."
     * Useful when updating one field without resending the whole object.
     *
     * TEST: PATCH http://localhost:8080/api/basic/items/1
     * BODY: { "price": 899.99 }   ← Only price changes, name stays the same
     * RETURNS: Confirmation of fields updated
     *
     * @param id     the item's ID from the URL
     * @param fields only the fields being changed
     */
    @PatchMapping("/items/{id}")
    public ResponseEntity<Map<String, Object>> partialUpdateItem(
            @PathVariable Long id,
            @RequestBody Map<String, Object> fields) {

        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("updatedFields", fields.keySet());
        response.put("updatedAt", LocalDateTime.now().toString());
        response.put("operation", "PARTIAL UPDATE via PATCH");
        return ResponseEntity.ok(response);
    }

    /**
     * EXAMPLE 13: DELETE — Remove a resource.
     *
     * Best practice: Return 204 No Content after successful deletion.
     *   - The resource is gone, so there is nothing to put in the response body.
     *   - 204 = "It worked, and there's nothing to return."
     *
     * TEST: DELETE http://localhost:8080/api/basic/items/1
     * RETURNS: HTTP 204 No Content (empty body)
     *
     * @param id the ID of the item to delete
     */
    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        // In a real app: itemService.deleteById(id);
        // ResponseEntity<Void> means the body type is "nothing"
        return ResponseEntity.noContent().build();   // HTTP 204 No Content
    }

    // ================================================================
    // LEVEL 1G: Reading HTTP Request Headers
    // ================================================================

    /**
     * EXAMPLE 14: @RequestHeader — reading HTTP headers.
     *
     * HTTP headers are key-value pairs sent with every request.
     * Common uses: authentication tokens, content type negotiation,
     * tracing IDs, API versioning, locale/language settings.
     *
     * TEST: GET http://localhost:8080/api/basic/headers
     *   Add headers:  X-API-Key: myKey123
     *                 Accept-Language: en-US
     * RETURNS: { "apiKey": "myKey123", "language": "en-US", ... }
     *
     * @param apiKey   value of the X-API-Key header (optional)
     * @param language value of Accept-Language header (optional)
     */
    @GetMapping("/headers")
    public Map<String, String> readHeaders(
            @RequestHeader(value = "X-API-Key",
                           required = false,
                           defaultValue = "not-provided") String apiKey,
            @RequestHeader(value = "Accept-Language",
                           required = false,
                           defaultValue = "en") String language) {

        Map<String, String> response = new HashMap<>();
        response.put("X-API-Key", apiKey);
        response.put("Accept-Language", language);
        response.put("tip", "These values came from your request headers!");
        return response;
    }
}

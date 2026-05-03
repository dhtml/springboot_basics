package com.tutorial.api.controller;

import com.tutorial.api.dto.StudentRequestDTO;
import com.tutorial.api.dto.StudentResponseDTO;
import com.tutorial.api.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ============================================================
 * LEVEL 3: ADVANCED REST API — Full CRUD with Database
 * ============================================================
 *
 * This controller brings everything together:
 *
 * ✓ Layered Architecture (Controller → Service → Repository → DB)
 * ✓ DTO Pattern (separate request/response shapes)
 * ✓ Input Validation (@Valid triggers @NotBlank, @Email, etc.)
 * ✓ Correct HTTP methods (GET, POST, PUT, DELETE)
 * ✓ Correct status codes (200, 201, 204, 400, 404, 409, 500)
 * ✓ Centralized errors (GlobalExceptionHandler handles exceptions)
 * ✓ Pagination (avoid returning millions of records at once)
 * ✓ Search & filtering (query params for flexible data retrieval)
 * ✓ H2 Database (persisted via Spring Data JPA)
 *
 * BASE URL: http://localhost:8080/api/students
 *
 * ============================================================
 * FULL API REFERENCE
 * ============================================================
 *
 * METHOD ENDPOINT DESCRIPTION
 * ────── ─────────────────────────────────── ───────────────────────────────
 * GET /api/students Get all students
 * GET /api/students/{id} Get student by ID
 * GET /api/students/search?name=alice Search students by name
 * GET /api/students/course/{name} Filter students by course
 * GET /api/students/course/{name}/search Search within a course
 * GET /api/students/page?page=0&size=3 Paginated students
 * GET /api/students/stats Aggregated statistics
 * POST /api/students Create new student
 * PUT /api/students/{id} Update entire student record
 * DELETE /api/students/{id} Delete student
 *
 * TIP: Explore all endpoints interactively at:
 * http://localhost:8080/swagger-ui.html
 */
@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    /**
     * The controller's ONLY jobs:
     * 1. Receive the HTTP request and extract parameters
     * 2. Call the appropriate service method
     * 3. Return a ResponseEntity with the correct HTTP status code
     *
     * The controller does NOT contain business logic. That's in the service.
     */
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // ================================================================
    // GET ALL STUDENTS
    // ================================================================

    /**
     * GET /api/students
     *
     * Returns ALL students. In production you'd always paginate this
     * (see the /page endpoint below), but this is useful for learning.
     *
     * SAMPLE RESPONSE: HTTP 200
     * [
     * { "id": 1, "name": "Alice Johnson", "email": "alice@example.com", ... },
     * { "id": 2, "name": "Bob Smith", ... }
     * ]
     */
    @GetMapping
    public ResponseEntity<List<StudentResponseDTO>> getAllStudents() {
        List<StudentResponseDTO> students = studentService.getAllStudents();
        return ResponseEntity.ok(students); // HTTP 200 OK
    }

    // ================================================================
    // GET SINGLE STUDENT BY ID
    // ================================================================

    /**
     * GET /api/students/{id}
     *
     * Fetch one student by their database ID.
     *
     * If the student doesn't exist, the service throws
     * ResourceNotFoundException → GlobalExceptionHandler returns HTTP 404.
     *
     * SAMPLE: GET /api/students/1
     * RESPONSE HTTP 200: { "id": 1, "name": "Alice Johnson", ... }
     *
     * SAMPLE: GET /api/students/999 (not found)
     * RESPONSE HTTP 404: { "status": 404, "message": "Student not found with id :
     * '999'" }
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> getStudentById(@PathVariable Long id) {
        StudentResponseDTO student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    // ================================================================
    // SEARCH BY NAME
    // ================================================================

    /**
     * GET /api/students/search?name=alice
     *
     * Case-insensitive partial name search.
     *
     * SAMPLE: /api/students/search?name=ali → finds "Alice Johnson", "Mali Chen"
     * SAMPLE: /api/students/search?name=son → finds "Alice Johnson", "Bob Johnson"
     *
     * @param name the search term (required query parameter)
     */
    @GetMapping("/search")
    public ResponseEntity<List<StudentResponseDTO>> searchByName(
            @RequestParam String name) {
        List<StudentResponseDTO> students = studentService.searchByName(name);
        return ResponseEntity.ok(students);
    }

    // ================================================================
    // FILTER BY COURSE
    // ================================================================

    /**
     * GET /api/students/course/{courseName}
     *
     * Get all students in a specific course (exact name match).
     *
     * SAMPLE: GET /api/students/course/Computer%20Science
     * RESPONSE: All students enrolled in "Computer Science"
     *
     * @param courseName the course name from the URL path
     */
    @GetMapping("/course/{courseName}")
    public ResponseEntity<List<StudentResponseDTO>> getStudentsByCourse(
            @PathVariable String courseName) {
        List<StudentResponseDTO> students = studentService.getStudentsByCourse(courseName);
        return ResponseEntity.ok(students);
    }

    /**
     * GET /api/students/course/{courseName}/search?name=ali
     *
     * Advanced: search by name WITHIN a specific course.
     * Combines a path variable AND a query parameter.
     * Uses the custom @Query method in StudentRepository.
     *
     * SAMPLE: GET /api/students/course/Computer%20Science/search?name=eve
     * RESPONSE: Students in "Computer Science" whose name contains "eve"
     *
     * @param courseName the course to filter by (path variable)
     * @param name       name to search for within that course (query param)
     */
    @GetMapping("/course/{courseName}/search")
    public ResponseEntity<List<StudentResponseDTO>> searchByCourseAndName(
            @PathVariable String courseName,
            @RequestParam String name) {
        List<StudentResponseDTO> students = studentService.searchByCourseAndName(courseName, name);
        return ResponseEntity.ok(students);
    }

    // ================================================================
    // PAGINATION
    // ================================================================

    /**
     * GET /api/students/page?page=0&size=3&sortBy=name
     *
     * Returns students with PAGINATION support.
     *
     * WHY PAGINATE?
     * Without pagination, GET /api/students could return thousands of records,
     * hammering your database, your network, and your client.
     * Pagination returns a manageable "page" of results at a time.
     *
     * The response is a Spring Page object which contains:
     * "content" → the array of student DTOs on this page
     * "totalElements" → total number of students in the database
     * "totalPages" → total number of pages
     * "number" → current page number (0-based)
     * "size" → items per page
     * "first" → true if this is the first page
     * "last" → true if this is the last page
     *
     * SAMPLE URLS:
     * /api/students/page → page 0, size 10, sorted by name
     * /api/students/page?page=1&size=3 → page 1 (4th–6th students), 3 per page
     * /api/students/page?sortBy=course → sorted by course name
     *
     * @param page   page index, 0-based (default 0 = first page)
     * @param size   items per page (default 10)
     * @param sortBy entity field to sort by (default "name")
     */
    @GetMapping("/page")
    public ResponseEntity<Page<StudentResponseDTO>> getStudentsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy) {

        Page<StudentResponseDTO> pageResult = studentService.getStudentsPaginated(page, size, sortBy);
        return ResponseEntity.ok(pageResult);
    }

    // ================================================================
    // CREATE NEW STUDENT
    // ================================================================

    /**
     * POST /api/students
     *
     * Creates a new student in the database.
     *
     * @Valid triggers all the validation annotations in StudentRequestDTO
     *        (@NotBlank, @Email, @Size, @Pattern). If any fail, Spring throws
     *        MethodArgumentNotValidException → GlobalExceptionHandler returns HTTP
     *        400.
     *
     *        REQUEST BODY (Content-Type: application/json):
     *        {
     *        "name": "John Doe",
     *        "email": "john.doe@example.com",
     *        "course": "Computer Science",
     *        "grade": "B+"
     *        }
     *
     *        SUCCESS RESPONSE: HTTP 201 Created
     *        {
     *        "id": 8,
     *        "name": "John Doe",
     *        "email": "john.doe@example.com",
     *        "course": "Computer Science",
     *        "grade": "B+",
     *        "createdAt": "2024-10-15T10:30:00"
     *        }
     *
     *        ERROR (duplicate email): HTTP 409 Conflict
     *        ERROR (invalid data): HTTP 400 Bad Request
     *
     * @param requestDTO the validated student data from the request body
     */
    @PostMapping
    public ResponseEntity<StudentResponseDTO> createStudent(
            @Valid @RequestBody StudentRequestDTO requestDTO) {

        StudentResponseDTO createdStudent = studentService.createStudent(requestDTO);

        // Return 201 CREATED (not 200 OK) — because we created a new resource
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
    }

    // ================================================================
    // UPDATE STUDENT
    // ================================================================

    /**
     * PUT /api/students/{id}
     *
     * Completely replaces an existing student's data.
     *
     * PUT = Full replacement. All fields must be provided.
     * If you want to update just one field, see PATCH (not implemented
     * here but shown in BasicController).
     *
     * REQUEST BODY:
     * {
     * "name": "Alice Updated",
     * "email": "alice.updated@example.com",
     * "course": "Data Science",
     * "grade": "A+"
     * }
     *
     * SUCCESS RESPONSE: HTTP 200 OK with updated student data
     * NOT FOUND: HTTP 404 if student ID doesn't exist
     * CONFLICT: HTTP 409 if new email is already taken by someone else
     *
     * @param id         the student ID from the URL
     * @param requestDTO the new data (all fields required)
     */
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequestDTO requestDTO) {

        StudentResponseDTO updatedStudent = studentService.updateStudent(id, requestDTO);
        return ResponseEntity.ok(updatedStudent); // HTTP 200 OK
    }

    // ================================================================
    // DELETE STUDENT
    // ================================================================

    /**
     * DELETE /api/students/{id}
     *
     * Permanently deletes a student from the database.
     *
     * SUCCESS RESPONSE: HTTP 204 No Content (empty body)
     * - 204 means "success, but nothing to return"
     * - The student is gone, so there is nothing meaningful to return
     *
     * NOT FOUND: HTTP 404 if the student ID doesn't exist
     *
     * @param id the ID of the student to delete
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build(); // HTTP 204 No Content
    }

    // ================================================================
    // STATISTICS — Bonus advanced endpoint
    // ================================================================

    /**
     * GET /api/students/stats
     *
     * Returns aggregate statistics about the student database.
     * This demonstrates combining multiple data points in one response.
     *
     * SAMPLE RESPONSE:
     * {
     * "totalStudents": 7,
     * "studentsByCourse": {
     * "Computer Science": 2,
     * "Mathematics": 2,
     * "Physics": 2,
     * "Chemistry": 1
     * },
     * "studentsWithGrade": 7,
     * "studentsWithoutGrade": 0
     * }
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        List<StudentResponseDTO> allStudents = studentService.getAllStudents();

        // Count students per course using Java Streams
        // .collect(groupingBy(classifier, counting())) groups items by a key and counts
        // each group
        Map<String, Long> studentsByCourse = new HashMap<>();
        allStudents.forEach(student ->
        // merge: if key exists, add 1 to existing count; otherwise start at 1
        studentsByCourse.merge(student.getCourse(), 1L, Long::sum));

        long studentsWithGrade = allStudents.stream()
                .filter(s -> s.getGrade() != null && !s.getGrade().isEmpty())
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", allStudents.size());
        stats.put("studentsByCourse", studentsByCourse);
        stats.put("studentsWithGrade", studentsWithGrade);
        stats.put("studentsWithoutGrade", allStudents.size() - studentsWithGrade);

        return ResponseEntity.ok(stats);
    }
}

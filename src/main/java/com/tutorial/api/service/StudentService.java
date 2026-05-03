package com.tutorial.api.service;

import com.tutorial.api.dto.StudentRequestDTO;
import com.tutorial.api.dto.StudentResponseDTO;
import com.tutorial.api.exception.ResourceNotFoundException;
import com.tutorial.api.model.Student;
import com.tutorial.api.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================
 * STUDENT SERVICE — Business Logic Layer
 * ============================================================
 *
 * THE LAYERED ARCHITECTURE PATTERN:
 *
 *   ┌──────────────────────────────────────────────────────┐
 *   │  CLIENT (Browser / Postman / Mobile App)             │
 *   └──────────────────────┬───────────────────────────────┘
 *                          │ HTTP Request/Response
 *   ┌──────────────────────▼───────────────────────────────┐
 *   │  CONTROLLER LAYER   (@RestController)                │
 *   │  Responsibility: Parse HTTP, call service, return    │
 *   │  HTTP response with correct status code.             │
 *   └──────────────────────┬───────────────────────────────┘
 *                          │ DTOs
 *   ┌──────────────────────▼───────────────────────────────┐
 *   │  SERVICE LAYER   (@Service)   ← YOU ARE HERE         │
 *   │  Responsibility: Business logic, validation rules,   │
 *   │  orchestrating operations, DTO-Entity conversions.   │
 *   └──────────────────────┬───────────────────────────────┘
 *                          │ Entities
 *   ┌──────────────────────▼───────────────────────────────┐
 *   │  REPOSITORY LAYER   (@Repository)                    │
 *   │  Responsibility: Database queries only. No logic.    │
 *   └──────────────────────┬───────────────────────────────┘
 *                          │ SQL
 *   ┌──────────────────────▼───────────────────────────────┐
 *   │  DATABASE  (H2 in-memory for this tutorial)          │
 *   └──────────────────────────────────────────────────────┘
 *
 * @Service
 *   Marks this class as a Spring-managed service bean.
 *   Spring creates a single instance (singleton) and injects it
 *   wherever needed via constructor injection.
 *
 * @Transactional
 *   Wraps each public method in a database transaction.
 *   If an exception occurs mid-operation, all database changes
 *   in that method are ROLLED BACK automatically.
 *   Think of it as "all or nothing" database safety.
 */
@Service
@Transactional
public class StudentService {

    /**
     * CONSTRUCTOR INJECTION — the preferred way to inject dependencies.
     *
     * WHY not @Autowired on a field?
     *   Field injection:       @Autowired private StudentRepository repo;  ← AVOID
     *   Constructor injection: (shown below)                               ← PREFER
     *
     * Constructor injection is better because:
     *   1. Makes dependencies explicit — you can see what this class needs
     *   2. Allows easy unit testing — pass a mock in the constructor
     *   3. Ensures object is always fully initialized
     *   4. 'final' keyword guarantees it cannot be changed after construction
     */
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // ================================================================
    // READ OPERATIONS
    // @Transactional(readOnly = true) — optimization for queries:
    //   - Hibernate skips "dirty checking" (no need to track changes)
    //   - Some databases can route read queries to replicas
    // ================================================================

    /**
     * Retrieve ALL students.
     *
     * Uses Java Streams to convert each Student entity to a DTO.
     * Stream pipeline: List<Student> → Stream → map to DTO → collect to List
     *
     * @return list of all students as response DTOs
     */
    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll()          // Returns List<Student>
                .stream()                           // Converts to Stream<Student>
                .map(this::convertToResponseDTO)    // Converts each Student → StudentResponseDTO
                .collect(Collectors.toList());      // Collects back to List<StudentResponseDTO>
    }

    /**
     * Retrieve a single student by ID.
     *
     * findById() returns Optional<Student> — it may or may not contain a value.
     * .orElseThrow() extracts the value if present, or throws the exception if empty.
     *
     * @param id the student's primary key
     * @return the student as a response DTO
     * @throws ResourceNotFoundException if no student with that ID exists
     */
    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
        return convertToResponseDTO(student);
    }

    /**
     * Get all students in a specific course.
     *
     * @param course the course name (exact match)
     * @return list of students in that course
     */
    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getStudentsByCourse(String course) {
        return studentRepository.findByCourse(course)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search students by name (case-insensitive partial match).
     *
     * @param name partial name to search for
     * @return list of matching students
     */
    @Transactional(readOnly = true)
    public List<StudentResponseDTO> searchByName(String name) {
        return studentRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get students with PAGINATION.
     *
     * PAGINATION EXPLAINED:
     *   Imagine you have 10,000 students. Returning all 10,000 at once would:
     *     - Consume huge memory on the server
     *     - Transfer massive data over the network
     *     - Crash the browser trying to render it
     *
     *   With pagination, you return, e.g., 10 students per page:
     *     Page 0: students 1–10
     *     Page 1: students 11–20
     *     ...
     *
     * PageRequest.of(page, size, Sort.by(...)):
     *   page    — which page to return (0-based: 0 = first page)
     *   size    — how many records per page
     *   Sort.by — how to sort the results
     *
     * The returned Page<T> object contains:
     *   .getContent()       → the actual list of DTOs
     *   .getTotalElements() → total records in the database
     *   .getTotalPages()    → how many pages exist total
     *   .getNumber()        → current page number
     *   .getSize()          → records per page
     *   .isFirst()          → true if this is the first page
     *   .isLast()           → true if this is the last page
     *
     * @param page   page number (0-indexed)
     * @param size   number of items per page
     * @param sortBy the field name to sort by (e.g., "name", "course")
     * @return a Page containing the slice of results plus metadata
     */
    @Transactional(readOnly = true)
    public Page<StudentResponseDTO> getStudentsPaginated(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return studentRepository.findAll(pageable)
                .map(this::convertToResponseDTO);   // Page.map() works like Stream.map()
    }

    /**
     * Search within a course by name (uses custom @Query from repository).
     *
     * @param course the course to filter by
     * @param name   partial name to search
     * @return matching students
     */
    @Transactional(readOnly = true)
    public List<StudentResponseDTO> searchByCourseAndName(String course, String name) {
        return studentRepository.findByCourseAndNameContaining(course, name)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // ================================================================
    // CREATE
    // ================================================================

    /**
     * Create a new student.
     *
     * STEPS:
     *   1. Business rule check: reject duplicate emails
     *   2. Convert incoming DTO → Entity (for saving to DB)
     *   3. Save to database (Hibernate generates INSERT SQL)
     *   4. Convert saved Entity → Response DTO (includes generated ID)
     *   5. Return the DTO
     *
     * @param requestDTO validated student data from the client
     * @return the newly created student with its auto-generated ID
     * @throws IllegalArgumentException if the email is already in use
     */
    public StudentResponseDTO createStudent(StudentRequestDTO requestDTO) {
        // Business rule: email must be unique
        if (studentRepository.existsByEmail(requestDTO.getEmail())) {
            throw new IllegalArgumentException(
                    "A student with email '" + requestDTO.getEmail() + "' already exists.");
        }

        // Convert DTO to Entity for persistence
        Student newStudent = convertToEntity(requestDTO);

        // save() performs an INSERT because this is a new entity (no ID yet)
        // After saving, the entity has its auto-generated ID populated
        Student savedStudent = studentRepository.save(newStudent);

        return convertToResponseDTO(savedStudent);
    }

    // ================================================================
    // UPDATE
    // ================================================================

    /**
     * Update all fields of an existing student (PUT semantics).
     *
     * The pattern: Load existing entity → Update its fields → Save.
     * We update the SAME entity object so its ID (primary key) stays unchanged.
     *
     * @param id         the ID of the student to update
     * @param requestDTO the new data for the student
     * @return the updated student as a response DTO
     * @throws ResourceNotFoundException if no student with that ID exists
     * @throws IllegalArgumentException  if the new email is taken by another student
     */
    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO requestDTO) {
        // Fetch existing student (throws 404 if not found)
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));

        // Allow keeping their own email, but reject if another student has the new email
        if (!existingStudent.getEmail().equals(requestDTO.getEmail())
                && studentRepository.existsByEmail(requestDTO.getEmail())) {
            throw new IllegalArgumentException(
                    "Email '" + requestDTO.getEmail() + "' is already used by another student.");
        }

        // Update the existing entity's fields (not creating a new object!)
        existingStudent.setName(requestDTO.getName());
        existingStudent.setEmail(requestDTO.getEmail());
        existingStudent.setCourse(requestDTO.getCourse());
        existingStudent.setGrade(requestDTO.getGrade());

        // save() on an entity WITH an ID performs UPDATE (not INSERT)
        Student updatedStudent = studentRepository.save(existingStudent);

        return convertToResponseDTO(updatedStudent);
    }

    // ================================================================
    // DELETE
    // ================================================================

    /**
     * Delete a student by ID.
     *
     * @param id the ID of the student to delete
     * @throws ResourceNotFoundException if no student with that ID exists
     */
    public void deleteStudent(Long id) {
        // Verify existence first — gives a meaningful error if not found
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student", "id", id);
        }
        studentRepository.deleteById(id);
    }

    // ================================================================
    // PRIVATE HELPER METHODS — DTO <-> Entity conversion
    //
    // In larger projects you would use a mapping library like MapStruct
    // (it auto-generates these at compile time). For learning purposes,
    // manual conversion makes the mapping explicit and easy to understand.
    // ================================================================

    /**
     * Convert a Student entity to a StudentResponseDTO.
     * Used when returning data to the client.
     */
    private StudentResponseDTO convertToResponseDTO(Student student) {
        return new StudentResponseDTO(
                student.getId(),
                student.getName(),
                student.getEmail(),
                student.getCourse(),
                student.getGrade(),
                student.getCreatedAt()
        );
    }

    /**
     * Convert a StudentRequestDTO to a Student entity.
     * Used when receiving data from the client and persisting it.
     */
    private Student convertToEntity(StudentRequestDTO dto) {
        return new Student(
                dto.getName(),
                dto.getEmail(),
                dto.getCourse(),
                dto.getGrade()
        );
    }
}

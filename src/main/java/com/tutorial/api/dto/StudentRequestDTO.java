package com.tutorial.api.dto;

import jakarta.validation.constraints.*;

/**
 * ============================================================
 * StudentRequestDTO — Data received FROM the client
 * ============================================================
 *
 * WHY USE DTOs (Data Transfer Objects)?
 *
 * Beginners often wonder: "Why not just use the Student entity
 * directly in the controller?" Here's why DTOs are better:
 *
 * 1. SECURITY
 *    Your @Entity has database metadata (@Id, @GeneratedValue,
 *    @PrePersist, etc.). Exposing entities directly lets clients
 *    potentially tamper with fields like 'id' or 'createdAt'.
 *    With a DTO, you expose ONLY what the client should provide.
 *
 * 2. FLEXIBILITY
 *    Your database schema and your API contract can evolve
 *    independently. Rename a DB column? Update the entity.
 *    The DTO (and thus the API) stays unchanged.
 *
 * 3. VALIDATION SEPARATION
 *    API validation rules (is email valid?) belong in the DTO.
 *    Database constraints (unique, not null) belong in the entity.
 *
 * 4. CLEAN DESIGN
 *    "Separation of Concerns" — each class has one clear purpose.
 *    Entity = database model. DTO = API data shape.
 *
 * THIS DTO represents the JSON body for creating/updating a student.
 * Notice: no 'id' or 'createdAt' — the server sets those, not the client.
 *
 * EXAMPLE JSON that maps to this DTO:
 * {
 *   "name": "Alice Johnson",
 *   "email": "alice@example.com",
 *   "course": "Computer Science",
 *   "grade": "A"
 * }
 */
public class StudentRequestDTO {

    /** Student's full name (required, 2–100 characters) */
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    /** Student's email address (required, must be valid format) */
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address (e.g., user@example.com)")
    private String email;

    /** The course the student is enrolled in (required) */
    @NotBlank(message = "Course is required")
    @Size(max = 100, message = "Course name cannot exceed 100 characters")
    private String course;

    /**
     * Letter grade (optional — a student may not have a grade yet).
     * Valid values: A+, A, A-, B+, B, B-, C+, C, C-, D+, D, D-, F
     */
    @Pattern(regexp = "^[A-F][+-]?$",
             message = "Grade must be a valid letter grade (e.g., A, B+, C-, F)")
    private String grade;

    // Constructors
    public StudentRequestDTO() {}

    public StudentRequestDTO(String name, String email, String course, String grade) {
        this.name = name;
        this.email = email;
        this.course = course;
        this.grade = grade;
    }

    // Getters and Setters — Jackson uses these for JSON serialization/deserialization
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
}

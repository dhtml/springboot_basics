package com.tutorial.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * ============================================================
 * STUDENT ENTITY — JPA / Database Model
 * ============================================================
 *
 * An @Entity class is a Java class that maps directly to a
 * database TABLE. Each field becomes a COLUMN, and each
 * instance of this class represents one ROW in the table.
 *
 * Hibernate (included via spring-boot-starter-data-jpa) reads
 * this class and automatically creates the "students" table
 * in H2 — no SQL CREATE TABLE statement needed!
 *
 * RESULTING TABLE STRUCTURE:
 * ┌────────────────────────────────────────────────────────┐
 * │ TABLE: students                                         │
 * ├──────────────┬──────────────────────┬──────────────────┤
 * │ id           │ BIGINT, PK, AUTO INC │ @Id              │
 * │ name         │ VARCHAR(100), NN     │ @NotBlank        │
 * │ email        │ VARCHAR, NN, UNIQUE  │ @Email           │
 * │ course       │ VARCHAR(100), NN     │ @NotBlank        │
 * │ grade        │ VARCHAR, NULLABLE    │ @Pattern         │
 * │ created_at   │ TIMESTAMP, NN        │ @PrePersist      │
 * └──────────────┴──────────────────────┴──────────────────┘
 */
@Entity                    // Marks this class as a JPA-managed database entity
@Table(name = "students")  // Maps to a table named "students" (default would be "Student")
public class Student {

    /**
     * PRIMARY KEY
     *
     * Every table needs a unique identifier for each row.
     *
     * @Id              — marks this field as the primary key column
     * @GeneratedValue  — tells Hibernate to auto-generate the ID value
     * IDENTITY strategy — the database increments it automatically (1, 2, 3, ...)
     *
     * We use Long (not int) because IDs can grow very large over time.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * NAME column
     *
     * @Column(nullable = false)
     *   → Database-level constraint: the column cannot be NULL
     *   → Enforced by the DB engine itself
     *
     * @NotBlank(message = "...")
     *   → Application-level validation: rejects null, empty, or whitespace-only strings
     *   → Enforced by Spring's @Valid before data even reaches the DB
     *
     * @Size(min, max)
     *   → Application-level validation: enforces string length limits
     *
     * Having BOTH @Column constraints AND @Notblank/@Size is best practice:
     *   - @Column constraint protects the database integrity
     *   - @NotBlank/@Size gives nicer user-facing error messages
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    /**
     * EMAIL column
     *
     * unique = true creates a UNIQUE constraint in the database,
     * meaning two students cannot have the same email address.
     *
     * @Email validates the format (must contain @, valid TLD, etc.)
     */
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    private String email;

    /**
     * COURSE column
     *
     * The academic course the student is enrolled in.
     */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Course is required")
    @Size(max = 100, message = "Course name cannot exceed 100 characters")
    private String course;

    /**
     * GRADE column — Optional
     *
     * Students may not have a grade yet, so this field is nullable.
     *
     * @Pattern validates the format: A+, A, A-, B+, B, B-, C+, C, C-, D, F
     * The regex: ^[A-F][+-]?$
     *   ^      = start of string
     *   [A-F]  = one character: A, B, C, D, E, or F
     *   [+-]?  = optionally followed by + or -
     *   $      = end of string
     */
    @Column
    @Pattern(regexp = "^[A-F][+-]?$",
             message = "Grade must be a valid letter grade (e.g., A, B+, C-, F)")
    private String grade;

    /**
     * CREATED_AT column — Automatically set by the app.
     *
     * updatable = false means Hibernate will NEVER include this column
     * in an UPDATE statement — once set, it stays the same forever.
     *
     * The value is set by @PrePersist before the entity is first saved.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * JPA LIFECYCLE CALLBACK
     *
     * @PrePersist marks a method to run AUTOMATICALLY right before
     * the entity is first persisted (saved) to the database.
     *
     * We use it to automatically set the createdAt timestamp,
     * so callers never need to set it manually.
     *
     * Other lifecycle callbacks you can use:
     *   @PostPersist  — after INSERT
     *   @PreUpdate    — before UPDATE
     *   @PostUpdate   — after UPDATE
     *   @PreRemove    — before DELETE
     *   @PostLoad     — after SELECT
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ================================================================
    // CONSTRUCTORS
    // ================================================================

    /** Required by JPA — Hibernate needs a no-arg constructor to instantiate entities. */
    public Student() {}

    /** Convenience constructor for creating a Student without an ID (ID is auto-generated). */
    public Student(String name, String email, String course, String grade) {
        this.name = name;
        this.email = email;
        this.course = course;
        this.grade = grade;
    }

    // ================================================================
    // GETTERS AND SETTERS
    // Jackson uses these to serialize (Java → JSON) and
    // deserialize (JSON → Java) this object.
    // ================================================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', email='" + email
                + "', course='" + course + "', grade='" + grade + "'}";
    }
}

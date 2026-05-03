package com.tutorial.api.dto;

import java.time.LocalDateTime;

/**
 * ============================================================
 * StudentResponseDTO — Data sent TO the client
 * ============================================================
 *
 * This DTO represents what we return in API responses.
 * It includes server-generated fields (id, createdAt) that
 * clients receive but never send.
 *
 * Separation from StudentRequestDTO lets you:
 *   - Include 'id' and 'createdAt' in responses (server-generated)
 *   - Exclude sensitive fields if needed (e.g., passwords, tokens)
 *   - Add computed fields (e.g., "fullTitle", "isHonorStudent")
 *
 * EXAMPLE JSON that this DTO produces:
 * {
 *   "id": 1,
 *   "name": "Alice Johnson",
 *   "email": "alice@example.com",
 *   "course": "Computer Science",
 *   "grade": "A",
 *   "createdAt": "2024-10-15T10:30:00"
 * }
 */
public class StudentResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String course;
    private String grade;
    private LocalDateTime createdAt;

    // Constructors
    public StudentResponseDTO() {}

    public StudentResponseDTO(Long id, String name, String email,
                               String course, String grade, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.course = course;
        this.grade = grade;
        this.createdAt = createdAt;
    }

    // Getters and Setters
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
}

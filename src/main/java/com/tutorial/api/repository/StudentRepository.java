package com.tutorial.api.repository;

import com.tutorial.api.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ============================================================
 * STUDENT REPOSITORY — Database Access Layer
 * ============================================================
 *
 * The Repository is the layer responsible for all database
 * communication. Think of it as your database "API".
 *
 * HOW IT WORKS:
 *   By extending JpaRepository<Student, Long>, Spring Data JPA
 *   automatically generates a complete implementation at runtime.
 *   You write an interface — Spring writes the implementation!
 *
 * JpaRepository<Student, Long> generic parameters:
 *   Student  — the entity type this repository manages
 *   Long     — the type of the primary key (@Id field)
 *
 * ============================================================
 * FREE METHODS — No code needed, they just work:
 * ============================================================
 *
 *   findAll()                  → SELECT * FROM students
 *   findAll(Pageable pageable) → Paginated SELECT with sorting
 *   findById(Long id)          → SELECT * FROM students WHERE id = ?
 *   save(Student s)            → INSERT (new) or UPDATE (existing)
 *   deleteById(Long id)        → DELETE FROM students WHERE id = ?
 *   existsById(Long id)        → SELECT COUNT(*) > 0 WHERE id = ?
 *   count()                    → SELECT COUNT(*) FROM students
 *
 * ============================================================
 * DERIVED QUERY METHODS — Name-based auto-generation:
 * ============================================================
 *
 * Spring reads the method name and generates the SQL automatically!
 * Format: findBy + FieldName + OptionalConditions
 *
 * Examples:
 *   findByEmail          → WHERE email = ?
 *   findByCourse         → WHERE course = ?
 *   findByNameContaining → WHERE name LIKE '%?%'
 *   findByGradeAndCourse → WHERE grade = ? AND course = ?
 *
 * @Repository tells Spring this is a data-access component.
 * (Optional here since JpaRepository implies it, but good for clarity.)
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Find all students enrolled in a specific course.
     *
     * Spring generates: SELECT * FROM students WHERE course = ?
     *
     * @param course the exact course name to match
     * @return list of students in that course (empty list if none found)
     */
    List<Student> findByCourse(String course);

    /**
     * Find a student by their email address.
     *
     * Returns Optional<Student> to safely handle "not found" without exceptions.
     * ALWAYS use Optional for single-result queries that may return nothing.
     *
     * Spring generates: SELECT * FROM students WHERE email = ?
     *
     * @param email the email to search for
     * @return Optional containing the student, or Optional.empty() if not found
     */
    Optional<Student> findByEmail(String email);

    /**
     * Search students by name — case-insensitive partial match.
     *
     * "ContainingIgnoreCase" translates to:
     *   WHERE UPPER(name) LIKE UPPER(CONCAT('%', ?, '%'))
     *
     * Example: findByNameContainingIgnoreCase("ali") finds "Alice", "Mali"
     *
     * @param name the search term (partial name)
     * @return list of students whose name contains the search term
     */
    List<Student> findByNameContainingIgnoreCase(String name);

    /**
     * Paginated search within a specific course.
     *
     * Pagination is CRITICAL for production APIs. Without it, fetching
     * 1 million records at once would crash your server and browser.
     *
     * The Pageable parameter carries: page number, page size, sort order.
     * The returned Page<Student> carries: the data + metadata (total count,
     * total pages, current page, etc.)
     *
     * @param course   the course to filter by
     * @param pageable pagination configuration (from PageRequest.of(...))
     * @return a Page containing a slice of results plus metadata
     */
    Page<Student> findByCourse(String course, Pageable pageable);

    /**
     * Check if a student with the given email already exists.
     *
     * Used BEFORE creating a new student to enforce the unique email rule.
     * Faster than findByEmail() because it only needs a COUNT, not the full row.
     *
     * Spring generates: SELECT COUNT(*) > 0 FROM students WHERE email = ?
     *
     * @param email the email to check
     * @return true if a student with this email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Count how many students are enrolled in a specific course.
     *
     * Spring generates: SELECT COUNT(*) FROM students WHERE course = ?
     *
     * @param course the course name
     * @return the number of students in that course
     */
    long countByCourse(String course);

    /**
     * CUSTOM JPQL QUERY — for when the derived method name gets complex.
     *
     * JPQL (Java Persistence Query Language) is like SQL, but uses
     * CLASS names and FIELD names (not table/column names).
     *
     * Example:
     *   SQL:   SELECT * FROM students WHERE course = ? AND LOWER(name) LIKE ?
     *   JPQL:  SELECT s FROM Student s WHERE s.course = :course AND LOWER(s.name) LIKE ...
     *
     * Use @Param to bind named parameters (:paramName) in the query.
     *
     * @param course the course to filter by
     * @param name   partial name to search for within the course
     * @return list of students in that course whose name matches
     */
    @Query("SELECT s FROM Student s WHERE s.course = :course " +
           "AND LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Student> findByCourseAndNameContaining(
            @Param("course") String course,
            @Param("name") String name);
}

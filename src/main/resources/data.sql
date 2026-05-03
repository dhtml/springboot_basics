-- ============================================================
-- INITIAL SEED DATA
-- ============================================================
-- This file is executed automatically on every application startup
-- (configured via spring.sql.init.mode=always in application.properties)
--
-- It runs AFTER Hibernate creates the "students" table from the
-- Student.java entity (thanks to spring.jpa.defer-datasource-initialization=true)
--
-- Use the H2 Console at http://localhost:8080/h2-console to browse this data.
-- ============================================================

INSERT INTO students (name, email, course, grade, created_at) VALUES
    ('Alice Johnson',  'alice@example.com',  'Computer Science', 'A',  CURRENT_TIMESTAMP),
    ('Bob Smith',      'bob@example.com',    'Mathematics',      'B+', CURRENT_TIMESTAMP),
    ('Carol White',    'carol@example.com',  'Physics',          'A-', CURRENT_TIMESTAMP),
    ('David Brown',    'david@example.com',  'Chemistry',        'B',  CURRENT_TIMESTAMP),
    ('Eve Davis',      'eve@example.com',    'Computer Science', 'A+', CURRENT_TIMESTAMP),
    ('Frank Miller',   'frank@example.com',  'Mathematics',      'C+', CURRENT_TIMESTAMP),
    ('Grace Wilson',   'grace@example.com',  'Physics',          'B-', CURRENT_TIMESTAMP);

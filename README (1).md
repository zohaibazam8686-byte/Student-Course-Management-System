# 🎓 Student Course Management System

> A robust relational database-backed system for managing student enrollments, course catalogs, and academic records.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Database Schema](#database-schema)
- [Tables](#tables)
- [Relationships](#relationships)
- [Setup & Installation](#setup--installation)
- [Known Issues & Fixes](#known-issues--fixes)
- [Sample Queries](#sample-queries)
- [Project Structure](#project-structure)
- [Contributing](#contributing)

---

## Overview

The **Student Course Management System** is a MySQL-based backend solution designed to manage students, courses, and their enrollments in an academic environment. It provides a clean relational schema with referential integrity enforced through foreign keys and cascading deletes.

**Key Features:**
- Track students with unique roll numbers, contact info, and GPA
- Manage a course catalog with credit hours
- Enroll students in multiple courses with automatic integrity checks
- Cascade deletions to prevent orphaned records

---

## Database Schema

```
student_management
│
├── students          (roll_number PK, name, email, gpa)
│
├── courses           (course_code PK, title, credit_hours)
│
└── enrollments       (enrollment_id PK, roll_number FK, course_code FK)
                            │                    │
                      → students           → courses
```

---

## Tables

### `students`

Stores all registered student information.

| Column        | Type          | Constraints   | Description                    |
|---------------|---------------|---------------|--------------------------------|
| `roll_number` | VARCHAR(50)   | PRIMARY KEY   | Unique student identifier      |
| `name`        | VARCHAR(100)  | —             | Full name of the student       |
| `email`       | VARCHAR(100)  | —             | Contact email address          |
| `gpa`         | DOUBLE        | —             | Current GPA (0.0 – 4.0 scale)  |

---

### `courses`

Stores all available courses in the system.

| Column        | Type          | Constraints   | Description                    |
|---------------|---------------|---------------|--------------------------------|
| `course_code` | VARCHAR(50)   | PRIMARY KEY   | Unique course identifier       |
| `title`       | VARCHAR(100)  | —             | Full name of the course        |
| `credit_hours`| INT           | —             | Number of credit hours         |

---

### `enrollments`

Junction table mapping students to courses (many-to-many relationship).

| Column          | Type          | Constraints                        | Description                        |
|-----------------|---------------|------------------------------------|------------------------------------|
| `enrollment_id` | INT           | PRIMARY KEY, AUTO_INCREMENT        | Auto-generated enrollment ID       |
| `roll_number`   | VARCHAR(50)   | FOREIGN KEY → `students`           | References the enrolled student    |
| `course_code`   | VARCHAR(50)   | FOREIGN KEY → `courses`            | References the enrolled course     |

---

## Relationships

```
students (1) ──────────< enrollments >────────── (1) courses
              (many enrollments           (many enrollments
               per student)               per course)
```

- One **student** can enroll in many **courses**
- One **course** can have many **students**
- The `enrollments` table resolves this many-to-many relationship

---

## Setup & Installation

### Prerequisites

- MySQL 5.7+ or MySQL 8.0+
- MySQL Workbench or any MySQL-compatible client

### Steps

```sql
-- Step 1: Run the database script
SOURCE database_script.sql;

-- Step 2: Verify tables were created
USE student_management;
SHOW TABLES;

-- Step 3: Confirm table structure
DESCRIBE students;
DESCRIBE courses;
DESCRIBE enrollments;
```

---

## Known Issues & Fixes

> ⚠️ **Duplicate Table Definition in `database_script.sql`**

The script currently defines the `enrollments` table **twice**. The second definition is missing the `ON DELETE CASCADE` clause, which means the second `CREATE TABLE` statement will fail at runtime if the first has already been executed.

**Problematic section in the script:**

```sql
-- ✅ First definition (CORRECT — has ON DELETE CASCADE)
CREATE TABLE enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    roll_number VARCHAR(50),
    course_code VARCHAR(50),
    FOREIGN KEY (roll_number) REFERENCES students(roll_number) ON DELETE CASCADE,
    FOREIGN KEY (course_code) REFERENCES courses(course_code) ON DELETE CASCADE
);

-- ❌ Second definition (DUPLICATE — should be removed)
CREATE TABLE enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    roll_number VARCHAR(50),
    course_code VARCHAR(50),
    FOREIGN KEY (roll_number) REFERENCES students(roll_number),
    FOREIGN KEY (course_code) REFERENCES courses(course_code)
);
```

**Fix:** Remove the second `CREATE TABLE enrollments` block entirely. Keep only the first definition with `ON DELETE CASCADE`.

---

## Sample Queries

```sql
-- Insert a student
INSERT INTO students (roll_number, name, email, gpa)
VALUES ('S001', 'Ali Hassan', 'ali@example.com', 3.7);

-- Insert a course
INSERT INTO courses (course_code, title, credit_hours)
VALUES ('CS101', 'Introduction to Programming', 3);

-- Enroll a student in a course
INSERT INTO enrollments (roll_number, course_code)
VALUES ('S001', 'CS101');

-- Get all courses a student is enrolled in
SELECT c.title, c.credit_hours
FROM courses c
JOIN enrollments e ON c.course_code = e.course_code
WHERE e.roll_number = 'S001';

-- Get all students enrolled in a specific course
SELECT s.name, s.email, s.gpa
FROM students s
JOIN enrollments e ON s.roll_number = e.roll_number
WHERE e.course_code = 'CS101';

-- Get total credit hours per student
SELECT s.name, SUM(c.credit_hours) AS total_credits
FROM students s
JOIN enrollments e ON s.roll_number = e.roll_number
JOIN courses c ON e.course_code = c.course_code
GROUP BY s.name;
```

---

## Project Structure

```
StudentCourseManagementSystem/
│
├── database_script.sql        # Main SQL schema and table definitions
├── README.md                  # Project documentation (this file)
└── (application files...)     # Java/.NET/Python app files (if applicable)
```

---

## Contributing

1. Fork this repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -m "Add your feature"`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Open a Pull Request

---

## License

This project is intended for academic and educational use.

---

*Made with ❤️ for academic excellence.*

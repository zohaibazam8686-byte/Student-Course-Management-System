CREATE DATABASE student_management;
USE student_management;

CREATE TABLE students (
    roll_number VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100),
    gpa DOUBLE
);

CREATE TABLE courses (
    course_code VARCHAR(50) PRIMARY KEY,
    title VARCHAR(100),
    credit_hours INT
);

CREATE TABLE enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    roll_number VARCHAR(50),
    course_code VARCHAR(50),
    FOREIGN KEY (roll_number) REFERENCES students(roll_number) ON DELETE CASCADE,
    FOREIGN KEY (course_code) REFERENCES courses(course_code) ON DELETE CASCADE
);
CREATE TABLE enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    roll_number VARCHAR(50),
    course_code VARCHAR(50),
    FOREIGN KEY (roll_number) REFERENCES students(roll_number),
    FOREIGN KEY (course_code) REFERENCES courses(course_code)
);
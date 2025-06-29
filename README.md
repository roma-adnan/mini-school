# mini-school
A Spring Boot REST API to manage students, courses, enrollments, grades, and academic progress.

## Tech Stack
- Java 17
- Spring Boot 3.2.5
- Spring Data JPA
- H2 (in-memory)
- Hibernate
- Lombok
- Swagger/OpenAPI
- Maven

## Setup Instructions
1. Clone the repo
git clone https://github.com/roma-adnan/mini-school.git
cd mini-school

2. Build the project
mvn clean install

3. Run the application
mvn spring-boot:run

4. Visit Swagger UI at http://localhost:8080/swagger-ui.html

5. Sample CSV Format

studentName,studentEmail,courseTitle,courseDesc,courseCode,letterGrade,prerequisites
John Doe,john@example.com,Intro to CS,CS Basics,CS101,A,
Jane Smith,jane@example.com,Data Structures,DS Course,CS102,B,CS101


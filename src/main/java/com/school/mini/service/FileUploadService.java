package com.school.mini.service;

import com.school.mini.dto.FileUploadResponse;
import com.school.mini.entity.Course;
import com.school.mini.entity.Enrollment;
import com.school.mini.entity.Grade;
import com.school.mini.entity.Student;
import com.school.mini.repository.CourseRepository;
import com.school.mini.repository.EnrollmentRepository;
import com.school.mini.repository.GradeRepository;
import com.school.mini.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileUploadService {

    private final Map<String, String> importStatusMap = new ConcurrentHashMap<>();
    private final Map<String, List<String>> errorMap = new ConcurrentHashMap<>();

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GradeRepository gradeRepository;

    public FileUploadResponse upload(MultipartFile file) {
        String importId = UUID.randomUUID().toString();
        importStatusMap.put(importId, "PENDING");
        processAsync(importId, file);
        return FileUploadResponse.builder().importId(importId).status("PENDING").errors(new ArrayList<>()).build();
    }

    @Async
    @Transactional
    public void processAsync(String importId, MultipartFile file) {

        List<String> errors = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            importStatusMap.put(importId, "PROCESSING");

            String line;
            boolean headerSkipped = false;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length < 7) {
                    errors.add("Line " + lineNumber + ": Expected 7 fields, found " + parts.length);
                    continue;
                }

                String studentName = parts[0].trim();
                String studentEmail = parts[1].trim();
                String courseTitle = parts[2].trim();
                String courseDesc = parts[3].trim();
                String couseCode = parts[4].trim();
                String letterGrade = parts[5].trim();
                String prerequisitesRaw = (parts.length > 6 && !parts[6].trim().isEmpty()) ? parts[6].trim() : "";
                try {

                    if (studentName.isEmpty() || studentEmail.isEmpty() || courseTitle.isEmpty() || couseCode.isEmpty() || letterGrade.isEmpty()) {
                        throw new IllegalArgumentException("Line " + line + ": Fields must not be blank");
                    }

                    if (!studentEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                        throw new IllegalArgumentException("Line " + line + ": Invalid email format");
                    }

                    if (!letterGrade.matches("A|B|C|D|F")) {
                        throw new IllegalArgumentException("Line " + line + ": Invalid grade value");
                    }

                    Student student = studentRepository.findByEmail(studentEmail)
                            .orElseGet(() -> studentRepository.save(Student.builder()
                                    .name(studentName)
                                    .email(studentEmail)
                                    .build()));

                    Course course = courseRepository.findAll().stream()
                            .filter(c -> c.getTitle().equalsIgnoreCase(courseTitle))
                            .findFirst()
                            .orElseGet(() -> courseRepository.save(Course.builder()
                                    .title(courseTitle)
                                    .description(courseDesc)
                                    .capacity(30)
                                    .code(couseCode)
                                    .build()));

                    Set<Course> prerequisites;
                    if (!prerequisitesRaw.isBlank()) {
                        Set<String> prerequisiteCodes = Arrays.stream(prerequisitesRaw.split("\\|"))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .collect(Collectors.toSet());

                        prerequisites = courseRepository.findAll().stream()
                                .filter(c -> prerequisiteCodes.contains(c.getCode()))
                                .collect(Collectors.toSet());

                        Set<String> missingCodes = prerequisiteCodes.stream()
                                .filter(code -> prerequisites.stream().noneMatch(c -> c.getCode().equals(code)))
                                .collect(Collectors.toSet());

                        if (!missingCodes.isEmpty()) {
                            errors.add("Line " + lineNumber + ": Prerequisite course codes not found: " + missingCodes);
                        }

                        for (Course prereq : prerequisites) {
                            Optional<Enrollment> prereqEnrollment = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), prereq.getId());
                            if (prereqEnrollment.isEmpty() || !prereqEnrollment.get().isCompleted()) {
                                throw new IllegalArgumentException("Student has not completed prerequisite: " + prereq.getCode());
                            }

                            Optional<Grade> grade = gradeRepository.findByEnrollment(prereqEnrollment.get());
                            if (grade.isEmpty() || !grade.get().isCompleted() || grade.get().getLetter().equalsIgnoreCase("F")) {
                                throw new IllegalArgumentException("Student did not pass prerequisite: " + prereq.getCode());
                            }
                        }

                        if (!prerequisites.contains(course)) {
                            course.setPrerequisites(prerequisites);
                            courseRepository.save(course);
                        }
                    } else {
                        prerequisites = new HashSet<>();
                    }

                    // Capacity check
                    long currentEnrollment = enrollmentRepository.countByCourseIdAndCompletedFalse(course.getId());
                    if (currentEnrollment >= course.getCapacity()) {
                        throw new IllegalArgumentException("Course is full: " + course.getCode());
                    }


                    // Enroll if not already
                    Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), course.getId())
                            .orElseGet(() -> enrollmentRepository.save(Enrollment.builder()
                                    .student(student)
                                    .course(course)
                                    .completed(true)
                                    .build()));

                    // Record grade if not already present
                    Optional<Grade> existingGrade = gradeRepository.findByEnrollment(enrollment);
                    if (existingGrade.isEmpty()) {
                        Grade grade = Grade.builder()
                                .enrollment(enrollment)
                                .letter(letterGrade)
                                .completed(true)
                                .build();
                        gradeRepository.save(grade);
                    } else {
                        // Update only if course is not completed
                        if (!enrollment.isCompleted()) {
                            Grade grade = existingGrade.get();
                            grade.setLetter(letterGrade);
                            gradeRepository.save(grade);
                        } else {
                            throw new IllegalArgumentException("Cannot update grade: course already completed for student.");
                        }
                    }


                } catch (Exception e) {
                    log.warn("Line {}: {}", lineNumber, e.getMessage());
                    errors.add("Line " + lineNumber + ": " + e.getMessage());
                }
            }

            importStatusMap.put(importId, errors.isEmpty() ? "COMPLETED" : "COMPLETED_WITH_ERRORS");
            errorMap.put(importId, errors);

        } catch (Exception e) {
            log.error("Failed to import file", e);
            importStatusMap.put(importId, "FAILED");
            errorMap.put(importId, List.of("Critical error: " + e.getMessage()));
        }
    }

    public FileUploadResponse getStatus(String importId) {
        return FileUploadResponse.builder()
                .importId(importId)
                .status(importStatusMap.getOrDefault(importId, "NOT_FOUND"))
                .errors(errorMap.getOrDefault(importId, new ArrayList<>()))
                .build();
    }
}


package com.school.mini.service;

import com.school.mini.dto.EnrollmentRequest;
import com.school.mini.entity.Course;
import com.school.mini.entity.Enrollment;
import com.school.mini.entity.Grade;
import com.school.mini.entity.Student;
import com.school.mini.exception.BusinessException;
import com.school.mini.repository.CourseRepository;
import com.school.mini.repository.EnrollmentRepository;
import com.school.mini.repository.GradeRepository;
import com.school.mini.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final Set<String> PASSING_GRADES = Set.of("A", "A-", "B+", "B", "B-", "C+", "C");

    public void enrollStudent(EnrollmentRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new BusinessException("Student not found"));

        for (Long courseId : request.getCourseId()) {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new BusinessException("Course not found: " + courseId));

            if (enrollmentRepository.findByStudentIdAndCourseId(student.getId(), course.getId()).isPresent()) {
                throw new BusinessException("Already enrolled: " + course.getTitle());
            }

            for (Course prerequisite : course.getPrerequisites()) {
                Optional<Enrollment> preEnrollment = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), prerequisite.getId());
                if (preEnrollment.isEmpty() || !preEnrollment.get().isCompleted()) {
                    throw new BusinessException("Student has not completed prerequisite: " + course.getTitle());
                }
            }

            long enrolledCount = enrollmentRepository.countByCourseIdAndCompletedFalse(course.getId());
            if (enrolledCount >= course.getCapacity()) {
                throw new BusinessException("Course full: " + course.getTitle());
            }

            Enrollment enrollment = Enrollment.builder()
                    .student(student)
                    .course(course)
                    .completed(false)
                    .build();

            enrollmentRepository.save(enrollment);
        }
    }
}

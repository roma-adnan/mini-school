package com.school.mini.service;

import com.school.mini.dto.AcademicProgressDto;
import com.school.mini.dto.GradeDto;
import com.school.mini.entity.Enrollment;
import com.school.mini.entity.Grade;
import com.school.mini.entity.Student;
import com.school.mini.exception.BusinessException;
import com.school.mini.repository.EnrollmentRepository;
import com.school.mini.repository.GradeRepository;
import com.school.mini.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;

    public void recordGrade(GradeDto dto) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(dto.getStudentId(), dto.getCourseId())
                .orElseThrow(() -> new BusinessException("Student is not enrolled in this course"));

        String letterGrade = dto.getLetterGrade();
        if (!letterGrade.matches("A|B|C|D|F")) {
            throw new BusinessException("Invalid grade. Must be one of A, B, C, D, F.");
        }

        Optional<Grade> existingGrade = gradeRepository.findByEnrollment(enrollment);

        if (existingGrade.isPresent()) {
            if (enrollment.isCompleted()) {
                throw new BusinessException("Cannot update grade. Course already marked as completed.");
            }

            Grade grade = existingGrade.get();
            grade.setLetter(letterGrade);
            gradeRepository.save(grade);

        } else {

            Grade grade = new Grade();
            grade.setEnrollment(enrollment);
            grade.setLetter(letterGrade);
            grade.setCompleted(true);

            enrollment.setCompleted(true);
            gradeRepository.save(grade);
        }
    }

    public AcademicProgressDto getAcademicProgress(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException("Student not found"));

        List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsWithGradesByStudentId(studentId);
        Map<String, String> courseGrades = new HashMap<>();
        double total = 0;
        int count = 0;

        for (Enrollment e : enrollments) {
            Optional<Grade> gradeOpt = gradeRepository.findByEnrollment(e);
            if (gradeOpt.isPresent()) {
                String letter = gradeOpt.get().getLetter();
                courseGrades.put(e.getCourse().getTitle(), letter);
                total += letterToGpa(letter);
                count++;
            }
        }

        double gpa = count == 0 ? 0.0 : total / count;

        return AcademicProgressDto.builder()
                .studentId(student.getId())
                .studentName(student.getName())
                .courseGrades(courseGrades)
                .gpa(gpa)
                .build();
    }

    private double letterToGpa(String letter) {
        return switch (letter) {
            case "A" -> 4.0;
            case "B" -> 3.0;
            case "C" -> 2.0;
            case "D" -> 1.0;
            case "F" -> 0.0;
            default -> 0.0;
        };
    }
}

package com.school.mini.repository;

import com.school.mini.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentId(Long studentId);
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    long countByCourseId(Long courseId);
    long countByCourseIdAndCompletedFalse(Long courseId);

    @Query("SELECT e FROM Enrollment e JOIN FETCH e.course c LEFT JOIN FETCH Grade g ON g.enrollment = e WHERE e.student.id = :studentId")
    List<Enrollment> findEnrollmentsWithGradesByStudentId(Long studentId);
}

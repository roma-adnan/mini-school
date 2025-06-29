package com.school.mini.validation;

import com.school.mini.dto.GradeDto;
import com.school.mini.exception.BusinessException;

import java.util.Objects;
import java.util.Set;

public class GradeDtoValidator {

    private static final Set<String> VALID_GRADES = Set.of("A", "B", "C", "D", "F");

    public static void validate(GradeDto dto) {
        if (Objects.isNull(dto)) {
            throw new BusinessException("Grade data is required");
        }

        if (Objects.isNull(dto.getStudentId()) || dto.getStudentId() <= 0) {
            throw new BusinessException("Valid student ID is required");
        }

        if (Objects.isNull(dto.getCourseId()) || dto.getCourseId() <= 0) {
            throw new BusinessException("Valid course ID is required");
        }

        if (!VALID_GRADES.contains(dto.getLetterGrade())) {
            throw new BusinessException("Grade must be one of A, B, C, D, F");
        }
    }
}

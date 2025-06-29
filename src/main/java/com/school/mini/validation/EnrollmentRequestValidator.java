package com.school.mini.validation;

import com.school.mini.dto.EnrollmentRequest;
import com.school.mini.exception.BusinessException;

import java.util.Objects;

public class EnrollmentRequestValidator {

    public static void validate(EnrollmentRequest request) {
        if (Objects.isNull(request)) {
            throw new BusinessException("Enrollment data is required");
        }

        if (Objects.isNull(request.getStudentId()) || request.getStudentId() <= 0) {
            throw new BusinessException("Valid student ID is required");
        }

        if (Objects.isNull(request.getCourseId()) || request.getCourseId().isEmpty()) {
            throw new BusinessException("At least one course ID must be provided");
        }

        boolean invalid = request.getCourseId().stream().anyMatch(id -> id == null || id <= 0);
        if (invalid) {
            throw new BusinessException("All course IDs must be valid and greater than 0");
        }
    }
}

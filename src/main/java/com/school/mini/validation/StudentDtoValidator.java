package com.school.mini.validation;

import com.school.mini.dto.StudentDto;
import com.school.mini.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class StudentDtoValidator {

    public static void validateForCreate(StudentDto dto) {
        baseValidation(dto);
    }

    public static void validateForUpdate(Long id, StudentDto dto) {
        if (Objects.isNull(id) || id <= 0) {
            throw new BusinessException("Student ID must be provided for update and greater than 0");
        }
        baseValidation(dto);
    }

    private static void baseValidation(StudentDto dto) {
        if (Objects.isNull(dto)) {
            throw new BusinessException("Student data is required");
        }

        if (StringUtils.isBlank(dto.getName())) {
            throw new BusinessException("Student name is required");
        }

        if (StringUtils.isBlank(dto.getEmail())) {
            throw new BusinessException("Student email is required");
        }

        if (!dto.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new BusinessException("Student email format is invalid");
        }
    }
}

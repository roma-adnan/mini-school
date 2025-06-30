package com.school.mini.validation;

import com.school.mini.dto.CourseDto;
import com.school.mini.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class CourseRequestValidator {

    public static void validateForCreate(CourseDto courseDto) {
        baseValidation(courseDto);
    }

    public static void validateForUpdate(Long id, CourseDto courseDto) {
        if (Objects.isNull(id) || id <= 0) {
            throw new BusinessException("Course ID is required for update and must be greater than 0");
        }
        baseValidation(courseDto);
    }

    private static void baseValidation(CourseDto courseDto) {
        if (Objects.isNull(courseDto)) {
            throw new BusinessException("Course data is required");
        }

        if (StringUtils.isBlank(courseDto.getTitle())) {
            throw new BusinessException("Course title is required");
        }

        if (StringUtils.isBlank(courseDto.getCode())) {
            throw new BusinessException("Course code is required");
        }

        if (courseDto.getCapacity() <= 0) {
            throw new BusinessException("Course capacity must be greater than zero");
        }

//        if (Objects.isNull(courseDto.getPrerequisites())) {
//            throw new BusinessException("Prerequisite list must not be null (can be empty)");
//        }
    }
}

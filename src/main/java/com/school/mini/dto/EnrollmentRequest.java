package com.school.mini.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentRequest {

    @NotNull(message = "Student ID must not be null")
    private Long studentId;

    @NotEmpty(message = "At least one course ID must be provided")
    private List<Long> courseId;
}

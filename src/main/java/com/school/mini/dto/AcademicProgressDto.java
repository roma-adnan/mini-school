package com.school.mini.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicProgressDto {
    private Long studentId;
    private String studentName;
    private Map<String, String> courseGrades;
    private double gpa;
}

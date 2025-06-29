package com.school.mini.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeDto {
    private Long studentId;
    private Long courseId;
    @Pattern(regexp = "A|B|C|D|F")
    private String letterGrade;
}
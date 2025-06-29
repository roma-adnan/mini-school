package com.school.mini.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradeRequest {

    @NotNull
    private Long studentId;

    @NotNull
    private Long courseId;

    @Pattern(regexp="A|B|C|D|F")
    private String letter;

}

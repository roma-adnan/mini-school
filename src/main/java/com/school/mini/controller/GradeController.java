package com.school.mini.controller;

import com.school.mini.dto.AcademicProgressDto;
import com.school.mini.dto.GenericMessageResponse;
import com.school.mini.dto.GradeDto;
import com.school.mini.service.GradeService;
import com.school.mini.validation.GradeDtoValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
@Slf4j
public class GradeController {

    @Autowired
    private GradeService gradeService;

    @PostMapping
    public ResponseEntity<GenericMessageResponse> recordGrade(@Valid @RequestBody GradeDto dto) {
        GradeDtoValidator.validate(dto);
        gradeService.recordGrade(dto);
        return ResponseEntity.ok(new GenericMessageResponse("Grade recorded successfully"));
    }

    @GetMapping("/progress/{studentId}")
    public ResponseEntity<AcademicProgressDto> getProgress(@PathVariable Long studentId) {
        AcademicProgressDto progress = gradeService.getAcademicProgress(studentId);
        return ResponseEntity.ok(progress);
    }
}


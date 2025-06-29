package com.school.mini.controller;

import com.school.mini.dto.EnrollmentRequest;
import com.school.mini.dto.GenericMessageResponse;
import com.school.mini.service.EnrollmentService;
import com.school.mini.validation.EnrollmentRequestValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@Slf4j
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<GenericMessageResponse> enroll(@Valid @RequestBody EnrollmentRequest request) {
        EnrollmentRequestValidator.validate(request);
        enrollmentService.enrollStudent(request);
        return ResponseEntity.ok(new GenericMessageResponse("Student successfully enrolled in selected courses."));
    }
}

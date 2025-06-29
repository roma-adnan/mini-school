package com.school.mini.controller;

import com.school.mini.dto.AssignPrerequisitesRequest;
import com.school.mini.dto.CourseDto;
import com.school.mini.dto.GenericMessageResponse;
import com.school.mini.service.CourseService;
import com.school.mini.validation.CourseRequestValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseDto> create(@Valid @RequestBody CourseDto courseDto) {
        CourseRequestValidator.validateForCreate(courseDto);
        return ResponseEntity.ok(courseService.create(courseDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.get(id));
    }

    @GetMapping
    public ResponseEntity<List<CourseDto>> getAll(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(courseService.getAll(PageRequest.of(page, size)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseDto> update(@PathVariable Long id, @Valid @RequestBody CourseDto dto) {
        CourseRequestValidator.validateForUpdate(id, dto);
        return ResponseEntity.ok(courseService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/assign-prerequisites")
    public ResponseEntity<GenericMessageResponse> assignPrerequisites(@RequestBody AssignPrerequisitesRequest request) {
        courseService.assignPrerequisites(request);
        return ResponseEntity.ok(new GenericMessageResponse("Prerequisites assigned successfully."));
    }
}

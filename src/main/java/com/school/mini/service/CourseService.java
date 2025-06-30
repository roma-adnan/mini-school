package com.school.mini.service;


import com.school.mini.dto.AssignPrerequisitesRequest;
import com.school.mini.dto.CourseDto;
import com.school.mini.entity.Course;
import com.school.mini.exception.BusinessException;
import com.school.mini.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseDto create(CourseDto dto) {
        if (dto == null) {
            throw new BusinessException("Course data must not be null");
        }
        Course course = Course.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .capacity(dto.getCapacity())
                .code(dto.getCode())
                .build();

        if (dto.getPrerequisites() != null && !dto.getPrerequisites().isEmpty()) {
            List<Course> prerequisites = courseRepository.findAllById(dto.getPrerequisites());

            if (prerequisites.size() != dto.getPrerequisites().size()) {
                throw new BusinessException("Some prerequisite course IDs are invalid");
            }

            if (prerequisites.contains(course)) {
                throw new BusinessException("A course cannot be its own prerequisite");
            }

            course.setPrerequisites(new HashSet<>(prerequisites));
        } else {
            course.setPrerequisites(new HashSet<>());
        }

        Course saved = courseRepository.save(course);
        return toDto(saved);
    }

    public CourseDto get(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Course not found with id: " + id));
        return toDto(course);
    }

    public List<CourseDto> getAll(Pageable pageable) {
        return courseRepository.findAll(pageable)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public CourseDto update(Long id, CourseDto dto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Course not found with id: " + id));

        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setCapacity(dto.getCapacity());
        course.setCode(dto.getCode());

        if (dto.getPrerequisites() != null) {
            List<Course> prerequisites = courseRepository.findAllById(dto.getPrerequisites());
            if (prerequisites.contains(course)) {
                throw new BusinessException("A course cannot be its own prerequisite");
            }
            course.setPrerequisites(new HashSet<>(prerequisites));
        }
        else {
            course.setPrerequisites(new HashSet<>());
        }

        return toDto(courseRepository.save(course));
    }

    public void delete(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new BusinessException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

    private CourseDto toDto(Course course) {
        return CourseDto.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .capacity(course.getCapacity())
                .code(course.getCode())
                .prerequisites(
                        course.getPrerequisites()
                                .stream()
                                .map(Course::getId)
                                .toList()
                )
                .build();
    }

    @Transactional
    public void assignPrerequisites(AssignPrerequisitesRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new BusinessException("Course not found"));

        List<Course> prerequisites = courseRepository.findAllById(request.getPrerequisiteIds());

        if (prerequisites.contains(course)) {
            throw new BusinessException("Course cannot be its own prerequisite");
        }

        course.setPrerequisites(new HashSet<>(prerequisites));
        courseRepository.save(course);
    }


}

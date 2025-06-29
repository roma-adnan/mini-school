package com.school.mini.service;

import com.school.mini.dto.StudentDto;
import com.school.mini.entity.Student;
import com.school.mini.exception.BusinessException;
import com.school.mini.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentDto createStudent(StudentDto dto) {
        Student student = Student.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
        Student saved = studentRepository.save(student);
        return toDto(saved);
    }

    public StudentDto getStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Student not found with id: " + id));
        return toDto(student);
    }

    public List<StudentDto> getAllStudents(Pageable pageable) {
        Page<Student> page = studentRepository.findAll(pageable);
        return page.stream().map(this::toDto).collect(Collectors.toList());
    }

    public StudentDto updateStudent(Long id, StudentDto dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Student not found with id: " + id));
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        return toDto(studentRepository.save(student));
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new BusinessException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    private StudentDto toDto(Student student) {
        return StudentDto.builder()
                .id(student.getId())
                .name(student.getName())
                .email(student.getEmail())
                .build();
    }


}

package com.example.studentservice.controller;

import com.example.studentservice.dto.StudentRequest;
import com.example.studentservice.dto.StudentResponse;
import com.example.studentservice.dto.StudentStatusRequest;
import com.example.studentservice.model.Student;
import com.example.studentservice.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Students", description = "Registration, lookup, search, and status transitions")
public class StudentController {

    private static final Logger log = LoggerFactory.getLogger(StudentController.class);

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public Page<StudentResponse> getAllStudents(Pageable pageable) {
        log.info("GET /api/students page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        return studentService.getAllStudents(pageable).map(StudentResponse::from);
    }

    @GetMapping("/search")
    public Page<StudentResponse> searchStudents(@RequestParam String name, Pageable pageable) {
        log.info("GET /api/students/search name={}", name);
        return studentService.searchStudentsByName(name, pageable).map(StudentResponse::from);
    }

    @GetMapping("/{id}")
    public StudentResponse getStudentById(@PathVariable Long id) {
        log.info("GET /api/students/{}", id);
        return StudentResponse.from(studentService.getStudentById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponse createStudent(@Valid @RequestBody StudentRequest request) {
        log.info("POST /api/students email={}", request.getEmail());
        Student student = new Student(null, request.getName(), request.getEmail());
        StudentResponse response = StudentResponse.from(studentService.createStudent(student));
        log.info("Created student id={}", response.getId());
        return response;
    }

    @PutMapping("/{id}")
    public StudentResponse updateStudent(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        log.info("PUT /api/students/{}", id);
        Student student = new Student(null, request.getName(), request.getEmail());
        return StudentResponse.from(studentService.updateStudent(id, student));
    }

    @Operation(summary = "Change a student's status",
            description = "Valid transitions: ACTIVE <-> INACTIVE, either <-> GRADUATED. "
                    + "GRADUATED is terminal - once set, no further transitions are allowed "
                    + "and this endpoint returns 409 Conflict.")
    @PatchMapping("/{id}/status")
    public StudentResponse changeStatus(@PathVariable Long id, @Valid @RequestBody StudentStatusRequest request) {
        log.info("PATCH /api/students/{}/status -> {}", id, request.getStatus());
        return StudentResponse.from(studentService.changeStatus(id, request.getStatus()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable Long id) {
        log.info("DELETE /api/students/{}", id);
        studentService.deleteStudent(id);
    }
}

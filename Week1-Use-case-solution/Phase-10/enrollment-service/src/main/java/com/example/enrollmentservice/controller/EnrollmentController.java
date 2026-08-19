package com.example.enrollmentservice.controller;

import com.example.enrollmentservice.dto.EnrollmentResponse;
import com.example.enrollmentservice.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@Tag(name = "Enrollments", description = "Enrolling students into courses; validates against " +
        "student-service and course-service live over HTTP, and enforces course capacity")
public class EnrollmentController {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentController.class);

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @Operation(summary = "Enroll a student in a course",
            description = "Calls student-service and course-service to confirm both exist, "
                    + "rejects a duplicate enrollment (409) and rejects enrolling into a course "
                    + "that is already at capacity (409).")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EnrollmentResponse enroll(@RequestParam Long studentId, @RequestParam Long courseId) {
        log.info("POST /api/enrollments studentId={} courseId={}", studentId, courseId);
        return enrollmentService.enroll(studentId, courseId);
    }

    @GetMapping("/student/{studentId}")
    public List<EnrollmentResponse> getEnrollmentsForStudent(@PathVariable Long studentId) {
        log.info("GET /api/enrollments/student/{}", studentId);
        return enrollmentService.getEnrollmentsForStudent(studentId);
    }

    @GetMapping("/course/{courseId}")
    public List<EnrollmentResponse> getEnrollmentsForCourse(@PathVariable Long courseId) {
        log.info("GET /api/enrollments/course/{}", courseId);
        return enrollmentService.getEnrollmentsForCourse(courseId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEnrollment(@PathVariable Long id) {
        log.info("DELETE /api/enrollments/{}", id);
        enrollmentService.deleteEnrollment(id);
    }
}

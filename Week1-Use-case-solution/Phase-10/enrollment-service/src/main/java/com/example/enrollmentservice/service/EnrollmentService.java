package com.example.enrollmentservice.service;

import com.example.enrollmentservice.client.CourseClient;
import com.example.enrollmentservice.client.StudentClient;
import com.example.enrollmentservice.dto.CourseDto;
import com.example.enrollmentservice.dto.EnrollmentResponse;
import com.example.enrollmentservice.dto.StudentDto;
import com.example.enrollmentservice.exception.AlreadyEnrolledException;
import com.example.enrollmentservice.exception.CourseCapacityExceededException;
import com.example.enrollmentservice.exception.CourseNotFoundException;
import com.example.enrollmentservice.exception.EnrollmentNotFoundException;
import com.example.enrollmentservice.exception.StudentNotFoundException;
import com.example.enrollmentservice.model.Enrollment;
import com.example.enrollmentservice.repository.EnrollmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnrollmentService {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentService.class);

    private final EnrollmentRepository enrollmentRepository;
    private final StudentClient studentClient;
    private final CourseClient courseClient;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                              StudentClient studentClient,
                              CourseClient courseClient) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentClient = studentClient;
        this.courseClient = courseClient;
    }

    public EnrollmentResponse enroll(Long studentId, Long courseId) {
        StudentDto student = studentClient.findById(studentId)
                .orElseThrow(() -> {
                    log.warn("Enroll rejected: student {} not found", studentId);
                    return new StudentNotFoundException(studentId);
                });
        CourseDto course = courseClient.findById(courseId)
                .orElseThrow(() -> {
                    log.warn("Enroll rejected: course {} not found", courseId);
                    return new CourseNotFoundException(courseId);
                });

        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            log.warn("Enroll rejected: student {} already enrolled in course {}", studentId, courseId);
            throw new AlreadyEnrolledException(studentId, courseId);
        }

        long currentEnrollments = enrollmentRepository.countByCourseId(courseId);
        if (currentEnrollments >= course.getCapacity()) {
            log.warn("Enroll rejected: course {} is at capacity ({}/{})", courseId, currentEnrollments, course.getCapacity());
            throw new CourseCapacityExceededException(courseId, course.getCapacity());
        }

        Enrollment saved = enrollmentRepository.save(new Enrollment(null, studentId, courseId));
        log.debug("Persisted enrollment id={}", saved.getId());
        return EnrollmentResponse.from(saved, student, course);
    }

    public List<EnrollmentResponse> getEnrollmentsForStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
                .map(this::enrich)
                .toList();
    }

    public List<EnrollmentResponse> getEnrollmentsForCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId).stream()
                .map(this::enrich)
                .toList();
    }

    public void deleteEnrollment(Long id) {
        if (!enrollmentRepository.existsById(id)) {
            log.warn("Rejected delete: enrollment {} not found", id);
            throw new EnrollmentNotFoundException(id);
        }
        enrollmentRepository.deleteById(id);
        log.debug("Deleted enrollment id={}", id);
    }

    private EnrollmentResponse enrich(Enrollment enrollment) {
        StudentDto student = studentClient.findById(enrollment.getStudentId())
                .orElseThrow(() -> new StudentNotFoundException(enrollment.getStudentId()));
        CourseDto course = courseClient.findById(enrollment.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException(enrollment.getCourseId()));
        return EnrollmentResponse.from(enrollment, student, course);
    }
}

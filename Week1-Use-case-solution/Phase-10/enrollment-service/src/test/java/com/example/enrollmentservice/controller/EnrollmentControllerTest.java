package com.example.enrollmentservice.controller;

import com.example.enrollmentservice.dto.CourseDto;
import com.example.enrollmentservice.dto.EnrollmentResponse;
import com.example.enrollmentservice.dto.StudentDto;
import com.example.enrollmentservice.exception.AlreadyEnrolledException;
import com.example.enrollmentservice.exception.CourseCapacityExceededException;
import com.example.enrollmentservice.exception.EnrollmentNotFoundException;
import com.example.enrollmentservice.exception.StudentNotFoundException;
import com.example.enrollmentservice.model.Enrollment;
import com.example.enrollmentservice.service.EnrollmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnrollmentController.class)
class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnrollmentService enrollmentService;

    private EnrollmentResponse sampleResponse() {
        StudentDto student = new StudentDto();
        student.setId(1L);
        student.setName("Rahul");
        CourseDto course = new CourseDto();
        course.setId(1L);
        course.setTitle("Java");
        course.setCapacity(30);
        return EnrollmentResponse.from(new Enrollment(10L, 1L, 1L), student, course);
    }

    @Test
    void enrollReturns201() throws Exception {
        given(enrollmentService.enroll(1L, 1L)).willReturn(sampleResponse());

        mockMvc.perform(post("/api/enrollments?studentId=1&courseId=1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentName").value("Rahul"))
                .andExpect(jsonPath("$.courseTitle").value("Java"));
    }

    @Test
    void enrollReturns404WhenStudentMissing() throws Exception {
        given(enrollmentService.enroll(99L, 1L)).willThrow(new StudentNotFoundException(99L));

        mockMvc.perform(post("/api/enrollments?studentId=99&courseId=1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void enrollReturns409WhenAlreadyEnrolled() throws Exception {
        given(enrollmentService.enroll(1L, 1L)).willThrow(new AlreadyEnrolledException(1L, 1L));

        mockMvc.perform(post("/api/enrollments?studentId=1&courseId=1"))
                .andExpect(status().isConflict());
    }

    @Test
    void enrollReturns409WhenCourseAtCapacity() throws Exception {
        given(enrollmentService.enroll(1L, 1L)).willThrow(new CourseCapacityExceededException(1L, 30));

        mockMvc.perform(post("/api/enrollments?studentId=1&courseId=1"))
                .andExpect(status().isConflict());
    }

    @Test
    void getEnrollmentsForStudentReturnsList() throws Exception {
        given(enrollmentService.getEnrollmentsForStudent(1L)).willReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/enrollments/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentId").value(1));
    }

    @Test
    void getEnrollmentsForCourseReturnsList() throws Exception {
        given(enrollmentService.getEnrollmentsForCourse(1L)).willReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/enrollments/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseId").value(1));
    }

    @Test
    void deleteEnrollmentReturns204WhenFound() throws Exception {
        mockMvc.perform(delete("/api/enrollments/10"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteEnrollmentReturns404WhenMissing() throws Exception {
        org.mockito.Mockito.doThrow(new EnrollmentNotFoundException(99L)).when(enrollmentService).deleteEnrollment(99L);

        mockMvc.perform(delete("/api/enrollments/99"))
                .andExpect(status().isNotFound());
    }
}

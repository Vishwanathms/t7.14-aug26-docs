package com.example.studentservice.controller;

import com.example.studentservice.exception.EmailAlreadyExistsException;
import com.example.studentservice.exception.InvalidStatusTransitionException;
import com.example.studentservice.exception.StudentNotFoundException;
import com.example.studentservice.model.Student;
import com.example.studentservice.model.StudentStatus;
import com.example.studentservice.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    @Test
    void getAllStudentsReturnsPagedList() throws Exception {
        Student rahul = new Student(1L, "Rahul", "rahul@example.com");
        given(studentService.getAllStudents(any())).willReturn(new PageImpl<>(List.of(rahul), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Rahul"))
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"));
    }

    @Test
    void getStudentByIdReturns404WhenMissing() throws Exception {
        given(studentService.getStudentById(99L)).willThrow(new StudentNotFoundException(99L));

        mockMvc.perform(get("/api/students/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createStudentReturns201ForValidRequest() throws Exception {
        Student created = new Student(1L, "Rahul", "rahul@example.com");
        given(studentService.createStudent(any())).willReturn(created);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Rahul\",\"email\":\"rahul@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createStudentReturns409ForDuplicateEmail() throws Exception {
        given(studentService.createStudent(any())).willThrow(new EmailAlreadyExistsException("rahul@example.com"));

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Rahul\",\"email\":\"rahul@example.com\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void changeStatusReturns200ForValidTransition() throws Exception {
        Student inactive = new Student(1L, "Rahul", "rahul@example.com", StudentStatus.INACTIVE);
        given(studentService.changeStatus(1L, StudentStatus.INACTIVE)).willReturn(inactive);

        mockMvc.perform(patch("/api/students/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"INACTIVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void changeStatusReturns409ForInvalidTransition() throws Exception {
        given(studentService.changeStatus(1L, StudentStatus.ACTIVE))
                .willThrow(new InvalidStatusTransitionException(StudentStatus.GRADUATED, StudentStatus.ACTIVE));

        mockMvc.perform(patch("/api/students/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ACTIVE\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteStudentReturns204() throws Exception {
        mockMvc.perform(delete("/api/students/1"))
                .andExpect(status().isNoContent());
    }
}

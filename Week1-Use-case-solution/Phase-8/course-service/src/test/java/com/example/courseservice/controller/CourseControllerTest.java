package com.example.courseservice.controller;

import com.example.courseservice.exception.CourseNotFoundException;
import com.example.courseservice.model.Course;
import com.example.courseservice.service.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @Test
    void getAllCoursesReturnsList() throws Exception {
        given(courseService.getAllCourses()).willReturn(List.of(new Course(1L, "Java", 30)));

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Java"))
                .andExpect(jsonPath("$[0].capacity").value(30));
    }

    @Test
    void getCourseByIdReturns404WhenMissing() throws Exception {
        given(courseService.getCourseById(99L)).willThrow(new CourseNotFoundException(99L));

        mockMvc.perform(get("/api/courses/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createCourseReturns201ForValidRequest() throws Exception {
        given(courseService.createCourse(any())).willReturn(new Course(1L, "Java", 30));

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Java\",\"capacity\":30}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createCourseReturns400ForMissingCapacity() throws Exception {
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Java\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[0]").value("capacity: capacity is required"));
    }

    @Test
    void createCourseReturns400ForZeroCapacity() throws Exception {
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Java\",\"capacity\":0}"))
                .andExpect(status().isBadRequest());
    }
}

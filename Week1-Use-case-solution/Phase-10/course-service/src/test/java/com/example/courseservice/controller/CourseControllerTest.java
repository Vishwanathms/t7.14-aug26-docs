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

    @Test
    void updateCourseReturns200ForValidRequest() throws Exception {
        given(courseService.updateCourse(any(), any())).willReturn(new Course(1L, "Advanced Java", 40));

        mockMvc.perform(put("/api/courses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Advanced Java\",\"capacity\":40}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Advanced Java"))
                .andExpect(jsonPath("$.capacity").value(40));
    }

    @Test
    void updateCourseReturns404WhenMissing() throws Exception {
        given(courseService.updateCourse(any(), any())).willThrow(new CourseNotFoundException(99L));

        mockMvc.perform(put("/api/courses/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Java\",\"capacity\":30}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCourseReturns204WhenFound() throws Exception {
        mockMvc.perform(delete("/api/courses/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCourseReturns404WhenMissing() throws Exception {
        org.mockito.Mockito.doThrow(new CourseNotFoundException(99L)).when(courseService).deleteCourse(99L);

        mockMvc.perform(delete("/api/courses/99"))
                .andExpect(status().isNotFound());
    }
}

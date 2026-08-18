package com.example.courseservice.service;

import com.example.courseservice.exception.CourseNotFoundException;
import com.example.courseservice.model.Course;
import com.example.courseservice.repository.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseService.class);

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Course {} not found", id);
                    return new CourseNotFoundException(id);
                });
    }

    public Course createCourse(Course course) {
        course.setId(null);
        Course saved = courseRepository.save(course);
        log.debug("Persisted course id={}", saved.getId());
        return saved;
    }
}

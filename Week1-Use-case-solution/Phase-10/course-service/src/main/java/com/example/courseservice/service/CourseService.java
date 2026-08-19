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

    public Course updateCourse(Long id, Course course) {
        Course existing = getCourseById(id);
        existing.setTitle(course.getTitle());
        existing.setCapacity(course.getCapacity());
        return courseRepository.save(existing);
    }

    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            log.warn("Rejected delete: course {} not found", id);
            throw new CourseNotFoundException(id);
        }
        courseRepository.deleteById(id);
        log.debug("Deleted course id={}", id);
    }
}

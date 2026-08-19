package com.example.courseservice.service;

import com.example.courseservice.exception.CourseNotFoundException;
import com.example.courseservice.model.Course;
import com.example.courseservice.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    @Test
    void getAllCoursesReturnsEverything() {
        given(courseRepository.findAll()).willReturn(List.of(new Course(1L, "Java", 30)));

        List<Course> result = courseService.getAllCourses();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Java");
    }

    @Test
    void getCourseByIdReturnsCourseWhenFound() {
        given(courseRepository.findById(1L)).willReturn(Optional.of(new Course(1L, "Java", 30)));

        Course result = courseService.getCourseById(1L);

        assertThat(result.getCapacity()).isEqualTo(30);
    }

    @Test
    void getCourseByIdThrowsWhenMissing() {
        given(courseRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.getCourseById(99L))
                .isInstanceOf(CourseNotFoundException.class);
    }

    @Test
    void createCourseAssignsNoIdBeforeSaving() {
        given(courseRepository.save(any(Course.class))).willAnswer(inv -> {
            Course c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        Course result = courseService.createCourse(new Course(999L, "Spring Boot", 25));

        assertThat(result.getId()).isEqualTo(1L);
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void updateCourseAppliesNewTitleAndCapacity() {
        given(courseRepository.findById(1L)).willReturn(Optional.of(new Course(1L, "Java", 30)));
        given(courseRepository.save(any(Course.class))).willAnswer(inv -> inv.getArgument(0));

        Course result = courseService.updateCourse(1L, new Course(null, "Advanced Java", 40));

        assertThat(result.getTitle()).isEqualTo("Advanced Java");
        assertThat(result.getCapacity()).isEqualTo(40);
    }

    @Test
    void updateCourseThrowsWhenMissing() {
        given(courseRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.updateCourse(99L, new Course(null, "X", 1)))
                .isInstanceOf(CourseNotFoundException.class);
    }

    @Test
    void deleteCourseRemovesExistingCourse() {
        given(courseRepository.existsById(1L)).willReturn(true);

        courseService.deleteCourse(1L);

        verify(courseRepository).deleteById(1L);
    }

    @Test
    void deleteCourseThrowsWhenMissing() {
        given(courseRepository.existsById(99L)).willReturn(false);

        assertThatThrownBy(() -> courseService.deleteCourse(99L))
                .isInstanceOf(CourseNotFoundException.class);
    }
}

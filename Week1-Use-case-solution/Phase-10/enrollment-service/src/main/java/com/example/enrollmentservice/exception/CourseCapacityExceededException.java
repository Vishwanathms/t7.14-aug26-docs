package com.example.enrollmentservice.exception;

public class CourseCapacityExceededException extends RuntimeException {

    public CourseCapacityExceededException(Long courseId, int capacity) {
        super("Course " + courseId + " is at capacity (" + capacity + ")");
    }
}

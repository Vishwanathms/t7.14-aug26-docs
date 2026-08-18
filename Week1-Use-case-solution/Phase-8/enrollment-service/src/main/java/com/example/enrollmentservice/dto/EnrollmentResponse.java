package com.example.enrollmentservice.dto;

import com.example.enrollmentservice.model.Enrollment;

public class EnrollmentResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseTitle;

    public EnrollmentResponse() {
    }

    public EnrollmentResponse(Long id, Long studentId, String studentName, Long courseId, String courseTitle) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
    }

    public static EnrollmentResponse from(Enrollment enrollment, StudentDto student, CourseDto course) {
        return new EnrollmentResponse(
                enrollment.getId(),
                student.getId(),
                student.getName(),
                course.getId(),
                course.getTitle());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }
}

package com.example.studentservice.dto;

import com.example.studentservice.model.StudentStatus;
import jakarta.validation.constraints.NotNull;

public class StudentStatusRequest {

    @NotNull(message = "status is required")
    private StudentStatus status;

    public StudentStatusRequest() {
    }

    public StudentStatusRequest(StudentStatus status) {
        this.status = status;
    }

    public StudentStatus getStatus() {
        return status;
    }

    public void setStatus(StudentStatus status) {
        this.status = status;
    }
}

package com.example.studentservice.exception;

import com.example.studentservice.model.StudentStatus;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(StudentStatus from, StudentStatus to) {
        super("Cannot change student status from " + from + " to " + to);
    }
}

package com.college;

import org.springframework.stereotype.Component;

@Component
public class Department {

    private int departmentId = 101;
    private String departmentName = "Computer Science";

    private final Professor professor;

    public Department(Professor professor) {
        this.professor = professor;
    }

    public void displayDepartment() {

        System.out.println("Department : " + departmentName);

        professor.displayProfessor();
    }
}
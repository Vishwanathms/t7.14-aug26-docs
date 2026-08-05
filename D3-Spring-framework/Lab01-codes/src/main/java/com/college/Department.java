package com.college;

public class Department {

    private int departmentId;
    private String departmentName;

    private Professor professor;

    public Department(int departmentId,
                      String departmentName,
                      Professor professor) {

        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.professor = professor;
    }

    public void displayDepartment() {

        System.out.println("Department : " + departmentName);

        professor.displayProfessor();
    }
}
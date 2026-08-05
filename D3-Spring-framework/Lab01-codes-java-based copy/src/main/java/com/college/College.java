package com.college;

import org.springframework.stereotype.Service;

@Service
public class College {

    private String collegeName = "ABC Engineering College";
    private String location = "Bangalore";

    private final Department department;

    public College(Department department) {
        this.department = department;
    }

    public void displayCollegeInformation() {

        System.out.println("College : " + collegeName);
        System.out.println("Location : " + location);

        department.displayDepartment();
    }
}
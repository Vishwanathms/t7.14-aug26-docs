package com.college;

public class College {

    private String collegeName;
    private String location;

    private Department department;

    public College(String collegeName,
                   String location,
                   Department department) {

        this.collegeName = collegeName;
        this.location = location;
        this.department = department;
    }

    public void displayCollegeInformation() {

        System.out.println("-----------------------------------");

        System.out.println("College : " + collegeName);
        System.out.println("Location : " + location);

        department.displayDepartment();

        System.out.println("-----------------------------------");
    }
}
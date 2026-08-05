package com.college;

public class Main {

    public static void main(String[] args) {

        Course course =
                new Course(101,
                        "Spring Framework",
                        "30 Hours");

        Professor professor =
                new Professor(
                        1,
                        "Dr. Ravi Kumar",
                        "Spring",
                        course);

        Department department =
                new Department(
                        10,
                        "Computer Science",
                        professor);

        College college =
                new College(
                        "ABC Engineering College",
                        "Bangalore",
                        department);

        college.displayCollegeInformation();
    }
}
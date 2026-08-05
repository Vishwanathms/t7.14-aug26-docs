package com.college;

import java.util.List;
import java.util.Scanner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.college.config.AppConfig;
import com.college.model.Student;
import com.college.service.StudentService;

public class Main {

    public static void main(String[] args) {

        ApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        StudentService studentService =
                context.getBean(StudentService.class);

        Scanner scanner = new Scanner(System.in);

        int choice;

        do {

            System.out.println("\n=====================================");
            System.out.println(" STUDENT MANAGEMENT SYSTEM");
            System.out.println("=====================================");
            System.out.println("1. Add Student");
            System.out.println("2. View Students");
            System.out.println("3. Search Student");
            System.out.println("4. Update Student");
            System.out.println("5. Delete Student");
            System.out.println("6. Display Highest Marks");
            System.out.println("7. Calculate Average");
            System.out.println("8. Exit");
            System.out.print("Enter Choice : ");

            choice = scanner.nextInt();

            switch (choice) {

                case 1:

                    System.out.print("Enter Student ID : ");
                    int id = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter Name : ");
                    String name = scanner.nextLine();

                    System.out.print("Enter Age : ");
                    int age = scanner.nextInt();

                    System.out.print("Enter Marks : ");
                    double marks = scanner.nextDouble();

                    Student student =
                            new Student(id, name, age, marks);

                    studentService.addStudent(student);

                    break;

                case 2:

                    List<Student> students =
                            studentService.viewStudents();

                    if (students.isEmpty()) {

                        System.out.println("No Students Found.");

                    } else {

                        for (Student s : students) {

                            s.displayStudent();

                        }

                    }

                    break;

                case 3:

                    System.out.print("Enter Student ID : ");
                    id = scanner.nextInt();

                    Student searchedStudent =
                            studentService.searchStudent(id);

                    if (searchedStudent != null) {

                        searchedStudent.displayStudent();

                    } else {

                        System.out.println("Student Not Found.");

                    }

                    break;

                case 4:

                    System.out.print("Enter Student ID : ");
                    id = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter New Name : ");
                    name = scanner.nextLine();

                    System.out.print("Enter New Age : ");
                    age = scanner.nextInt();

                    System.out.print("Enter New Marks : ");
                    marks = scanner.nextDouble();

                    Student updatedStudent =
                            new Student(id, name, age, marks);

                    studentService.updateStudent(updatedStudent);

                    break;

                case 5:

                    System.out.print("Enter Student ID : ");
                    id = scanner.nextInt();

                    studentService.deleteStudent(id);

                    break;

                case 6:

                    Student topper =
                            studentService.displayHighestMarks();

                    if (topper != null) {

                        System.out.println("\nTopper Details");

                        topper.displayStudent();

                    } else {

                        System.out.println("No Students Found.");

                    }

                    break;

                case 7:

                    double average =
                            studentService.calculateAverage();

                    System.out.println("Class Average : " + average);

                    break;

                case 8:

                    System.out.println("Thank You.");

                    break;

                default:

                    System.out.println("Invalid Choice.");

            }

        } while (choice != 8);

        scanner.close();
        ((AnnotationConfigApplicationContext) context).close();

    }

}
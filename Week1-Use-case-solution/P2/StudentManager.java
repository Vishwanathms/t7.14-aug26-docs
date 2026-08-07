import java.util.Scanner;

public class StudentManager {

    private Student[] students = new Student[100];
    private int count = 0;

    private Scanner scanner = new Scanner(System.in);

    // Add Student
    public void addStudent() {

        if (count == students.length) {
            System.out.println("Student storage is full.");
            return;
        }

        System.out.print("Enter Student ID : ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter Name : ");
        String name = scanner.nextLine();

        System.out.print("Enter Age : ");
        int age = scanner.nextInt();

        System.out.print("Enter Marks : ");
        double marks = scanner.nextDouble();

        students[count] = new Student(id, name, age, marks);
        count++;

        System.out.println("Student Added Successfully.");
    }

    // View Students
    public void viewStudents() {

        if (count == 0) {
            System.out.println("No Students Found.");
            return;
        }

        for (int i = 0; i < count; i++) {
            students[i].displayStudent();
        }
    }

    // Search Student
    public void searchStudent() {

        System.out.print("Enter Student ID : ");
        int id = scanner.nextInt();

        for (int i = 0; i < count; i++) {

            if (students[i].getId() == id) {

                students[i].displayStudent();
                return;

            }

        }

        System.out.println("Student Not Found.");
    }

    // Update Student
    public void updateStudent() {

        System.out.print("Enter Student ID : ");
        int id = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < count; i++) {

            if (students[i].getId() == id) {

                System.out.print("Enter New Name : ");
                students[i].setName(scanner.nextLine());

                System.out.print("Enter New Age : ");
                students[i].setAge(scanner.nextInt());

                System.out.print("Enter New Marks : ");
                students[i].setMarks(scanner.nextDouble());

                System.out.println("Student Updated Successfully.");

                return;
            }

        }

        System.out.println("Student Not Found.");
    }

    // Delete Student
    public void deleteStudent() {

        System.out.print("Enter Student ID : ");
        int id = scanner.nextInt();

        for (int i = 0; i < count; i++) {

            if (students[i].getId() == id) {

                for (int j = i; j < count - 1; j++) {
                    students[j] = students[j + 1];
                }

                students[count - 1] = null;
                count--;

                System.out.println("Student Deleted Successfully.");
                return;
            }

        }

        System.out.println("Student Not Found.");
    }

    // Highest Marks
    public void displayHighestMarks() {

        if (count == 0) {

            System.out.println("No Students Available.");
            return;

        }

        Student topper = students[0];

        for (int i = 1; i < count; i++) {

            if (students[i].getMarks() > topper.getMarks()) {

                topper = students[i];

            }

        }

        System.out.println("\nTopper Details");
        topper.displayStudent();

    }

    // Average Marks
    public void calculateAverage() {

        if (count == 0) {

            System.out.println("No Students Available.");
            return;

        }

        double total = 0;

        for (int i = 0; i < count; i++) {

            total += students[i].getMarks();

        }

        double average = total / count;

        System.out.println("Average Marks : " + average);

    }
}
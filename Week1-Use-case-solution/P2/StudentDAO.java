import java.sql.*;
import java.util.Scanner;

public class StudentDAO {

    private Scanner scanner = new Scanner(System.in);

    // Add Student
    public void addStudent() {

        try {

            System.out.print("Enter Student ID : ");
            int id = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter Name : ");
            String name = scanner.nextLine();

            System.out.print("Enter Age : ");
            int age = scanner.nextInt();

            System.out.print("Enter Marks : ");
            double marks = scanner.nextDouble();

            Connection con = DBConnection.getConnection();

            String sql = "INSERT INTO students VALUES (?,?,?,?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setInt(3, age);
            ps.setDouble(4, marks);

            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println("Student Added Successfully.");

            con.close();

        } catch (Exception e) {

            System.out.println(e.getMessage());

        }

    }

    // View Students
    public void viewStudents() {

        try {

            Connection con = DBConnection.getConnection();

            String sql = "SELECT * FROM students";

            PreparedStatement ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Student student = new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getDouble("marks")
                );

                student.displayStudent();

            }

            con.close();

        } catch (Exception e) {

            System.out.println(e.getMessage());

        }

    }

    // Search Student
    public void searchStudent() {

        try {

            System.out.print("Enter Student ID : ");
            int id = scanner.nextInt();

            Connection con = DBConnection.getConnection();

            String sql = "SELECT * FROM students WHERE id=?";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Student student = new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getDouble("marks")
                );

                student.displayStudent();

            } else {

                System.out.println("Student Not Found.");

            }

            con.close();

        } catch (Exception e) {

            System.out.println(e.getMessage());

        }

    }

    // Update Student
    public void updateStudent() {

        try {

            System.out.print("Enter Student ID : ");
            int id = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter New Name : ");
            String name = scanner.nextLine();

            System.out.print("Enter New Age : ");
            int age = scanner.nextInt();

            System.out.print("Enter New Marks : ");
            double marks = scanner.nextDouble();

            Connection con = DBConnection.getConnection();

            String sql =
                    "UPDATE students SET name=?, age=?, marks=? WHERE id=?";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setDouble(3, marks);
            ps.setInt(4, id);

            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println("Student Updated Successfully.");
            else
                System.out.println("Student Not Found.");

            con.close();

        } catch (Exception e) {

            System.out.println(e.getMessage());

        }

    }

    // Delete Student
    public void deleteStudent() {

        try {

            System.out.print("Enter Student ID : ");
            int id = scanner.nextInt();

            Connection con = DBConnection.getConnection();

            String sql = "DELETE FROM students WHERE id=?";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println("Student Deleted Successfully.");
            else
                System.out.println("Student Not Found.");

            con.close();

        } catch (Exception e) {

            System.out.println(e.getMessage());

        }

    }

    // Highest Marks
    public void displayHighestMarks() {

        try {

            Connection con = DBConnection.getConnection();

            String sql =
                    "SELECT * FROM students ORDER BY marks DESC LIMIT 1";

            PreparedStatement ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Student student = new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getDouble("marks")
                );

                System.out.println("\nTopper Details");

                student.displayStudent();

            } else {

                System.out.println("No Students Found.");

            }

            con.close();

        } catch (Exception e) {

            System.out.println(e.getMessage());

        }

    }

    // Average
    public void calculateAverage() {

        try {

            Connection con = DBConnection.getConnection();

            String sql = "SELECT AVG(marks) AS average FROM students";

            PreparedStatement ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                System.out.println(
                        "Class Average : " +
                                rs.getDouble("average"));

            }

            con.close();

        } catch (Exception e) {

            System.out.println(e.getMessage());

        }

    }

}
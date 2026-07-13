package database_connection;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        StudentDAO dao = new StudentDAO();

        try {
            DatabaseConnection.initializeDatabase();
        } catch (SQLException exception) {
            System.out.println("Database setup failed: " + exception.getMessage());
            return;
        }

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n===== Student Management System =====");
                System.out.println("1. Add Student");
                System.out.println("2. Delete Student");
                System.out.println("3. Show All Students");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");

                int choice = readInt(scanner);

                switch (choice) {
                    case 1 -> addStudent(scanner, dao);
                    case 2 -> deleteStudent(scanner, dao);
                    case 3 -> showAllStudents(dao);
                    case 4 -> {
                        System.out.println("Thank you!");
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            }
        }
    }

    private static void addStudent(Scanner scanner, StudentDAO dao) {
        System.out.print("Enter Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter Department: ");
        String department = scanner.nextLine().trim();

        System.out.print("Enter CGPA: ");
        double cgpa = readDouble(scanner);

        if (name.isBlank() || department.isBlank()) {
            System.out.println("Name and department are required.");
            return;
        }

        try {
            dao.addStudent(new Student(0, name, department, cgpa));
            System.out.println("Student added successfully.");
        } catch (SQLException exception) {
            System.out.println("Could not add student: " + exception.getMessage());
        }
    }

    private static void deleteStudent(Scanner scanner, StudentDAO dao) {
        System.out.print("Enter Student ID to delete: ");
        int id = readInt(scanner);

        try {
            boolean deleted = dao.deleteStudent(id);
            System.out.println(deleted ? "Student deleted successfully." : "No student found with that ID.");
        } catch (SQLException exception) {
            System.out.println("Could not delete student: " + exception.getMessage());
        }
    }

    private static void showAllStudents(StudentDAO dao) {
        try {
            List<Student> students = dao.getAllStudents();

            if (students.isEmpty()) {
                System.out.println("No students found.");
                return;
            }

            System.out.println("\n-----------------------------------------------");
            System.out.printf("%-5s %-20s %-15s %-5s%n", "ID", "Name", "Department", "CGPA");
            System.out.println("-----------------------------------------------");

            for (Student student : students) {
                System.out.printf("%-5d %-20s %-15s %-5.2f%n",
                        student.getId(),
                        student.getName(),
                        student.getDepartment(),
                        student.getCgpa());
            }

            System.out.println("-----------------------------------------------");
        } catch (SQLException exception) {
            System.out.println("Could not load students: " + exception.getMessage());
        }
    }

    private static int readInt(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.print("Enter a number: ");
            scanner.nextLine();
        }

        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }

    private static double readDouble(Scanner scanner) {
        while (!scanner.hasNextDouble()) {
            System.out.print("Enter a valid CGPA: ");
            scanner.nextLine();
        }

        double value = scanner.nextDouble();
        scanner.nextLine();
        return value;
    }
}

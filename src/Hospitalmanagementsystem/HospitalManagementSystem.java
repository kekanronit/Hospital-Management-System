package Hospitalmanagementsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class HospitalManagementSystem {

    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = System.getenv("DB_PASSWORD");

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);

        try {

            Connection connection =
                    DriverManager.getConnection(url, username, password);

            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);

            while (true) {

                System.out.println("\n===== HOSPITAL MANAGEMENT SYSTEM =====");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. Search Patient");
                System.out.println("4. Update Patient");
                System.out.println("5. Delete Patient");
                System.out.println("6. View Doctors");
                System.out.println("7. Get Appointment");
                System.out.println("8. Exit");

                System.out.println("Enter your choice:");
                int choice = scanner.nextInt();

                switch (choice) {

                    case 1:
                        patient.addPatient();
                        break;

                    case 2:
                        patient.viewPatient();
                        break;

                    case 3:
                        patient.searchPatient();
                        break;

                    case 4:
                        patient.updatePatient();
                        break;

                    case 5:
                        patient.deletePatient();
                        break;

                    case 6:
                        doctor.viewDoctor();
                        break;

                    case 7:
                        bookAppointment.BookAppointment(
                                patient,
                                doctor,
                                connection,
                                scanner
                        );
                        break;

                    case 8:
                        System.out.println(
                                "Thank you for using Hospital Management System."
                        );

                        connection.close();
                        scanner.close();
                        return;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
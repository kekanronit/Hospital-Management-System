package Hospitalmanagementsystem;


import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";

    private static final String username = "root";

    private static final String password = "01062004Rk@";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection , scanner);
            Doctor doctor = new Doctor(connection);
            while (true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patient");
                System.out.println("3. View Doctors");
                System.out.println("4. Get Appointment");
                System.out.println("5. Exit");
                System.out.println("Enter your choice");
                int choice = scanner.nextInt();

                switch(choice){
                    case 1:
                        // Add patient
                        patient.addPatient();
                        break;
                    case 2:
                        // View Patient
                       patient.viewPatient();
                        break;
                    case 3:
                        // View Doctors
                        doctor.viewDoctor();
                        break;
                    case 4:
                        // Get Appointment
                        bookAppointment.BookAppointment(patient , doctor , connection , scanner);
                        break;
                    case 5:
                        System.out.println("Thank you for using Hospital Management System.");
                        connection.close();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Enter valid choice");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }



//    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection , Scanner scanner){
//        System.out.print("Enter Patient Id");
//        int patientId = scanner.nextInt();
//        System.out.print("Enter Doctor Id");
//        int doctorid = scanner.nextInt();
//        System.out.println("Enter appointment date (YYYY-MM-DD)");
//        String appointmentDate = scanner.next();
//
//        if (Patient.getPatientById(patientId, connection) && Doctor.getDoctorsById(doctorid, connection)){
//             if (checkDoctorAvailability(doctorid, appointmentDate, connection )){
//                 String appointmentQuery = "INSERT INTO appointments(patient_id , doctor_id , appointment_date) VALUES(?,?,?)";
//                 try{
//                     PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
//                     preparedStatement.setInt(1, patientId);
//                     preparedStatement.setInt(2, doctorid);
//                     preparedStatement.setString(3, appointmentDate);
//                     int rowsAffected = preparedStatement.executeUpdate();
//                     if (rowsAffected > 0){
//                         System.out.println("Appointment Booked");
//                     }else {
//                         System.out.println("Failed to book appointment");
//                     }
//
//                 }catch (SQLException e){
//                     e.printStackTrace();
//                 }
//             }else {
//                 System.out.println("Doctor not available on this date");
//             }
//        }else{
//            System.out.println("Either doctor or patient doesn't exist!!");
//        }
//
//
//    }

//    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate , Connection connection){
//        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
//        try {
//            PreparedStatement preparedStatement = connection.prepareStatement(query);
//            preparedStatement.setInt(1, doctorId);
//            preparedStatement.setString(2, appointmentDate);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            if (resultSet.next()){
//                int count = resultSet.getInt(1);
//                if (count==0){
//                    return count == 0;
//                }
//
//            }
//        }catch (SQLException e){
//            e.printStackTrace();
//        }
//        return false;
//    }





}

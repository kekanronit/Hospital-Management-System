package Hospitalmanagementsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class BookAppointment {

    public static void BookAppointment(Patient patient, Doctor doctor, Connection connection , Scanner scanner){
        System.out.print("Enter Patient Id");
        int patientId = scanner.nextInt();
        System.out.print("Enter Doctor Id");
        int doctorid = scanner.nextInt();
        System.out.println("Enter appointment date (YYYY-MM-DD)");
        String appointmentDate = scanner.next();

        if (Patient.getPatientById(patientId, connection) && Doctor.getDoctorsById(doctorid, connection)){
            if (checkDoctorAvailability(doctorid, appointmentDate, connection )){
                String appointmentQuery = "INSERT INTO appointments(patient_id , doctor_id , appointment_date) VALUES(?,?,?)";
                try{
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorid);
                    preparedStatement.setString(3, appointmentDate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0){
                        System.out.println("Appointment Booked");
                    }else {
                        System.out.println("Failed to book appointment");
                    }

                }catch (SQLException e){
                    e.printStackTrace();
                }
            }else {
                System.out.println("Doctor not available on this date");
            }
        }else{
            System.out.println("Either doctor or patient doesn't exist!!");
        }


    }
}

public static boolean checkDoctorAvailability(int doctorId, String appointmentDate , Connection connection){
    String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
    try {
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, doctorId);
        preparedStatement.setString(2, appointmentDate);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()){
            int count = resultSet.getInt(1);
            if (count==0){
                return count == 0;
            }

        }
    }catch (SQLException e){
        e.printStackTrace();
    }
    return false;
}


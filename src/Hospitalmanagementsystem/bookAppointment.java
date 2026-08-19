package Hospitalmanagementsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class bookAppointment {

    public static void BookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner) {
        System.out.print("Enter Patient Id");
        int patientId = scanner.nextInt();

        System.out.print("Enter Doctor Id");
        int doctorid = scanner.nextInt();

        System.out.println("Enter appointment date (YYYY-MM-DD)");
        String appointmentDate = scanner.next();

        System.out.println("Enter appointment time (HH:MM:SS)");
        String appointmentTime = scanner.next();


        if (Patient.getPatientById(patientId, connection) && Doctor.getDoctorsById(doctorid, connection)) {

            if (checkDoctorAvailability(doctorid, appointmentDate,  connection ,appointmentTime)) {

                if (isAppointmentAlreadyBooked(doctorid, appointmentDate, connection , appointmentTime)) {
                    System.out.println("This appointment slot is already booked.");
                    return;
                }

                String appointmentQuery = "INSERT INTO appointments(patient_id , doctor_id , appointment_date, appointment_time) VALUES(?,?,?,?)";



                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);

                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorid);
                    preparedStatement.setString(3, appointmentDate);
                    preparedStatement.setString(4, appointmentTime);

                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Appointment Booked");
                    } else {
                        System.out.println("Failed to book appointment");
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Doctor not available on this date");
            }
        } else {
            System.out.println("Either doctor or patient doesn't exist!!");
        }


    }


    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection , String appointmentTime) {

        LocalDate date = LocalDate.parse(appointmentDate);
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        String dayName = dayOfWeek.toString().charAt(0) + dayOfWeek.toString().substring(1).toLowerCase();

        System.out.println("Day: " + dayName);

        String query = """
              SELECT COUNT(*)
              FROM  doctor_availability
              WHERE doctor_id = ?
              AND day_of_week = ?
              AND ? BETWEEN start_time AND end_time
              AND is_available = TRUE 
              """;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);


            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, dayName);
            preparedStatement.setString(3, appointmentTime);



            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isAppointmentAlreadyBooked(int doctorId, String appointmentDate, Connection connection , String appointmentTime) {


        String query = """
                SELECT COUNT(*)
                FROM appointments
                WHERE doctor_id = ?
                AND appointment_date = ?
                AND appointment_time = ?
                """;

        try {

            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1 , doctorId);
            preparedStatement.setString(2, appointmentDate);
            preparedStatement.setString(3, appointmentTime);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){
                int  count = resultSet.getInt(1);
                return count > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void viewAppointment(Connection connection){


        String query = """
        SELECT
            a.id AS appointment_id,
            p.NAME AS patient_name,
            d.NAME AS doctor_name,
            d.specialization,
            a.appointment_date,
            a.appointment_time
        FROM appointments a
        JOIN patients p
            ON a.patient_id = p.id
        JOIN doctors d
            ON a.doctor_id = d.id
        ORDER BY a.appointment_date, a.appointment_time
        """;

                try {

                    PreparedStatement preparedStatement = connection.prepareStatement(query);

                    ResultSet resultSet = preparedStatement.executeQuery();

                    System.out.println("------------------------------------------------------------------------------------------");
                    System.out.printf("%-5s %-15s %-15s %-18s %-15s %-12s%n",
                            "ID", "Patient", "Doctor", "Specialization", "Date", "Time");
                    System.out.println("------------------------------------------------------------------------------------------");

                    while (resultSet.next()){

                        int id = resultSet.getInt("appointment_id");
                        String patientName = resultSet.getString("patient_name");
                        String doctorName = resultSet.getString("doctor_name");
                        String specialization = resultSet.getString("specialization");
                        String date = resultSet.getString("appointment_date");
                        String time = resultSet.getString("appointment_time");

                        System.out.printf("%-5d %-15s %-15s %-18s %-15s %-12s%n",
                                id,
                                patientName,
                                doctorName,
                                specialization,
                                date,
                                time);




                    }

                    System.out.println("------------------------------------------------------------------------------------------");


                }catch (SQLException e){
                    e.printStackTrace();
                }
    }

    public static void cancelAppointment(Connection connection, Scanner scanner) {

        System.out.print("Enter Appointment ID to cancel: ");
        int appointmentId = scanner.nextInt();

        String query = """
        SELECT
            a.id AS appointment_id,
            p.NAME AS patient_name,
            d.NAME AS doctor_name,
            d.specialization,
            a.appointment_date,
            a.appointment_time,
            a.status
        FROM appointments a
        JOIN patients p
            ON a.patient_id = p.id
        JOIN doctors d
            ON a.doctor_id = d.id
        WHERE a.id = ?
        """;

        try {

            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);

            preparedStatement.setInt(1, appointmentId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                String patientName = resultSet.getString("patient_name");
                String doctorName = resultSet.getString("doctor_name");
                String specialization = resultSet.getString("specialization");
                String appointmentDate = resultSet.getString("appointment_date");
                String appointmentTime = resultSet.getString("appointment_time");
                String status = resultSet.getString("status");

                System.out.println("----------------------------------------");
                System.out.println("Appointment Details");
                System.out.println("----------------------------------------");
                System.out.println("Patient: " + patientName);
                System.out.println("Doctor: " + doctorName);
                System.out.println("Specialization: " + specialization);
                System.out.println("Date: " + appointmentDate);
                System.out.println("Time: " + appointmentTime);
                System.out.println("Status: " + status);
                System.out.println("----------------------------------------");

                // Check if already cancelled
                if (status.equalsIgnoreCase("Cancelled")) {

                    System.out.println("Appointment is already cancelled.");
                    return;
                }

                System.out.print("Confirm cancellation? (Y/N): ");
                String confirmation = scanner.next();

                if (confirmation.equalsIgnoreCase("Y")) {

                    String updateQuery = """
                    UPDATE appointments
                    SET status = 'Cancelled'
                    WHERE id = ?
                    """;

                    PreparedStatement updateStatement =
                            connection.prepareStatement(updateQuery);

                    updateStatement.setInt(1, appointmentId);

                    int rowsAffected = updateStatement.executeUpdate();

                    if (rowsAffected > 0) {

                        System.out.println("----------------------------------------");
                        System.out.println("Appointment cancelled successfully.");
                        System.out.println("----------------------------------------");

                    } else {

                        System.out.println("Failed to cancel appointment.");

                    }

                } else {

                    System.out.println("Appointment cancellation cancelled.");

                }

            } else {

                System.out.println("Appointment not found.");

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }
    }
    public static void rescheduleAppointment(Connection connection, Scanner scanner) {

        System.out.println("Enter Appointment ID to Reschedule: ");
        int appointmentId = scanner.nextInt();

        String query = """
            SELECT
                a.id AS appointment_id,
                a.doctor_id,
                p.NAME AS patient_name,
                d.NAME AS doctor_name,
                d.specialization,
                a.appointment_date,
                a.appointment_time
            FROM appointments a
            JOIN patients p
                ON a.patient_id = p.id
            JOIN doctors d
                ON a.doctor_id = d.id
            WHERE a.id = ?
            """;

        try {

            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, appointmentId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                int doctorId = resultSet.getInt("doctor_id");
                String patientName = resultSet.getString("patient_name");
                String doctorName = resultSet.getString("doctor_name");
                String specialization = resultSet.getString("specialization");
                String currentDate = resultSet.getString("appointment_date");
                String currentTime = resultSet.getString("appointment_time");

                // Display current appointment
                System.out.println("----------------------------------------");
                System.out.println("Current Appointment");
                System.out.println("----------------------------------------");
                System.out.println("Patient: " + patientName);
                System.out.println("Doctor: " + doctorName);
                System.out.println("Specialization: " + specialization);
                System.out.println("Date: " + currentDate);
                System.out.println("Time: " + currentTime);
                System.out.println("----------------------------------------");

                // Get new date
                System.out.println("Enter new appointment date (YYYY-MM-DD): ");
                String newDate = scanner.next();

                // Get new time
                System.out.println("Enter new appointment time (HH:MM:SS): ");
                String newTime = scanner.next();


                // Check doctor availability
                String checkQuery = """
                    SELECT id
                    FROM appointments
                    WHERE doctor_id = ?
                    AND appointment_date = ?
                    AND appointment_time = ?
                    AND id != ?
                    """;

                PreparedStatement checkStatement =
                        connection.prepareStatement(checkQuery);

                checkStatement.setInt(1, doctorId);
                checkStatement.setString(2, newDate);
                checkStatement.setString(3, newTime);
                checkStatement.setInt(4, appointmentId);

                ResultSet checkResult = checkStatement.executeQuery();

                if (checkResult.next()) {

                    System.out.println("----------------------------------------");
                    System.out.println("Doctor is already booked at this date and time.");
                    System.out.println("Please choose another appointment slot.");
                    System.out.println("----------------------------------------");

                } else {

                    // Update appointment
                    String updateQuery = """
                        UPDATE appointments
                        SET appointment_date = ?,
                            appointment_time = ?,
                            status = 'Scheduled'
                        WHERE id = ?
                        """;

                    PreparedStatement updateStatement =
                            connection.prepareStatement(updateQuery);

                    updateStatement.setString(1, newDate);
                    updateStatement.setString(2, newTime);
                    updateStatement.setInt(3, appointmentId);

                    int rowsAffected = updateStatement.executeUpdate();

                    if (rowsAffected > 0) {

                        System.out.println("----------------------------------------");
                        System.out.println("Appointment Rescheduled Successfully!");
                        System.out.println("----------------------------------------");
                        System.out.println("Patient: " + patientName);
                        System.out.println("Doctor: " + doctorName);
                        System.out.println("Specialization: " + specialization);
                        System.out.println("New Date: " + newDate);
                        System.out.println("New Time: " + newTime);
                        System.out.println("Status: Scheduled");
                        System.out.println("----------------------------------------");

                    } else {

                        System.out.println("Failed to reschedule appointment.");

                    }
                }

            } else {

                System.out.println("Appointment not found.");

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }
    }

    public static void appointmentHistory(Connection connection) {

        String query = """
        SELECT
            a.id AS appointment_id,
            p.name AS patient_name,
            d.name AS doctor_name,
            d.specialization,
            a.appointment_date,
            a.appointment_time,
            a.status
        FROM appointments a
        JOIN patients p
            ON a.patient_id = p.id
        JOIN doctors d
            ON a.doctor_id = d.id
        ORDER BY a.appointment_date DESC, a.appointment_time DESC
        """;

        try {

            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {

                System.out.println("No appointment history found.");
                return;
            }

            do {

                int appointmentId =
                        resultSet.getInt("appointment_id");

                String patientName =
                        resultSet.getString("patient_name");

                String doctorName =
                        resultSet.getString("doctor_name");

                String specialization =
                        resultSet.getString("specialization");

                String appointmentDate =
                        resultSet.getString("appointment_date");

                String appointmentTime =
                        resultSet.getString("appointment_time");

                String status =
                        resultSet.getString("status");

                System.out.println("----------------------------------------");
                System.out.println("Appointment History");
                System.out.println("----------------------------------------");
                System.out.println("Appointment ID: " + appointmentId);
                System.out.println("Patient: " + patientName);
                System.out.println("Doctor: " + doctorName);
                System.out.println("Specialization: " + specialization);
                System.out.println("Date: " + appointmentDate);
                System.out.println("Time: " + appointmentTime);
                System.out.println("Status: " + status);
                System.out.println("----------------------------------------");

            } while (resultSet.next());

        } catch (SQLException e) {

            e.printStackTrace();

        }
    }

}

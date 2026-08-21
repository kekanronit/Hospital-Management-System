package Hospitalmanagementsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.Scanner;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class bookAppointment {

    public static void BookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner) {

        // ==========================================
        // 1. Validate Patient ID
        // ==========================================

        int patientId;

        while (true) {

            System.out.print("Enter Patient ID: ");

            if (scanner.hasNextInt()) {

                patientId = scanner.nextInt();

                if (patientId <= 0) {

                    System.out.println(
                            "Patient ID must be greater than 0."
                    );

                    continue;
                }

                if (Patient.getPatientById(patientId, connection)) {
                    break;
                }

                System.out.println(
                        "Patient not found. Please enter a valid Patient ID."
                );

            } else {

                System.out.println(
                        "Invalid input. Please enter a number."
                );

                scanner.next();
            }
        }


        // ==========================================
        // 2. Validate Doctor ID
        // ==========================================

        int doctorId;

        while (true) {

            System.out.print("Enter Doctor ID: ");

            if (scanner.hasNextInt()) {

                doctorId = scanner.nextInt();

                if (doctorId <= 0) {

                    System.out.println(
                            "Doctor ID must be greater than 0."
                    );

                    continue;
                }

                if (Doctor.getDoctorsById(doctorId, connection)) {
                    break;
                }

                System.out.println(
                        "Doctor not found. Please enter a valid Doctor ID."
                );

            } else {

                System.out.println(
                        "Invalid input. Please enter a number."
                );

                scanner.next();
            }
        }


        // ==========================================
        // 3. Validate Appointment Date
        // ==========================================

        String appointmentDate;

        while (true) {

            System.out.println(
                    "Enter appointment date (YYYY-MM-DD):"
            );

            appointmentDate = scanner.next();

            try {

                java.time.LocalDate date =
                        java.time.LocalDate.parse(appointmentDate);

                if (date.isBefore(java.time.LocalDate.now())) {

                    System.out.println(
                            "Appointment date cannot be in the past."
                    );

                    continue;
                }

                break;

            } catch (java.time.format.DateTimeParseException e) {

                System.out.println(
                        "Invalid date format. Please use YYYY-MM-DD."
                );
            }
        }


        // ==========================================
        // 4. Validate Appointment Time
        // ==========================================

        String appointmentTime;

        while (true) {

            System.out.println(
                    "Enter appointment time (HH:MM:SS):"
            );

            appointmentTime = scanner.next();

            try {

                java.time.LocalTime.parse(appointmentTime);

                break;

            } catch (java.time.format.DateTimeParseException e) {

                System.out.println(
                        "Invalid time format. Please use HH:MM:SS."
                );
            }
        }


        // ==========================================
        // 5. Check Doctor Availability
        // ==========================================

        if (checkDoctorAvailability(
                doctorId,
                appointmentDate,
                connection,
                appointmentTime)) {


            // ==========================================
            // 6. Check Already Booked
            // ==========================================

            if (isAppointmentAlreadyBooked(
                    doctorId,
                    appointmentDate,
                    connection,
                    appointmentTime)) {

                System.out.println(
                        "This appointment slot is already booked."
                );

                return;
            }


            // ==========================================
            // 7. Book Appointment
            // ==========================================

            String appointmentQuery = """
                INSERT INTO appointments
                (patient_id, doctor_id, appointment_date, appointment_time)
                VALUES (?, ?, ?, ?)
                """;

            try {

                PreparedStatement preparedStatement =
                        connection.prepareStatement(appointmentQuery);

                preparedStatement.setInt(1, patientId);
                preparedStatement.setInt(2, doctorId);
                preparedStatement.setString(3, appointmentDate);
                preparedStatement.setString(4, appointmentTime);

                int rowsAffected =
                        preparedStatement.executeUpdate();

                if (rowsAffected > 0) {

                    System.out.println(
                            "Appointment booked successfully."
                    );

                } else {

                    System.out.println(
                            "Failed to book appointment."
                    );
                }

            } catch (SQLException e) {

                e.printStackTrace();
            }

        } else {

            System.out.println(
                    "Doctor is not available at this date and time."
            );
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection, String appointmentTime) {

        // ==========================================
        // 1. Validate Doctor ID
        // ==========================================

        if (doctorId <= 0) {

            System.out.println(
                    "Invalid Doctor ID."
            );

            return false;
        }


        // ==========================================
        // 2. Validate Appointment Date
        // ==========================================

        LocalDate date;

        try {

            date = LocalDate.parse(appointmentDate);

        } catch (java.time.format.DateTimeParseException e) {

            System.out.println(
                    "Invalid appointment date."
            );

            return false;
        }


        // ==========================================
        // 3. Validate Appointment Time
        // ==========================================

        java.time.LocalTime time;

        try {

            time = java.time.LocalTime.parse(appointmentTime);

        } catch (java.time.format.DateTimeParseException e) {

            System.out.println(
                    "Invalid appointment time."
            );

            return false;
        }


        // ==========================================
        // 4. Get Day of Week
        // ==========================================

        DayOfWeek dayOfWeek = date.getDayOfWeek();

        String dayName =
                dayOfWeek.toString().charAt(0)
                        + dayOfWeek.toString()
                        .substring(1)
                        .toLowerCase();

        System.out.println("Day: " + dayName);


        // ==========================================
        // 5. Check Doctor Availability
        // ==========================================

        String query = """
            SELECT COUNT(*)
            FROM doctor_availability
            WHERE doctor_id = ?
            AND day_of_week = ?
            AND ? BETWEEN start_time AND end_time
            AND is_available = TRUE
            """;

        try {

            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);

            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, dayName);

            /*
             * Convert LocalTime to java.sql.Time
             * before sending it to MySQL.
             */
            preparedStatement.setTime(
                    3,
                    java.sql.Time.valueOf(time)
            );


            ResultSet resultSet =
                    preparedStatement.executeQuery();

            if (resultSet.next()) {

                int count = resultSet.getInt(1);

                return count > 0;
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return false;
    }

    public static boolean isAppointmentAlreadyBooked(int doctorId, String appointmentDate, Connection connection, String appointmentTime) {

        // ==========================================
        // 1. Validate Doctor ID
        // ==========================================

        if (doctorId <= 0) {

            System.out.println(
                    "Invalid Doctor ID."
            );

            return false;
        }


        // ==========================================
        // 2. Validate Appointment Date
        // ==========================================

        LocalDate date;

        try {

            date = LocalDate.parse(appointmentDate);

        } catch (java.time.format.DateTimeParseException e) {

            System.out.println(
                    "Invalid appointment date."
            );

            return false;
        }


        // ==========================================
        // 3. Validate Appointment Time
        // ==========================================

        LocalTime time;

        try {

            time = LocalTime.parse(appointmentTime);

        } catch (java.time.format.DateTimeParseException e) {

            System.out.println(
                    "Invalid appointment time."
            );

            return false;
        }


        // ==========================================
        // 4. Check Existing Appointment
        // ==========================================

        String query = """
            SELECT COUNT(*)
            FROM appointments
            WHERE doctor_id = ?
            AND appointment_date = ?
            AND appointment_time = ?
            AND status = 'Scheduled'
            """;

        try {

            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);

            preparedStatement.setInt(1, doctorId);
            preparedStatement.setDate(
                    2,
                    java.sql.Date.valueOf(date)
            );

            preparedStatement.setTime(
                    3,
                    java.sql.Time.valueOf(time)
            );


            ResultSet resultSet =
                    preparedStatement.executeQuery();

            if (resultSet.next()) {

                int count = resultSet.getInt(1);

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

        // ==========================================
        // 1. Validate Appointment ID
        // ==========================================

        int appointmentId;

        while (true) {

            System.out.print("Enter Appointment ID to cancel: ");

            if (scanner.hasNextInt()) {

                appointmentId = scanner.nextInt();

                if (appointmentId > 0) {
                    break;
                }

                System.out.println(
                        "Appointment ID must be greater than 0."
                );

            } else {

                System.out.println(
                        "Invalid input. Please enter a number."
                );

                scanner.next();
            }
        }


        // ==========================================
        // 2. Find Appointment
        // ==========================================

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
            WHERE a.id = ?
            """;


        try {

            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);

            preparedStatement.setInt(1, appointmentId);

            ResultSet resultSet =
                    preparedStatement.executeQuery();


            // ==========================================
            // 3. Check Appointment Exists
            // ==========================================

            if (resultSet.next()) {

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


                // ==========================================
                // 4. Display Appointment
                // ==========================================

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


                // ==========================================
                // 5. Check Already Cancelled
                // ==========================================

                if ("Cancelled".equalsIgnoreCase(status)) {

                    System.out.println(
                            "Appointment is already cancelled."
                    );

                    return;
                }


                // ==========================================
                // 6. Confirmation
                // ==========================================

                String confirmation;

                while (true) {

                    System.out.print(
                            "Confirm cancellation? (Y/N): "
                    );

                    confirmation = scanner.next();

                    if (confirmation.equalsIgnoreCase("Y")
                            || confirmation.equalsIgnoreCase("N")) {

                        break;
                    }

                    System.out.println(
                            "Invalid input. Please enter Y or N."
                    );
                }


                // ==========================================
                // 7. Cancel Appointment
                // ==========================================

                if (confirmation.equalsIgnoreCase("Y")) {

                    String updateQuery = """
                        UPDATE appointments
                        SET status = 'Cancelled'
                        WHERE id = ?
                        """;


                    PreparedStatement updateStatement =
                            connection.prepareStatement(updateQuery);

                    updateStatement.setInt(1, appointmentId);


                    int rowsAffected =
                            updateStatement.executeUpdate();


                    if (rowsAffected > 0) {

                        System.out.println(
                                "----------------------------------------"
                        );

                        System.out.println(
                                "Appointment cancelled successfully."
                        );

                        System.out.println(
                                "----------------------------------------"
                        );

                    } else {

                        System.out.println(
                                "Failed to cancel appointment."
                        );
                    }

                } else {

                    System.out.println(
                            "Appointment cancellation cancelled."
                    );
                }


            } else {

                System.out.println(
                        "Appointment not found."
                );
            }


        } catch (SQLException e) {

            e.printStackTrace();
        }
    }


    public static void rescheduleAppointment(Connection connection, Scanner scanner) {

        // ==========================================
        // 1. Validate Appointment ID
        // ==========================================

        int appointmentId;

        while (true) {

            System.out.print("Enter Appointment ID to Reschedule: ");

            if (scanner.hasNextInt()) {

                appointmentId = scanner.nextInt();

                if (appointmentId > 0) {
                    break;
                }

                System.out.println(
                        "Appointment ID must be greater than 0."
                );

            } else {

                System.out.println(
                        "Invalid input. Please enter a number."
                );

                scanner.next();
            }
        }


        // ==========================================
        // 2. Get Current Appointment
        // ==========================================

        String query = """
            SELECT
                a.id AS appointment_id,
                a.doctor_id,
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
            WHERE a.id = ?
            """;


        try {

            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);

            preparedStatement.setInt(1, appointmentId);

            ResultSet resultSet =
                    preparedStatement.executeQuery();


            // ==========================================
            // 3. Check Appointment Exists
            // ==========================================

            if (!resultSet.next()) {

                System.out.println(
                        "Appointment not found."
                );

                return;
            }


            int doctorId =
                    resultSet.getInt("doctor_id");

            String patientName =
                    resultSet.getString("patient_name");

            String doctorName =
                    resultSet.getString("doctor_name");

            String specialization =
                    resultSet.getString("specialization");

            String currentDate =
                    resultSet.getString("appointment_date");

            String currentTime =
                    resultSet.getString("appointment_time");

            String status =
                    resultSet.getString("status");


            // ==========================================
            // 4. Display Current Appointment
            // ==========================================

            System.out.println("----------------------------------------");
            System.out.println("Current Appointment");
            System.out.println("----------------------------------------");
            System.out.println("Patient: " + patientName);
            System.out.println("Doctor: " + doctorName);
            System.out.println("Specialization: " + specialization);
            System.out.println("Date: " + currentDate);
            System.out.println("Time: " + currentTime);
            System.out.println("Status: " + status);
            System.out.println("----------------------------------------");


            // ==========================================
            // 5. Check Appointment Status
            // ==========================================

            if ("Cancelled".equalsIgnoreCase(status)) {

                System.out.println(
                        "Cancelled appointments cannot be rescheduled."
                );

                return;
            }


            // ==========================================
            // 6. Validate New Date
            // ==========================================

            LocalDate newDate;

            while (true) {

                System.out.print(
                        "Enter new appointment date (YYYY-MM-DD): "
                );

                String newDateInput = scanner.next();

                try {

                    newDate = LocalDate.parse(newDateInput);

                    if (newDate.isBefore(LocalDate.now())) {

                        System.out.println(
                                "Appointment date cannot be in the past."
                        );

                        continue;
                    }

                    break;

                } catch (java.time.format.DateTimeParseException e) {

                    System.out.println(
                            "Invalid date format. Please use YYYY-MM-DD."
                    );
                }
            }


            // ==========================================
            // 7. Validate New Time
            // ==========================================

            LocalTime newTime;

            while (true) {

                System.out.print(
                        "Enter new appointment time (HH:MM:SS): "
                );

                String newTimeInput = scanner.next();

                try {

                    newTime = LocalTime.parse(newTimeInput);

                    break;

                } catch (java.time.format.DateTimeParseException e) {

                    System.out.println(
                            "Invalid time format. Please use HH:MM:SS."
                    );
                }
            }


            // ==========================================
            // 8. Check Doctor Availability
            // ==========================================

            if (!checkDoctorAvailability(
                    doctorId,
                    newDate.toString(),
                    connection,
                    newTime.toString())) {

                System.out.println("----------------------------------------");
                System.out.println(
                        "Doctor is not available at the selected date and time."
                );
                System.out.println("----------------------------------------");

                return;
            }


            // ==========================================
            // 9. Check Already Booked Slot
            // ==========================================

            String checkQuery = """
                SELECT id
                FROM appointments
                WHERE doctor_id = ?
                AND appointment_date = ?
                AND appointment_time = ?
                AND status = 'Scheduled'
                AND id != ?
                """;


            PreparedStatement checkStatement =
                    connection.prepareStatement(checkQuery);

            checkStatement.setInt(1, doctorId);

            checkStatement.setDate(
                    2,
                    java.sql.Date.valueOf(newDate)
            );

            checkStatement.setTime(
                    3,
                    java.sql.Time.valueOf(newTime)
            );

            checkStatement.setInt(4, appointmentId);


            ResultSet checkResult =
                    checkStatement.executeQuery();


            if (checkResult.next()) {

                System.out.println("----------------------------------------");
                System.out.println(
                        "Doctor is already booked at this date and time."
                );
                System.out.println(
                        "Please choose another appointment slot."
                );
                System.out.println("----------------------------------------");

                return;
            }


            // ==========================================
            // 10. Update Appointment
            // ==========================================

            String updateQuery = """
                UPDATE appointments
                SET appointment_date = ?,
                    appointment_time = ?,
                    status = 'Scheduled'
                WHERE id = ?
                """;


            PreparedStatement updateStatement =
                    connection.prepareStatement(updateQuery);

            updateStatement.setDate(
                    1,
                    java.sql.Date.valueOf(newDate)
            );

            updateStatement.setTime(
                    2,
                    java.sql.Time.valueOf(newTime)
            );

            updateStatement.setInt(
                    3,
                    appointmentId
            );


            int rowsAffected =
                    updateStatement.executeUpdate();


            // ==========================================
            // 11. Result
            // ==========================================

            if (rowsAffected > 0) {

                System.out.println("----------------------------------------");
                System.out.println(
                        "Appointment Rescheduled Successfully!"
                );
                System.out.println("----------------------------------------");
                System.out.println("Patient: " + patientName);
                System.out.println("Doctor: " + doctorName);
                System.out.println("Specialization: " + specialization);
                System.out.println("New Date: " + newDate);
                System.out.println("New Time: " + newTime);
                System.out.println("Status: Scheduled");
                System.out.println("----------------------------------------");

            } else {

                System.out.println(
                        "Failed to reschedule appointment."
                );
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

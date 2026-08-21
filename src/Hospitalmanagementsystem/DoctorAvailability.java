package Hospitalmanagementsystem;


import java.sql.*;
import java.util.Scanner;

public class DoctorAvailability {

    private Connection connection;
    private Scanner scanner;

    public DoctorAvailability(Connection connection, Scanner scanner){
        this.connection = connection;
        this.scanner = scanner;
    }

    public void viewAvailability(){

        String query = "select * from doctor_availability";

        try{

            PreparedStatement preparedStatement = connection.prepareStatement(query);

            ResultSet resultSet = preparedStatement.executeQuery();

            System.out.println(
                    "---------------------------------------------------------------------");

            System.out.printf(
                    "%-5s %-10s %-12s %-12s %-12s %-10s%n",
                    "ID",
                    "Doctor ID",
                    "Day",
                    "Start",
                    "End",
                    "Available");

            System.out.println(
                    "---------------------------------------------------------------------");

            while (resultSet.next()) {

                System.out.printf(
                        "%-5d %-10d %-12s %-12s %-12s %-10s%n",
                        resultSet.getInt("availability_id"),
                        resultSet.getInt("doctor_id"),
                        resultSet.getString("day_of_week"),
                        resultSet.getTime("start_time"),
                        resultSet.getTime("end_time"),
                        resultSet.getBoolean("is_available")
                );
            }



        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    public void addAvailability() {

        // ==============================
        // 1. Validate Doctor ID
        // ==============================

        int doctorId;

        while (true) {

            System.out.println("Enter doctor ID:");

            if (scanner.hasNextInt()) {

                doctorId = scanner.nextInt();

                if (doctorId <= 0) {

                    System.out.println(
                            "Doctor ID must be greater than 0."
                    );

                    continue;
                }

                // Check whether doctor exists
                if (Doctor.getDoctorsById(doctorId, connection)) {

                    break;

                } else {

                    System.out.println("Doctor not found.");
                }

            } else {

                System.out.println(
                        "Invalid input. Please enter a number."
                );

                scanner.next();
            }
        }


        // ==============================
        // 2. Validate Day of Week
        // ==============================

        String dayOfWeek;

        while (true) {

            System.out.println("Enter day of week:");

            dayOfWeek = scanner.next();

            if (dayOfWeek.equalsIgnoreCase("Monday")
                    || dayOfWeek.equalsIgnoreCase("Tuesday")
                    || dayOfWeek.equalsIgnoreCase("Wednesday")
                    || dayOfWeek.equalsIgnoreCase("Thursday")
                    || dayOfWeek.equalsIgnoreCase("Friday")
                    || dayOfWeek.equalsIgnoreCase("Saturday")
                    || dayOfWeek.equalsIgnoreCase("Sunday")) {

                break;
            }

            System.out.println(
                    "Invalid day. Please enter a valid day of the week."
            );
        }


        // ==============================
        // 3. Validate Start Time
        // ==============================

        Time startTime;

        while (true) {

            System.out.println(
                    "Enter start time (HH:MM:SS):"
            );

            String startInput = scanner.next();

            try {

                startTime = Time.valueOf(startInput);

                break;

            } catch (IllegalArgumentException e) {

                System.out.println(
                        "Invalid time format. Please use HH:MM:SS."
                );
            }
        }


        // ==============================
        // 4. Validate End Time
        // ==============================

        Time endTime;

        while (true) {

            System.out.println(
                    "Enter end time (HH:MM:SS):"
            );

            String endInput = scanner.next();

            try {

                endTime = Time.valueOf(endInput);

                if (endTime.after(startTime)) {

                    break;

                } else {

                    System.out.println(
                            "End time must be after start time."
                    );
                }

            } catch (IllegalArgumentException e) {

                System.out.println(
                        "Invalid time format. Please use HH:MM:SS."
                );
            }
        }


        // ==============================
        // 5. Insert Availability
        // ==============================

        String query = """
            INSERT INTO doctor_availability
            (doctor_id, day_of_week, start_time, end_time, is_available)
            VALUES (?, ?, ?, ?, ?)
            """;

        try {

            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);

            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, dayOfWeek);
            preparedStatement.setTime(3, startTime);
            preparedStatement.setTime(4, endTime);
            preparedStatement.setBoolean(5, true);

            int rowsAffected =
                    preparedStatement.executeUpdate();

            if (rowsAffected > 0) {

                System.out.println(
                        "Doctor availability has been added successfully."
                );

            } else {

                System.out.println(
                        "Failed to add doctor availability."
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public void updateAvailability() {

        // Validate Availability ID
        int availabilityId;

        while (true) {

            System.out.println("Enter availability ID:");

            if (scanner.hasNextInt()) {

                availabilityId = scanner.nextInt();

                if (availabilityId > 0) {
                    break;
                }

                System.out.println(
                        "Availability ID must be greater than 0."
                );

            } else {

                System.out.println(
                        "Invalid input. Please enter a number."
                );

                scanner.next();
            }
        }


        // Check whether availability exists
        String checkQuery =
                "SELECT * FROM doctor_availability WHERE availability_id = ?";

        try {

            PreparedStatement checkStatement =
                    connection.prepareStatement(checkQuery);

            checkStatement.setInt(1, availabilityId);

            ResultSet resultSet =
                    checkStatement.executeQuery();

            if (!resultSet.next()) {

                System.out.println("Availability record not found.");
                return;
            }

        } catch (SQLException e) {

            e.printStackTrace();
            return;
        }


        // Validate Day
        String dayOfWeek;

        while (true) {

            System.out.println("Enter new day of week:");

            dayOfWeek = scanner.next();

            if (dayOfWeek.equalsIgnoreCase("Monday")
                    || dayOfWeek.equalsIgnoreCase("Tuesday")
                    || dayOfWeek.equalsIgnoreCase("Wednesday")
                    || dayOfWeek.equalsIgnoreCase("Thursday")
                    || dayOfWeek.equalsIgnoreCase("Friday")
                    || dayOfWeek.equalsIgnoreCase("Saturday")
                    || dayOfWeek.equalsIgnoreCase("Sunday")) {

                break;
            }

            System.out.println(
                    "Invalid day. Please enter a valid day of the week."
            );
        }


        // Validate Start Time
        Time startTime;

        while (true) {

            System.out.println("Enter new start time (HH:MM:SS):");

            String startInput = scanner.next();

            try {

                startTime = Time.valueOf(startInput);
                break;

            } catch (IllegalArgumentException e) {

                System.out.println(
                        "Invalid time format. Use HH:MM:SS."
                );
            }
        }


        // Validate End Time
        Time endTime;

        while (true) {

            System.out.println("Enter new end time (HH:MM:SS):");

            String endInput = scanner.next();

            try {

                endTime = Time.valueOf(endInput);

                if (endTime.after(startTime)) {
                    break;
                }

                System.out.println(
                        "End time must be after start time."
                );

            } catch (IllegalArgumentException e) {

                System.out.println(
                        "Invalid time format. Use HH:MM:SS."
                );
            }
        }


        // Update availability
        String query = """
            UPDATE doctor_availability
            SET day_of_week = ?,
                start_time = ?,
                end_time = ?
            WHERE availability_id = ?
            """;

        try {

            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);

            preparedStatement.setString(1, dayOfWeek);
            preparedStatement.setTime(2, startTime);
            preparedStatement.setTime(3, endTime);
            preparedStatement.setInt(4, availabilityId);

            int rowsAffected =
                    preparedStatement.executeUpdate();

            if (rowsAffected > 0) {

                System.out.println(
                        "Doctor availability has been updated successfully."
                );

            } else {

                System.out.println(
                        "Failed to update availability."
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }


    public void deleteAvailability() {

        int availabilityId;

        // Validate Availability ID
        while (true) {

            System.out.println("Enter availability ID:");

            if (scanner.hasNextInt()) {

                availabilityId = scanner.nextInt();

                if (availabilityId > 0) {
                    break;
                }

                System.out.println(
                        "Availability ID must be greater than 0."
                );

            } else {

                System.out.println(
                        "Invalid input. Please enter a number."
                );

                scanner.next();
            }
        }


        // Delete availability
        String query =
                "DELETE FROM doctor_availability WHERE availability_id = ?";

        try {

            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);

            preparedStatement.setInt(1, availabilityId);

            int rowsAffected =
                    preparedStatement.executeUpdate();

            if (rowsAffected > 0) {

                System.out.println(
                        "Doctor availability deleted successfully."
                );

            } else {

                System.out.println(
                        "Availability record not found."
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }
    }

package Hospitalmanagementsystem;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public void addAvailability(){

        System.out.println("Enter doctor ID");
        int doctor_id = scanner.nextInt();

        System.out.println("Enter day of week");
        String day_of_week = scanner.next();

        System.out.println("Enter start time (HH:MM:SS)");
        String startTime = scanner.next();

        System.out.println("Enter end time (HH:MM:SS)");
        String endTime = scanner.next();

        String query = """
                INSERT INTO doctor_availability (doctor_id, day_of_week, start_time, end_time , is_available)     
                VALUES (? , ? , ? , ? , ?)
                """;

        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, doctor_id);
            preparedStatement.setString(2, day_of_week);
            preparedStatement.setString(3, startTime);
            preparedStatement.setString(4, endTime);
            preparedStatement.setBoolean(5, true);

            int rowAffected = preparedStatement.executeUpdate();

            if(rowAffected > 0){
                System.out.println("Doctor  availability has been added");
            }else{
                System.out.println("Failed to add availability");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void updateAvailability(){

        System.out.println("Enter availability ID");
        int availability_id = scanner.nextInt();

        System.out.println("Enter new day of week");
        String day_of_week = scanner.next();

        System.out.println("Enter new start time (HH:MM:SS)");
        String startTime = scanner.next();

        System.out.println("Enter new end time (HH:MM:SS)");
        String endTime = scanner.next();

        String query = """
                UPDATE  doctor_availability
                SET day_of_week = ? , start_time = ? , end_time =?
                WHERE availability_id = ? ;
                """;

        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, day_of_week);
            preparedStatement.setString(2, startTime);
            preparedStatement.setString(3, endTime);
            preparedStatement.setInt(4, availability_id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0){
                System.out.println("Doctor availability has been updated");
            }else{
                System.out.println("Failed to update availability");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void deleteAvailability(){
        System.out.println("Enter availability ID");
        int availability_id = scanner.nextInt();

        String query = "DELETE FROM  doctor_availability WHERE availability_id = ?;";

        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);

            preparedStatement.setInt(1, availability_id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Doctor availability deleted successfully.");
            } else {
                System.out.println("Availability record not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    }

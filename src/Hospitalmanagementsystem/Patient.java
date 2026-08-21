package Hospitalmanagementsystem;

import java.sql.*;
import java.util.Scanner;

public class Patient {

    private Connection connection;

    private Scanner scanner;

    public Patient(Connection connection , Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }



    public void addPatient(){

        String name;

        while (true) {

            System.out.println("Enter patient name:");
            name = scanner.next();

            if (name.matches("[a-zA-Z]+")) {
                break;
            }

            System.out.println("Invalid name. Please enter alphabets only.");
        }


        int age;

        while (true) {

            System.out.println("Enter patient age:");

            if (scanner.hasNextInt()) {

                age = scanner.nextInt();

                if (age > 0 && age <= 120) {
                    break;
                }

                System.out.println("Invalid age. Age must be between 1 and 120.");

            } else {

                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
            }
        }


        String gender;

        while (true) {

            System.out.println("Enter patient gender:");
            gender = scanner.next();

            if (gender.equalsIgnoreCase("Male")
                    || gender.equalsIgnoreCase("Female")
                    || gender.equalsIgnoreCase("Other")) {

                break;
            }

            System.out.println(
                    "Invalid gender. Please enter Male, Female, or Other."
            );
        }

        try {
            String query = "Insert INTO patients(name, age , gender) VALUES(?, ? ,?)";

            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, age);
            preparedStatement.setString(3, gender);

            int affectedRows = preparedStatement.executeUpdate();

            if(affectedRows>0){
                System.out.println("Patient added succesfully");
            }else{
                System.out.println("Failed to add patient");
            }


        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    public void viewPatient() {

        String query = "SELECT * FROM patients";

        try {

            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);

            ResultSet resultSet =
                    preparedStatement.executeQuery();

            boolean found = false;

            System.out.println("\nPatients:");
            System.out.println("+-----------+--------------+-----------+------------------+");
            System.out.println("| Patient ID| Name         | Age       | Gender           |");
            System.out.println("+-----------+--------------+-----------+------------------+");

            while (resultSet.next()) {

                found = true;

                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String gender = resultSet.getString("gender");

                System.out.printf(
                        "| %-10d| %-13s| %-10d| %-17s|%n",
                        id,
                        name,
                        age,
                        gender
                );

                System.out.println(
                        "+-----------+--------------+-----------+------------------+"
                );
            }

            if (!found) {

                System.out.println("No patients found.");

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }
    }

    public void searchPatient(){

        int id;

        while (true) {

            System.out.println("Enter patient ID:");

            if (scanner.hasNextInt()) {

                id = scanner.nextInt();

                if (id > 0) {
                    break;
                }

                System.out.println("Patient ID must be greater than 0.");

            } else {

                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
            }
        }


        String query = "select * from patients where id = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Patient found succesfully");
                System.out.println("-------------------------");
                System.out.println("ID: " + resultSet.getInt("id"));
                String name = resultSet.getString("name");
                System.out.println("Age: " + resultSet.getInt("age"));
                System.out.println("Gender: " + resultSet.getString("gender"));
            }else{
                System.out.println("Patient not found.");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    public void updatePatient(){
        int id;

        while (true) {

            System.out.println("Enter patient ID:");

            if (scanner.hasNextInt()) {

                id = scanner.nextInt();

                if (id > 0) {
                    break;
                }

                System.out.println("Patient ID must be greater than 0.");

            } else {

                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
            }
        }


        // Validate Patient Name
        String name;

        while (true) {

            System.out.println("Enter new patient name:");
            name = scanner.next();

            if (name.matches("[a-zA-Z]+")) {
                break;
            }

            System.out.println("Invalid name. Please enter alphabets only.");
        }


        // Validate Patient Age
        int age;

        while (true) {

            System.out.println("Enter new patient age:");

            if (scanner.hasNextInt()) {

                age = scanner.nextInt();

                if (age > 0 && age <= 120) {
                    break;
                }

                System.out.println(
                        "Invalid age. Age must be between 1 and 120."
                );

            } else {

                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
            }
        }


        // Validate Patient Gender
        String gender;

        while (true) {

            System.out.println("Enter new patient gender:");
            gender = scanner.next();

            if (gender.equalsIgnoreCase("Male")
                    || gender.equalsIgnoreCase("Female")
                    || gender.equalsIgnoreCase("Other")) {

                break;
            }

            System.out.println(
                    "Invalid gender. Please enter Male, Female, or Other."
            );
        }

        String query = "update patients SET name = ?, age = ? , gender = ? where id = ?";

        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);

            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, age);
            preparedStatement.setString(3, gender);
            preparedStatement.setInt(4, id);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Patient updated successfully");
            } else {
                System.out.println("Patient not found");
            }


        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void deletePatient(){
        int id;

        // Validate Patient ID
        while (true) {

            System.out.println("Enter patient ID:");

            if (scanner.hasNextInt()) {

                id = scanner.nextInt();

                if (id > 0) {
                    break;
                }

                System.out.println("Patient ID must be greater than 0.");

            } else {

                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
            }
        }

        String query = "delete from patients where id = ?";

        try{
            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);

            preparedStatement.setInt(1, id);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Patient deleted successfully");
            }else{
                System.out.println("Patient not found");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }



    public static boolean getPatientById(int id, Connection connection){
        String query = "SELECT * FROM Patients WHERE id = ? ";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return true;
            }else {
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }


}

package Hospitalmanagementsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Doctor {

    private Connection connection;



    public Doctor(Connection connection) {
        this.connection = connection;

    }



    public void viewDoctor() {
        String query = "select * from doctors";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("Doctors: ");
            System.out.println("+-----------+--------------+------------------------------+");
            System.out.println("| Doctors Id| Name         | Specializations              |");
            System.out.println("+-----------+--------------+------------------------------+");
            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String specialization = resultSet.getString("specialization");
                System.out.printf("| %-10d | %-15s | %-25s |\n", id, name, specialization);
                System.out.println("+-----------+--------------+------------------------+");
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static boolean getDoctorsById(int id , Connection connection){
        String query = "SELECT * FROM doctors WHERE id = ? ";
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


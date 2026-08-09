# 🏥 Hospital Management System

A **Java-based Hospital Management System** developed using **Core Java, Object-Oriented Programming (OOP), JDBC, and MySQL**. The application is a console-based system designed to manage patient information, doctor details, and appointments while storing data in a MySQL database.

## 📌 Project Overview

The Hospital Management System is designed to simplify basic hospital management operations through a menu-driven console application.

The system allows users to manage patient records, view available doctors, and book appointments. The application uses **JDBC** to establish a connection between the Java application and the **MySQL database**, allowing hospital-related data to be stored and retrieved from the database.

This project was developed to practice Java programming, OOP concepts, database connectivity, and CRUD operations in a practical real-world application.

## ✨ Features

* 👤 Add patient details
* 📋 View patient records
* 👨‍⚕️ View doctor details
* 📅 Book appointments
* 🗄️ Store data in MySQL database
* 🔗 Connect Java application with MySQL using JDBC
* 🔄 Perform database CRUD operations
* 🖥️ Menu-driven console interface

## 🛠️ Technologies Used

* **Java**
* **Core Java**
* **Object-Oriented Programming (OOP)**
* **JDBC**
* **MySQL**
* **MySQL Connector/J**
* **IntelliJ IDEA**
* **Git & GitHub**

## 🧠 Concepts Used

The project demonstrates several important Java and database concepts:

### Java / OOP

* Classes and Objects
* Encapsulation
* Constructors
* Methods
* Getters and Setters
* Conditional Statements
* Loops
* Exception Handling
* `Scanner` for user input

### Database

* MySQL
* SQL Queries
* CRUD Operations
* JDBC Connection
* `PreparedStatement`
* `ResultSet`
* Database-driven application design

## 📂 Project Structure

```text
Hospital-Management-System/
│
├── src/
│   └── Hospitalmanagementsystem/
│       ├── HospitalManagementSystem.java
│       ├── Patient.java
│       ├── Doctor.java
│       ├── BookAppointment.java
│       └── DatabaseConnection.java
│
├── .gitignore
└── README.md
```

## 📄 Main Classes

### 👤 Patient.java

The `Patient` class represents patient information and provides functionality for managing patient records.

### 👨‍⚕️ Doctor.java

The `Doctor` class represents doctors and contains information related to doctors available in the hospital.

### 📅 BookAppointment.java

The appointment functionality manages the process of booking an appointment between a patient and a doctor.

### 🔌 DatabaseConnection.java

The `DatabaseConnection` class is responsible for establishing the connection between the Java application and the MySQL database using JDBC.

### 🖥️ HospitalManagementSystem.java

This is the main class of the application. It provides the menu-driven console interface and allows users to perform different hospital management operations.

## 🗄️ Database

The application uses **MySQL** as the database.

Example database:

```sql
CREATE DATABASE hospital;
```

The Java application connects to the database using JDBC:

```text
jdbc:mysql://localhost:3306/hospital
```

Make sure MySQL Server is running before starting the application.

## ▶️ How to Run

### 1. Clone the Repository

```bash
git clone https://github.com/kekanronit/Hospital-Management-System.git
```

### 2. Open the Project

Open the project in **IntelliJ IDEA** or another Java IDE.

### 3. Configure MySQL

Create the required database in MySQL:

```sql
CREATE DATABASE hospital;
```

Create the required tables according to the SQL queries used by the application.

### 4. Configure JDBC

Update the database connection details in the Java database connection class:

```text
URL      : jdbc:mysql://localhost:3306/hospital
Username : your_mysql_username
Password : your_mysql_password
```

### 5. Add MySQL Connector

Make sure **MySQL Connector/J** is added to the project classpath.

### 6. Run the Application

Run:

```text
HospitalManagementSystem.java
```

The application will start in the console.

## 💻 Sample Menu

```text
===== HOSPITAL MANAGEMENT SYSTEM =====

1. Add Patient
2. View Patients
3. View Doctors
4. Book Appointment
5. Exit

Enter your choice:
```

## 🔮 Future Improvements

The current version is a basic console-based hospital management application. Possible improvements include:

* Add patient search functionality
* Add patient update and delete operations
* Add doctor registration
* Add doctor availability management
* Add appointment cancellation
* Add appointment history
* Add user authentication
* Add input validation
* Improve exception handling
* Add a graphical user interface
* Convert the backend into a **Spring Boot REST API**
* Develop a **React.js frontend**
* Add role-based access for Admin, Doctor, and Receptionist

## 🎯 Learning Objectives

This project helped in developing practical knowledge of:

* Core Java
* Object-Oriented Programming
* JDBC
* MySQL
* SQL
* CRUD operations
* Database connectivity
* Exception handling
* Console-based application development
* Git and GitHub

## 👨‍💻 Author

**Ronit Kekan**

GitHub: [@kekanronit](https://github.com/kekanronit)

---

⭐ **If you find this project useful, consider giving the repository a star!**

**Built with Java ☕ and MySQL 🗄️**

# 🎓 Class Enrollment Management System

## 🧠 Project Description

The Class Enrollment Management System is a web-based application developed with Spring Boot, designed to manage the enrollment process between students, lecturers, and administrators.
This system is conceptually inspired by the 1USU university portal, aiming to provide similar functionalities for managing courses, classes, and user roles. You can explore the 1USU portal here: 1USU Website

It leverages Spring Framework, including Spring Data JPA, Spring Security, and Thymeleaf, with Neon PostgreSQL Cloud as the primary database.

This project demonstrates Object-Oriented Programming (OOP) and the Model-View-Controller (MVC) architecture in developing enterprise-level Java applications, showcasing how to build a secure, modular, and interactive web application.

Credit: This project references the design and workflow concepts from the official 1USU portal
.

## 🎯 Project Goals

1. Build a secure, efficient, and modular web-based class management system.

2. Use Spring Boot for rapid and structured enterprise application development.

3. Implement Spring Security for login and role-based access control.

4. Integrate Spring Data JPA with Neon PostgreSQL Cloud for persistent and scalable data storage.

5. Utilize Thymeleaf to create an interactive web interface.

## 👥 System Roles

1. Admin –> Manages users (lecturers, students), courses, and class schedules.

2. Lecturer –> Teaches courses and views the classes they are assigned to.

3. Student –> Enrolls in courses and views the classes they have registered for.

## ⚙️ Key Features

1. Secure Login: Spring Security with BCrypt password encryption.

2. Student Management: Add, view, and enroll in courses.

3. Lecturer Management: Assign and manage multiple courses.

4. Course & Class Management: Admin manages courses and classes.

5. System Rule Validation: Each class can have only one assigned lecturer.

6. Interactive Web Interface: Built using Thymeleaf + Bootstrap.

7. Cloud Database: Data stored in Neon PostgreSQL Cloud.

## 🧰 Tech Stack

1. Backend Framework: Spring Boot

2. Web Framework: Spring Web (MVC)

3. Database: PostgreSQL (Neon Cloud Database)

4. ORM: Spring Data JPA (Hibernate ORM)

5. Security: Spring Security (BCrypt Password Encoder)

6. Template Engine: Thymeleaf

7. Validation: Jakarta Validation / Hibernate Validator

8. Build Tool: Maven

9. Language: Java 17

## 🧩 Main Entities

1. User → Represents system users (username, password, role).

2. Student → Inherits from User, can enroll in multiple courses.

3. Lecturer → Inherits from User, can teach multiple courses.

4. Course → Stores course data (code, name, credit units).

5. Class → Represents the relation between Course, Lecturer, and Student.

## 🚀 Installation & Running the Project

1. Clone the repository:

git clone https://github.com/yourusername/class-enrollment-system.git


2. Open the project in your IDE (IntelliJ IDEA / VSCode).

Make sure you have installed:

Java JDK 17

Maven

3. Create the connection string for backend setup:

<img width="1713" height="728" alt="image" src="https://github.com/user-attachments/assets/f8cd53f4-1eae-4285-a53a-7541c59bd6bf" />


Run the application:

mvn spring-boot:run


Access the application via browser:
http://localhost:8080

## 🔐 Login & Security

1. Spring Security is used for authentication.

2. Passwords are encrypted with BCrypt.

3. Role-based access:

/admin/** → Admin only

/lecturer/** → Lecturer only

/student/** → Student only

## 🧪 Database

1. Neon PostgreSQL Cloud is used (not H2).

2. Persistent, cloud-based, and accessible remotely.

3. Can be managed via Neon dashboard or PostgreSQL client.

## 🎨 UI/UX Design

Figma project design & workflow: [Link Figma: https://www.figma.com/design/O4AJ5mEWNICCF81bdl4u8W/Untitled?node-id=0-1&t=DLWsbd3HPCdMXN6f-1]

## 🎥 YouTube Links

Project Explanation / Overview: [https://youtu.be/xQ9TPusshOE?si=qlIRwDexxSCs7L9l]

Project Demonstration: [Insert Demonstration Video Link Here]

## 🧾 Conclusion

This project demonstrates a full-stack Spring Boot application for class enrollment management with:

1. MVC Architecture

2. Spring Security for authentication & authorization

3. Spring Data JPA + PostgreSQL Cloud (Neon)

4. Interactive web interface using Thymeleaf

5. It serves as an educational reference for learning Spring Boot and a foundation for building scalable, modern web-based academic management systems.

**Credit: Inspired by SATUUSU Portal (https://satu.usu.ac.id)**

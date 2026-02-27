# VacciTrack
A robust Java-based desktop application developed by Sapphire for the MJ-IRP. It securely manages large-scale vaccination programs by tracking vaccine inventory, validating citizen eligibility, and recording dose schedules using Swing and a MySQL backend.


# Vaccination Drive Management System 💉

## Overview
Large-scale vaccination programs require accurate dose scheduling, secure record management, and strict vaccine storage monitoring. Manual systems are prone to missed doses, duplicate records, and inventory mismanagement. 

This project provides a centralized, digital desktop solution to ensure safe, timely, and verifiable immunization management. It was developed as part of the MJ-Industry Ready Program (MJ-IRP) by the Sapphire collective.

## Key Features
* **Dashboard Analytics:** Real-time SQL-based summaries of total vaccines, registered citizens, and administered doses.
* **Inventory Management:** Track vaccine batches, manufacturers, and expiry dates to prevent supply issues.
* **Citizen Registration:** Securely log demographic data and validate Aadhar identification.
* **Dose Administration:** Transaction-safe database operations that link citizens to specific vaccine batches while automatically decrementing available stock.
* **Robust Validation:** Strict input checks to prevent SQL injection and invalid data entry.

## Tech Stack
* **Frontend:** Java AWT/Swing
* **Backend:** Core Java
* **Database:** MySQL
* **Integration:** Java Database Connectivity (JDBC)
* **Architecture:** Model-View-Controller (MVC) Pattern

## Architecture Overview
The application follows a modular MVC architecture:
* **Models:** Plain Java Objects (`Vaccine`, `Citizen`) representing database entities.
* **Views:** Swing UI components (`DashboardPanel`, `InventoryPanel`) separated from business logic.
* **Controllers:** ActionListeners handling events, input validation, and secure `PreparedStatement` database executions.

## Setup & Installation

### Prerequisites
* Java Development Kit (JDK) 8 or higher
* MySQL Server installed and running
* An IDE (Eclipse, IntelliJ IDEA, or VS Code)

### Database Configuration
1. Open your MySQL client.
2. Run the provided SQL schema script (located in the `/database` folder) to create the `VaccinationDriveDB` database and necessary tables.
3. Open `DBConnection.java` in the project.
4. Update the database credentials:
   ```java
   private static final String DB_USER = "your_mysql_username";
   private static final String DB_PASS = "your_mysql_password";
   ```

### Running the Application
1. Clone the repository: git clone https://github.com/yourusername/vaccination-drive-system.git
2. Open the project in your preferred IDE.
3. Ensure the MySQL JDBC Connector (mysql-connector-java.jar) is added to your project's build path/dependencies.
4. Run the VaccinationDriveSystem.java file to launch the application.

### Development Team
Built by Sapphire.

Project Lead: Shaik Zayed Saleem
Designated Partners: Roushna Khatoon, Syeda Aneesa Sultana

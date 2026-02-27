# Model-View-Controller (MVC) Architecture

MVC is a design pattern used to separate an application into three interconnected main logical components. This separation organizes code, making it easier to scale, test, and maintain.

## 1. The Model (Data & Logic)
The Model represents the data and the rules that govern access to and updates of this data. It knows nothing about the user interface.
* **Entities (POJOs):** Plain Java Objects like `Vaccine.java` or `Citizen.java`. They hold private variables (id, name, stock) and public getters/setters.
* **Data Access Objects (DAO):** Classes like `VaccineDAO.java`. They contain the SQL queries and JDBC logic to talk to MySQL. 
* **Rule:** If you change the database from MySQL to PostgreSQL, you only change the DAO, not the GUI.

## 2. The View (User Interface)
The View is the visual representation of the data. It displays information to the user and captures their input.
* **Components:** `JFrame`, `JPanel`, `JButton`, `JTable`.
* **Files:** `InventoryPanel.java`, `DashboardPanel.java`.
* **Rule:** The View should be "dumb". It should not contain complex math or database connection strings. It just shows what it is told to show and passes user clicks to the Controller.

## 3. The Controller (The Brain)
The Controller acts as the middleman. It listens to the View, processes the rules, and updates the Model.
* **Event Listeners:** `ActionListener`, `MouseListener`.
* **Action Flow:**
    1. User clicks "Save" in the View.
    2. Controller catches the click.
    3. Controller validates the input (e.g., checks if age is > 0).
    4. Controller passes the valid data to the Model (DAO) to be saved.
    5. Controller tells the View to refresh the table.

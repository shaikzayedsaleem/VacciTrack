The system follows a strict Model-View-Controller pattern to decouple the Swing UI from the JDBC logic.

Model (Data Layer)
Vaccine.java: Entity for vaccine details (Name, Batch, Expiry, Quantity).

Citizen.java: Entity for citizen details (Aadhar, Age, Phone).

VaccinationRecord.java: Links a Citizen to a Vaccine batch with a timestamp.

DatabaseManager.java: Handles JDBC connections and CRUD operations.

View (UI Layer)
MainDashboard.java: Extends JFrame, implements the JTabbedPane shown in your flowchart.

InventoryPanel.java: Contains the JTable for stock and "Add Vaccine" forms.

CitizenPanel.java: Contains registration forms and the vaccination logging interface.

Controller (Logic Layer)
SystemController.java: The glue. It listens for button clicks in the View, validates input (e.g., checking if age > 0), calls the Model to save to the DB, and then triggers a UI refresh.

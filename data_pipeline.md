Input: User fills JTextField or JComboBox.

Validation: Regex checks for Aadhar (12 digits) and Age (Numeric).

Persistence: Controller calls the DAO (Data Access Object).

Transaction:

Java
connection.setAutoCommit(false);
// 1. Insert Vaccination Record
// 2. Update Vaccine Inventory (Decrement)
connection.commit();
Feedback: JOptionPane.showMessageDialog confirms success and triggers a JTable.repaint().

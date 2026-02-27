1. Input: User fills JTextField or JComboBox.
2. Validation: Regex checks for Aadhar (12 digits) and Age (Numeric).
3. Persistence: Controller calls the DAO (Data Access Object).
4. Transaction:

Java
connection.setAutoCommit(false);
// 1. Insert Vaccination Record
// 2. Update Vaccine Inventory (Decrement)
connection.commit();

5. Feedback: JOptionPane.showMessageDialog confirms success and triggers a JTable.repaint().

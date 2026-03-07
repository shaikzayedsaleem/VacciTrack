package com.vaccination.ui;

import com.vaccination.dao.UserDAO;
import com.vaccination.model.User;
import com.vaccination.ui.components.UITheme;

import javax.swing.*;
import java.awt.*;

public class SignUpFrame extends JDialog {

    private final UserDAO userDAO = new UserDAO();
    private JTextField tfName, tfUsername, tfEmail, tfPhone;
    private JPasswordField tfPass, tfConfirm;
    private JComboBox<String> cbRole;
    private JLabel lblError;

    public SignUpFrame(JFrame parent) {
        super(parent, "Create New Account", true);
        setSize(500, 550);
        setLocationRelativeTo(parent);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        JLabel title = UITheme.label("Create Account", new Font("Segoe UI", Font.BOLD, 20), Color.WHITE);
        JLabel sub = UITheme.label("VaxDrive Staff Registration", UITheme.FONT_SMALL, new Color(0xAA, 0xCC, 0xFF));
        JPanel headerText = new JPanel(new GridLayout(2, 1));
        headerText.setOpaque(false);
        headerText.add(title); headerText.add(sub);
        header.add(headerText);
        root.add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(5, 0, 5, 0);
        gc.weightx = 1.0;

        tfName     = UITheme.styledField();
        tfUsername = UITheme.styledField();
        tfEmail    = UITheme.styledField();
        tfPhone    = UITheme.styledField();
        tfPass     = UITheme.styledPass();
        tfConfirm  = UITheme.styledPass();
        cbRole     = new JComboBox<>(new String[]{"STAFF", "ADMIN"});
        cbRole.setFont(UITheme.FONT_BODY);

        addRow(form, gc, 0, "Full Name *", tfName);
        addRow(form, gc, 1, "Username *", tfUsername);
        addRow(form, gc, 2, "Email", tfEmail);
        addRow(form, gc, 3, "Phone", tfPhone);
        addRow(form, gc, 4, "Password *", tfPass);
        addRow(form, gc, 5, "Confirm Password *", tfConfirm);
        addRow(form, gc, 6, "Role", cbRole);

        lblError = UITheme.label("", UITheme.FONT_SMALL, UITheme.DANGER);
        gc.gridy = 7; gc.gridwidth = 2;
        form.add(lblError, gc);

        root.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        btnPanel.setBackground(Color.WHITE);
        JButton btnCancel = UITheme.outlineButton("Cancel");
        JButton btnRegister = UITheme.primaryButton("Register");
        btnCancel.addActionListener(e -> dispose());
        btnRegister.addActionListener(e -> doRegister());
        btnPanel.add(btnCancel);
        btnPanel.add(btnRegister);
        root.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void addRow(JPanel p, GridBagConstraints gc, int row, String label, JComponent field) {
        gc.gridwidth = 1; gc.gridy = row;
        gc.gridx = 0; gc.weightx = 0.3;
        p.add(UITheme.label(label, UITheme.FONT_BODY, UITheme.TEXT_DARK), gc);
        gc.gridx = 1; gc.weightx = 0.7;
        p.add(field, gc);
    }

    private void doRegister() {
        String name     = tfName.getText().trim();
        String username = tfUsername.getText().trim();
        String email    = tfEmail.getText().trim();
        String phone    = tfPhone.getText().trim();
        String pass     = new String(tfPass.getPassword());
        String confirm  = new String(tfConfirm.getPassword());
        String role     = (String) cbRole.getSelectedItem();

        // Validation
        if (name.isEmpty() || username.isEmpty() || pass.isEmpty()) {
            lblError.setText("Name, username, and password are required."); return;
        }
        if (!pass.equals(confirm)) {
            lblError.setText("Passwords do not match."); return;
        }
        if (pass.length() < 6) {
            lblError.setText("Password must be at least 6 characters."); return;
        }
        if (!username.matches("[a-zA-Z0-9_]{3,20}")) {
            lblError.setText("Username: 3–20 chars, letters/numbers/underscore only."); return;
        }
        if (!email.isEmpty() && !email.matches("^[\\w.+-]+@[\\w-]+\\.[a-z]{2,}$")) {
            lblError.setText("Invalid email format."); return;
        }
        if (!phone.isEmpty() && !phone.matches("\\d{10,15}")) {
            lblError.setText("Phone must be 10–15 digits."); return;
        }
        if (userDAO.usernameExists(username)) {
            lblError.setText("Username already taken."); return;
        }

        User user = new User();
        user.setFullName(name); user.setUsername(username);
        user.setPassword(pass); user.setEmail(email);
        user.setPhone(phone); user.setRole(role);

        if (userDAO.register(user)) {
            JOptionPane.showMessageDialog(this, "Account created successfully!\nYou can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            lblError.setText("Registration failed. Please try again.");
        }
    }
}

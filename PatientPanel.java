package com.vaccination.ui.panels;

import com.vaccination.dao.PatientDAO;
import com.vaccination.model.Patient;
import com.vaccination.model.User;
import com.vaccination.ui.components.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class PatientPanel extends JPanel {

    private final User user;
    private final PatientDAO dao = new PatientDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField tfSearch;

    public PatientPanel(User user) {
        this.user = user;
        setOpaque(false);
        setLayout(new BorderLayout(0, 15));
        buildUI();
        loadData(dao.getAll());
    }

    private void buildUI() {
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel title = UITheme.label("🧑‍⚕️ Patient Registry", UITheme.FONT_TITLE, UITheme.PRIMARY);
        JLabel sub   = UITheme.label("Register and manage patient records", UITheme.FONT_BODY, UITheme.TEXT_LIGHT);
        JPanel tt = new JPanel(new GridLayout(2,1)); tt.setOpaque(false); tt.add(title); tt.add(sub);
        titleRow.add(tt, BorderLayout.WEST);
        add(titleRow, BorderLayout.NORTH);

        // Toolbar
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setOpaque(false);

        tfSearch = UITheme.styledField();
        tfSearch.setPreferredSize(new Dimension(280, 36));
        tfSearch.setToolTipText("Search by name, phone, or ID number");
        JButton btnSearch = UITheme.outlineButton("Search");
        btnSearch.setPreferredSize(new Dimension(90, 36));
        JPanel sp = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); sp.setOpaque(false);
        sp.add(tfSearch); sp.add(btnSearch);
        toolbar.add(sp, BorderLayout.WEST);

        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); bp.setOpaque(false);
        JButton btnAdd    = UITheme.primaryButton("➕ Add Patient");
        JButton btnEdit   = UITheme.outlineButton("✏️ Edit");
        JButton btnDelete = UITheme.dangerButton("🗑️ Delete");
        JButton btnRefresh= UITheme.outlineButton("🔄 All");
        btnAdd.setPreferredSize(new Dimension(140, 36));
        btnEdit.setPreferredSize(new Dimension(95, 36));
        btnDelete.setPreferredSize(new Dimension(100, 36));
        btnRefresh.setPreferredSize(new Dimension(80, 36));
        bp.add(btnRefresh); bp.add(btnEdit); bp.add(btnDelete); bp.add(btnAdd);
        toolbar.add(bp, BorderLayout.EAST);

        String[] cols = {"ID","Full Name","DOB","Age","Gender","Phone","Email","ID Number","Registered"};
        tableModel = new DefaultTableModel(cols, 0);
        table = UITheme.styledTable(tableModel);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(3).setMaxWidth(50);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new UITheme.RoundBorder(new Color(0xDD,0xDD,0xDD), 10, 1));
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel card = UITheme.card("");
        card.setLayout(new BorderLayout(0, 10));
        card.add(toolbar, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> openForm(null));
        btnEdit.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) { alert("Select a patient."); return; }
            int id = (int) tableModel.getValueAt(table.convertRowIndexToModel(r), 0);
            openForm(dao.getById(id));
        });
        btnDelete.addActionListener(e -> deleteSelected());
        btnRefresh.addActionListener(e -> loadData(dao.getAll()));
        btnSearch.addActionListener(e -> loadData(dao.search(tfSearch.getText().trim())));
        tfSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) loadData(dao.search(tfSearch.getText().trim()));
            }
        });
    }

    private void loadData(List<Patient> list) {
        tableModel.setRowCount(0);
        for (Patient p : list) {
            tableModel.addRow(new Object[]{
                p.getPatientId(), p.getFullName(), p.getDateOfBirth(), p.getAge(),
                p.getGender(), p.getPhone(), p.getEmail(), p.getIdNumber(), p.getAge() + " yrs"
            });
        }
    }

    private void openForm(Patient patient) {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            patient == null ? "Register Patient" : "Edit Patient", true);
        dlg.setSize(480, 540);
        dlg.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL; gc.insets = new Insets(6, 5, 6, 5); gc.weightx = 1;

        JTextField fName   = UITheme.styledField();
        JTextField fDob    = UITheme.styledField(); fDob.setToolTipText("YYYY-MM-DD");
        JTextField fAge    = UITheme.styledField();
        JComboBox<String> cbGender = new JComboBox<>(new String[]{"Male","Female","Other"});
        cbGender.setFont(UITheme.FONT_BODY);
        JTextField fPhone  = UITheme.styledField();
        JTextField fEmail  = UITheme.styledField();
        JTextField fIdNum  = UITheme.styledField();
        JTextArea  fAddr   = new JTextArea(3, 20);
        fAddr.setFont(UITheme.FONT_BODY);
        fAddr.setBorder(BorderFactory.createCompoundBorder(
            new UITheme.RoundBorder(new Color(0xCC,0xCC,0xCC),8,1),
            BorderFactory.createEmptyBorder(5,10,5,10)));

        if (patient != null) {
            fName.setText(patient.getFullName());
            if (patient.getDateOfBirth() != null) fDob.setText(patient.getDateOfBirth().toString());
            fAge.setText(String.valueOf(patient.getAge()));
            cbGender.setSelectedItem(patient.getGender());
            fPhone.setText(patient.getPhone()); fEmail.setText(patient.getEmail());
            fIdNum.setText(patient.getIdNumber()); fAddr.setText(patient.getAddress());
        }

        Object[][] rows = {{"Full Name *",fName},{"Date of Birth (YYYY-MM-DD)",fDob},{"Age *",fAge},
            {"Gender",cbGender},{"Phone *",fPhone},{"Email",fEmail},{"Aadhaar/ID Number",fIdNum},{"Address",new JScrollPane(fAddr)}};

        for (int i = 0; i < rows.length; i++) {
            gc.gridy=i; gc.gridx=0; gc.weightx=0.35;
            form.add(UITheme.label((String)rows[i][0], UITheme.FONT_BODY, UITheme.TEXT_DARK), gc);
            gc.gridx=1; gc.weightx=0.65;
            form.add((JComponent)rows[i][1], gc);
        }

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btns.setBackground(Color.WHITE);
        JButton btnCancel = UITheme.outlineButton("Cancel");
        JButton btnSave   = UITheme.primaryButton("Save");
        btns.add(btnCancel); btns.add(btnSave);

        dlg.setLayout(new BorderLayout());
        dlg.add(new JScrollPane(form), BorderLayout.CENTER);
        dlg.add(btns, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dlg.dispose());
        btnSave.addActionListener(e -> {
            String name  = fName.getText().trim();
            String ageStr= fAge.getText().trim();
            String phone = fPhone.getText().trim();

            // Validation
            if (name.isEmpty()) { showErr(dlg, "Full Name is required."); return; }
            if (ageStr.isEmpty()) { showErr(dlg, "Age is required."); return; }
            if (phone.isEmpty()) { showErr(dlg, "Phone is required."); return; }
            if (!ageStr.matches("\\d+") || Integer.parseInt(ageStr) < 0 || Integer.parseInt(ageStr) > 120) {
                showErr(dlg, "Enter a valid age (0–120)."); return; }
            if (!phone.matches("\\d{10,15}")) { showErr(dlg, "Phone must be 10–15 digits."); return; }

            try {
                Patient p = patient == null ? new Patient() : patient;
                p.setFullName(name);
                if (!fDob.getText().trim().isEmpty()) p.setDateOfBirth(Date.valueOf(fDob.getText().trim()));
                p.setAge(Integer.parseInt(ageStr));
                p.setGender((String) cbGender.getSelectedItem());
                p.setPhone(phone); p.setEmail(fEmail.getText().trim());
                p.setIdNumber(fIdNum.getText().trim()); p.setAddress(fAddr.getText().trim());

                boolean ok;
                if (patient == null) ok = dao.add(p) > 0;
                else ok = dao.update(p);

                if (ok) { dlg.dispose(); loadData(dao.getAll()); alert("Patient saved successfully."); }
                else alert("Save failed – ID number may already exist.");
            } catch (Exception ex) {
                showErr(dlg, "Invalid data: " + ex.getMessage());
            }
        });

        dlg.setVisible(true);
    }

    private void deleteSelected() {
        int r = table.getSelectedRow();
        if (r < 0) { alert("Select a patient."); return; }
        int id = (int) tableModel.getValueAt(table.convertRowIndexToModel(r), 0);
        String name = (String) tableModel.getValueAt(table.convertRowIndexToModel(r), 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete patient \"" + name + "\" and all their records?",
            "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.delete(id)) { loadData(dao.getAll()); alert("Patient deleted."); }
            else alert("Delete failed.");
        }
    }

    private void alert(String msg) { JOptionPane.showMessageDialog(this, msg, "Patients", JOptionPane.INFORMATION_MESSAGE); }
    private void showErr(JDialog d, String msg) { JOptionPane.showMessageDialog(d, msg, "Validation", JOptionPane.WARNING_MESSAGE); }
}

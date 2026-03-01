package com.vaccination.ui.panels;

import com.vaccination.dao.*;
import com.vaccination.model.*;
import com.vaccination.ui.components.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class AppointmentPanel extends JPanel {

    private final User user;
    private final AppointmentDAO dao     = new AppointmentDAO();
    private final PatientDAO patientDAO  = new PatientDAO();
    private final VaccineDAO vaccineDAO  = new VaccineDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbFilter;

    public AppointmentPanel(User user) {
        this.user = user;
        setOpaque(false);
        setLayout(new BorderLayout(0, 15));
        buildUI();
        loadData("ALL");
    }

    private void buildUI() {
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel title = UITheme.label("📅 Appointments", UITheme.FONT_TITLE, UITheme.PRIMARY);
        JLabel sub   = UITheme.label("Schedule and manage vaccination appointments", UITheme.FONT_BODY, UITheme.TEXT_LIGHT);
        JPanel tt = new JPanel(new GridLayout(2,1)); tt.setOpaque(false); tt.add(title); tt.add(sub);
        titleRow.add(tt, BorderLayout.WEST);
        add(titleRow, BorderLayout.NORTH);

        // Toolbar
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        left.add(UITheme.label("Filter:", UITheme.FONT_BODY, UITheme.TEXT_DARK));
        cbFilter = new JComboBox<>(new String[]{"ALL","SCHEDULED","COMPLETED","CANCELLED","MISSED","TODAY"});
        cbFilter.setFont(UITheme.FONT_BODY);
        cbFilter.setPreferredSize(new Dimension(140, 36));
        left.add(cbFilter);
        toolbar.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        JButton btnNew      = UITheme.primaryButton("➕ Book");
        JButton btnComplete = UITheme.primaryButton("✅ Complete");
        JButton btnCancel2  = UITheme.dangerButton("❌ Cancel");
        JButton btnRefresh  = UITheme.outlineButton("🔄");
        btnNew.setPreferredSize(new Dimension(110, 36));
        btnComplete.setPreferredSize(new Dimension(130, 36));
        btnCancel2.setPreferredSize(new Dimension(110, 36));
        btnRefresh.setPreferredSize(new Dimension(50, 36));
        right.add(btnRefresh); right.add(btnCancel2); right.add(btnComplete); right.add(btnNew);
        toolbar.add(right, BorderLayout.EAST);

        String[] cols = {"ID","Patient","Vaccine","Dose #","Date","Time","Status","Notes"};
        tableModel = new DefaultTableModel(cols, 0);
        table = UITheme.styledTable(tableModel);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(3).setMaxWidth(60);
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : UITheme.TABLE_ALT);
                    if (col == 6 && v != null) {
                        switch (v.toString()) {
                            case "COMPLETED": c.setForeground(UITheme.SECONDARY); break;
                            case "CANCELLED": c.setForeground(UITheme.DANGER); break;
                            case "MISSED":    c.setForeground(UITheme.ACCENT); break;
                            default:          c.setForeground(UITheme.PRIMARY);
                        }
                    } else c.setForeground(UITheme.TEXT_DARK);
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new UITheme.RoundBorder(new Color(0xDD,0xDD,0xDD), 10, 1));
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel card = UITheme.card("");
        card.setLayout(new BorderLayout(0, 10));
        card.add(toolbar, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        cbFilter.addActionListener(e -> loadData((String) cbFilter.getSelectedItem()));
        btnNew.addActionListener(e -> openBookingForm());
        btnComplete.addActionListener(e -> completeSelected());
        btnCancel2.addActionListener(e -> cancelSelected());
        btnRefresh.addActionListener(e -> loadData((String) cbFilter.getSelectedItem()));
    }

    private void loadData(String filter) {
        tableModel.setRowCount(0);
        List<Appointment> list;
        if ("TODAY".equals(filter)) list = dao.getToday();
        else if ("ALL".equals(filter)) list = dao.getAll();
        else list = dao.getByStatus(filter);
        for (Appointment a : list) {
            tableModel.addRow(new Object[]{
                a.getAppointmentId(), a.getPatientName(), a.getVaccineName(),
                a.getDoseNumber(), a.getScheduledDate(), a.getScheduledTime(),
                a.getStatus(), a.getNotes()
            });
        }
    }

    private void openBookingForm() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Book Appointment", true);
        dlg.setSize(520, 500);
        dlg.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL; gc.insets = new Insets(7, 5, 7, 5); gc.weightx = 1;

        // Patient combo
        List<Patient> patients = patientDAO.getAll();
        DefaultComboBoxModel<String> patModel = new DefaultComboBoxModel<>();
        int[] patIds = new int[patients.size()];
        for (int i = 0; i < patients.size(); i++) {
            Patient p = patients.get(i);
            patModel.addElement(p.getPatientId() + " – " + p.getFullName() + " (Age: " + p.getAge() + ")");
            patIds[i] = p.getPatientId();
        }
        JComboBox<String> cbPatient = new JComboBox<>(patModel);
        cbPatient.setFont(UITheme.FONT_BODY);

        // Vaccine combo
        List<Vaccine> vaccines = vaccineDAO.getAll();
        DefaultComboBoxModel<String> vacModel = new DefaultComboBoxModel<>();
        int[] vacIds = new int[vaccines.size()];
        for (int i = 0; i < vaccines.size(); i++) {
            Vaccine v = vaccines.get(i);
            vacModel.addElement(v.getVaccineId() + " – " + v.getVaccineName() + " (Stock: " + v.getQuantityAvailable() + ")");
            vacIds[i] = v.getVaccineId();
        }
        JComboBox<String> cbVaccine = new JComboBox<>(vacModel);
        cbVaccine.setFont(UITheme.FONT_BODY);

        JTextField fDose  = UITheme.styledField(); fDose.setText("1");
        JTextField fDate  = UITheme.styledField(); fDate.setToolTipText("YYYY-MM-DD");
        JTextField fTime  = UITheme.styledField(); fTime.setText("09:00");
        JTextArea  fNotes = new JTextArea(2, 20); fNotes.setFont(UITheme.FONT_BODY);
        fNotes.setBorder(BorderFactory.createCompoundBorder(new UITheme.RoundBorder(new Color(0xCC,0xCC,0xCC),8,1), BorderFactory.createEmptyBorder(5,10,5,10)));

        JLabel lblElig = UITheme.label("", UITheme.FONT_SMALL, UITheme.SECONDARY);
        JButton btnCheck = UITheme.outlineButton("Check Eligibility");
        btnCheck.setPreferredSize(new Dimension(160, 32));
        btnCheck.addActionListener(e -> {
            int pi = cbPatient.getSelectedIndex();
            int vi = cbVaccine.getSelectedIndex();
            if (pi < 0 || vi < 0) return;
            String result = patientDAO.validateEligibility(patIds[pi], vacIds[vi]);
            if ("ELIGIBLE".equals(result)) {
                lblElig.setText("✅ Patient is ELIGIBLE for this vaccine");
                lblElig.setForeground(UITheme.SECONDARY);
            } else {
                lblElig.setText("❌ " + result);
                lblElig.setForeground(UITheme.DANGER);
            }
        });

        Object[][] rows = {{"Patient *", cbPatient},{"Vaccine *", cbVaccine},{"Dose Number *", fDose},
            {"Date (YYYY-MM-DD) *", fDate},{"Time (HH:MM)", fTime},{"Notes", new JScrollPane(fNotes)},
            {"Eligibility Check", btnCheck},{"", lblElig}};

        for (int i = 0; i < rows.length; i++) {
            gc.gridy=i; gc.gridx=0; gc.weightx=0.35;
            form.add(UITheme.label((String)rows[i][0], UITheme.FONT_BODY, UITheme.TEXT_DARK), gc);
            gc.gridx=1; gc.weightx=0.65;
            form.add((JComponent)rows[i][1], gc);
        }

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btns.setBackground(Color.WHITE);
        JButton btnCancel = UITheme.outlineButton("Cancel");
        JButton btnSave   = UITheme.primaryButton("Book");
        btnSave.setPreferredSize(new Dimension(120, 36));
        btns.add(btnCancel); btns.add(btnSave);

        dlg.setLayout(new BorderLayout());
        dlg.add(new JScrollPane(form), BorderLayout.CENTER);
        dlg.add(btns, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dlg.dispose());
        btnSave.addActionListener(e -> {
            if (cbPatient.getSelectedIndex() < 0 || cbVaccine.getSelectedIndex() < 0 || fDate.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Patient, vaccine, and date are required.", "Validation", JOptionPane.WARNING_MESSAGE); return;
            }
            try {
                int pi = cbPatient.getSelectedIndex();
                int vi = cbVaccine.getSelectedIndex();

                // Auto-validate eligibility
                String elig = patientDAO.validateEligibility(patIds[pi], vacIds[vi]);
                if (!"ELIGIBLE".equals(elig)) {
                    int proceed = JOptionPane.showConfirmDialog(dlg, "⚠️ Eligibility Warning:\n" + elig + "\n\nProceed anyway?",
                        "Eligibility Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (proceed != JOptionPane.YES_OPTION) return;
                }

                Appointment a = new Appointment();
                a.setPatientId(patIds[pi]);
                a.setVaccineId(vacIds[vi]);
                a.setDoseNumber(Integer.parseInt(fDose.getText().trim()));
                a.setScheduledDate(Date.valueOf(fDate.getText().trim()));
                a.setScheduledTime(fTime.getText().trim());
                a.setStatus("SCHEDULED");
                a.setAdministeredBy(user.getUserId());
                a.setNotes(fNotes.getText().trim());

                int id = dao.add(a);
                if (id > 0) { dlg.dispose(); loadData((String) cbFilter.getSelectedItem());
                    JOptionPane.showMessageDialog(this, "Appointment booked! ID: " + id, "Success", JOptionPane.INFORMATION_MESSAGE);
                } else JOptionPane.showMessageDialog(dlg, "Booking failed.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.setVisible(true);
    }

    private void completeSelected() {
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this,"Select an appointment.","Info",JOptionPane.INFORMATION_MESSAGE); return; }
        int id     = (int) tableModel.getValueAt(table.convertRowIndexToModel(r), 0);
        String status = (String) tableModel.getValueAt(table.convertRowIndexToModel(r), 6);
        if (!"SCHEDULED".equals(status)) { JOptionPane.showMessageDialog(this,"Only SCHEDULED appointments can be completed.","Info",JOptionPane.WARNING_MESSAGE); return; }

        String batch = JOptionPane.showInputDialog(this, "Enter batch number used:");
        if (batch == null) return;

        boolean ok = dao.complete(id, user.getUserId(), batch, null);
        if (ok) { loadData((String) cbFilter.getSelectedItem());
            JOptionPane.showMessageDialog(this,"Vaccination recorded successfully!\nStock deducted.","Complete",JOptionPane.INFORMATION_MESSAGE);
        } else JOptionPane.showMessageDialog(this,"Failed to complete appointment.","Error",JOptionPane.ERROR_MESSAGE);
    }

    private void cancelSelected() {
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this,"Select an appointment.","Info",JOptionPane.INFORMATION_MESSAGE); return; }
        int id = (int) tableModel.getValueAt(table.convertRowIndexToModel(r), 0);
        int c = JOptionPane.showConfirmDialog(this,"Cancel this appointment?","Confirm",JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            if (dao.updateStatus(id,"CANCELLED")) loadData((String) cbFilter.getSelectedItem());
        }
    }
}

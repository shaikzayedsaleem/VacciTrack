package com.vaccination.ui.panels;

import com.vaccination.dao.*;
import com.vaccination.model.*;
import com.vaccination.ui.components.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class AEFIPanel extends JPanel {

    private final User user;
    private final AEFIDAO dao           = new AEFIDAO();
    private final PatientDAO patientDAO = new PatientDAO();
    private final VaccineDAO vaccineDAO = new VaccineDAO();
    private JTable table;
    private DefaultTableModel tableModel;

    public AEFIPanel(User user) {
        this.user = user;
        setOpaque(false);
        setLayout(new BorderLayout(0, 15));
        buildUI();
        loadData();
    }

    private void buildUI() {
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel title = UITheme.label("⚠️ AEFI Reports", UITheme.FONT_TITLE, UITheme.PRIMARY);
        JLabel sub   = UITheme.label("Adverse Events Following Immunization", UITheme.FONT_BODY, UITheme.TEXT_LIGHT);
        JPanel tt = new JPanel(new GridLayout(2,1)); tt.setOpaque(false); tt.add(title); tt.add(sub);
        titleRow.add(tt, BorderLayout.WEST);
        add(titleRow, BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolbar.setOpaque(false);
        JButton btnReport = UITheme.dangerButton("⚠️ Log AEFI");
        JButton btnRefresh= UITheme.outlineButton("🔄 Refresh");
        btnReport.setPreferredSize(new Dimension(130, 36));
        btnRefresh.setPreferredSize(new Dimension(110, 36));
        toolbar.add(btnRefresh); toolbar.add(btnReport);

        String[] cols = {"ID","Patient","Vaccine","Event Type","Severity","Onset Date","Description","Reported By","Reported At"};
        tableModel = new DefaultTableModel(cols, 0);
        table = UITheme.styledTable(tableModel);
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : UITheme.TABLE_ALT);
                    if (col == 4 && v != null) {
                        switch (v.toString()) {
                            case "SEVERE":   c.setForeground(UITheme.DANGER); break;
                            case "MODERATE": c.setForeground(UITheme.ACCENT); break;
                            default:         c.setForeground(UITheme.SECONDARY);
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

        btnReport.addActionListener(e -> openForm());
        btnRefresh.addActionListener(e -> loadData());
    }

    private void loadData() {
        tableModel.setRowCount(0);
        for (AEFIReport r : dao.getAll()) {
            tableModel.addRow(new Object[]{
                r.getAefiId(), r.getPatientName(), r.getVaccineName(),
                r.getEventType(), r.getSeverity(), r.getOnsetDate(),
                r.getDescription(), r.getReporterName(),
                r.getReportedAt() != null ? r.getReportedAt().toString().substring(0, 16) : ""
            });
        }
    }

    private void openForm() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Log AEFI Report", true);
        dlg.setSize(500, 470);
        dlg.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL; gc.insets = new Insets(7, 5, 7, 5); gc.weightx = 1;

        List<Patient> patients = patientDAO.getAll();
        DefaultComboBoxModel<String> pm = new DefaultComboBoxModel<>();
        int[] patIds = new int[patients.size()];
        for (int i = 0; i < patients.size(); i++) { pm.addElement(patients.get(i).getFullName()); patIds[i] = patients.get(i).getPatientId(); }
        JComboBox<String> cbPat = new JComboBox<>(pm); cbPat.setFont(UITheme.FONT_BODY);

        List<Vaccine> vaccines = vaccineDAO.getAll();
        DefaultComboBoxModel<String> vm = new DefaultComboBoxModel<>();
        int[] vacIds = new int[vaccines.size()];
        for (int i = 0; i < vaccines.size(); i++) { vm.addElement(vaccines.get(i).getVaccineName()); vacIds[i] = vaccines.get(i).getVaccineId(); }
        JComboBox<String> cbVac = new JComboBox<>(vm); cbVac.setFont(UITheme.FONT_BODY);

        JComboBox<String> cbSeverity = new JComboBox<>(new String[]{"MILD","MODERATE","SEVERE"});
        cbSeverity.setFont(UITheme.FONT_BODY);

        String[] eventTypes = {"Fever","Rash","Swelling at injection site","Allergic reaction","Anaphylaxis","Fainting","Vomiting","Other"};
        JComboBox<String> cbEvent = new JComboBox<>(eventTypes); cbEvent.setFont(UITheme.FONT_BODY);

        JTextField fOnset = UITheme.styledField(); fOnset.setToolTipText("YYYY-MM-DD");
        JTextArea fDesc = new JTextArea(3, 20); fDesc.setFont(UITheme.FONT_BODY);
        fDesc.setBorder(BorderFactory.createCompoundBorder(new UITheme.RoundBorder(new Color(0xCC,0xCC,0xCC),8,1), BorderFactory.createEmptyBorder(5,10,5,10)));

        Object[][] rows = {{"Patient *", cbPat},{"Vaccine *", cbVac},{"Event Type *", cbEvent},
            {"Severity *", cbSeverity},{"Onset Date (YYYY-MM-DD) *", fOnset},{"Description", new JScrollPane(fDesc)}};

        for (int i = 0; i < rows.length; i++) {
            gc.gridy=i; gc.gridx=0; gc.weightx=0.4;
            form.add(UITheme.label((String)rows[i][0], UITheme.FONT_BODY, UITheme.TEXT_DARK), gc);
            gc.gridx=1; gc.weightx=0.6;
            form.add((JComponent)rows[i][1], gc);
        }

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btns.setBackground(Color.WHITE);
        JButton btnCancel = UITheme.outlineButton("Cancel");
        JButton btnSave   = UITheme.dangerButton("Log Report");
        btnSave.setPreferredSize(new Dimension(130, 36));
        btns.add(btnCancel); btns.add(btnSave);

        dlg.setLayout(new BorderLayout());
        dlg.add(new JScrollPane(form), BorderLayout.CENTER);
        dlg.add(btns, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dlg.dispose());
        btnSave.addActionListener(e -> {
            if (fOnset.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dlg,"Onset date is required.","Validation",JOptionPane.WARNING_MESSAGE); return;
            }
            try {
                AEFIReport r = new AEFIReport();
                r.setPatientId(patIds[cbPat.getSelectedIndex()]);
                r.setVaccineId(vacIds[cbVac.getSelectedIndex()]);
                r.setEventType((String) cbEvent.getSelectedItem());
                r.setSeverity((String) cbSeverity.getSelectedItem());
                r.setOnsetDate(Date.valueOf(fOnset.getText().trim()));
                r.setDescription(fDesc.getText().trim());
                r.setReportedBy(user.getUserId());

                if (dao.add(r)) { dlg.dispose(); loadData();
                    JOptionPane.showMessageDialog(this,"AEFI Report logged successfully.","Success",JOptionPane.INFORMATION_MESSAGE);
                } else JOptionPane.showMessageDialog(dlg,"Failed to log report.","Error",JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg,"Invalid data: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.setVisible(true);
    }
}

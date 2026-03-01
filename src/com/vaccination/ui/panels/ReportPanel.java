package com.vaccination.ui.panels;

import com.vaccination.db.DatabaseConnection;
import com.vaccination.model.User;
import com.vaccination.ui.components.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class ReportPanel extends JPanel {

    private final User user;
    private JTabbedPane tabs;

    public ReportPanel(User user) {
        this.user = user;
        setOpaque(false);
        setLayout(new BorderLayout(0, 15));
        buildUI();
    }

    private void buildUI() {
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel title = UITheme.label("📊 Reports & Analytics", UITheme.FONT_TITLE, UITheme.PRIMARY);
        JLabel sub   = UITheme.label("SQL-based summary reports and data analytics", UITheme.FONT_BODY, UITheme.TEXT_LIGHT);
        JPanel tt = new JPanel(new GridLayout(2,1)); tt.setOpaque(false); tt.add(title); tt.add(sub);
        titleRow.add(tt, BorderLayout.WEST);
        JButton btnRefresh = UITheme.primaryButton("🔄 Refresh All");
        btnRefresh.setPreferredSize(new Dimension(130, 36));
        titleRow.add(btnRefresh, BorderLayout.EAST);
        add(titleRow, BorderLayout.NORTH);

        tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_BODY);
        tabs.setBackground(Color.WHITE);

        tabs.addTab("📈 Vaccination Summary", buildVaccinationSummary());
        tabs.addTab("💊 Inventory Status",    buildInventoryReport());
        tabs.addTab("🧑‍⚕️ Patient Statistics",  buildPatientStats());
        tabs.addTab("⚠️ AEFI Analysis",        buildAEFIReport());
        tabs.addTab("📅 Appointment Report",   buildAppointmentReport());

        add(tabs, BorderLayout.CENTER);
        btnRefresh.addActionListener(e -> refreshAll());
    }

    private JPanel buildVaccinationSummary() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String sql = "SELECT v.vaccine_name, COUNT(vr.record_id) AS total_doses, " +
                     "SUM(CASE WHEN vr.dose_number=1 THEN 1 ELSE 0 END) AS first_doses, " +
                     "SUM(CASE WHEN vr.dose_number=2 THEN 1 ELSE 0 END) AS second_doses, " +
                     "SUM(CASE WHEN vr.dose_number=3 THEN 1 ELSE 0 END) AS third_doses " +
                     "FROM vaccines v LEFT JOIN vaccination_records vr ON v.vaccine_id = vr.vaccine_id " +
                     "GROUP BY v.vaccine_id, v.vaccine_name ORDER BY total_doses DESC";

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Vaccine","Total Doses","1st Dose","2nd Dose","3rd Dose"}, 0);
        populateTable(model, sql);
        JTable table = UITheme.styledTable(model);

        JPanel statsRow = new JPanel(new GridLayout(1, 3, 15, 0));
        statsRow.setOpaque(false);
        statsRow.setPreferredSize(new Dimension(0, 90));
        statsRow.add(kpiBox("Total Records",  scalarQuery("SELECT COUNT(*) FROM vaccination_records"), UITheme.SECONDARY));
        statsRow.add(kpiBox("Today's Doses",  scalarQuery("SELECT COUNT(*) FROM vaccination_records WHERE administered_date=CURDATE()"), UITheme.PRIMARY));
        statsRow.add(kpiBox("This Month",     scalarQuery("SELECT COUNT(*) FROM vaccination_records WHERE MONTH(administered_date)=MONTH(CURDATE()) AND YEAR(administered_date)=YEAR(CURDATE())"), UITheme.ACCENT));

        p.add(statsRow, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildInventoryReport() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String sql = "SELECT vaccine_name, manufacturer, batch_number, quantity_available, expiry_date, " +
                     "CASE WHEN quantity_available < 50 THEN 'CRITICAL' " +
                     "     WHEN quantity_available < 150 THEN 'LOW' ELSE 'OK' END AS stock_status, " +
                     "CASE WHEN expiry_date <= DATE_ADD(CURDATE(), INTERVAL 30 DAY) THEN 'EXPIRING SOON' " +
                     "     WHEN expiry_date < CURDATE() THEN 'EXPIRED' ELSE 'VALID' END AS expiry_status " +
                     "FROM vaccines ORDER BY quantity_available ASC";

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Vaccine","Manufacturer","Batch No.","Stock","Expiry","Stock Status","Expiry Status"}, 0);
        populateTable(model, sql);
        JTable table = UITheme.styledTable(model);
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : UITheme.TABLE_ALT);
                    c.setForeground(UITheme.TEXT_DARK);
                    if ((col == 5 || col == 6) && v != null) {
                        if (v.toString().contains("CRITICAL") || v.toString().contains("EXPIRING") || v.toString().contains("EXPIRED"))
                            c.setForeground(UITheme.DANGER);
                        else if (v.toString().contains("LOW")) c.setForeground(UITheme.ACCENT);
                        else c.setForeground(UITheme.SECONDARY);
                    }
                }
                return c;
            }
        });

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildPatientStats() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String sql = "SELECT gender, COUNT(*) AS count, " +
                     "AVG(age) AS avg_age, MIN(age) AS min_age, MAX(age) AS max_age " +
                     "FROM patients GROUP BY gender";

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Gender","Count","Avg Age","Min Age","Max Age"}, 0);
        populateTable(model, sql);
        JTable table = UITheme.styledTable(model);

        String sql2 = "SELECT DATE_FORMAT(registered_at, '%Y-%m') AS month, COUNT(*) AS new_patients " +
                      "FROM patients GROUP BY month ORDER BY month DESC LIMIT 12";
        DefaultTableModel model2 = new DefaultTableModel(new String[]{"Month","New Patients"}, 0);
        populateTable(model2, sql2);
        JTable table2 = UITheme.styledTable(model2);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(table), new JScrollPane(table2));
        split.setDividerLocation(350);
        split.setBorder(null);
        p.add(split, BorderLayout.CENTER);

        JPanel kpis = new JPanel(new GridLayout(1, 3, 15, 0));
        kpis.setOpaque(false); kpis.setPreferredSize(new Dimension(0, 90));
        kpis.add(kpiBox("Total Patients",        scalarQuery("SELECT COUNT(*) FROM patients"), UITheme.SECONDARY));
        kpis.add(kpiBox("Registered This Month", scalarQuery("SELECT COUNT(*) FROM patients WHERE MONTH(registered_at)=MONTH(CURDATE())"), UITheme.PRIMARY));
        kpis.add(kpiBox("Vaccinated Patients",   scalarQuery("SELECT COUNT(DISTINCT patient_id) FROM vaccination_records"), UITheme.ACCENT));
        p.add(kpis, BorderLayout.NORTH);
        return p;
    }

    private JPanel buildAEFIReport() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String sql = "SELECT severity, COUNT(*) AS count, " +
                     "GROUP_CONCAT(DISTINCT event_type ORDER BY event_type SEPARATOR ', ') AS event_types " +
                     "FROM aefi_reports GROUP BY severity";

        DefaultTableModel model = new DefaultTableModel(new String[]{"Severity","Count","Event Types"}, 0);
        populateTable(model, sql);
        JTable table1 = UITheme.styledTable(model);

        String sql2 = "SELECT v.vaccine_name, ar.severity, COUNT(*) AS aefi_count " +
                      "FROM aefi_reports ar JOIN vaccines v ON ar.vaccine_id = v.vaccine_id " +
                      "GROUP BY v.vaccine_id, v.vaccine_name, ar.severity ORDER BY aefi_count DESC";
        DefaultTableModel model2 = new DefaultTableModel(new String[]{"Vaccine","Severity","AEFI Count"}, 0);
        populateTable(model2, sql2);
        JTable table2 = UITheme.styledTable(model2);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(table1), new JScrollPane(table2));
        split.setDividerLocation(150); split.setBorder(null);
        p.add(split, BorderLayout.CENTER);

        JPanel kpis = new JPanel(new GridLayout(1, 3, 15, 0));
        kpis.setOpaque(false); kpis.setPreferredSize(new Dimension(0, 90));
        kpis.add(kpiBox("Total AEFI",   scalarQuery("SELECT COUNT(*) FROM aefi_reports"), UITheme.DANGER));
        kpis.add(kpiBox("Severe Cases", scalarQuery("SELECT COUNT(*) FROM aefi_reports WHERE severity='SEVERE'"), UITheme.DANGER));
        kpis.add(kpiBox("This Month",   scalarQuery("SELECT COUNT(*) FROM aefi_reports WHERE MONTH(reported_at)=MONTH(CURDATE())"), UITheme.ACCENT));
        p.add(kpis, BorderLayout.NORTH);
        return p;
    }

    private JPanel buildAppointmentReport() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String sql = "SELECT status, COUNT(*) AS count FROM appointments GROUP BY status";
        DefaultTableModel model = new DefaultTableModel(new String[]{"Status","Count"}, 0);
        populateTable(model, sql);
        JTable t1 = UITheme.styledTable(model);

        String sql2 = "SELECT DATE(scheduled_date) AS date, COUNT(*) AS total, " +
                      "SUM(CASE WHEN status='COMPLETED' THEN 1 ELSE 0 END) AS completed, " +
                      "SUM(CASE WHEN status='CANCELLED' THEN 1 ELSE 0 END) AS cancelled " +
                      "FROM appointments WHERE scheduled_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
                      "GROUP BY DATE(scheduled_date) ORDER BY date DESC";
        DefaultTableModel model2 = new DefaultTableModel(new String[]{"Date","Total","Completed","Cancelled"}, 0);
        populateTable(model2, sql2);
        JTable t2 = UITheme.styledTable(model2);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(t1), new JScrollPane(t2));
        split.setDividerLocation(200); split.setBorder(null);
        p.add(split, BorderLayout.CENTER);

        JPanel kpis = new JPanel(new GridLayout(1, 3, 15, 0));
        kpis.setOpaque(false); kpis.setPreferredSize(new Dimension(0, 90));
        kpis.add(kpiBox("Total",     scalarQuery("SELECT COUNT(*) FROM appointments"), UITheme.PRIMARY));
        kpis.add(kpiBox("Completed", scalarQuery("SELECT COUNT(*) FROM appointments WHERE status='COMPLETED'"), UITheme.SECONDARY));
        kpis.add(kpiBox("Scheduled", scalarQuery("SELECT COUNT(*) FROM appointments WHERE status='SCHEDULED'"), UITheme.ACCENT));
        p.add(kpis, BorderLayout.NORTH);
        return p;
    }

    private void populateTable(DefaultTableModel model, String sql) {
        try (Statement st = DatabaseConnection.getInstance().getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            while (rs.next()) {
                Object[] row = new Object[cols];
                for (int i = 0; i < cols; i++) row[i] = rs.getObject(i + 1);
                model.addRow(row);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private int scalarQuery(String sql) {
        try (Statement st = DatabaseConnection.getInstance().getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private JPanel kpiBox(String label, int value, Color color) {
        JPanel p = UITheme.statCard(String.valueOf(value), label, color);
        JPanel wrap = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-2, getHeight()-2, 12, 12);
                g2.dispose();
            }
        };
        wrap.setOpaque(false); wrap.add(p); return wrap;
    }

    private void refreshAll() {
        tabs.removeAll();
        tabs.addTab("📈 Vaccination Summary", buildVaccinationSummary());
        tabs.addTab("💊 Inventory Status",    buildInventoryReport());
        tabs.addTab("🧑‍⚕️ Patient Statistics",  buildPatientStats());
        tabs.addTab("⚠️ AEFI Analysis",        buildAEFIReport());
        tabs.addTab("📅 Appointment Report",   buildAppointmentReport());
        tabs.revalidate(); tabs.repaint();
    }
}

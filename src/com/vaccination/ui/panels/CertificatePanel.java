package com.vaccination.ui.panels;

import com.vaccination.dao.*;
import com.vaccination.db.DatabaseConnection;
import com.vaccination.model.*;
import com.vaccination.ui.components.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class CertificatePanel extends JPanel {

    private final User user;
    private final PatientDAO patientDAO = new PatientDAO();
    private JComboBox<String> cbPatient;
    private int[] patIds;
    private JPanel certPreview;

    public CertificatePanel(User user) {
        this.user = user;
        setOpaque(false);
        setLayout(new BorderLayout(0, 15));
        buildUI();
    }

    private void buildUI() {
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel title = UITheme.label("🏅 Vaccination Certificates", UITheme.FONT_TITLE, UITheme.PRIMARY);
        JLabel sub   = UITheme.label("Generate and view digital vaccination certificates", UITheme.FONT_BODY, UITheme.TEXT_LIGHT);
        JPanel tt = new JPanel(new GridLayout(2,1)); tt.setOpaque(false); tt.add(title); tt.add(sub);
        titleRow.add(tt, BorderLayout.WEST);
        add(titleRow, BorderLayout.NORTH);

        // Patient selector
        JPanel selector = UITheme.card("Select Patient");
        selector.setPreferredSize(new Dimension(0, 80));
        selector.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));

        java.util.List<Patient> patients = patientDAO.getAll();
        DefaultComboBoxModel<String> pm = new DefaultComboBoxModel<>();
        pm.addElement("-- Select Patient --");
        patIds = new int[patients.size()];
        for (int i = 0; i < patients.size(); i++) {
            Patient p = patients.get(i);
            pm.addElement(p.getPatientId() + " – " + p.getFullName() + " (Age: " + p.getAge() + ")");
            patIds[i] = p.getPatientId();
        }
        cbPatient = new JComboBox<>(pm);
        cbPatient.setFont(UITheme.FONT_BODY);
        cbPatient.setPreferredSize(new Dimension(350, 36));

        JButton btnGenerate = UITheme.primaryButton("🏅 Generate Certificate");
        btnGenerate.setPreferredSize(new Dimension(200, 36));
        selector.add(UITheme.label("Patient:", UITheme.FONT_BODY, UITheme.TEXT_DARK));
        selector.add(cbPatient);
        selector.add(btnGenerate);
        add(selector, BorderLayout.NORTH);

        // Certificate preview area
        certPreview = new JPanel(new BorderLayout());
        certPreview.setOpaque(false);

        JPanel placeholder = new JPanel(new GridBagLayout());
        placeholder.setBackground(Color.WHITE);
        placeholder.setBorder(new UITheme.RoundBorder(new Color(0xDD,0xDD,0xDD), 12, 1));
        JLabel ph = UITheme.label("Select a patient and click 'Generate Certificate'", UITheme.FONT_BODY, UITheme.TEXT_LIGHT);
        ph.setHorizontalAlignment(SwingConstants.CENTER);
        placeholder.add(ph);
        certPreview.add(placeholder, BorderLayout.CENTER);
        add(certPreview, BorderLayout.CENTER);

        btnGenerate.addActionListener(e -> {
            if (cbPatient.getSelectedIndex() < 1) {
                JOptionPane.showMessageDialog(this,"Please select a patient.","Info",JOptionPane.INFORMATION_MESSAGE); return;
            }
            int patId = patIds[cbPatient.getSelectedIndex() - 1];
            generateCertificate(patId);
        });
    }

    private void generateCertificate(int patientId) {
        Patient patient = patientDAO.getById(patientId);
        if (patient == null) return;

        // Fetch vaccination records
        java.util.List<Object[]> records = new ArrayList<>();
        String sql = "SELECT vr.dose_number, vr.administered_date, vr.batch_used, v.vaccine_name, u.full_name " +
                     "FROM vaccination_records vr " +
                     "JOIN vaccines v ON vr.vaccine_id = v.vaccine_id " +
                     "LEFT JOIN users u ON vr.administered_by = u.user_id " +
                     "WHERE vr.patient_id = ? ORDER BY vr.administered_date";
        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                records.add(new Object[]{
                    rs.getString("vaccine_name"), rs.getInt("dose_number"),
                    rs.getDate("administered_date"), rs.getString("batch_used"), rs.getString("full_name")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }

        if (records.isEmpty()) {
            JOptionPane.showMessageDialog(this,"No vaccination records found for this patient.","Info",JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Save certificate record
        String certNum = "CERT-" + patientId + "-" + System.currentTimeMillis() % 100000;
        String issDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        StringBuilder vaccines = new StringBuilder();
        for (Object[] r : records) vaccines.append(r[0]).append(", ");

        try (PreparedStatement ps = DatabaseConnection.getInstance().getConnection().prepareStatement(
            "INSERT IGNORE INTO certificates (patient_id, certificate_number, issued_date, issued_by, vaccines_covered) VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE issued_date=?")) {
            ps.setInt(1, patientId); ps.setString(2, certNum);
            ps.setString(3, issDate); ps.setInt(4, user.getUserId());
            ps.setString(5, vaccines.toString()); ps.setString(6, issDate);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }

        // Render certificate
        certPreview.removeAll();
        JPanel cert = createCertificateView(patient, records, certNum, issDate);
        certPreview.add(new JScrollPane(cert), BorderLayout.CENTER);
        certPreview.revalidate(); certPreview.repaint();
    }

    private JPanel createCertificateView(Patient patient, java.util.List<Object[]> records, String certNum, String issDate) {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(new Color(0xF0, 0xF4, 0xFF));

        JPanel cert = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Background
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),20,20));
                // Top banner
                GradientPaint gp = new GradientPaint(0,0,UITheme.PRIMARY,getWidth(),0,new Color(0x0D,0x9B,0x76));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),90,20,20));
                g2.setColor(UITheme.PRIMARY);
                g2.fillRect(0,50,getWidth(),40);
                // Border
                g2.setColor(UITheme.SECONDARY); g2.setStroke(new BasicStroke(3));
                g2.draw(new RoundRectangle2D.Float(3,3,getWidth()-6,getHeight()-6,18,18));
                g2.dispose();
            }
        };
        cert.setLayout(null);
        cert.setOpaque(false);
        cert.setPreferredSize(new Dimension(750, 500 + records.size() * 50));

        // Header text
        JLabel lTitle = UITheme.label("VACCINATION CERTIFICATE", new Font("Segoe UI", Font.BOLD, 22), Color.WHITE);
        lTitle.setBounds(220, 15, 400, 35); cert.add(lTitle);
        JLabel lSub = UITheme.label("Government of India | Ministry of Health", UITheme.FONT_SMALL, new Color(0xCC, 0xFF, 0xEE));
        lSub.setBounds(250, 50, 350, 20); cert.add(lSub);

        int y = 105;
        // Cert number
        JLabel lCertNo = UITheme.label("Certificate No: " + certNum + "    Issued: " + issDate, UITheme.FONT_SMALL, UITheme.TEXT_LIGHT);
        lCertNo.setBounds(20, y, 710, 20); cert.add(lCertNo); y += 30;

        // Patient info
        addCertLabel(cert, "Patient Name:", patient.getFullName(), 20, y); y += 30;
        addCertLabel(cert, "Date of Birth:", patient.getDateOfBirth() != null ? patient.getDateOfBirth().toString() : "N/A", 20, y); y += 30;
        addCertLabel(cert, "Age:", String.valueOf(patient.getAge()) + " years", 20, y);
        addCertLabel(cert, "Gender:", patient.getGender(), 400, y); y += 30;
        addCertLabel(cert, "ID Number:", patient.getIdNumber() != null ? patient.getIdNumber() : "N/A", 20, y); y += 40;

        // Divider
        JLabel divider = new JLabel(); divider.setOpaque(true); divider.setBackground(new Color(0xEE,0xEE,0xEE));
        divider.setBounds(20, y, 710, 1); cert.add(divider); y += 15;

        JLabel lRec = UITheme.label("VACCINATION RECORDS", new Font("Segoe UI", Font.BOLD, 14), UITheme.PRIMARY);
        lRec.setBounds(20, y, 400, 22); cert.add(lRec); y += 30;

        // Table header
        String[] headers = {"Vaccine", "Dose", "Date Administered", "Batch No.", "Administered By"};
        int[] widths = {200, 50, 130, 120, 160};
        int x = 20;
        for (int i = 0; i < headers.length; i++) {
            JLabel h = UITheme.label(headers[i], UITheme.FONT_SMALL, UITheme.PRIMARY);
            h.setFont(new Font("Segoe UI", Font.BOLD, 12));
            h.setBounds(x, y, widths[i], 22); cert.add(h);
            x += widths[i];
        }
        y += 25;

        for (Object[] row : records) {
            JLabel divRow = new JLabel(); divRow.setOpaque(true); divRow.setBackground(new Color(0xEE,0xEE,0xEE));
            divRow.setBounds(20, y, 710, 1); cert.add(divRow); y += 5;
            x = 20;
            String[] vals = {String.valueOf(row[0]), String.valueOf(row[1]), String.valueOf(row[2]), String.valueOf(row[3]), String.valueOf(row[4])};
            for (int i = 0; i < vals.length; i++) {
                JLabel v = UITheme.label(vals[i] != null ? vals[i] : "–", UITheme.FONT_BODY, UITheme.TEXT_DARK);
                v.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                v.setBounds(x, y, widths[i], 24); cert.add(v); x += widths[i];
            }
            y += 30;
        }

        y += 20;
        JLabel checkmark = UITheme.label("✅ This certificate is digitally verified by VaxDrive System", UITheme.FONT_SMALL, UITheme.SECONDARY);
        checkmark.setBounds(20, y, 710, 22); cert.add(checkmark); y += 30;

        JLabel issuer = UITheme.label("Issued by: " + user.getFullName() + " (" + user.getRole() + ")", UITheme.FONT_SMALL, UITheme.TEXT_LIGHT);
        issuer.setBounds(20, y, 710, 22); cert.add(issuer);

        cert.setPreferredSize(new Dimension(750, y + 40));
        outer.add(cert);
        return outer;
    }

    private void addCertLabel(JPanel cert, String lbl, String val, int x, int y) {
        JLabel l = UITheme.label(lbl, new Font("Segoe UI", Font.BOLD, 13), UITheme.TEXT_LIGHT);
        l.setBounds(x, y, 130, 22); cert.add(l);
        JLabel v = UITheme.label(val != null ? val : "N/A", new Font("Segoe UI", Font.PLAIN, 13), UITheme.TEXT_DARK);
        v.setBounds(x + 130, y, 230, 22); cert.add(v);
    }
}

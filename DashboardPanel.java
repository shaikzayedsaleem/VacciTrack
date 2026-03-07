package com.vaccination.ui.panels;

import com.vaccination.dao.*;
import com.vaccination.model.User;
import com.vaccination.ui.DashboardFrame;
import com.vaccination.ui.components.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DashboardPanel extends JPanel {

    private final User user;
    private final DashboardFrame frame;
    private final VaccineDAO vaccineDAO     = new VaccineDAO();
    private final PatientDAO patientDAO     = new PatientDAO();
    private final AppointmentDAO apptDAO    = new AppointmentDAO();
    private final AEFIDAO aefiDAO           = new AEFIDAO();

    public DashboardPanel(User user, DashboardFrame frame) {
        this.user = user; this.frame = frame;
        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        buildUI();
    }

    private void buildUI() {
        // ── Page Title ──────────────────────────────────────
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel title = UITheme.label("Dashboard Overview", UITheme.FONT_TITLE, UITheme.PRIMARY);
        String date = new SimpleDateFormat("EEEE, dd MMMM yyyy").format(new Date());
        JLabel dateLbl = UITheme.label(date, UITheme.FONT_BODY, UITheme.TEXT_LIGHT);
        titleRow.add(title, BorderLayout.WEST);
        titleRow.add(dateLbl, BorderLayout.EAST);
        add(titleRow, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        // ── KPI Cards ───────────────────────────────────────
        JPanel kpiRow = new JPanel(new GridLayout(1, 4, 15, 0));
        kpiRow.setOpaque(false);
        kpiRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        kpiRow.add(wrapCard(UITheme.statCard(String.valueOf(patientDAO.getTotalPatients()),  "Total Patients",      UITheme.SECONDARY)));
        kpiRow.add(wrapCard(UITheme.statCard(String.valueOf(apptDAO.getTodayCount()),         "Today's Appointments",UITheme.ACCENT)));
        kpiRow.add(wrapCard(UITheme.statCard(String.valueOf(vaccineDAO.getLowStockCount()),   "Low Stock Vaccines",  UITheme.DANGER)));
        kpiRow.add(wrapCard(UITheme.statCard(String.valueOf(aefiDAO.getTotalAEFI()),          "AEFI Reports",        UITheme.PRIMARY)));

        body.add(kpiRow);
        body.add(Box.createVerticalStrut(20));

        // ── Second row: quick actions + alerts ──────────────
        JPanel midRow = new JPanel(new GridLayout(1, 2, 15, 0));
        midRow.setOpaque(false);
        midRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        // Quick Actions
        JPanel quickCard = UITheme.card("⚡ Quick Actions");
        quickCard.setLayout(new GridLayout(2, 2, 10, 10));
        quickCard.setBorder(BorderFactory.createCompoundBorder(
            quickCard.getBorder(), BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        quickCard.add(quickBtn("➕ New Patient",     1, UITheme.SECONDARY));
        quickCard.add(quickBtn("📅 Book Appointment", 2, UITheme.PRIMARY));
        quickCard.add(quickBtn("💊 Manage Inventory", 0, UITheme.ACCENT));
        quickCard.add(quickBtn("📊 View Reports",     5, new Color(0x8E, 0x44, 0xAD)));

        // Alerts
        JPanel alertCard = UITheme.card("🔔 System Alerts");
        alertCard.setLayout(new BoxLayout(alertCard, BoxLayout.Y_AXIS));

        int lowStock = vaccineDAO.getLowStockCount();
        int expiring = vaccineDAO.getExpiringCount();
        int todayCompleted = apptDAO.getCompletedTodayCount();

        addAlert(alertCard, lowStock > 0 ? "⚠️ " + lowStock + " vaccine(s) running low on stock" : "✅ All vaccine stocks are adequate",
                 lowStock > 0 ? UITheme.DANGER : UITheme.SECONDARY);
        addAlert(alertCard, expiring > 0 ? "⚠️ " + expiring + " vaccine batch(es) expiring within 30 days" : "✅ No vaccines expiring soon",
                 expiring > 0 ? UITheme.ACCENT : UITheme.SECONDARY);
        addAlert(alertCard, "📌 " + todayCompleted + " vaccination(s) completed today", UITheme.PRIMARY);
        addAlert(alertCard, "🧾 " + aefiDAO.getTotalAEFI() + " total AEFI reports logged", UITheme.TEXT_LIGHT);

        midRow.add(quickCard);
        midRow.add(alertCard);
        body.add(midRow);
        body.add(Box.createVerticalStrut(20));

        // ── Inventory summary ───────────────────────────────
        JPanel invCard = buildInventoryBar();
        body.add(invCard);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel wrapCard(JPanel inner) {
        JPanel wrap = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fill(new RoundRectangle2D.Float(2, 3, getWidth()-2, getHeight()-2, 16, 16));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth()-2, getHeight()-3, 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        wrap.setOpaque(false);
        wrap.setLayout(new BorderLayout());
        wrap.add(inner);
        return wrap;
    }

    private JButton quickBtn(String text, int navIdx, Color color) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? color.darker() : color);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setForeground(Color.WHITE);
        b.setFont(UITheme.FONT_BODY);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> frame.navigate(navIdx));
        return b;
    }

    private void addAlert(JPanel panel, String text, Color color) {
        JLabel l = UITheme.label(text, UITheme.FONT_BODY, color);
        l.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(l);
    }

    private JPanel buildInventoryBar() {
        JPanel card = UITheme.card("💊 Vaccine Inventory Summary");
        card.setLayout(new BorderLayout(0, 10));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        java.util.List<com.vaccination.model.Vaccine> vaccines = new VaccineDAO().getAll();
        JPanel bars = new JPanel(new GridLayout(vaccines.size(), 1, 0, 5));
        bars.setOpaque(false);

        for (com.vaccination.model.Vaccine v : vaccines) {
            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setOpaque(false);
            JLabel name = UITheme.label(v.getVaccineName(), UITheme.FONT_SMALL, UITheme.TEXT_DARK);
            name.setPreferredSize(new Dimension(180, 18));
            row.add(name, BorderLayout.WEST);

            int qty = v.getQuantityAvailable();
            Color barColor = qty < 50 ? UITheme.DANGER : qty < 150 ? UITheme.ACCENT : UITheme.SECONDARY;
            JProgressBar pb = new JProgressBar(0, 600);
            pb.setValue(Math.min(qty, 600));
            pb.setForeground(barColor);
            pb.setBackground(new Color(0xEE, 0xEE, 0xEE));
            pb.setStringPainted(false);
            pb.setPreferredSize(new Dimension(0, 16));
            pb.setBorder(null);
            row.add(pb, BorderLayout.CENTER);

            JLabel qtyLbl = UITheme.label(qty + " doses", UITheme.FONT_SMALL, barColor);
            qtyLbl.setPreferredSize(new Dimension(70, 18));
            qtyLbl.setHorizontalAlignment(SwingConstants.RIGHT);
            row.add(qtyLbl, BorderLayout.EAST);
            bars.add(row);
        }

        JScrollPane sp = new JScrollPane(bars);
        sp.setOpaque(false); sp.getViewport().setOpaque(false); sp.setBorder(null);
        card.add(sp, BorderLayout.CENTER);
        return card;
    }
}

package com.vaccination.ui;

import com.vaccination.model.User;
import com.vaccination.ui.components.UITheme;
import com.vaccination.ui.panels.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DashboardFrame extends JFrame {

    private final User currentUser;
    private JPanel contentArea;
    private JLabel lblClock;
    private final String[] menuItems = {
        "Dashboard", "Vaccine Inventory", "Patients",
        "Appointments", "AEFI Reports", "Certificates", "Reports"
    };
    private final String[] menuIcons = { "🏠", "💊", "🧑‍⚕️", "📅", "⚠️", "🏅", "📊" };
    private final JButton[] menuBtns;
    private int activeMenu = 0;

    public DashboardFrame(User user) {
        this.currentUser = user;
        menuBtns = new JButton[menuItems.length];
        setTitle("VaxDrive – " + user.getFullName());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1100, 650));
        UITheme.setGlobalLookAndFeel();
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UITheme.BG);

        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildTopBar(), BorderLayout.NORTH);

        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(UITheme.BG);
        contentArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        root.add(contentArea, BorderLayout.CENTER);

        setContentPane(root);
        navigate(0); // Default: Dashboard
        startClock();
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, UITheme.PRIMARY, 0, getHeight(), new Color(0x0A, 0x24, 0x4A));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Logo area
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        logoPanel.setOpaque(false);
        JLabel logo = UITheme.label("💉 VaxDrive", new Font("Segoe UI", Font.BOLD, 18), Color.WHITE);
        logoPanel.add(logo);
        sidebar.add(logoPanel);

        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0xFF, 0xFF, 0xFF, 40));
        sep.setMaximumSize(new Dimension(230, 1));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(10));

        // Menu items
        for (int i = 0; i < menuItems.length; i++) {
            final int idx = i;
            JButton btn = createMenuButton(menuIcons[i] + "  " + menuItems[i], i == 0);
            btn.addActionListener(e -> navigate(idx));
            menuBtns[i] = btn;
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(3));
        }

        sidebar.add(Box.createVerticalGlue());

        // User info at bottom
        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(0xFF, 0xFF, 0xFF, 40));
        sep2.setMaximumSize(new Dimension(230, 1));
        sidebar.add(sep2);

        JPanel userPanel = new JPanel(new BorderLayout(10, 5));
        userPanel.setOpaque(false);
        userPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 15));
        JLabel uName = UITheme.label(currentUser.getFullName(), UITheme.FONT_BODY, Color.WHITE);
        JLabel uRole = UITheme.label(currentUser.getRole(), UITheme.FONT_SMALL, new Color(0xAA, 0xCC, 0xFF));
        JPanel userInfo = new JPanel(new GridLayout(2, 1));
        userInfo.setOpaque(false);
        userInfo.add(uName); userInfo.add(uRole);
        userPanel.add(userInfo, BorderLayout.CENTER);

        JButton btnLogout = new JButton("↩");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnLogout.setForeground(new Color(0xFF, 0x88, 0x88));
        btnLogout.setContentAreaFilled(false); btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false); btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.setToolTipText("Logout");
        btnLogout.addActionListener(e -> doLogout());
        userPanel.add(btnLogout, BorderLayout.EAST);
        sidebar.add(userPanel);

        return sidebar;
    }

    private JButton createMenuButton(String text, boolean selected) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getClientProperty("selected") == Boolean.TRUE) {
                    g2.setColor(UITheme.SIDEBAR_SEL);
                    g2.fillRoundRect(8, 2, getWidth()-16, getHeight()-4, 10, 10);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(0xFF, 0xFF, 0xFF, 25));
                    g2.fillRoundRect(8, 2, getWidth()-16, getHeight()-4, 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(UITheme.FONT_BODY);
        btn.setForeground(Color.WHITE);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(230, 42));
        btn.setPreferredSize(new Dimension(230, 42));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 10));
        if (selected) btn.putClientProperty("selected", Boolean.TRUE);
        return btn;
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xEE, 0xEE, 0xEE)),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)));

        JLabel appLabel = UITheme.label("Vaccination Drive Management System", UITheme.FONT_HEADER, UITheme.PRIMARY);
        bar.add(appLabel, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        right.setOpaque(false);
        lblClock = UITheme.label("", UITheme.FONT_BODY, UITheme.TEXT_LIGHT);
        right.add(lblClock);
        JLabel userBadge = UITheme.label("👤 " + currentUser.getFullName() + " | " + currentUser.getRole(), UITheme.FONT_BODY, UITheme.TEXT_DARK);
        right.add(userBadge);
        bar.add(right, BorderLayout.EAST);

        return bar;
    }

    public void navigate(int idx) {
        // Update button states
        for (int i = 0; i < menuBtns.length; i++) {
            menuBtns[i].putClientProperty("selected", i == idx ? Boolean.TRUE : null);
            menuBtns[i].repaint();
        }
        activeMenu = idx;
        contentArea.removeAll();

        JPanel panel;
        switch (idx) {
            case 0:  panel = new DashboardPanel(currentUser, this); break;
            case 1:  panel = new InventoryPanel(currentUser); break;
            case 2:  panel = new PatientPanel(currentUser); break;
            case 3:  panel = new AppointmentPanel(currentUser); break;
            case 4:  panel = new AEFIPanel(currentUser); break;
            case 5:  panel = new CertificatePanel(currentUser); break;
            case 6:  panel = new ReportPanel(currentUser); break;
            default: panel = new DashboardPanel(currentUser, this);
        }

        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private void startClock() {
        Timer timer = new Timer(1000, e -> {
            lblClock.setText(new SimpleDateFormat("dd MMM yyyy  HH:mm:ss").format(new Date()));
        });
        timer.start();
    }

    private void doLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }
}

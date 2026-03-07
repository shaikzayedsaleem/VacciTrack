package com.vaccination.ui;

import com.vaccination.dao.UserDAO;
import com.vaccination.model.User;
import com.vaccination.ui.components.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class LoginFrame extends JFrame {

    private final UserDAO userDAO = new UserDAO();
    private JTextField tfUser;
    private JPasswordField tfPass;
    private JLabel lblError;

    public LoginFrame() {
        setTitle("Vaccination Drive Management System - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        UITheme.setGlobalLookAndFeel();
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, UITheme.PRIMARY, getWidth(), getHeight(), new Color(0x0D, 0x5B, 0xA4));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        root.setLayout(new GridBagLayout());

        // ── White Card ──────────────────────────────────────
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                // Left accent stripe
                g2.setColor(UITheme.SECONDARY);
                g2.fill(new RoundRectangle2D.Float(0, 0, 6, getHeight(), 0, 0));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(780, 440));

        // Logo / Icon area (left half)
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setOpaque(false);
        leftPanel.setBounds(0, 0, 380, 440);

        JPanel iconCircle = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.PRIMARY);
                g2.fillOval(0, 0, 100, 100);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 48));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("💉", (100 - fm.stringWidth("💉")) / 2, 68);
                g2.dispose();
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(100, 100));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = 0; gc.anchor = GridBagConstraints.CENTER;
        leftPanel.add(iconCircle, gc);

        gc.gridy = 1; gc.insets = new Insets(15, 0, 5, 0);
        JLabel appName = UITheme.label("VaxDrive", new Font("Segoe UI", Font.BOLD, 30), UITheme.PRIMARY);
        leftPanel.add(appName, gc);

        gc.gridy = 2; gc.insets = new Insets(0, 20, 0, 20);
        JLabel appSub = UITheme.label("Vaccination Drive Management System", UITheme.FONT_SMALL, UITheme.TEXT_LIGHT);
        appSub.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(appSub, gc);

        gc.gridy = 3; gc.insets = new Insets(20, 20, 0, 20);
        JLabel inst = UITheme.label("Muffakham Jah College of Engineering & Technology", UITheme.FONT_SMALL, UITheme.TEXT_LIGHT);
        inst.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(inst, gc);

        gc.gridy = 4;
        JLabel inst2 = UITheme.label("MJ Industry Ready Program", UITheme.FONT_SMALL, UITheme.TEXT_LIGHT);
        inst2.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(inst2, gc);

        card.add(leftPanel);

        // ── Right: Form ─────────────────────────────────────
        JPanel rightPanel = new JPanel(null);
        rightPanel.setOpaque(false);
        rightPanel.setBounds(390, 0, 390, 440);

        int y = 60;

        JLabel title = UITheme.label("Welcome Back", new Font("Segoe UI", Font.BOLD, 24), UITheme.PRIMARY);
        title.setBounds(30, y, 300, 35); rightPanel.add(title); y += 40;

        JLabel subtitle = UITheme.label("Sign in to your account", UITheme.FONT_BODY, UITheme.TEXT_LIGHT);
        subtitle.setBounds(30, y, 300, 25); rightPanel.add(subtitle); y += 45;

        JLabel lUser = UITheme.label("Username", UITheme.FONT_BODY, UITheme.TEXT_DARK);
        lUser.setBounds(30, y, 300, 20); rightPanel.add(lUser); y += 25;

        tfUser = UITheme.styledField();
        tfUser.setBounds(30, y, 310, 38); rightPanel.add(tfUser); y += 50;

        JLabel lPass = UITheme.label("Password", UITheme.FONT_BODY, UITheme.TEXT_DARK);
        lPass.setBounds(30, y, 300, 20); rightPanel.add(lPass); y += 25;

        tfPass = UITheme.styledPass();
        tfPass.setBounds(30, y, 310, 38); rightPanel.add(tfPass); y += 50;

        lblError = UITheme.label("", UITheme.FONT_SMALL, UITheme.DANGER);
        lblError.setBounds(30, y, 310, 18); rightPanel.add(lblError); y += 25;

        JButton btnLogin = UITheme.primaryButton("Sign In");
        btnLogin.setBounds(30, y, 150, 40); rightPanel.add(btnLogin);

        JButton btnSignUp = UITheme.outlineButton("Register");
        btnSignUp.setBounds(190, y, 150, 40); rightPanel.add(btnSignUp); y += 55;

        JLabel hint = UITheme.label("Default: admin / Admin@123", UITheme.FONT_SMALL, UITheme.TEXT_LIGHT);
        hint.setBounds(30, y, 310, 18); rightPanel.add(hint);

        card.add(rightPanel);

        // ── Events ──────────────────────────────────────────
        btnLogin.addActionListener(e -> doLogin());
        btnSignUp.addActionListener(e -> openSignUp());
        tfPass.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin(); }
        });

        root.add(card);
        setContentPane(root);
    }

    private void doLogin() {
        String u = tfUser.getText().trim();
        String p = new String(tfPass.getPassword());

        if (u.isEmpty() || p.isEmpty()) {
            lblError.setText("Username and password are required."); return;
        }
        User user = userDAO.authenticate(u, p);
        if (user == null) {
            lblError.setText("Invalid username or password.");
            tfPass.setText("");
        } else {
            lblError.setText("");
            dispose();
            SwingUtilities.invokeLater(() -> new DashboardFrame(user).setVisible(true));
        }
    }

    private void openSignUp() {
        new SignUpFrame(this).setVisible(true);
    }
}

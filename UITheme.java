package com.vaccination.ui.components;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/** Central UI theme / component factory */
public class UITheme {

    // ── Palette ──────────────────────────────────────────────
    public static final Color PRIMARY     = new Color(0x1A, 0x3C, 0x6E);   // Navy
    public static final Color SECONDARY   = new Color(0x0D, 0x9B, 0x76);   // Teal
    public static final Color ACCENT      = new Color(0xF5, 0xA6, 0x23);   // Amber
    public static final Color DANGER      = new Color(0xE7, 0x4C, 0x3C);   // Red
    public static final Color BG          = new Color(0xF4, 0xF6, 0xF9);   // Light grey
    public static final Color CARD        = Color.WHITE;
    public static final Color TEXT_DARK   = new Color(0x2C, 0x3E, 0x50);
    public static final Color TEXT_LIGHT  = new Color(0x7F, 0x8C, 0x8D);
    public static final Color SIDEBAR_BG  = new Color(0x1A, 0x3C, 0x6E);
    public static final Color SIDEBAR_SEL = new Color(0x0D, 0x9B, 0x76);
    public static final Color TABLE_ALT   = new Color(0xF0, 0xF4, 0xFF);

    // ── Fonts ────────────────────────────────────────────────
    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_MONO   = new Font("Consolas", Font.PLAIN, 12);

    // ── Factory Methods ──────────────────────────────────────

    public static JButton primaryButton(String text) {
        return styledButton(text, SECONDARY, Color.WHITE);
    }

    public static JButton dangerButton(String text) {
        return styledButton(text, DANGER, Color.WHITE);
    }

    public static JButton outlineButton(String text) {
        JButton b = styledButton(text, Color.WHITE, PRIMARY);
        b.setBorder(new RoundBorder(PRIMARY, 6, 1));
        return b;
    }

    private static JButton styledButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() :
                            getModel().isRollover() ? bg.brighter() : bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setForeground(fg);
        b.setFont(FONT_BODY);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(120, 36));
        return b;
    }

    public static JTextField styledField() {
        JTextField f = new JTextField();
        f.setFont(FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(new Color(0xCC, 0xCC, 0xCC), 8, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        f.setBackground(Color.WHITE);
        return f;
    }

    public static JPasswordField styledPass() {
        JPasswordField f = new JPasswordField();
        f.setFont(FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(new Color(0xCC, 0xCC, 0xCC), 8, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        f.setBackground(Color.WHITE);
        return f;
    }

    public static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    public static JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) c.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ALT);
                else { c.setBackground(SECONDARY); c.setForeground(Color.WHITE); }
                return c;
            }
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table.setFont(FONT_BODY);
        table.setRowHeight(32);
        table.getTableHeader().setFont(FONT_HEADER);
        table.getTableHeader().setBackground(PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(0xE8, 0xEC, 0xF2));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        return table;
    }

    public static JPanel card(String title) {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(new Color(0xDD, 0xDD, 0xDD), 12, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        if (title != null && !title.isEmpty()) {
            JLabel lbl = label(title, FONT_HEADER, PRIMARY);
            lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xEE, 0xEE, 0xEE)));
            p.add(lbl, BorderLayout.NORTH);
        }
        return p;
    }

    /** Stat card (dashboard KPI box) */
    public static JPanel statCard(String value, String label, Color accent) {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(accent);
                g2.fill(new RoundRectangle2D.Float(0, 0, 8, getHeight(), 0, 0));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setLayout(new GridLayout(2, 1, 0, 5));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 15));

        JLabel valLbl = new JLabel(value, SwingConstants.LEFT);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valLbl.setForeground(accent);

        JLabel nameLbl = new JLabel(label, SwingConstants.LEFT);
        nameLbl.setFont(FONT_BODY);
        nameLbl.setForeground(TEXT_LIGHT);

        p.add(valLbl); p.add(nameLbl);
        return p;
    }

    public static void setGlobalLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("Panel.background", BG);
            UIManager.put("OptionPane.background", CARD);
            UIManager.put("OptionPane.messageForeground", TEXT_DARK);
        } catch (Exception ignored) {}
    }

    // ── Inner: Rounded Border ────────────────────────────────
    public static class RoundBorder extends AbstractBorder {
        private final Color color; private final int radius, thickness;
        public RoundBorder(Color c, int r, int t) { color=c; radius=r; thickness=t; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color); g2.setStroke(new BasicStroke(thickness));
            g2.draw(new RoundRectangle2D.Float(x+1, y+1, w-2, h-2, radius, radius));
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(radius/2,radius/2,radius/2,radius/2); }
    }
}

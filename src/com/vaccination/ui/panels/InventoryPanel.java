package com.vaccination.ui.panels;

import com.vaccination.dao.VaccineDAO;
import com.vaccination.model.User;
import com.vaccination.model.Vaccine;
import com.vaccination.ui.components.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class InventoryPanel extends JPanel {

    private final User user;
    private final VaccineDAO dao = new VaccineDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField tfSearch;

    public InventoryPanel(User user) {
        this.user = user;
        setOpaque(false);
        setLayout(new BorderLayout(0, 15));
        buildUI();
        loadData();
    }

    private void buildUI() {
        // Title row
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel title = UITheme.label("💊 Vaccine Inventory", UITheme.FONT_TITLE, UITheme.PRIMARY);
        JLabel sub = UITheme.label("Manage vaccine batches, stock levels, and expiry dates", UITheme.FONT_BODY, UITheme.TEXT_LIGHT);
        JPanel titleText = new JPanel(new GridLayout(2,1)); titleText.setOpaque(false);
        titleText.add(title); titleText.add(sub);
        titleRow.add(titleText, BorderLayout.WEST);
        add(titleRow, BorderLayout.NORTH);

        // Toolbar
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setOpaque(false);

        tfSearch = UITheme.styledField();
        tfSearch.setPreferredSize(new Dimension(250, 36));
        tfSearch.putClientProperty("placeholder", "Search vaccines...");
        tfSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { filterTable(tfSearch.getText()); }
        });

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(UITheme.label("🔍 ", UITheme.FONT_BODY, UITheme.TEXT_LIGHT));
        searchPanel.add(tfSearch);
        toolbar.add(searchPanel, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        JButton btnAdd    = UITheme.primaryButton("➕ Add Vaccine");
        JButton btnEdit   = UITheme.outlineButton("✏️ Edit");
        JButton btnDelete = UITheme.dangerButton("🗑️ Delete");
        JButton btnRefresh= UITheme.outlineButton("🔄 Refresh");
        btnAdd.setPreferredSize(new Dimension(140, 36));
        btnEdit.setPreferredSize(new Dimension(100, 36));
        btnDelete.setPreferredSize(new Dimension(100, 36));
        btnRefresh.setPreferredSize(new Dimension(110, 36));
        btnPanel.add(btnRefresh); btnPanel.add(btnEdit); btnPanel.add(btnDelete); btnPanel.add(btnAdd);
        toolbar.add(btnPanel, BorderLayout.EAST);

        // Table
        String[] cols = {"ID","Vaccine Name","Manufacturer","Batch No.","Stock","Expiry Date","Doses Req.","Interval(d)","Age Range","Storage"};
        tableModel = new DefaultTableModel(cols, 0);
        table = UITheme.styledTable(tableModel);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new UITheme.RoundBorder(new Color(0xDD,0xDD,0xDD), 10, 1));
        scroll.getViewport().setBackground(Color.WHITE);

        // Legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        legend.setOpaque(false);
        legend.add(dot(UITheme.DANGER));   legend.add(UITheme.label("Low Stock (<50)", UITheme.FONT_SMALL, UITheme.TEXT_LIGHT));
        legend.add(dot(UITheme.ACCENT));   legend.add(UITheme.label("Expiring Soon (30d)", UITheme.FONT_SMALL, UITheme.TEXT_LIGHT));
        legend.add(dot(UITheme.SECONDARY));legend.add(UITheme.label("OK", UITheme.FONT_SMALL, UITheme.TEXT_LIGHT));

        // Center card
        JPanel card = UITheme.card("");
        card.setLayout(new BorderLayout(0, 10));
        card.add(toolbar, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        card.add(legend, BorderLayout.SOUTH);
        add(card, BorderLayout.CENTER);

        // Events
        btnAdd.addActionListener(e -> openForm(null));
        btnEdit.addActionListener(e -> { int r = table.getSelectedRow(); if (r < 0) { alert("Select a vaccine first."); return; }
            openForm((Vaccine) table.getClientProperty("data_" + table.convertRowIndexToModel(r))); });
        btnDelete.addActionListener(e -> deleteSelected());
        btnRefresh.addActionListener(e -> loadData());

        // Colour renderer for stock
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : UITheme.TABLE_ALT);
                    c.setForeground(UITheme.TEXT_DARK);
                    if (col == 4) { // Stock column
                        try {
                            int stock = Integer.parseInt(v.toString());
                            if (stock < 50) c.setForeground(UITheme.DANGER);
                            else if (stock < 150) c.setForeground(UITheme.ACCENT);
                            else c.setForeground(UITheme.SECONDARY);
                        } catch (NumberFormatException ignored) {}
                    }
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
    }

    void loadData() {
        tableModel.setRowCount(0);
        table.getClientProperties().clear();
        List<Vaccine> list = dao.getAll();
        for (int i = 0; i < list.size(); i++) {
            Vaccine v = list.get(i);
            tableModel.addRow(new Object[]{
                v.getVaccineId(), v.getVaccineName(), v.getManufacturer(),
                v.getBatchNumber(), v.getQuantityAvailable(), v.getExpiryDate(),
                v.getDosesRequired(), v.getIntervalDays(),
                v.getMinAge() + "–" + v.getMaxAge() + " yrs", v.getStorageTemp()
            });
            table.putClientProperty("data_" + i, v);
        }
    }

    private void filterTable(String kw) {
        tableModel.setRowCount(0);
        String k = kw.toLowerCase();
        List<Vaccine> list = dao.getAll();
        for (int i = 0; i < list.size(); i++) {
            Vaccine v = list.get(i);
            if (v.getVaccineName().toLowerCase().contains(k) ||
                v.getManufacturer().toLowerCase().contains(k) ||
                v.getBatchNumber().toLowerCase().contains(k)) {
                tableModel.addRow(new Object[]{
                    v.getVaccineId(), v.getVaccineName(), v.getManufacturer(),
                    v.getBatchNumber(), v.getQuantityAvailable(), v.getExpiryDate(),
                    v.getDosesRequired(), v.getIntervalDays(),
                    v.getMinAge() + "–" + v.getMaxAge() + " yrs", v.getStorageTemp()
                });
                table.putClientProperty("data_" + (tableModel.getRowCount()-1), v);
            }
        }
    }

    private void openForm(Vaccine vaccine) {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            vaccine == null ? "Add Vaccine" : "Edit Vaccine", true);
        dlg.setSize(480, 520);
        dlg.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL; gc.insets = new Insets(6, 5, 6, 5); gc.weightx = 1;

        JTextField fName  = UITheme.styledField();
        JTextField fMfr   = UITheme.styledField();
        JTextField fBatch = UITheme.styledField();
        JTextField fQty   = UITheme.styledField();
        JTextField fExp   = UITheme.styledField(); fExp.setToolTipText("YYYY-MM-DD");
        JTextField fDoses = UITheme.styledField();
        JTextField fIntv  = UITheme.styledField();
        JTextField fMin   = UITheme.styledField();
        JTextField fMax   = UITheme.styledField();
        JTextField fTemp  = UITheme.styledField();

        if (vaccine != null) {
            fName.setText(vaccine.getVaccineName()); fMfr.setText(vaccine.getManufacturer());
            fBatch.setText(vaccine.getBatchNumber()); fQty.setText(String.valueOf(vaccine.getQuantityAvailable()));
            if (vaccine.getExpiryDate() != null) fExp.setText(vaccine.getExpiryDate().toString());
            fDoses.setText(String.valueOf(vaccine.getDosesRequired()));
            fIntv.setText(String.valueOf(vaccine.getIntervalDays()));
            fMin.setText(String.valueOf(vaccine.getMinAge())); fMax.setText(String.valueOf(vaccine.getMaxAge()));
            fTemp.setText(vaccine.getStorageTemp());
        } else {
            fDoses.setText("2"); fIntv.setText("28"); fMin.setText("0"); fMax.setText("99"); fTemp.setText("2–8°C");
        }

        Object[][] rows = {{"Vaccine Name *", fName},{"Manufacturer *", fMfr},{"Batch Number *", fBatch},
            {"Quantity Available *", fQty},{"Expiry Date (YYYY-MM-DD) *", fExp},{"Doses Required", fDoses},
            {"Interval Days", fIntv},{"Min Age", fMin},{"Max Age", fMax},{"Storage Temp", fTemp}};

        for (int i = 0; i < rows.length; i++) {
            gc.gridx=0; gc.gridy=i; gc.weightx=0.35;
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
            if (fName.getText().trim().isEmpty() || fMfr.getText().trim().isEmpty() ||
                fBatch.getText().trim().isEmpty() || fQty.getText().trim().isEmpty() || fExp.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Please fill all required fields.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                Vaccine v = vaccine == null ? new Vaccine() : vaccine;
                v.setVaccineName(fName.getText().trim());
                v.setManufacturer(fMfr.getText().trim());
                v.setBatchNumber(fBatch.getText().trim());
                v.setQuantityAvailable(Integer.parseInt(fQty.getText().trim()));
                v.setExpiryDate(Date.valueOf(fExp.getText().trim()));
                v.setDosesRequired(fDoses.getText().trim().isEmpty() ? 1 : Integer.parseInt(fDoses.getText().trim()));
                v.setIntervalDays(fIntv.getText().trim().isEmpty() ? 0 : Integer.parseInt(fIntv.getText().trim()));
                v.setMinAge(fMin.getText().trim().isEmpty() ? 0 : Integer.parseInt(fMin.getText().trim()));
                v.setMaxAge(fMax.getText().trim().isEmpty() ? 99 : Integer.parseInt(fMax.getText().trim()));
                v.setStorageTemp(fTemp.getText().trim());

                boolean ok = vaccine == null ? dao.add(v) : dao.update(v);
                if (ok) { dlg.dispose(); loadData(); alert("Saved successfully!"); }
                else alert("Save failed – check for duplicate batch number.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Invalid data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.setVisible(true);
    }

    private void deleteSelected() {
        int r = table.getSelectedRow();
        if (r < 0) { alert("Select a vaccine to delete."); return; }
        int id = (int) tableModel.getValueAt(table.convertRowIndexToModel(r), 0);
        String name = (String) tableModel.getValueAt(table.convertRowIndexToModel(r), 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete \"" + name + "\"?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.delete(id)) { loadData(); alert("Vaccine deleted."); }
            else alert("Cannot delete – vaccine may have linked records.");
        }
    }

    private void alert(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Inventory", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel dot(Color c) {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(c); g.fillOval(0, 0, 12, 12);
            }
        };
        p.setPreferredSize(new Dimension(12, 12)); p.setOpaque(false); return p;
    }
}

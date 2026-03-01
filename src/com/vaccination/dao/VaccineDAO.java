package com.vaccination.dao;

import com.vaccination.db.DatabaseConnection;
import com.vaccination.model.Vaccine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VaccineDAO {

    private Connection conn() { return DatabaseConnection.getInstance().getConnection(); }

    public List<Vaccine> getAll() {
        List<Vaccine> list = new ArrayList<>();
        String sql = "SELECT * FROM vaccines ORDER BY vaccine_name";
        Statement st = null; ResultSet rs = null;
        try {
            st = conn().createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        finally { DatabaseConnection.close(rs, st); }
        return list;
    }

    public Vaccine getById(int id) {
        String sql = "SELECT * FROM vaccines WHERE vaccine_id=?";
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = conn().prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        finally { DatabaseConnection.close(rs, ps); }
        return null;
    }

    public boolean add(Vaccine v) {
        String sql = "INSERT INTO vaccines (vaccine_name,manufacturer,batch_number,quantity_available," +
                     "expiry_date,doses_required,interval_days,min_age,max_age,storage_temp) VALUES (?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = null;
        try {
            ps = conn().prepareStatement(sql);
            ps.setString(1, v.getVaccineName());
            ps.setString(2, v.getManufacturer());
            ps.setString(3, v.getBatchNumber());
            ps.setInt(4, v.getQuantityAvailable());
            ps.setDate(5, v.getExpiryDate());
            ps.setInt(6, v.getDosesRequired());
            ps.setInt(7, v.getIntervalDays());
            ps.setInt(8, v.getMinAge());
            ps.setInt(9, v.getMaxAge());
            ps.setString(10, v.getStorageTemp());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
        finally { DatabaseConnection.close(null, ps); }
    }

    public boolean update(Vaccine v) {
        String sql = "UPDATE vaccines SET vaccine_name=?,manufacturer=?,batch_number=?,quantity_available=?," +
                     "expiry_date=?,doses_required=?,interval_days=?,min_age=?,max_age=?,storage_temp=? WHERE vaccine_id=?";
        PreparedStatement ps = null;
        try {
            ps = conn().prepareStatement(sql);
            ps.setString(1, v.getVaccineName());
            ps.setString(2, v.getManufacturer());
            ps.setString(3, v.getBatchNumber());
            ps.setInt(4, v.getQuantityAvailable());
            ps.setDate(5, v.getExpiryDate());
            ps.setInt(6, v.getDosesRequired());
            ps.setInt(7, v.getIntervalDays());
            ps.setInt(8, v.getMinAge());
            ps.setInt(9, v.getMaxAge());
            ps.setString(10, v.getStorageTemp());
            ps.setInt(11, v.getVaccineId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
        finally { DatabaseConnection.close(null, ps); }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM vaccines WHERE vaccine_id=?";
        PreparedStatement ps = null;
        try {
            ps = conn().prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
        finally { DatabaseConnection.close(null, ps); }
    }

    /** Reduce stock after vaccination */
    public boolean deductStock(int vaccineId, int qty) {
        String sql = "UPDATE vaccines SET quantity_available = quantity_available - ? WHERE vaccine_id=? AND quantity_available >= ?";
        PreparedStatement ps = null;
        try {
            ps = conn().prepareStatement(sql);
            ps.setInt(1, qty); ps.setInt(2, vaccineId); ps.setInt(3, qty);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
        finally { DatabaseConnection.close(null, ps); }
    }

    /** Dashboard summary */
    public int getTotalVaccines() {
        return scalarQuery("SELECT COUNT(*) FROM vaccines");
    }
    public int getLowStockCount() {
        return scalarQuery("SELECT COUNT(*) FROM vaccines WHERE quantity_available < 50");
    }
    public int getExpiringCount() {
        return scalarQuery("SELECT COUNT(*) FROM vaccines WHERE expiry_date <= DATE_ADD(CURDATE(), INTERVAL 30 DAY) AND expiry_date >= CURDATE()");
    }

    private int scalarQuery(String sql) {
        Statement st = null; ResultSet rs = null;
        try {
            st = conn().createStatement();
            rs = st.executeQuery(sql);
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        finally { DatabaseConnection.close(rs, st); }
        return 0;
    }

    private Vaccine map(ResultSet rs) throws SQLException {
        Vaccine v = new Vaccine();
        v.setVaccineId(rs.getInt("vaccine_id"));
        v.setVaccineName(rs.getString("vaccine_name"));
        v.setManufacturer(rs.getString("manufacturer"));
        v.setBatchNumber(rs.getString("batch_number"));
        v.setQuantityAvailable(rs.getInt("quantity_available"));
        v.setExpiryDate(rs.getDate("expiry_date"));
        v.setDosesRequired(rs.getInt("doses_required"));
        v.setIntervalDays(rs.getInt("interval_days"));
        v.setMinAge(rs.getInt("min_age"));
        v.setMaxAge(rs.getInt("max_age"));
        v.setStorageTemp(rs.getString("storage_temp"));
        return v;
    }
}

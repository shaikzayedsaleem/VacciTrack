package com.vaccination.dao;

import com.vaccination.db.DatabaseConnection;
import com.vaccination.model.AEFIReport;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AEFIDAO {

    private Connection conn() { return DatabaseConnection.getInstance().getConnection(); }

    public List<AEFIReport> getAll() {
        List<AEFIReport> list = new ArrayList<>();
        String sql = "SELECT ar.*, p.full_name AS patient_name, v.vaccine_name, u.full_name AS reporter_name " +
                     "FROM aefi_reports ar " +
                     "JOIN patients p ON ar.patient_id = p.patient_id " +
                     "JOIN vaccines v ON ar.vaccine_id = v.vaccine_id " +
                     "LEFT JOIN users u ON ar.reported_by = u.user_id " +
                     "ORDER BY ar.reported_at DESC";
        Statement st = null; ResultSet rs = null;
        try {
            st = conn().createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        finally { DatabaseConnection.close(rs, st); }
        return list;
    }

    public boolean add(AEFIReport r) {
        String sql = "INSERT INTO aefi_reports (patient_id,vaccine_id,record_id,event_type,severity,description,onset_date,reported_by) VALUES (?,?,?,?,?,?,?,?)";
        PreparedStatement ps = null;
        try {
            ps = conn().prepareStatement(sql);
            ps.setInt(1, r.getPatientId()); ps.setInt(2, r.getVaccineId());
            if (r.getRecordId() > 0) ps.setInt(3, r.getRecordId()); else ps.setNull(3, Types.INTEGER);
            ps.setString(4, r.getEventType()); ps.setString(5, r.getSeverity());
            ps.setString(6, r.getDescription()); ps.setDate(7, r.getOnsetDate());
            ps.setInt(8, r.getReportedBy());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
        finally { DatabaseConnection.close(null, ps); }
    }

    public int getTotalAEFI() {
        Statement st = null; ResultSet rs = null;
        try {
            st = conn().createStatement();
            rs = st.executeQuery("SELECT COUNT(*) FROM aefi_reports");
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        finally { DatabaseConnection.close(rs, st); }
        return 0;
    }

    private AEFIReport map(ResultSet rs) throws SQLException {
        AEFIReport r = new AEFIReport();
        r.setAefiId(rs.getInt("aefi_id"));
        r.setPatientId(rs.getInt("patient_id"));
        r.setVaccineId(rs.getInt("vaccine_id"));
        r.setEventType(rs.getString("event_type"));
        r.setSeverity(rs.getString("severity"));
        r.setDescription(rs.getString("description"));
        r.setOnsetDate(rs.getDate("onset_date"));
        r.setReportedAt(rs.getTimestamp("reported_at"));
        r.setPatientName(rs.getString("patient_name"));
        r.setVaccineName(rs.getString("vaccine_name"));
        r.setReporterName(rs.getString("reporter_name"));
        return r;
    }
}

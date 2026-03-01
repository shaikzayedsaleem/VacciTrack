package com.vaccination.dao;

import com.vaccination.db.DatabaseConnection;
import com.vaccination.model.Appointment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    private Connection conn() { return DatabaseConnection.getInstance().getConnection(); }

    private static final String SELECT_SQL =
        "SELECT a.*, p.full_name AS patient_name, v.vaccine_name, u.full_name AS admin_name " +
        "FROM appointments a " +
        "JOIN patients p ON a.patient_id = p.patient_id " +
        "JOIN vaccines v ON a.vaccine_id = v.vaccine_id " +
        "LEFT JOIN users u ON a.administered_by = u.user_id ";

    public List<Appointment> getAll() {
        return query(SELECT_SQL + "ORDER BY a.scheduled_date DESC", null);
    }

    public List<Appointment> getByStatus(String status) {
        return query(SELECT_SQL + "WHERE a.status=? ORDER BY a.scheduled_date", new Object[]{status});
    }

    public List<Appointment> getToday() {
        return query(SELECT_SQL + "WHERE a.scheduled_date = CURDATE() ORDER BY a.scheduled_time", null);
    }

    public int add(Appointment a) {
        String sql = "INSERT INTO appointments (patient_id,vaccine_id,dose_number,scheduled_date,scheduled_time,status,administered_by,notes) " +
                     "VALUES (?,?,?,?,?,?,?,?)";
        PreparedStatement ps = null; ResultSet keys = null;
        try {
            ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, a.getPatientId()); ps.setInt(2, a.getVaccineId());
            ps.setInt(3, a.getDoseNumber()); ps.setDate(4, a.getScheduledDate());
            ps.setString(5, a.getScheduledTime()); ps.setString(6, a.getStatus());
            if (a.getAdministeredBy() > 0) ps.setInt(7, a.getAdministeredBy()); else ps.setNull(7, Types.INTEGER);
            ps.setString(8, a.getNotes());
            if (ps.executeUpdate() > 0) {
                keys = ps.getGeneratedKeys();
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { DatabaseConnection.close(keys, ps); }
        return -1;
    }

    public boolean updateStatus(int appointmentId, String status) {
        String sql = "UPDATE appointments SET status=? WHERE appointment_id=?";
        PreparedStatement ps = null;
        try {
            ps = conn().prepareStatement(sql);
            ps.setString(1, status); ps.setInt(2, appointmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
        finally { DatabaseConnection.close(null, ps); }
    }

    /** Mark as completed + create vaccination record */
    public boolean complete(int appointmentId, int adminUserId, String batchUsed, Date nextDoseDate) {
        PreparedStatement ps = null;
        try {
            conn().setAutoCommit(false);

            // 1. Get appointment info
            ps = conn().prepareStatement("SELECT * FROM appointments WHERE appointment_id=?");
            ps.setInt(1, appointmentId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) { conn().rollback(); return false; }
            int patientId = rs.getInt("patient_id");
            int vaccineId = rs.getInt("vaccine_id");
            int doseNum   = rs.getInt("dose_number");
            DatabaseConnection.close(rs, ps);

            // 2. Update appointment status
            ps = conn().prepareStatement("UPDATE appointments SET status='COMPLETED', administered_by=? WHERE appointment_id=?");
            ps.setInt(1, adminUserId); ps.setInt(2, appointmentId);
            ps.executeUpdate(); ps.close();

            // 3. Insert vaccination record
            ps = conn().prepareStatement("INSERT INTO vaccination_records (appointment_id,patient_id,vaccine_id,dose_number,administered_date,administered_by,batch_used,next_dose_date) VALUES (?,?,?,?,CURDATE(),?,?,?)");
            ps.setInt(1, appointmentId); ps.setInt(2, patientId); ps.setInt(3, vaccineId);
            ps.setInt(4, doseNum); ps.setInt(5, adminUserId); ps.setString(6, batchUsed);
            if (nextDoseDate != null) ps.setDate(7, nextDoseDate); else ps.setNull(7, Types.DATE);
            ps.executeUpdate(); ps.close();

            // 4. Deduct stock
            ps = conn().prepareStatement("UPDATE vaccines SET quantity_available = quantity_available - 1 WHERE vaccine_id=?");
            ps.setInt(1, vaccineId);
            ps.executeUpdate();

            conn().commit();
            return true;
        } catch (SQLException e) {
            try { conn().rollback(); } catch (SQLException ignored) {}
            e.printStackTrace(); return false;
        } finally {
            try { conn().setAutoCommit(true); } catch (SQLException ignored) {}
            DatabaseConnection.close(null, ps);
        }
    }

    public int getTodayCount() {
        Statement st = null; ResultSet rs = null;
        try {
            st = conn().createStatement();
            rs = st.executeQuery("SELECT COUNT(*) FROM appointments WHERE scheduled_date=CURDATE()");
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        finally { DatabaseConnection.close(rs, st); }
        return 0;
    }

    public int getCompletedTodayCount() {
        Statement st = null; ResultSet rs = null;
        try {
            st = conn().createStatement();
            rs = st.executeQuery("SELECT COUNT(*) FROM appointments WHERE scheduled_date=CURDATE() AND status='COMPLETED'");
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        finally { DatabaseConnection.close(rs, st); }
        return 0;
    }

    private List<Appointment> query(String sql, Object[] params) {
        List<Appointment> list = new ArrayList<>();
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = conn().prepareStatement(sql);
            if (params != null) {
                for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                Appointment a = new Appointment();
                a.setAppointmentId(rs.getInt("appointment_id"));
                a.setPatientId(rs.getInt("patient_id"));
                a.setVaccineId(rs.getInt("vaccine_id"));
                a.setDoseNumber(rs.getInt("dose_number"));
                a.setScheduledDate(rs.getDate("scheduled_date"));
                a.setScheduledTime(rs.getString("scheduled_time"));
                a.setStatus(rs.getString("status"));
                a.setNotes(rs.getString("notes"));
                a.setPatientName(rs.getString("patient_name"));
                a.setVaccineName(rs.getString("vaccine_name"));
                list.add(a);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { DatabaseConnection.close(rs, ps); }
        return list;
    }
}

package com.vaccination.dao;

import com.vaccination.db.DatabaseConnection;
import com.vaccination.model.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    private Connection conn() { return DatabaseConnection.getInstance().getConnection(); }

    public List<Patient> getAll() {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY full_name";
        Statement st = null; ResultSet rs = null;
        try {
            st = conn().createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        finally { DatabaseConnection.close(rs, st); }
        return list;
    }

    public Patient getById(int id) {
        String sql = "SELECT * FROM patients WHERE patient_id=?";
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

    public List<Patient> search(String keyword) {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE full_name LIKE ? OR phone LIKE ? OR id_number LIKE ?";
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = conn().prepareStatement(sql);
            String k = "%" + keyword + "%";
            ps.setString(1, k); ps.setString(2, k); ps.setString(3, k);
            rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        finally { DatabaseConnection.close(rs, ps); }
        return list;
    }

    public int add(Patient p) {
        String sql = "INSERT INTO patients (full_name,date_of_birth,age,gender,phone,email,address,id_number) VALUES (?,?,?,?,?,?,?,?)";
        PreparedStatement ps = null; ResultSet keys = null;
        try {
            ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, p.getFullName());
            ps.setDate(2, p.getDateOfBirth());
            ps.setInt(3, p.getAge());
            ps.setString(4, p.getGender());
            ps.setString(5, p.getPhone());
            ps.setString(6, p.getEmail());
            ps.setString(7, p.getAddress());
            ps.setString(8, p.getIdNumber());
            if (ps.executeUpdate() > 0) {
                keys = ps.getGeneratedKeys();
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { DatabaseConnection.close(keys, ps); }
        return -1;
    }

    public boolean update(Patient p) {
        String sql = "UPDATE patients SET full_name=?,date_of_birth=?,age=?,gender=?,phone=?,email=?,address=?,id_number=? WHERE patient_id=?";
        PreparedStatement ps = null;
        try {
            ps = conn().prepareStatement(sql);
            ps.setString(1, p.getFullName()); ps.setDate(2, p.getDateOfBirth());
            ps.setInt(3, p.getAge()); ps.setString(4, p.getGender());
            ps.setString(5, p.getPhone()); ps.setString(6, p.getEmail());
            ps.setString(7, p.getAddress()); ps.setString(8, p.getIdNumber());
            ps.setInt(9, p.getPatientId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
        finally { DatabaseConnection.close(null, ps); }
    }

    public boolean delete(int id) {
        PreparedStatement ps = null;
        try {
            ps = conn().prepareStatement("DELETE FROM patients WHERE patient_id=?");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
        finally { DatabaseConnection.close(null, ps); }
    }

    public int getTotalPatients() {
        Statement st = null; ResultSet rs = null;
        try {
            st = conn().createStatement();
            rs = st.executeQuery("SELECT COUNT(*) FROM patients");
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        finally { DatabaseConnection.close(rs, st); }
        return 0;
    }

    /** Validate dose eligibility: age range + time interval since last dose */
    public String validateEligibility(int patientId, int vaccineId) {
        // Check age
        String ageSql = "SELECT p.age, v.min_age, v.max_age, v.interval_days, v.doses_required " +
                        "FROM patients p, vaccines v WHERE p.patient_id=? AND v.vaccine_id=?";
        // Check last dose
        String lastDoseSql = "SELECT MAX(administered_date) as last_date, COUNT(*) as dose_count " +
                             "FROM vaccination_records WHERE patient_id=? AND vaccine_id=?";
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = conn().prepareStatement(ageSql);
            ps.setInt(1, patientId); ps.setInt(2, vaccineId);
            rs = ps.executeQuery();
            if (!rs.next()) return "Patient or vaccine not found";

            int age = rs.getInt("age");
            int minAge = rs.getInt("min_age");
            int maxAge = rs.getInt("max_age");
            int intervalDays = rs.getInt("interval_days");
            int dosesRequired = rs.getInt("doses_required");
            DatabaseConnection.close(rs, ps);

            if (age < minAge || age > maxAge)
                return "Age " + age + " not in eligible range (" + minAge + "-" + maxAge + ")";

            ps = conn().prepareStatement(lastDoseSql);
            ps.setInt(1, patientId); ps.setInt(2, vaccineId);
            rs = ps.executeQuery();
            if (rs.next()) {
                int doseCount = rs.getInt("dose_count");
                if (doseCount >= dosesRequired) return "All doses already completed (" + doseCount + "/" + dosesRequired + ")";
                Date lastDate = rs.getDate("last_date");
                if (lastDate != null && intervalDays > 0) {
                    long daysSince = (System.currentTimeMillis() - lastDate.getTime()) / (1000 * 60 * 60 * 24);
                    if (daysSince < intervalDays)
                        return "Must wait " + (intervalDays - daysSince) + " more day(s) between doses";
                }
            }
            return "ELIGIBLE";
        } catch (SQLException e) { e.printStackTrace(); return "Validation error: " + e.getMessage(); }
        finally { DatabaseConnection.close(rs, ps); }
    }

    private Patient map(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setPatientId(rs.getInt("patient_id"));
        p.setFullName(rs.getString("full_name"));
        p.setDateOfBirth(rs.getDate("date_of_birth"));
        p.setAge(rs.getInt("age"));
        p.setGender(rs.getString("gender"));
        p.setPhone(rs.getString("phone"));
        p.setEmail(rs.getString("email"));
        p.setAddress(rs.getString("address"));
        p.setIdNumber(rs.getString("id_number"));
        return p;
    }
}

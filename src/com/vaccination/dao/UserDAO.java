package com.vaccination.dao;

import com.vaccination.db.DatabaseConnection;
import com.vaccination.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /** Authenticate user – returns User object or null */
    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn().prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();
            if (rs.next()) {
                return map(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(rs, ps);
        }
        return null;
    }

    /** Register new user */
    public boolean register(User user) {
        String sql = "INSERT INTO users (username,password,full_name,role,email,phone) VALUES (?,?,?,?,?,?)";
        PreparedStatement ps = null;
        try {
            ps = conn().prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getRole());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getPhone());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.close(null, ps);
        }
    }

    /** Check if username exists */
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username=?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn().prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.close(rs, ps);
        }
        return false;
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY full_name";
        Statement st = null; ResultSet rs = null;
        try {
            st = conn().createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        finally { DatabaseConnection.close(rs, st); }
        return list;
    }

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setFullName(rs.getString("full_name"));
        u.setRole(rs.getString("role"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        return u;
    }
}

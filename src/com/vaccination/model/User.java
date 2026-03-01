package com.vaccination.model;

public class User {
    private int userId;
    private String username, password, fullName, role, email, phone;

    public User() {}
    public User(int userId, String username, String fullName, String role, String email, String phone) {
        this.userId = userId; this.username = username; this.fullName = fullName;
        this.role = role; this.email = email; this.phone = phone;
    }

    public int getUserId()          { return userId; }
    public String getUsername()     { return username; }
    public String getPassword()     { return password; }
    public String getFullName()     { return fullName; }
    public String getRole()         { return role; }
    public String getEmail()        { return email; }
    public String getPhone()        { return phone; }

    public void setUserId(int i)        { userId = i; }
    public void setUsername(String s)   { username = s; }
    public void setPassword(String s)   { password = s; }
    public void setFullName(String s)   { fullName = s; }
    public void setRole(String s)       { role = s; }
    public void setEmail(String s)      { email = s; }
    public void setPhone(String s)      { phone = s; }

    @Override public String toString() { return fullName + " (" + role + ")"; }
}

package com.vaccination.model;

import java.sql.Date;

public class Patient {
    private int patientId, age;
    private String fullName, gender, phone, email, address, idNumber;
    private Date dateOfBirth;

    public Patient() {}

    public int getPatientId()       { return patientId; }
    public int getAge()             { return age; }
    public String getFullName()     { return fullName; }
    public String getGender()       { return gender; }
    public String getPhone()        { return phone; }
    public String getEmail()        { return email; }
    public String getAddress()      { return address; }
    public String getIdNumber()     { return idNumber; }
    public Date getDateOfBirth()    { return dateOfBirth; }

    public void setPatientId(int i)         { patientId = i; }
    public void setAge(int i)               { age = i; }
    public void setFullName(String s)       { fullName = s; }
    public void setGender(String s)         { gender = s; }
    public void setPhone(String s)          { phone = s; }
    public void setEmail(String s)          { email = s; }
    public void setAddress(String s)        { address = s; }
    public void setIdNumber(String s)       { idNumber = s; }
    public void setDateOfBirth(Date d)      { dateOfBirth = d; }

    @Override public String toString() { return fullName; }
}

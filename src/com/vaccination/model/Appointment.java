package com.vaccination.model;

import java.sql.Date;

public class Appointment {
    private int appointmentId, patientId, vaccineId, doseNumber, administeredBy;
    private String patientName, vaccineName, scheduledTime, status, notes;
    private Date scheduledDate;

    public Appointment() {}

    public int getAppointmentId()   { return appointmentId; }
    public int getPatientId()       { return patientId; }
    public int getVaccineId()       { return vaccineId; }
    public int getDoseNumber()      { return doseNumber; }
    public int getAdministeredBy()  { return administeredBy; }
    public String getPatientName()  { return patientName; }
    public String getVaccineName()  { return vaccineName; }
    public String getScheduledTime(){ return scheduledTime; }
    public String getStatus()       { return status; }
    public String getNotes()        { return notes; }
    public Date getScheduledDate()  { return scheduledDate; }

    public void setAppointmentId(int i)     { appointmentId = i; }
    public void setPatientId(int i)         { patientId = i; }
    public void setVaccineId(int i)         { vaccineId = i; }
    public void setDoseNumber(int i)        { doseNumber = i; }
    public void setAdministeredBy(int i)    { administeredBy = i; }
    public void setPatientName(String s)    { patientName = s; }
    public void setVaccineName(String s)    { vaccineName = s; }
    public void setScheduledTime(String s)  { scheduledTime = s; }
    public void setStatus(String s)         { status = s; }
    public void setNotes(String s)          { notes = s; }
    public void setScheduledDate(Date d)    { scheduledDate = d; }
}

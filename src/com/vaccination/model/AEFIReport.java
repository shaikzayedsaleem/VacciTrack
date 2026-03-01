package com.vaccination.model;

import java.sql.Date;
import java.sql.Timestamp;

public class AEFIReport {
    private int aefiId, patientId, vaccineId, recordId, reportedBy;
    private String patientName, vaccineName, eventType, severity, description, reporterName;
    private Date onsetDate;
    private Timestamp reportedAt;

    public AEFIReport() {}

    public int getAefiId()          { return aefiId; }
    public int getPatientId()       { return patientId; }
    public int getVaccineId()       { return vaccineId; }
    public int getRecordId()        { return recordId; }
    public int getReportedBy()      { return reportedBy; }
    public String getPatientName()  { return patientName; }
    public String getVaccineName()  { return vaccineName; }
    public String getEventType()    { return eventType; }
    public String getSeverity()     { return severity; }
    public String getDescription()  { return description; }
    public String getReporterName() { return reporterName; }
    public Date getOnsetDate()      { return onsetDate; }
    public Timestamp getReportedAt(){ return reportedAt; }

    public void setAefiId(int i)            { aefiId = i; }
    public void setPatientId(int i)         { patientId = i; }
    public void setVaccineId(int i)         { vaccineId = i; }
    public void setRecordId(int i)          { recordId = i; }
    public void setReportedBy(int i)        { reportedBy = i; }
    public void setPatientName(String s)    { patientName = s; }
    public void setVaccineName(String s)    { vaccineName = s; }
    public void setEventType(String s)      { eventType = s; }
    public void setSeverity(String s)       { severity = s; }
    public void setDescription(String s)    { description = s; }
    public void setReporterName(String s)   { reporterName = s; }
    public void setOnsetDate(Date d)        { onsetDate = d; }
    public void setReportedAt(Timestamp t)  { reportedAt = t; }
}

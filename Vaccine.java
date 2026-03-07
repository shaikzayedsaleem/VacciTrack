package com.vaccination.model;

import java.sql.Date;

public class Vaccine {
    private int vaccineId, quantityAvailable, dosesRequired, intervalDays, minAge, maxAge;
    private String vaccineName, manufacturer, batchNumber, storageTemp;
    private Date expiryDate;

    public Vaccine() {}

    public int getVaccineId()             { return vaccineId; }
    public int getQuantityAvailable()     { return quantityAvailable; }
    public int getDosesRequired()         { return dosesRequired; }
    public int getIntervalDays()          { return intervalDays; }
    public int getMinAge()                { return minAge; }
    public int getMaxAge()                { return maxAge; }
    public String getVaccineName()        { return vaccineName; }
    public String getManufacturer()       { return manufacturer; }
    public String getBatchNumber()        { return batchNumber; }
    public String getStorageTemp()        { return storageTemp; }
    public Date getExpiryDate()           { return expiryDate; }

    public void setVaccineId(int i)             { vaccineId = i; }
    public void setQuantityAvailable(int i)     { quantityAvailable = i; }
    public void setDosesRequired(int i)         { dosesRequired = i; }
    public void setIntervalDays(int i)          { intervalDays = i; }
    public void setMinAge(int i)                { minAge = i; }
    public void setMaxAge(int i)                { maxAge = i; }
    public void setVaccineName(String s)        { vaccineName = s; }
    public void setManufacturer(String s)       { manufacturer = s; }
    public void setBatchNumber(String s)        { batchNumber = s; }
    public void setStorageTemp(String s)        { storageTemp = s; }
    public void setExpiryDate(Date d)           { expiryDate = d; }

    @Override public String toString() { return vaccineName; }
}

Database Schema (ER Summary)
Users/Citizens: ID (PK), Name, Age, Aadhar_No, Phone.

Vaccines: Batch_ID (PK), Name, Manufacturer, Expiry_Date, Quantity.

Vaccination_Records: Record_ID (PK), Citizen_ID (FK), Batch_ID (FK), Dose_Number, Date_Administered, AEFI_Notes.

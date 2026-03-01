# 💉 VaxDrive – Vaccination Drive Management System

> **Mullathim IbK College of Engineering and Technology**  
> Department of Computer Science and Engineering  
> MCA Industry Ready Program (IA-407) | Academic Year 2023–24 – IV Semester  
> Java Programming Module | **Week 5 Final Project**

---

## 📌 Project Overview

VaxDrive is a **fully functional, professional-grade Vaccination Drive Management System** built with:
- **Java Swing / AWT** (no HTML elements)
- **JDBC** (MySQL Connector/J)
- **MySQL 8.x** database
- **MVC architecture** (Model-View-DAO pattern)

### Problem Statement Addressed
> Large-scale vaccination programs require accurate dose scheduling, secure record management, and proper vaccine storage monitoring. Manual systems cause missed doses, incorrect inventory tracking, and poor drive monitoring.

### Our Solution Features
| Feature | Description |
|---|---|
| 🔐 Secure Login/SignUp | Role-based access (Admin / Staff) |
| 💊 Vaccine Inventory | Full CRUD, batch tracking, expiry alerts |
| 🧑‍⚕️ Patient Registry | Register, search, validate eligibility |
| 📅 Appointments | Book, complete with JDBC transactions |
| ✅ Dose Eligibility | Age range + time-interval validation |
| ⚠️ AEFI Reports | Log adverse events by severity |
| 🏅 Digital Certificates | Auto-generated vaccination certificates |
| 📊 SQL Reports | 5 analytical dashboards with live queries |

---

## 🗂️ Project Structure

```
VaccinationDrive/
├── src/com/vaccination/
│   ├── Main.java                    ← Entry point
│   ├── db/DatabaseConnection.java   ← JDBC Singleton
│   ├── model/                       ← POJOs
│   │   ├── User.java
│   │   ├── Vaccine.java
│   │   ├── Patient.java
│   │   ├── Appointment.java
│   │   └── AEFIReport.java
│   ├── dao/                         ← Data Access Objects (JDBC)
│   │   ├── UserDAO.java
│   │   ├── VaccineDAO.java
│   │   ├── PatientDAO.java
│   │   ├── AppointmentDAO.java
│   │   └── AEFIDAO.java
│   └── ui/                          ← Swing UI
│       ├── LoginFrame.java
│       ├── SignUpFrame.java
│       ├── DashboardFrame.java
│       ├── components/UITheme.java   ← Centralised theming
│       └── panels/
│           ├── DashboardPanel.java  ← KPI overview
│           ├── InventoryPanel.java
│           ├── PatientPanel.java
│           ├── AppointmentPanel.java
│           ├── AEFIPanel.java
│           ├── CertificatePanel.java
│           └── ReportPanel.java
├── database/schema.sql              ← All DDL + seed data
├── lib/                             ← Place MySQL JAR here
├── compile.sh / compile.bat
├── run.sh / run.bat
└── README.md
```

---

## ⚙️ Prerequisites

| Requirement | Version |
|---|---|
| Java JDK | 8 or higher |
| MySQL Server | 8.0+ |
| MySQL Connector/J | 8.0.33 (place in `/lib/`) |

---

## 🚀 Step-by-Step Setup

### Step 1 – Install MySQL & Java
Make sure MySQL and JDK are installed and `java`, `javac` are in your PATH.

```bash
java -version   # should show 1.8+
mysql --version # should show 8.x
```

### Step 2 – Download MySQL Connector/J
1. Visit: https://dev.mysql.com/downloads/connector/j/
2. Download **mysql-connector-j-8.0.33.jar**
3. Place it in the `lib/` folder of this project

### Step 3 – Setup Database
Open MySQL command line and run:

```sql
source /path/to/VaccinationDrive/database/schema.sql
```

Or use MySQL Workbench → Open and run `database/schema.sql`

### Step 4 – Configure Database Password
Open `src/com/vaccination/db/DatabaseConnection.java`  
Change line 12 to match your MySQL password:

```java
private static final String PASSWORD = "root";   // ← your password here
```

### Step 5 – Compile

**Linux/Mac:**
```bash
chmod +x compile.sh run.sh
bash compile.sh
```

**Windows:**
```
compile.bat
```

### Step 6 – Run

**Linux/Mac:**
```bash
bash run.sh
```

**Windows:**
```
run.bat
```

Or directly:
```bash
# Linux
java -cp "out:lib/mysql-connector-j-8.0.33.jar" com.vaccination.Main

# Windows
java -cp "out;lib\mysql-connector-j-8.0.33.jar" com.vaccination.Main
```

---

## 🔑 Default Login

| Username | Password | Role |
|---|---|---|
| `admin` | `Admin@123` | ADMIN |

You can register new staff accounts via the **Register** button on the login screen.

---

## 🖥️ Application Screens

### 1. Login Screen
- Gradient background with card-based design
- Validation with error messages
- Sign Up button to register staff

### 2. Sign Up Screen
- Full validation: username format, email, phone, password strength
- Username uniqueness check via JDBC before save

### 3. Dashboard
- Real-time KPI cards (total patients, today's appointments, low stock, AEFI)
- Quick action buttons
- System alerts (low stock, expiry warnings)
- Vaccine inventory progress bars

### 4. Vaccine Inventory
- Add / Edit / Delete vaccines
- Stock level colour coding (red < 50, amber < 150, green ≥ 150)
- Expiry date alerts
- Live search filter

### 5. Patient Registry
- Register patients with full demographic data
- Search by name, phone, or Aadhaar ID
- **Eligibility validation** – age range + time interval check

### 6. Appointments
- Book with automatic eligibility check warning
- Filter by status (ALL/SCHEDULED/COMPLETED/CANCELLED/TODAY)
- **Complete appointment** → JDBC transaction: marks complete + creates vaccination record + deducts stock

### 7. AEFI Reports
- Log adverse events with severity levels (MILD / MODERATE / SEVERE)
- Colour-coded table
- Filterable by patient and vaccine

### 8. Digital Certificates
- Select patient → generates on-screen certificate showing all vaccination records
- Certificate number saved to database
- Professional government-style layout

### 9. Reports & Analytics
- **5 analytical tabs** powered by SQL aggregate queries
- Vaccination summary, inventory status, patient stats, AEFI analysis, appointment report

---

## 🔬 JDBC Lifecycle Explained (for Viva)

```
1. Load Driver      → Class.forName("com.mysql.cj.jdbc.Driver")
2. Get Connection   → DriverManager.getConnection(URL, USER, PASS)
3. Create Statement → connection.prepareStatement(sql)
4. Set Parameters   → ps.setString(1, value)
5. Execute          → ps.executeQuery() / ps.executeUpdate()
6. Process Results  → while(rs.next()) { ... }
7. Close Resources  → rs.close(); ps.close(); [connection kept as Singleton]
```

### Transaction Example (CompleteAppointment):
```java
conn.setAutoCommit(false);
// 1. Update appointment status
// 2. Insert vaccination record
// 3. Deduct vaccine stock
conn.commit();      // All or nothing
// On error: conn.rollback();
```

---

## 📐 ER Diagram

```
users ──────────────────────────────────────┐
  user_id (PK)                              │
  username, password, full_name, role       │
                                            │ administered_by
patients ──────────────┐                   │
  patient_id (PK)      │                   │
  full_name, age, ...  │                   │
                       │ patient_id        │
vaccines ──┐           ▼                   ▼
  vaccine_id (PK)   appointments ──────────────────
  vaccine_name      │  appointment_id (PK)
  batch_number      │  patient_id (FK)
  qty_available     │  vaccine_id (FK)
  expiry_date       │  status, dose_number, date
  └─────────────────┘
         │
         ▼
  vaccination_records ─────────────── aefi_reports
   record_id (PK)                     aefi_id (PK)
   patient_id (FK)                    patient_id (FK)
   vaccine_id (FK)                    vaccine_id (FK)
   dose_number, date                  severity, description
         │
         ▼
   certificates
    cert_id (PK)
    patient_id (FK)
    certificate_number
    issued_date
```

---

## ✅ Rubric Coverage (Excellent Category)

| Criterion | Implementation |
|---|---|
| All features 100% functional | ✅ Full CRUD on all entities |
| Close explanation of JDBC lifecycle | ✅ Singleton, PreparedStatement, transactions |
| Robust validation & data checks | ✅ Client + server-side validation on every form |
| SQL-based summary dashboard | ✅ 5 report tabs with aggregate SQL queries |
| Clear user alerts & basic reporting | ✅ JOptionPane alerts, status colour coding |
| Dose eligibility validation | ✅ Age range + time-interval rules via SQL |
| AEFI logging | ✅ Severity-coded adverse event tracking |
| Digital certificates | ✅ Generated from DB vaccination records |
| Professional UI | ✅ Custom Swing theme, sidebar, cards |
| Structured presentation | ✅ MVC pattern, documented code |

---

*VaxDrive © 2024 | MCA Industry Ready Program*

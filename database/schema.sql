-- ============================================================
--  VACCINATION DRIVE MANAGEMENT SYSTEM - Database Schema
--  Muffakham Jah College of Engineering and Technology
--  Java Programming Module | MCA Industry Ready Program
-- ============================================================

CREATE DATABASE IF NOT EXISTS vaccination_db;
USE vaccination_db;

-- ─────────────────────────────────────────────────────────────
-- 1. USERS  (Admin / Staff Login)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    user_id       INT          PRIMARY KEY AUTO_INCREMENT,
    username      VARCHAR(50)  UNIQUE NOT NULL,
    password      VARCHAR(255) NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    role          ENUM('ADMIN','STAFF') DEFAULT 'STAFF',
    email         VARCHAR(100),
    phone         VARCHAR(15),
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────────────────────
-- 2. VACCINES  (Inventory)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS vaccines (
    vaccine_id         INT          PRIMARY KEY AUTO_INCREMENT,
    vaccine_name       VARCHAR(100) NOT NULL,
    manufacturer       VARCHAR(100),
    batch_number       VARCHAR(50)  UNIQUE,
    quantity_available INT          DEFAULT 0,
    expiry_date        DATE,
    doses_required     INT          DEFAULT 2,
    interval_days      INT          DEFAULT 21,
    min_age            INT          DEFAULT 0,
    max_age            INT          DEFAULT 100,
    storage_temp       VARCHAR(50)  DEFAULT '2–8°C',
    created_at         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────────────────────
-- 3. PATIENTS
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS patients (
    patient_id    INT         PRIMARY KEY AUTO_INCREMENT,
    full_name     VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    age           INT,
    gender        ENUM('Male','Female','Other'),
    phone         VARCHAR(15),
    email         VARCHAR(100),
    address       TEXT,
    id_number     VARCHAR(50) UNIQUE,
    registered_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);

-- ─────────────────────────────────────────────────────────────
-- 4. APPOINTMENTS
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS appointments (
    appointment_id  INT     PRIMARY KEY AUTO_INCREMENT,
    patient_id      INT     NOT NULL,
    vaccine_id      INT     NOT NULL,
    dose_number     INT     DEFAULT 1,
    scheduled_date  DATE    NOT NULL,
    scheduled_time  VARCHAR(10),
    status          ENUM('SCHEDULED','COMPLETED','CANCELLED','MISSED') DEFAULT 'SCHEDULED',
    administered_by INT,
    notes           TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id)      REFERENCES patients(patient_id)  ON DELETE CASCADE,
    FOREIGN KEY (vaccine_id)      REFERENCES vaccines(vaccine_id)  ON DELETE CASCADE,
    FOREIGN KEY (administered_by) REFERENCES users(user_id)        ON DELETE SET NULL
);

-- ─────────────────────────────────────────────────────────────
-- 5. VACCINATION RECORDS
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS vaccination_records (
    record_id        INT     PRIMARY KEY AUTO_INCREMENT,
    appointment_id   INT,
    patient_id       INT     NOT NULL,
    vaccine_id       INT     NOT NULL,
    dose_number      INT     DEFAULT 1,
    administered_date DATE   NOT NULL,
    administered_by  INT,
    batch_used       VARCHAR(50),
    next_dose_date   DATE,
    FOREIGN KEY (appointment_id)  REFERENCES appointments(appointment_id) ON DELETE SET NULL,
    FOREIGN KEY (patient_id)      REFERENCES patients(patient_id)         ON DELETE CASCADE,
    FOREIGN KEY (vaccine_id)      REFERENCES vaccines(vaccine_id)         ON DELETE CASCADE
);

-- ─────────────────────────────────────────────────────────────
-- 6. AEFI REPORTS  (Adverse Events Following Immunization)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS aefi_reports (
    aefi_id      INT     PRIMARY KEY AUTO_INCREMENT,
    patient_id   INT     NOT NULL,
    vaccine_id   INT     NOT NULL,
    record_id    INT,
    event_type   VARCHAR(100),
    severity     ENUM('MILD','MODERATE','SEVERE') DEFAULT 'MILD',
    description  TEXT,
    onset_date   DATE,
    reported_by  INT,
    reported_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id)  REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (vaccine_id)  REFERENCES vaccines(vaccine_id) ON DELETE CASCADE,
    FOREIGN KEY (reported_by) REFERENCES users(user_id)       ON DELETE SET NULL
);

-- ─────────────────────────────────────────────────────────────
-- 7. DIGITAL CERTIFICATES
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS certificates (
    cert_id            INT         PRIMARY KEY AUTO_INCREMENT,
    patient_id         INT         NOT NULL,
    certificate_number VARCHAR(50) UNIQUE,
    issued_date        DATE        NOT NULL,
    issued_by          INT,
    vaccines_covered   TEXT,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (issued_by)  REFERENCES users(user_id)       ON DELETE SET NULL
);

-- ─────────────────────────────────────────────────────────────
-- SEED DATA
-- ─────────────────────────────────────────────────────────────
-- Default admin  (password: Admin@123  stored as plain for demo; use BCrypt in prod)
INSERT IGNORE INTO users (username, password, full_name, role, email, phone)
VALUES ('admin', 'Admin@123', 'System Administrator', 'ADMIN', 'admin@vaccination.gov', '9999999999');

INSERT IGNORE INTO vaccines (vaccine_name, manufacturer, batch_number, quantity_available, expiry_date, doses_required, interval_days, min_age, max_age, storage_temp)
VALUES
('COVID-19 (Covishield)',   'Serum Institute', 'SII-2024-001', 500, '2025-12-31', 2, 28,  18, 80, '2–8°C'),
('COVID-19 (Covaxin)',      'Bharat Biotech',  'BB-2024-002',  300, '2025-10-31', 2, 28,  18, 80, '2–8°C'),
('MMR',                     'Sanofi Pasteur',  'SP-2024-003',  200, '2026-06-30', 2, 42,   0, 12, '2–8°C'),
('Hepatitis B',             'GlaxoSmithKline', 'GSK-2024-004', 400, '2025-08-31', 3, 30,   0, 99, '2–8°C'),
('Polio (OPV)',              'Panacea Biotech', 'PB-2024-005',  600, '2025-03-31', 4, 30,   0,  5, '-15 to -25°C'),
('Influenza',               'Abbott',          'ABT-2024-006', 250, '2025-04-30', 1,  0,  65, 99, '2–8°C'),
('HPV (Gardasil)',          'Merck',           'MRK-2024-007', 150, '2026-01-31', 3, 60,   9, 26, '2–8°C'),
('Typhoid',                 'Biological E',    'BE-2024-008',  350, '2025-09-30', 1,  0,   2, 99, '2–8°C');

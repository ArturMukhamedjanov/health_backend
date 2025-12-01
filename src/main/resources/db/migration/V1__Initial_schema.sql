-- Initial database schema for Health application

-- Create _user table
CREATE TABLE IF NOT EXISTS _user (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_user_email ON _user(email);

-- Create clinics table
CREATE TABLE IF NOT EXISTS clinics (
    id BIGSERIAL PRIMARY KEY,
    _user BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    CONSTRAINT fk_clinic_user FOREIGN KEY (_user) REFERENCES _user(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_clinic_user ON clinics(_user);
CREATE INDEX IF NOT EXISTS idx_clinic_name ON clinics(name);

-- Create customers table
CREATE TABLE IF NOT EXISTS customers (
    id BIGSERIAL PRIMARY KEY,
    _user BIGINT NOT NULL,
    firstName VARCHAR(255) NOT NULL,
    lastName VARCHAR(255) NOT NULL,
    age INTEGER,
    weight INTEGER,
    gender VARCHAR(50),
    height INTEGER,
    CONSTRAINT fk_customer_user FOREIGN KEY (_user) REFERENCES _user(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_customer_user ON customers(_user);

-- Create doctors table
CREATE TABLE IF NOT EXISTS doctors (
    id BIGSERIAL PRIMARY KEY,
    _user BIGINT NOT NULL,
    clinics BIGINT NOT NULL,
    firstame VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    speciality VARCHAR(255) NOT NULL,
    CONSTRAINT fk_doctor_user FOREIGN KEY (_user) REFERENCES _user(id) ON DELETE CASCADE,
    CONSTRAINT fk_doctor_clinic FOREIGN KEY (clinics) REFERENCES clinics(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_doctor_user ON doctors(_user);
CREATE INDEX IF NOT EXISTS idx_doctor_clinic ON doctors(clinics);
CREATE INDEX IF NOT EXISTS idx_doctor_speciality ON doctors(speciality);
CREATE INDEX IF NOT EXISTS idx_doctor_name ON doctors(firstName, lastname);

-- Create timetables table
CREATE TABLE IF NOT EXISTS timetables (
    id BIGSERIAL PRIMARY KEY,
    doctors BIGINT NOT NULL,
    start TIMESTAMP NOT NULL UNIQUE,
    reserved BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_timetable_doctor FOREIGN KEY (doctors) REFERENCES doctors(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_timetable_doctor ON timetables(doctors);
CREATE INDEX IF NOT EXISTS idx_timetable_start ON timetables(start);
CREATE INDEX IF NOT EXISTS idx_timetable_reserved ON timetables(reserved);
CREATE INDEX IF NOT EXISTS idx_timetable_doctor_reserved ON timetables(doctors, reserved);

-- Create appointments table
CREATE TABLE IF NOT EXISTS appointments (
    id BIGSERIAL PRIMARY KEY,
    clinics BIGINT NOT NULL,
    doctors BIGINT NOT NULL,
    customers BIGINT NOT NULL,
    timetables BIGINT NOT NULL,
    CONSTRAINT fk_appointment_clinic FOREIGN KEY (clinics) REFERENCES clinics(id) ON DELETE CASCADE,
    CONSTRAINT fk_appointment_doctor FOREIGN KEY (doctors) REFERENCES doctors(id) ON DELETE CASCADE,
    CONSTRAINT fk_appointment_customer FOREIGN KEY (customers) REFERENCES customers(id) ON DELETE CASCADE,
    CONSTRAINT fk_appointment_timetable FOREIGN KEY (timetables) REFERENCES timetables(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_appointment_clinic ON appointments(clinics);
CREATE INDEX IF NOT EXISTS idx_appointment_doctor ON appointments(doctors);
CREATE INDEX IF NOT EXISTS idx_appointment_customer ON appointments(customers);
CREATE INDEX IF NOT EXISTS idx_appointment_timetable ON appointments(timetables);
CREATE INDEX IF NOT EXISTS idx_appointment_doctor_customer ON appointments(doctors, customers);

-- Create chats table
CREATE TABLE IF NOT EXISTS chats (
    id BIGSERIAL PRIMARY KEY,
    clinics BIGINT NOT NULL,
    doctors BIGINT NOT NULL,
    customers BIGINT NOT NULL,
    CONSTRAINT fk_chat_clinic FOREIGN KEY (clinics) REFERENCES clinics(id) ON DELETE CASCADE,
    CONSTRAINT fk_chat_doctor FOREIGN KEY (doctors) REFERENCES doctors(id) ON DELETE CASCADE,
    CONSTRAINT fk_chat_customer FOREIGN KEY (customers) REFERENCES customers(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_chat_clinic ON chats(clinics);
CREATE INDEX IF NOT EXISTS idx_chat_doctor ON chats(doctors);
CREATE INDEX IF NOT EXISTS idx_chat_customer ON chats(customers);
CREATE INDEX IF NOT EXISTS idx_chat_doctor_customer ON chats(doctors, customers);

-- Create messages table
CREATE TABLE IF NOT EXISTS messages (
    id BIGSERIAL PRIMARY KEY,
    chats BIGINT NOT NULL,
    text TEXT NOT NULL,
    role VARCHAR(50) NOT NULL,
    sendtime TIMESTAMP NOT NULL,
    CONSTRAINT fk_message_chat FOREIGN KEY (chats) REFERENCES chats(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_message_chat ON messages(chats);
CREATE INDEX IF NOT EXISTS idx_message_sendtime ON messages(sendtime);
CREATE INDEX IF NOT EXISTS idx_message_chat_sendtime ON messages(chats, sendtime);

-- Create analysis table
CREATE TABLE IF NOT EXISTS analysis (
    id BIGSERIAL PRIMARY KEY,
    customers BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    value VARCHAR(255) NOT NULL,
    unit VARCHAR(255) NOT NULL,
    date TIMESTAMP NOT NULL,
    CONSTRAINT fk_analysis_customer FOREIGN KEY (customers) REFERENCES customers(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_analysis_customer ON analysis(customers);
CREATE INDEX IF NOT EXISTS idx_analysis_name ON analysis(name);
CREATE INDEX IF NOT EXISTS idx_analysis_date ON analysis(date);
CREATE INDEX IF NOT EXISTS idx_analysis_customer_name ON analysis(customers, name);
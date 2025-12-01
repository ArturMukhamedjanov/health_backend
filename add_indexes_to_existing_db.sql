-- Script to add indexes to existing database
-- Run this manually in your PostgreSQL database to add performance indexes
-- This is for databases that were created by Hibernate ddl-auto before Flyway was enabled

-- User table indexes
CREATE INDEX IF NOT EXISTS idx_user_email ON _user(email);

-- Clinic table indexes
CREATE INDEX IF NOT EXISTS idx_clinic_user ON clinics(_user);
CREATE INDEX IF NOT EXISTS idx_clinic_name ON clinics(name);

-- Customer table indexes  
CREATE INDEX IF NOT EXISTS idx_customer_user ON customers(_user);

-- Doctor table indexes
CREATE INDEX IF NOT EXISTS idx_doctor_user ON doctors(_user);
CREATE INDEX IF NOT EXISTS idx_doctor_clinic ON doctors(clinics);
CREATE INDEX IF NOT EXISTS idx_doctor_speciality ON doctors(speciality);
CREATE INDEX IF NOT EXISTS idx_doctor_name ON doctors(firstname, lastname);

-- Timetable table indexes
CREATE INDEX IF NOT EXISTS idx_timetable_doctor ON timetables(doctors);
CREATE INDEX IF NOT EXISTS idx_timetable_start ON timetables(start);
CREATE INDEX IF NOT EXISTS idx_timetable_reserved ON timetables(reserved);
CREATE INDEX IF NOT EXISTS idx_timetable_doctor_reserved ON timetables(doctors, reserved);

-- Appointment table indexes
CREATE INDEX IF NOT EXISTS idx_appointment_clinic ON appointments(clinics);
CREATE INDEX IF NOT EXISTS idx_appointment_doctor ON appointments(doctors);
CREATE INDEX IF NOT EXISTS idx_appointment_customer ON appointments(customers);
CREATE INDEX IF NOT EXISTS idx_appointment_timetable ON appointments(timetables);
CREATE INDEX IF NOT EXISTS idx_appointment_doctor_customer ON appointments(doctors, customers);

-- Chat table indexes
CREATE INDEX IF NOT EXISTS idx_chat_clinic ON chats(clinics);
CREATE INDEX IF NOT EXISTS idx_chat_doctor ON chats(doctors);
CREATE INDEX IF NOT EXISTS idx_chat_customer ON chats(customers);
CREATE INDEX IF NOT EXISTS idx_chat_doctor_customer ON chats(doctors, customers);

-- Message table indexes
CREATE INDEX IF NOT EXISTS idx_message_chat ON messages(chats);
CREATE INDEX IF NOT EXISTS idx_message_sendtime ON messages(sendtime);
CREATE INDEX IF NOT EXISTS idx_message_chat_sendtime ON messages(chats, sendtime);

-- Analysis table indexes
CREATE INDEX IF NOT EXISTS idx_analysis_customer ON analysis(customers);
CREATE INDEX IF NOT EXISTS idx_analysis_name ON analysis(name);
CREATE INDEX IF NOT EXISTS idx_analysis_date ON analysis(date);
CREATE INDEX IF NOT EXISTS idx_analysis_customer_name ON analysis(customers, name);

-- Verify indexes were created
SELECT schemaname, tablename, indexname 
FROM pg_indexes 
WHERE schemaname = 'public' 
ORDER BY tablename, indexname;
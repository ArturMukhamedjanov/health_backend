-- Script to fix failed Flyway migration
-- Run this in your PostgreSQL database before restarting the application

-- Delete the failed migration record
DELETE FROM flyway_schema_history WHERE version = '1' AND success = false;

-- If you want to completely reset Flyway (use with caution):
-- DROP TABLE IF EXISTS flyway_schema_history;
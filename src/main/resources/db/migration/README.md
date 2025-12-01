# Database Migrations

This directory contains Flyway database migration scripts for the Health application.

## Migration Naming Convention

Flyway migrations follow this naming pattern:
- `V{version}__{description}.sql` - Versioned migrations (e.g., V1__Initial_schema.sql)
- `R__{description}.sql` - Repeatable migrations (run on every change)

## How to Create a New Migration

1. Create a new SQL file in this directory
2. Name it following the pattern: `V{next_version}__{description}.sql`
   - Example: `V2__Add_user_profile_table.sql`
3. Write your SQL DDL statements
4. Restart the application - Flyway will automatically apply the migration

## Migration Best Practices

1. **Never modify existing migrations** - Once a migration is applied, create a new one instead
2. **Always test migrations** - Test on a development database first
3. **Use transactions** - Wrap DDL statements in transactions when possible
4. **Include rollback scripts** - Document how to rollback changes in comments
5. **Keep migrations small** - One logical change per migration
6. **Add indexes carefully** - Consider using `CREATE INDEX CONCURRENTLY` for large tables

## Example Migration

```sql
-- V2__Add_user_profile_table.sql

-- Add new column to users table
ALTER TABLE _user ADD COLUMN IF NOT EXISTS phone_number VARCHAR(20);

-- Create index for phone number lookups
CREATE INDEX IF NOT EXISTS idx_user_phone ON _user(phone_number);

-- Rollback instructions:
-- ALTER TABLE _user DROP COLUMN IF EXISTS phone_number;
```

## Current Schema Version

Check the current schema version by querying:
```sql
SELECT * FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 1;
```

## Troubleshooting

If a migration fails:
1. Check the `flyway_schema_history` table for error details
2. Fix the issue in the database manually if needed
3. Update the migration file
4. Run: `mvn flyway:repair` to mark the failed migration as resolved
5. Restart the application

## Configuration

Flyway is configured in `application.properties`:
- `spring.flyway.enabled=true` - Enable Flyway
- `spring.flyway.baseline-on-migrate=true` - Baseline existing databases
- `spring.flyway.locations=classpath:db/migration` - Migration scripts location
- `spring.flyway.validate-on-migrate=true` - Validate migrations on startup
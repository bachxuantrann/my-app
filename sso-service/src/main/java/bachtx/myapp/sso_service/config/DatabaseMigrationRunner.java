package bachtx.myapp.sso_service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseMigrationRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            log.info("Starting database migrations...");
            // Drop NOT NULL constraint on client_secret_expires_at
            jdbcTemplate.execute("ALTER TABLE oauth2_registered_client ALTER COLUMN client_secret_expires_at DROP NOT NULL;");
            log.info("Successfully dropped NOT NULL constraint on client_secret_expires_at in oauth2_registered_client table.");

            // Fix user_id datatype mismatch
            jdbcTemplate.execute("ALTER TABLE audit_logs RENAME COLUMN user_id TO username;");
            jdbcTemplate.execute("ALTER TABLE audit_logs ADD COLUMN IF NOT EXISTS user_id BIGINT;");
            log.info("Migrated audit_logs table (renamed user_id to username and added BIGINT user_id).");
        } catch (Exception e) {
            log.warn("Migration warning (might have been applied already): {}", e.getMessage());
        }
    }
}

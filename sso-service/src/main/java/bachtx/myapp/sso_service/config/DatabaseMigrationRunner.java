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

            // Also check if audit_logs table exists and drop index if needed or other minor cleanups.
        } catch (Exception e) {
            log.warn("Migration warning (might have been applied already): {}", e.getMessage());
        }
    }
}

package db.migration.data.V0._3;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;

/**
 * @author Hadi Movaghar
 */
public class V0_3_11__Insert_admin_into_users implements SpringJdbcMigration {
    @Override
    public void migrate(final JdbcTemplate jdbcTemplate)
            throws Exception {
        final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        final String password = "'" + passwordEncoder.encode("test") + "'";

        jdbcTemplate.execute("insert into users (" +
                             "id, first_name, last_name, email_address, password, nickname, type," +
                             "phone_number, status, created_by, created_date" +
                             ") values (" +
                             "1, 'Joakim', 'Gottzén', 'joakim@animatedgames.se'," + password + ", 'jake', 'ADMIN'," +
                             "'', 'ACTIVE', 1, current_timestamp" +
                             ");");
    }
}

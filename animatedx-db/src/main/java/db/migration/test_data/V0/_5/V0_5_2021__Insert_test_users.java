package db.migration.test_data.V0._5;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;

/**
 * @author Joakim Gottzén
 */
public class V0_5_2021__Insert_test_users implements SpringJdbcMigration {
    @Override
    public void migrate(final JdbcTemplate jdbcTemplate)
            throws Exception {
        final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        final String password = "'" + passwordEncoder.encode("test") + "'";

        jdbcTemplate.execute("insert into users (" +
                             "id, first_name, last_name, email_address, password, nickname, type," +
                             "phone_number, status, created_by, created_date" +
                             ") values (" +
                             "2, 'Nicholas', 'Gatt', 'ko@animatedgames.se'," + password + ", 'nick', 'ADMIN'," +
                             "'', 'ACTIVE', 1, current_timestamp" +
                             ");");

        jdbcTemplate.execute("insert into users (" +
                             "id, first_name, last_name, email_address, password, nickname, type," +
                             "phone_number, status, created_by, created_date" +
                             ") values (" +
                             "3, 'MLRO', 'MLRO', 'mlro@animatedgames.se'," + password + ", 'mlro', 'ADMIN'," +
                             "'', 'ACTIVE', 1, current_timestamp" +
                             ");");

        jdbcTemplate.execute("insert into users (" +
                             "id, first_name, last_name, email_address, password, nickname, type," +
                             "phone_number, status, created_by, created_date" +
                             ") values (" +
                             "4, 'Marketing', 'Manager', 'marketing@animatedgames.se'," + password + ", 'marketing', 'ADMIN'," +
                             "'', 'ACTIVE', 1, current_timestamp" +
                             ");");

        jdbcTemplate.execute("insert into users (" +
                             "id, first_name, last_name, email_address, password, nickname, type," +
                             "phone_number, status, created_by, created_date" +
                             ") values (" +
                             "5, 'Casino', 'Manager', 'casino@animatedgames.se'," + password + ", 'casino', 'ADMIN'," +
                             "'', 'ACTIVE', 1, current_timestamp" +
                             ");");

        jdbcTemplate.execute("insert into users (" +
                             "id, first_name, last_name, email_address, password, nickname, type," +
                             "phone_number, status, created_by, created_date" +
                             ") values (" +
                             "6, 'Online', 'Marketing Manager', 'online@animatedgames.se'," + password + ", 'online', 'ADMIN'," +
                             "'', 'ACTIVE', 1, current_timestamp" +
                             ");");

        jdbcTemplate.execute("insert into users (" +
                             "id, first_name, last_name, email_address, password, nickname, type," +
                             "phone_number, status, created_by, created_date" +
                             ") values (" +
                             "7, 'Payments', 'Manager', 'payments@animatedgames.se'," + password + ", 'payments', 'ADMIN'," +
                             "'', 'ACTIVE', 1, current_timestamp" +
                             ");");

        jdbcTemplate.execute("insert into users (" +
                             "id, first_name, last_name, email_address, password, nickname, type," +
                             "phone_number, status, created_by, created_date" +
                             ") values (" +
                             "8, 'Customer', 'Support Manager', 'csm@animatedgames.se'," + password + ", 'csm', 'ADMIN'," +
                             "'', 'ACTIVE', 1, current_timestamp" +
                             ");");

        jdbcTemplate.execute("insert into users (" +
                             "id, first_name, last_name, email_address, password, nickname, type," +
                             "phone_number, status, created_by, created_date" +
                             ") values (" +
                             "9, 'Team Leader', 'Support', 'cstl@animatedgames.se'," + password + ", 'cstl', 'SUPPORT'," +
                             "'', 'ACTIVE', 1, current_timestamp" +
                             ");");

        jdbcTemplate.execute("insert into users (" +
                             "id, first_name, last_name, email_address, password, nickname, type," +
                             "phone_number, status, created_by, created_date" +
                             ") values (" +
                             "10, 'Agent', 'Support', 'agent@animatedgames.se'," + password + ", 'agent', 'SUPPORT'," +
                             "'', 'ACTIVE', 1, current_timestamp" +
                             ");");
    }
}

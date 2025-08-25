// src/main/java/hi_light/BootCheck.java
package hi_light;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BootCheck implements CommandLineRunner {
    private final JdbcTemplate jdbc;

    @Override
    public void run(String... args) {
        jdbc.queryForObject("SELECT 1", Integer.class);
        System.out.println("[DB OK] SELECT 1 성공");
    }
}

package dev.turbin.scraper.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HealthCheckRepository {
    private final JdbcTemplate jdbcTemplate;

    public int check() {
        return Optional.ofNullable(jdbcTemplate.queryForObject("select 1", Integer.class)).orElse(0);
    }
}

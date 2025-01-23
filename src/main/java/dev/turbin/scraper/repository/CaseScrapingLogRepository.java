package dev.turbin.scraper.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import dev.turbin.scraper.entity.CaseScrapingLogEntity;

@Repository
@RequiredArgsConstructor
public class CaseScrapingLogRepository {
    private final JdbcTemplate jdbcTemplate;

    public void save(CaseScrapingLogEntity entity) {


            String insertSql = "insert into case_scraping_log " +
                    "(case_id, scraping_task_id, code, result) " +
                    "VALUES (?, ?, ?, ?)";

            jdbcTemplate.update(insertSql,
                    entity.getCaseId(),
                    entity.getScrapingTaskId(),
                    entity.getCode(),
                    entity.getResult());

    }
}

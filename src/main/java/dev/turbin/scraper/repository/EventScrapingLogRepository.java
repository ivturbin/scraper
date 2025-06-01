package dev.turbin.scraper.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import dev.turbin.scraper.entity.CaseEventEntity;
import dev.turbin.scraper.entity.ScrapingTaskEntity;
import dev.turbin.scraper.enums.EventScrapingCode;

@Repository
@RequiredArgsConstructor
public class EventScrapingLogRepository {
    private final JdbcTemplate jdbcTemplate;

    public void saveEventLog(CaseEventEntity entity, ScrapingTaskEntity scrapingTaskEntity, EventScrapingCode eventScrapingCode) {
        String insertSql = "insert into event_scraping_log " +
                "(event_id, scraping_task_id, code) " +
                "values (?, ?, ?)";

        jdbcTemplate.update(insertSql,
                entity.getCaseEventId(),
                scrapingTaskEntity.getScrapingTaskId(),
                eventScrapingCode.name());
    }
}

package ru.develop.turbin.scraper.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.develop.turbin.scraper.entity.CaseEventEntity;
import ru.develop.turbin.scraper.entity.ScrapingTaskEntity;
import ru.develop.turbin.scraper.enums.EventScrapingCode;

@Repository
@RequiredArgsConstructor
public class EventScrapingLogRepository {
    private final JdbcTemplate jdbcTemplate;

    public void saveEventLog(CaseEventEntity entity, ScrapingTaskEntity scrapingTaskEntity) {
        String insertSql = "insert into event_scraping_log " +
                "(event_id, scraping_task_id, code) " +
                "values (?, ?, ?)";

        jdbcTemplate.update(insertSql,
                entity.getCaseEventId(),
                scrapingTaskEntity.getScrapingTaskId(),
                EventScrapingCode.OK.name());
    }
}

package dev.turbin.scraper.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import dev.turbin.scraper.entity.ScrapingTaskEntity;

import java.sql.PreparedStatement;

@Repository
@RequiredArgsConstructor
public class ScrapingTaskRepository {

    private final JdbcTemplate jdbcTemplate;

    public Long save(ScrapingTaskEntity taskEntity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String insertSql = "insert into scraping_task " +
                "(task_type, task_status, passed, failed) " +
                "values (?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, new String[] {"scraping_task_id"});
            ps.setString(1, taskEntity.getTaskType());
            ps.setString(2, taskEntity.getTaskStatus());
            ps.setInt(3, taskEntity.getPassed());
            ps.setInt(4, taskEntity.getFailed());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public void updateTask(ScrapingTaskEntity taskEntity) {
        jdbcTemplate.update("update scraping_task " +
                        "set " +
                        "task_status = ?, " +
                        "task_details = ?, " +
                        "passed = ?, " +
                        "failed = ? " +
                        "where " +
                        "scraping_task_id = ?",
                taskEntity.getTaskStatus(),
                taskEntity.getTaskDetails(),
                taskEntity.getPassed(),
                taskEntity.getFailed(),
                taskEntity.getScrapingTaskId()
        );
    }

    public void endTask(ScrapingTaskEntity taskEntity) {
        jdbcTemplate.update("update scraping_task " +
                        "set " +
                        "task_status = ?, " +
                        "task_details = ?, " +
                        "end_dttm = now() " +
                        "where " +
                        "scraping_task_id = ?",
                taskEntity.getTaskStatus(),
                taskEntity.getTaskDetails(),
                taskEntity.getScrapingTaskId()
        );
    }
}

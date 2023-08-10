package ru.develop.turbin.scraper.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.develop.turbin.scraper.entity.CourtCaseEntity;

import java.sql.PreparedStatement;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CourtCaseRepository {

    private final JdbcTemplate jdbcTemplate;

    public CourtCaseEntity getById(Long caseId) {
        return jdbcTemplate.queryForObject("select * from court_case where case_id = ?",
                   (rs, rowNum) -> new CourtCaseEntity(rs.getLong(1),
                           rs.getString(2),
                           rs.getTimestamp(3).toInstant().atOffset(ZoneOffset.UTC),
                           rs.getTimestamp(4).toInstant().atOffset(ZoneOffset.UTC),
                           rs.getString(5),
                           rs.getBoolean(6),
                           rs.getString(7)),
                caseId);
    }

    public CourtCaseEntity getByNumber(String caseNumber) {
        try {
            CourtCaseEntity courtCaseEntity = jdbcTemplate.queryForObject("select * from court_case where case_number = ?",
                    (rs, rowNum) -> new CourtCaseEntity(rs.getLong(1),
                            rs.getString(2),
                            rs.getTimestamp(3).toInstant().atOffset(ZoneOffset.UTC),
                            rs.getTimestamp(4).toInstant().atOffset(ZoneOffset.UTC),
                            rs.getString(5),
                            rs.getBoolean(6),
                            rs.getString(7)),
                    caseNumber);
            log.info("Дело {} найдено в БД", caseNumber);
            return courtCaseEntity;
        } catch (EmptyResultDataAccessException e) {
            log.info("Дело {} не найдено в БД", caseNumber);
            return null;
        }
    }

    public Long save(CourtCaseEntity caseEntity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String insertSql = "INSERT INTO court_case " +
                "(case_number, case_link, is_scraped) " +
                "VALUES (?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, new String[] {"case_id"});
            ps.setString(1, caseEntity.getCaseNumber());
            ps.setString(2, caseEntity.getCaseLink());
            ps.setBoolean(3, caseEntity.getIsScraped());
            return ps;
        }, keyHolder);

        log.info("Дело {} сохранено в БД", caseEntity.getCaseNumber());

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public List<String> getAllNumbersToScrape() {
        List<String> caseNumbers = jdbcTemplate.query("select case_number from court_case where is_scraped",
                (rs, rowNum) ->
                rs.getString(1));
        log.info("Получены все номера дел для скрейпинга");

        return caseNumbers;
    }

    public String getNumberToScrape() {
        String caseNumber = jdbcTemplate.queryForObject("select case_number " +
                        "from court_case " +
                        "where is_scraped " +
                        "order by updated is not null, updated, create_dttm desc " +
                        "limit 1",
                (rs, rowNum) ->
                        rs.getString(1));
        log.info("Получен номер дела {} для скрейпинга", caseNumber);

        return caseNumber;
    }

    public void updateCase(CourtCaseEntity caseEntity) {
        jdbcTemplate.update("update court_case " +
                        "set " +
                        "case_link = ?, " +
                        "is_scraped = ?, " +
                        "modify_dttm = now(), " +
                        "updated = now() " +
                        "where " +
                        "case_id = ?",
                caseEntity.getCaseLink(),
                caseEntity.getIsScraped(),
                caseEntity.getCaseId()
        );
    }

    public void markUpdated(String caseNumber) {
        jdbcTemplate.update("update court_case " +
                        "set " +
                        "updated = now(), " +
                        "modify_dttm = now() " +
                        "where " +
                        "case_number = ?",
                caseNumber
        );
    }
}

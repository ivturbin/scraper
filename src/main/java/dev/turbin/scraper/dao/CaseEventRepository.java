package dev.turbin.scraper.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import dev.turbin.scraper.entity.CaseEventEntity;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class CaseEventRepository {
    private final JdbcTemplate jdbcTemplate;

    public Long save(CaseEventEntity eventEntity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("insert into case_event " +
                    "(instantion, event_hash, event_id, event_date," +
                    "event_type, event_actor, event_description, file_link," +
                    "file_info, additional_info, is_signed, " +
                    "signature_info, original_data, event_case_id, court_name, " +
                    "data_court, create_dttm)" +
                    "values (?, ?, ?, ?," +
                    "?, ?, ?, ?," +
                    "?, ?, ?," +
                    "?, ?, ?, ?," +
                    "?, now())", new String[] {"case_event_id"});
            ps.setString(1, eventEntity.getInstantion());
            ps.setLong(2, eventEntity.getEventHash());
            ps.setString(3, eventEntity.getEventId());
            ps.setDate(4, Date.valueOf(eventEntity.getEventDate()));
            ps.setString(5, eventEntity.getEventType());
            ps.setString(6, eventEntity.getEventActor());
            ps.setString(7, eventEntity.getEventDescription());
            ps.setString(8, eventEntity.getFileLink());
            ps.setString(9, eventEntity.getFileInfo());
            ps.setString(10, eventEntity.getAdditionalInfo());
            ps.setBoolean(11, eventEntity.getIsSigned());
            ps.setString(12, eventEntity.getSignatureInfo());
            ps.setString(13, eventEntity.getOriginalData());
            ps.setLong(14, eventEntity.getEventCaseId());
            ps.setString(15, eventEntity.getCourtName());
            ps.setString(16, eventEntity.getDataCourt());
            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public void updateFileDataById(byte[] fileData, Long eventId) {
        jdbcTemplate.update("update case_event set file_data = ? where case_event_id = ?",
                fileData, eventId);
    }

    public List<CaseEventEntity> getByCaseId(Long caseId) {
        return jdbcTemplate.query("select * from case_event where event_case_id = ?",
                (rs, rowNum) -> new CaseEventEntity(rs.getLong(1),
                        rs.getString(2),
                        rs.getLong(3),
                        rs.getString(4),
                        rs.getDate(5).toLocalDate(),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getString(9),
                        rs.getBytes(10),
                        rs.getString(11),
                        rs.getString(12),
                        rs.getBoolean(13),
                        rs.getString(14),
                        rs.getString(15),
                        rs.getTimestamp(16).toInstant().atOffset(ZoneOffset.UTC),
                        rs.getTimestamp(17).toInstant().atOffset(ZoneOffset.UTC),
                        rs.getBoolean(18),
                        rs.getLong(19),
                        rs.getString(20),
                        rs.getString(21)),
                caseId);
    }

    public void update(CaseEventEntity caseEventEntity) {
        jdbcTemplate.update("update case_event " +
                        "set " +
                        "file_link = ?, " +
                        "file_info = ?, " +
                        "additional_info = ?, " +
                        "is_signed = ?, " +
                        "signature_info = ?, " +
                        "original_data = ?, " +
                        "modify_dttm = now() " +
                        "where case_event_id = ?",
                caseEventEntity.getFileLink(),
                caseEventEntity.getFileInfo(),
                caseEventEntity.getAdditionalInfo(),
                caseEventEntity.getIsSigned(),
                caseEventEntity.getSignatureInfo(),
                caseEventEntity.getOriginalData(),
                caseEventEntity.getCaseEventId()
                );
    }
}

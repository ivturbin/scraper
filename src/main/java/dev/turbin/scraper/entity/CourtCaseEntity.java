package dev.turbin.scraper.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourtCaseEntity {
    private Long caseId;
    private String caseNumber;
    private OffsetDateTime createDttm;
    private OffsetDateTime modifyDttm;
    private String caseLink;
    private Boolean isScraped;
    private String case1CId;
}

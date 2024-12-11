package dev.turbin.scraper.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseEventEntity {
    private Long caseEventId;
    private String instantion;
    private Long eventHash;
    private String eventId;
    private LocalDate eventDate;
    private String eventType;
    private String eventActor;
    private String eventDescription;
    private String fileLink;
    private byte[] fileData;
    private String fileInfo;
    private String additionalInfo;
    private Boolean isSigned;
    private String signatureInfo;
    private String originalData;
    private OffsetDateTime createDttm;
    private OffsetDateTime modifyDttm;
    private Boolean received1C;
    private Long eventCaseId;
    private String courtName;
    private String dataCourt;

    public void enrichEventHash(String caseNumber) {
        this.setEventHash((long) Objects.hash(caseNumber) +
                (long) Objects.hash(this.eventId) +
                (long) Objects.hash(this.eventDate) +
                (long) Objects.hash(this.eventActor) +
                (long) Objects.hash(this.eventType) +
                (long) Objects.hash(this.courtName) +
                (long) Objects.hash(this.dataCourt) +
                (long) Objects.hash(this.eventDescription));
    }
}

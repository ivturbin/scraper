package dev.turbin.scraper.model;

import lombok.Data;

@Data
public class CaseItem {
    private String dataDate;
    private String dataId;
    private String caseDate;
    private String caseType;
    private String additionalComment;
    private String eventActor;
    private String fileLink;
    private String eventDescription;
    private String fileDescription;
    private String fileDateOfPublication;
    private String signatureInfo;
    private String additionalInfo;
    private boolean isSigned;
}

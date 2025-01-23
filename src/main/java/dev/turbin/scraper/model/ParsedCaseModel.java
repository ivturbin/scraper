package dev.turbin.scraper.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class ParsedCaseModel {
    private String caseNumber;
    private String caseLink;
    private Map<CaseHeader, List<CaseItem>> parsedEventsByHeader;
    private final StringBuilder errorBuilder = new StringBuilder();
}

package dev.turbin.scraper.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class ParsedInfoModel {
    String caseNumber;
    String caseLink;
    Map<CaseHeader, List<CaseItem>> parsedEventsByHeader;
}

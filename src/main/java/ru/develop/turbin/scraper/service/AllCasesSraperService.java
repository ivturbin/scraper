package ru.develop.turbin.scraper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.develop.turbin.scraper.dao.CourtCaseRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AllCasesSraperService {
    private final CaseScraperService caseScraperService;
    private final CourtCaseRepository courtCaseRepository;

    public void scrapeAllCases() {
        List<String> caseNumbers = courtCaseRepository.getAllNumbersToScrape();
        caseScraperService.scrapeCases(caseNumbers);
    }
}

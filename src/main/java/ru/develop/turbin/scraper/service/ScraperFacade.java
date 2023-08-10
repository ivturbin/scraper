package ru.develop.turbin.scraper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.develop.turbin.scraper.dao.CourtCaseRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScraperFacade {
    private final CaseScraperService caseScraperService;
    private final CourtCaseRepository courtCaseRepository;

    public void scrapeAllCases() {
        List<String> caseNumbers = courtCaseRepository.getAllNumbersToScrape();
        caseScraperService.scrapeCases(caseNumbers);
    }

    public void scrapeNextCase() {
        String caseNumber = courtCaseRepository.getNumberToScrape();
        caseScraperService.scrapeCase(caseNumber);
    }

    public void scrapeCaseByNumber(String caseNumber) {
        caseScraperService.scrapeCase(caseNumber);
    }
}

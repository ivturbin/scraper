package ru.develop.turbin.scraper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.develop.turbin.scraper.dao.CourtCaseRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScraperFacade {
    private final CaseScraperService caseScraperService;
    private final CourtCaseRepository courtCaseRepository;

    public void scrapeAllCases() {
        List<String> caseNumbers = courtCaseRepository.getAllNumbersToScrape();
        caseNumbers.forEach(number -> {
            caseScraperService.scrapeCase(number);
            try {
                Thread.sleep(120000);
            } catch (InterruptedException e) {
                log.error("Ошибка ожидания между скрейпингом");
            }
        });
    }

    public void scrapeNextCase() {
        String caseNumber = courtCaseRepository.getNumberToScrape();
        caseScraperService.scrapeCase(caseNumber);
    }

    public void scrapeCaseByNumber(String caseNumber) {
        caseScraperService.scrapeCase(caseNumber);
    }
}

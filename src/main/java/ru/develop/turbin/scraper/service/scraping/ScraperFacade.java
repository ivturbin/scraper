package ru.develop.turbin.scraper.service.scraping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.develop.turbin.scraper.dao.CourtCaseRepository;
import ru.develop.turbin.scraper.entity.ScrapingTaskEntity;
import ru.develop.turbin.scraper.enums.ScrapingTaskTypeEnum;
import ru.develop.turbin.scraper.service.ScrapingTaskService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScraperFacade {
    private final CaseScraperService caseScraperService;
    private final CourtCaseRepository courtCaseRepository;
    private final ScrapingTaskService scrapingTaskService;

    @Value("${configuration.scraping_interval:120000}")
    private Long scrapingInterval;

    public void scrapeAllCases() {
        ScrapingTaskEntity scrapingTaskEntity = scrapingTaskService.startScrapingTask(ScrapingTaskTypeEnum.MANUAL_ALL);
        List<String> caseNumbers = courtCaseRepository.getAllNumbersToScrape();
        caseNumbers.forEach(number -> {
            caseScraperService.scrapeCase(number, scrapingTaskEntity);
            try {
                Thread.sleep(scrapingInterval);
            } catch (InterruptedException e) {
                log.error("Ошибка ожидания между скрейпингом");
            }
        });

        scrapingTaskService.endScrapingTask(scrapingTaskEntity);
    }

    public void scrapeNextCase(ScrapingTaskEntity scrapingTaskEntity) {
        String caseNumber = courtCaseRepository.getNumberToScrape();
        caseScraperService.scrapeCase(caseNumber, scrapingTaskEntity);
    }

    public void scrapeCaseByNumber(String caseNumber) {
        ScrapingTaskEntity scrapingTaskEntity = scrapingTaskService.startScrapingTask(ScrapingTaskTypeEnum.MANUAL_SINGLE);
        caseScraperService.scrapeCase(caseNumber, scrapingTaskEntity);
        scrapingTaskService.endScrapingTask(scrapingTaskEntity);
    }
}

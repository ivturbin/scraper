package dev.turbin.scraper.service.scraping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.NoSuchElementException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import dev.turbin.scraper.repository.CourtCaseRepository;
import dev.turbin.scraper.entity.ScrapingTaskEntity;
import dev.turbin.scraper.enums.ScrapingTaskTypeEnum;
import dev.turbin.scraper.service.ScrapingTaskService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScraperFacade {
    private final Scraper scraper;
    private final CourtCaseRepository courtCaseRepository;
    private final ScrapingTaskService scrapingTaskService;

    @Value("${configuration.scraping_interval:120000}")
    private Long scrapingInterval;

    public void scrapeAllCases() {
        ScrapingTaskEntity scrapingTaskEntity = scrapingTaskService.startScrapingTask(ScrapingTaskTypeEnum.MANUAL_ALL);
        List<String> caseNumbers = courtCaseRepository.getAllNumbersToScrape();
        caseNumbers.forEach(number -> {
            scraper.scrapeCase(number, scrapingTaskEntity);
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
        scraper.scrapeCase(caseNumber, scrapingTaskEntity);
    }

    public void getAndScrapeNextCase() {
        ScrapingTaskEntity scrapingTaskEntity = scrapingTaskService.startScrapingTask(ScrapingTaskTypeEnum.MANUAL_NEXT);
        scrapeNextCase(scrapingTaskEntity);
        scrapingTaskService.endScrapingTask(scrapingTaskEntity);
    }

    public void scrapeCaseByNumber(String caseNumber) {
        ScrapingTaskEntity scrapingTaskEntity = scrapingTaskService.startScrapingTask(ScrapingTaskTypeEnum.MANUAL_SINGLE);
        scraper.scrapeCase(caseNumber, scrapingTaskEntity);
        scrapingTaskService.endScrapingTask(scrapingTaskEntity);
    }

    private void scrapeCaseWithErrorsHandling(String caseNumber) {
        try {
    } catch (NoSuchElementException e) {
        log.error("Ошибка поиска элемента на странице, дело {}: {}", caseNumber, e.getLocalizedMessage());
    } catch (RuntimeException e) {
        log.error("Ошибка, дело {}: {}", caseNumber, e.getLocalizedMessage());
//        } catch (InterruptedException e) {
//            log.error("Ошибка: {}", e.getLocalizedMessage());
    }
    }
}

package ru.develop.turbin.scraper.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.develop.turbin.scraper.entity.ScrapingTaskEntity;
import ru.develop.turbin.scraper.enums.ScrapingTaskTypeEnum;
import ru.develop.turbin.scraper.service.ScrapingTaskService;
import ru.develop.turbin.scraper.service.scraping.ScraperFacade;
import ru.develop.turbin.scraper.service.HealthCheckService;
import ru.develop.turbin.scraper.task.ScheduledScrapingTask;


@RestController
@RequiredArgsConstructor
@Slf4j
public class ScraperController {

    private final HealthCheckService healthCheckService;
    private final ScraperFacade scraperFacade;
    private final ScrapingTaskService scrapingTaskService;
    private final ScheduledScrapingTask scheduledScrapingTask;

    @PostMapping("/scrape")
    public void scrapeCase(@RequestBody String caseNumber) {
        log.info("/scrape/{}", caseNumber);
        scraperFacade.scrapeCaseByNumber(caseNumber);
    }

    @PostMapping("/scrape/all")
    public void manualAllStart() {
        log.info("/scrape/all");
        scraperFacade.scrapeAllCases();
    }

    @PostMapping("/scrape/next")
    public void manualNextStart() {
        ScrapingTaskEntity scrapingTaskEntity = scrapingTaskService.startScrapingTask(ScrapingTaskTypeEnum.MANUAL_NEXT);
        log.info("/scrape/next");
        scraperFacade.scrapeNextCase(scrapingTaskEntity);
        scrapingTaskService.endScrapingTask(scrapingTaskEntity);
    }

    @PostMapping("/start")
    public void startScheduled() {
        log.info("/start");
        scheduledScrapingTask.setScheduledScrapingEnabled(true);
    }

    @PostMapping("/stop")
    public void stopScheduled() {
        log.info("/stop");
        scheduledScrapingTask.setScheduledScrapingEnabled(false);
    }

    @GetMapping("/status")
    public String checkStatus() {
        return healthCheckService.getStatus();
    }
}

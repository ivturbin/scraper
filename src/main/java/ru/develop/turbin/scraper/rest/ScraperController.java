package ru.develop.turbin.scraper.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.develop.turbin.scraper.service.ScraperFacade;
import ru.develop.turbin.scraper.service.HealthCheckService;
import ru.develop.turbin.scraper.service.CaseScraperService;


@RestController
@RequiredArgsConstructor
@Slf4j
public class ScraperController {

    private final HealthCheckService healthCheckService;
    private final ScraperFacade scraperFacade;

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
        log.info("/scrape/next");
        scraperFacade.scrapeNextCase();
    }

    @GetMapping("/status")
    public String checkStatus() {
        return healthCheckService.getStatus();
    }
}

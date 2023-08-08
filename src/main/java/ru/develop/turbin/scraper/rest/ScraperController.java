package ru.develop.turbin.scraper.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.develop.turbin.scraper.service.AllCasesSraperService;
import ru.develop.turbin.scraper.service.HealthCheckService;
import ru.develop.turbin.scraper.service.CaseScraperService;


@RestController
@Slf4j
public class ScraperController {

    private CaseScraperService scraper;
    private HealthCheckService healthCheckService;
    private AllCasesSraperService allCasesSraperService;

    @Autowired
    public void setScraper(CaseScraperService scraper) {
        this.scraper = scraper;
    }

    @Autowired
    public void setHealthCheckService(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }

    @PostMapping("/scrape")
    public void scrapeCase(@RequestBody String caseNumber) {
        log.info("/scrape/{}", caseNumber);
        scraper.scrapeCase(caseNumber);
    }

    @PostMapping("/start")
    public void manualStart() {
        log.info("/start");
        allCasesSraperService.scrapeAllCases();
    }

    @GetMapping("/status")
    public String checkStatus() {
        return healthCheckService.getStatus();
    }
}

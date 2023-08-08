package ru.develop.turbin.scraper.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.develop.turbin.scraper.service.AllCasesSraperService;
import ru.develop.turbin.scraper.service.HealthCheckService;
import ru.develop.turbin.scraper.service.CaseScraperService;


@RestController
@RequiredArgsConstructor
@Slf4j
public class ScraperController {

    private final CaseScraperService scraper;
    private final HealthCheckService healthCheckService;
    private final AllCasesSraperService allCasesSraperService;

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

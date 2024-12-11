package dev.turbin.scraper.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import dev.turbin.scraper.service.scraping.ScraperFacade;
import dev.turbin.scraper.task.ScheduledScrapingTask;


@RestController
@RequiredArgsConstructor
@Tag(name = "Скрейпинг дел с сайта \"Электронное правосудие\"")
@Slf4j
public class ScraperController {

    private final ScraperFacade scraperFacade;
    private final ScheduledScrapingTask scheduledScrapingTask;

    @Operation(
            summary = "Скрейпить дело по номеру",
            parameters = {
                    @Parameter(
                            name = "caseNumber",
                            required = true
                    )
            })
    @PostMapping("/scrape")
    public void scrapeCase(@RequestParam String caseNumber) {
        log.info("/scrape/{}", caseNumber);
        scraperFacade.scrapeCaseByNumber(caseNumber);
    }

    @Operation(summary = "Скрейпить все дела, имеющиеся в court_case")
    @PostMapping("/scrape/all")
    public void manualAllStart() {
        log.info("/scrape/all");
        scraperFacade.scrapeAllCases();
    }

    @Operation(summary = "Скрейпить следующее по порядку дело из court_case")
    @PostMapping("/scrape/next")
    public void manualNextStart() {
        log.info("/scrape/next");
        scraperFacade.getAndScrapeNextCase();
    }

    @Operation(summary = "Запустить интервальный скрейпинг")
    @PostMapping("/start")
    public void startScheduled() {
        log.info("/start");
        scheduledScrapingTask.setScheduledScrapingEnabled(true);
    }

    @Operation(summary = "Остановить интервальный скрейпинг")
    @PostMapping("/stop")
    public void stopScheduled() {
        log.info("/stop");
        scheduledScrapingTask.setScheduledScrapingEnabled(false);
    }
}

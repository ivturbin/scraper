package ru.develop.turbin.scraper.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.develop.turbin.scraper.service.scraping.ScraperFacade;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledScrapingTask {

    private final ScraperFacade scraperFacade;

    @Value("${configuration.scheduled_scraping_enabled:false}")
    private boolean isScheduledScrapingEnabled;

    @Scheduled(fixedDelayString = "${configuration.scraping_interval:120000}")
    public void scheduledStart() {
        if (isScheduledScrapingEnabled) {
            log.info("Старт скрейпинга по расписанию");
            scraperFacade.scrapeNextCase();
        }
    }

}

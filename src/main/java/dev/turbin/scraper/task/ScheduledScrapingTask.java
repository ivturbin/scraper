package dev.turbin.scraper.task;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import dev.turbin.scraper.entity.ScrapingTaskEntity;
import dev.turbin.scraper.enums.ScrapingTaskStatusEnum;
import dev.turbin.scraper.enums.ScrapingTaskTypeEnum;
import dev.turbin.scraper.service.ScrapingTaskService;
import dev.turbin.scraper.service.scraping.ScraperFacade;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledScrapingTask {

    private final ScraperFacade scraperFacade;
    private final ScrapingTaskService scrapingTaskService;

    private ScrapingTaskEntity scrapingTaskEntity;

    @Value("${configuration.scheduled_scraping_enabled:false}")
    private boolean isScheduledScrapingEnabled;

    @Scheduled(fixedDelayString = "${configuration.scraping_interval:120000}")
    public void scheduledStart() {
        if (isScheduledScrapingEnabled) {
            if (scrapingTaskEntity == null) {
                initScrapingTask();
                log.info("Инициализирована задача интервального скрейпинга {}", scrapingTaskEntity.getScrapingTaskId());
            }
            log.info("Интервальный скрейпинг запущен по расписанию");
            scraperFacade.scrapeNextCase(scrapingTaskEntity);
        } else if (scrapingTaskEntity != null) {
            finishScheduledTask();
        }
    }

    public void setScheduledScrapingEnabled(boolean scheduledScrapingEnabled) {
        isScheduledScrapingEnabled = scheduledScrapingEnabled;

        if (isScheduledScrapingEnabled) {
            log.info("Включен интервальный скрейпинг");
        } else {
            log.info("Выключен интервальный скрейпинг");
        }
    }

    private void initScrapingTask() {
        scrapingTaskEntity = scrapingTaskService.startScrapingTask(ScrapingTaskTypeEnum.SCHEDULED);
    }

    private void finishScheduledTask() {
        scrapingTaskEntity.setTaskStatus(ScrapingTaskStatusEnum.STOPPED_MANUALLY.name());
        scrapingTaskService.endScrapingTask(scrapingTaskEntity);
        scrapingTaskEntity = null;
    }

    @PreDestroy
    private void end() {
        if (scrapingTaskEntity != null) {
            scrapingTaskEntity.setTaskStatus(ScrapingTaskStatusEnum.STOPPED_ON_SHUTDOWN.name());
            scrapingTaskService.endScrapingTask(scrapingTaskEntity);
        }
    }
}

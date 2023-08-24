package ru.develop.turbin.scraper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.develop.turbin.scraper.dao.CourtCaseRepository;
import ru.develop.turbin.scraper.dao.ScrapingTaskRepository;
import ru.develop.turbin.scraper.entity.ScrapingTaskEntity;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScrapingResultHandler {

    private final CourtCaseRepository courtCaseRepository;
    private final ScrapingTaskService scrapingTaskService;

    //TODO сделать обработку и ещё не существующего в бд дела
    public void skipCase(ScrapingTaskEntity scrapingTaskEntity, String caseNumber, String error) {
        log.warn("Дело {} пропущено", caseNumber);
        try {
            courtCaseRepository.markUpdated(caseNumber, error);
        } catch (Exception e) {
            log.info("Дело {} не найдено в БД", caseNumber);
        }
        scrapingTaskService.updateFailed(scrapingTaskEntity);
    }

    public void succeed(ScrapingTaskEntity scrapingTaskEntity) {
        scrapingTaskService.updateSucceed(scrapingTaskEntity);
    }
}

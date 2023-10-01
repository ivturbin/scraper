package ru.develop.turbin.scraper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.develop.turbin.scraper.dao.CourtCaseRepository;
import ru.develop.turbin.scraper.entity.ScrapingTaskEntity;
import ru.develop.turbin.scraper.enums.ScrapingError;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScrapingResultHandler {

    private final CourtCaseRepository courtCaseRepository;
    private final ScrapingTaskService scrapingTaskService;
    private final CaseScrapingLogDaoDecorator caseScrapingLogDaoDecorator;

    @Value("${configuration.additional_awaiting_on_error:20000}")
    private Long additionalAwaitingOnError;

    public void skipCaseScraping(ScrapingTaskEntity scrapingTaskEntity, String caseNumber, String error) {
        log.warn("Дело {} пропущено", caseNumber);
        try {
            courtCaseRepository.markUpdated(caseNumber, error);
        } catch (Exception e) {
            log.debug("Дело {} не удалось пометить пройденным в БД", caseNumber);
        }
        scrapingTaskService.updateFailed(scrapingTaskEntity);

        Long caseId = null;

        try {
            caseId = courtCaseRepository.getByNumber(caseNumber).getCaseId();
        } catch (Exception e) {
            log.debug("Дело {} не найдено в БД", caseNumber);
        }

        caseScrapingLogDaoDecorator.writeFailedLog(scrapingTaskEntity, caseId, error);

        try {
            Thread.sleep(additionalAwaitingOnError);
        } catch (InterruptedException e) {
            log.error(ScrapingError.THREAD_ERROR.getLogMessage(), caseNumber);
        }
    }

    public void completeCaseScraping(ScrapingTaskEntity scrapingTaskEntity, Long caseId) {
        scrapingTaskService.updateSucceed(scrapingTaskEntity);
        caseScrapingLogDaoDecorator.writeSuccessfulLog(scrapingTaskEntity, caseId);
    }
}

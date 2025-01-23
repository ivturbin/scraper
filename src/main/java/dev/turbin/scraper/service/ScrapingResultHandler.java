package dev.turbin.scraper.service;

import dev.turbin.scraper.enums.CaseScrapingCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import dev.turbin.scraper.repository.CourtCaseRepository;
import dev.turbin.scraper.entity.ScrapingTaskEntity;
import dev.turbin.scraper.enums.ScrapingError;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScrapingResultHandler {

    private final CourtCaseRepository courtCaseRepository;
    private final ScrapingTaskService scrapingTaskService;
    private final CaseScrapingLogService caseScrapingLogService;

    @Value("${configuration.additional_awaiting_on_error:20000}")
    private Long additionalAwaitingOnError;

    public void skipCaseScraping(ScrapingTaskEntity scrapingTaskEntity, String caseNumber, String error) {
        log.warn("Дело {} пропущено", caseNumber);
        try {
            courtCaseRepository.markUpdatedWithErrorByNumber(caseNumber, error);
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

        caseScrapingLogService.writeLog(scrapingTaskEntity, caseId, CaseScrapingCode.SKIPPED, error);

        try {
            Thread.sleep(additionalAwaitingOnError);
        } catch (InterruptedException e) {
            log.error(ScrapingError.THREAD_SLEEP_ERROR.getLogMessage(), caseNumber);
        }
    }

    public void completeCaseScraping(ScrapingTaskEntity scrapingTaskEntity, Long caseId, StringBuilder error) {
        if (error.isEmpty()) {
            completeCaseScraping(scrapingTaskEntity, caseId);
        } else {
            completeCaseScrapingWithErrors(scrapingTaskEntity, caseId, error.toString());
        }
    }

    private void completeCaseScraping(ScrapingTaskEntity scrapingTaskEntity, Long caseId) {
        scrapingTaskService.updateSucceed(scrapingTaskEntity);
        caseScrapingLogService.writeLog(scrapingTaskEntity, caseId, CaseScrapingCode.OK, null);
    }

    private void completeCaseScrapingWithErrors(ScrapingTaskEntity scrapingTaskEntity, Long caseId, String error) {
        scrapingTaskService.updateFailed(scrapingTaskEntity);
        caseScrapingLogService.writeLog(scrapingTaskEntity, caseId, CaseScrapingCode.WITH_ERRORS, error);
        courtCaseRepository.markUpdatedWithErrorById(caseId, error);
    }
}

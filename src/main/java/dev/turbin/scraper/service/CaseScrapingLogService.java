package dev.turbin.scraper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import dev.turbin.scraper.repository.CaseScrapingLogRepository;
import dev.turbin.scraper.entity.CaseScrapingLogEntity;
import dev.turbin.scraper.entity.ScrapingTaskEntity;
import dev.turbin.scraper.enums.CaseScrapingCode;

@Service
@RequiredArgsConstructor
public class CaseScrapingLogService {
    private final CaseScrapingLogRepository caseScrapingLogRepository;

    @Deprecated
    public void writeSuccessfulLog(ScrapingTaskEntity scrapingTaskEntity, Long caseId) {
        CaseScrapingLogEntity caseScrapingLogEntity = new CaseScrapingLogEntity(
                caseId, scrapingTaskEntity.getScrapingTaskId(), CaseScrapingCode.OK.name(), null
        );

        caseScrapingLogRepository.save(caseScrapingLogEntity);

    }

    @Deprecated
    public void writeSkippedLog(ScrapingTaskEntity scrapingTaskEntity, Long caseId, String result) {
        CaseScrapingLogEntity caseScrapingLogEntity = new CaseScrapingLogEntity(
                caseId, scrapingTaskEntity.getScrapingTaskId(), CaseScrapingCode.SKIPPED.name(), result
        );

        caseScrapingLogRepository.save(caseScrapingLogEntity);

    }

    public void writeLog(ScrapingTaskEntity scrapingTaskEntity, Long caseId, CaseScrapingCode caseScrapingCode, String error) {
        CaseScrapingLogEntity caseScrapingLogEntity = new CaseScrapingLogEntity(
                caseId, scrapingTaskEntity.getScrapingTaskId(), caseScrapingCode.name(), error
        );
        caseScrapingLogRepository.save(caseScrapingLogEntity);
    }
}

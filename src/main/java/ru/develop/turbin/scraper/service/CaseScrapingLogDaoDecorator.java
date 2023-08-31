package ru.develop.turbin.scraper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.develop.turbin.scraper.dao.CaseScrapingLogRepository;
import ru.develop.turbin.scraper.entity.CaseScrapingLogEntity;
import ru.develop.turbin.scraper.entity.ScrapingTaskEntity;
import ru.develop.turbin.scraper.enums.CaseScrapingCode;

@Service
@RequiredArgsConstructor
public class CaseScrapingLogDaoDecorator {
    private final CaseScrapingLogRepository caseScrapingLogRepository;


    public void writeSuccessfulLog(ScrapingTaskEntity scrapingTaskEntity, Long caseId) {
        CaseScrapingLogEntity caseScrapingLogEntity = new CaseScrapingLogEntity(
                caseId, scrapingTaskEntity.getScrapingTaskId(), CaseScrapingCode.OK.name(), null
        );

        caseScrapingLogRepository.save(caseScrapingLogEntity);

    }

    public void writeFailedLog(ScrapingTaskEntity scrapingTaskEntity, Long caseId, String result) {
        CaseScrapingLogEntity caseScrapingLogEntity = new CaseScrapingLogEntity(
                caseId, scrapingTaskEntity.getScrapingTaskId(), CaseScrapingCode.SKIPPED.name(), result
        );

        caseScrapingLogRepository.save(caseScrapingLogEntity);

    }
}

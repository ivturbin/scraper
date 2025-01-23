package dev.turbin.scraper.service;

import dev.turbin.scraper.service.files.FileDownloader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import dev.turbin.scraper.repository.CaseEventRepository;
import dev.turbin.scraper.repository.CourtCaseRepository;
import dev.turbin.scraper.repository.EventScrapingLogRepository;
import dev.turbin.scraper.entity.CaseEventEntity;
import dev.turbin.scraper.entity.CourtCaseEntity;
import dev.turbin.scraper.entity.ScrapingTaskEntity;
import dev.turbin.scraper.model.CaseHeader;
import dev.turbin.scraper.model.CaseItem;
import dev.turbin.scraper.model.ParsedCaseModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParsedInfoProcessor {
    private final CourtCaseRepository courtCaseRepository;
    private final CaseEventRepository caseEventRepository;
    private final FileDownloader fileDownloader;
    private final EventScrapingLogRepository eventScrapingLogRepository;

    public Long process(ParsedCaseModel parsedInfoModel, ScrapingTaskEntity scrapingTaskEntity) {

        log.debug("Сохранение информации по делу {} в БД", parsedInfoModel.getCaseNumber());

        CourtCaseEntity caseEntity = getCaseEntity(parsedInfoModel);
        Long courtCaseId = caseEntity.getCaseId();

        List<CaseEventEntity> eventEntities = caseEventRepository.getByCaseId(courtCaseId);

        parsedInfoModel.getParsedEventsByHeader().forEach((k, v) -> v.forEach(event -> {
            CaseEventEntity caseEventEntity = getCaseEventEntity(caseEntity, k, event);

            Optional<CaseEventEntity> eventEntityByHash = eventEntities
                    .stream()
                    .filter(entity -> Objects.equals(entity.getEventHash(), caseEventEntity.getEventHash()))
                    .findFirst();

            Long caseEventId;
            String parsedFileLink = caseEventEntity.getFileLink();
            if (eventEntityByHash.isPresent()) {
                caseEventId = eventEntityByHash.get().getCaseEventId();
                caseEventEntity.setCaseEventId(caseEventId);
                caseEventRepository.update(caseEventEntity);

                if (fileLinkIsNotEmpty(parsedFileLink)
                        && eventEntityByHashNeedsFileUpdate(parsedFileLink, eventEntityByHash.get())
                ) {
                    fileDownloader.download(caseEventEntity.getFileLink(), caseEventId);
                }
            } else {
                try {
                    caseEventId = caseEventRepository.save(caseEventEntity);
                } catch (Exception e) {
                    throw new RuntimeException(String.format("Ошибка сохранения ивента %d с хэшем %d: %s",
                            caseEventEntity.getCaseEventId(), caseEventEntity.getEventHash(), e.getLocalizedMessage()));
                }

                if (fileLinkIsNotEmpty(parsedFileLink)) {
                    fileDownloader.download(caseEventEntity.getFileLink(), caseEventId);
                }
            }

            eventScrapingLogRepository.saveEventLog(caseEventEntity, scrapingTaskEntity);
        }));



        log.info("Дело {} сохранено", parsedInfoModel.getCaseNumber());

        return courtCaseId;
    }

    private boolean eventEntityByHashNeedsFileUpdate(String parsedFileLink, CaseEventEntity eventEntityByHash) {
        return !parsedFileLink.equals(eventEntityByHash.getFileLink())
                || eventEntityByHash.getFileData() == null;
    }

    private boolean fileLinkIsNotEmpty(String fileLink) {
        return fileLink != null && !fileLink.isEmpty();
    }

    private CaseEventEntity getCaseEventEntity(CourtCaseEntity caseEntity, CaseHeader header, CaseItem event) {
        CaseEventEntity caseEventEntity = new CaseEventEntity();
        caseEventEntity.setEventCaseId(caseEntity.getCaseId());

        caseEventEntity.setInstantion(header.getInstance());
        //TODO caseEventEntity.setEventId(k.get()); не всегда заполненный id в каждой строке, заполнен только для b-chrono-item js-chrono-item b-chrono-cols page-break g-ec even
        //data-instance_id
        try {
            caseEventEntity.setEventDate(LocalDate.parse(event.getDataDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        } catch (RuntimeException e) {
            log.error("Ошибка: дело {}, невозможно распарсить дату события: {}", caseEntity.getCaseNumber(), e.getLocalizedMessage());
        }

        caseEventEntity.setEventType(event.getCaseType());
        caseEventEntity.setEventActor(event.getEventActor());
        caseEventEntity.setEventDescription(event.getEventDescription());
        caseEventEntity.setFileLink(event.getFileLink());
        //TODO caseEventEntity.setFileData();
        caseEventEntity.setFileInfo(event.getFileDescription());
        caseEventEntity.setAdditionalInfo(event.getAdditionalInfo());
        caseEventEntity.setIsSigned(event.isSigned());
        caseEventEntity.setSignatureInfo(event.getSignatureInfo());
        //TODO caseEventEntity.setOriginalData();
        caseEventEntity.setCourtName(header.getCourtName());
        caseEventEntity.setDataCourt(header.getDataCourt());

        caseEventEntity.enrichEventHash(caseEntity.getCaseNumber());

        return caseEventEntity;
    }

    private CourtCaseEntity getCaseEntity(ParsedCaseModel parsedInfoModel) {
        CourtCaseEntity caseEntity = courtCaseRepository.getByNumber(parsedInfoModel.getCaseNumber());
        Long courtCaseId;
        if (caseEntity == null) {
            caseEntity = new CourtCaseEntity();
            caseEntity.setCaseNumber(parsedInfoModel.getCaseNumber());
            caseEntity.setCaseLink(parsedInfoModel.getCaseLink());
            caseEntity.setIsScraped(Boolean.TRUE);
            courtCaseId = courtCaseRepository.save(caseEntity);

        } else {
            courtCaseId = caseEntity.getCaseId();

            caseEntity.setCaseLink(parsedInfoModel.getCaseLink());
            caseEntity.setIsScraped(Boolean.TRUE);
            courtCaseRepository.updateCase(caseEntity);
        }

        caseEntity.setCaseId(courtCaseId);

        return caseEntity;
    }

}

package ru.develop.turbin.scraper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.develop.turbin.scraper.dao.CaseEventRepository;
import ru.develop.turbin.scraper.dao.CourtCaseRepository;
import ru.develop.turbin.scraper.entity.CaseEventEntity;
import ru.develop.turbin.scraper.entity.CourtCaseEntity;
import ru.develop.turbin.scraper.model.CaseHeader;
import ru.develop.turbin.scraper.model.CaseItem;
import ru.develop.turbin.scraper.model.ParsedInfoModel;

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

    public void process(ParsedInfoModel parsedInfoModel) {

        log.info("Сохранение информации по делу {} в БД", parsedInfoModel.getCaseNumber());

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
            if (eventEntityByHash.isPresent()) {
                caseEventId = eventEntityByHash.get().getCaseEventId();
                caseEventEntity.setCaseEventId(caseEventId);
                caseEventRepository.update(caseEventEntity);

                String fileLink = caseEventEntity.getFileLink();
                if (fileLink != null && !fileLink.isEmpty() && !fileLink.equals(eventEntityByHash.get().getFileLink())) {
                    fileDownloader.download(caseEventEntity.getFileLink(), caseEventId);
                }
            } else {
                caseEventId = caseEventRepository.save(caseEventEntity);
                String fileLink = caseEventEntity.getFileLink();
                if (fileLink != null && !fileLink.isEmpty()) {
                    fileDownloader.download(caseEventEntity.getFileLink(), caseEventId);
                }
            }
        }));

        log.info("Дело {} сохранено", parsedInfoModel.getCaseNumber());
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

    private CourtCaseEntity getCaseEntity(ParsedInfoModel parsedInfoModel) {
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
        }

        caseEntity.setCaseId(courtCaseId);

        return caseEntity;
    }

}

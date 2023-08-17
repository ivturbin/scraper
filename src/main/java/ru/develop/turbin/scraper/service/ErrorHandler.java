package ru.develop.turbin.scraper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.develop.turbin.scraper.dao.CourtCaseRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ErrorHandler {

    private final CourtCaseRepository courtCaseRepository;

    //TODO сделать обработку и ещё не существующего в бд дела
    public void skipCase(String caseNumber, String error) {
        log.warn("Дело {} пропущено", caseNumber);
        courtCaseRepository.markUpdated(caseNumber, error);
    }
}

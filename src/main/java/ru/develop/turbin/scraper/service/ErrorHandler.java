package ru.develop.turbin.scraper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.develop.turbin.scraper.dao.CourtCaseRepository;

@Service
@RequiredArgsConstructor
public class ErrorHandler {

    private final CourtCaseRepository courtCaseRepository;

    public void skipCase(String caseNumber) {
        courtCaseRepository.markUpdated(caseNumber);
    }
}

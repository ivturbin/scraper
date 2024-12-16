package dev.turbin.scraper.service.files;

import dev.turbin.scraper.dao.CaseEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;

@Service
@RequiredArgsConstructor
@Primary
@Slf4j
public class JavaFileDownloader implements FileDownloader {
    private final CaseEventRepository caseEventRepository;

    @Override
    public void download(String fileLink, Long eventId) {

        byte[] fileData;
        String[] splittedLink = fileLink.split("/");
        String fileName = splittedLink[splittedLink.length - 1];

        try {
            fileData = IOUtils.toByteArray(new URL(fileLink));
        } catch (IOException e) {
            log.error("Ошибка чтения файла: {}", fileName);
            return;
        }
        log.info("Файл успешно загружен {}", fileName);


        if (fileData != null) {
            caseEventRepository.updateFileDataById(fileData, eventId);
        }

    }

}

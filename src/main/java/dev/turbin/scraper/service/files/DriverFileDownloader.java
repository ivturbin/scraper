package dev.turbin.scraper.service.files;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import dev.turbin.scraper.dao.CaseEventRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Реализована загрузка без драйвера, требуется удостовериться в работоспособности.
 */
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "configuration", name = "download", havingValue = "java")
@Slf4j
public class DriverFileDownloader implements FileDownloader {
    private final CaseEventRepository caseEventRepository;
    private final WebDriver driver;
    private final String downloadsDirectory;

    @Override
    public void download(String fileLink, Long eventId) {
        driver.get(fileLink);
        log.info("Файл загружен {}", fileLink);

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        byte[] fileData = null;

        String fileName = getFileNameFromLink(fileLink);
        File fileToOpen = new File(downloadsDirectory, fileName);

        if (fileToOpen.exists()) {
            fileData = new byte[(int) fileToOpen.length()];

            try (FileInputStream fileInputStream = new FileInputStream(fileToOpen)) {
                if (fileInputStream.read(fileData) == fileToOpen.length()) {
                    log.info("Файл успешно считан {}", fileToOpen.getAbsolutePath());
                } else {
                    fileData = null;
                    log.error("Файл считан не полностью {}", fileToOpen.getAbsolutePath());
                }
            } catch (IOException e) {
                fileData = null;
                log.error("Ошибка чтения файла: {}", fileToOpen.getAbsolutePath());
            } finally {
                if (fileToOpen.delete()) {
                    log.info("Файл успешно удален с диска {}", fileToOpen.getAbsolutePath());
                } else {
                    log.debug("Не удалось удалить файл с диска {}", fileToOpen.getAbsolutePath());
                }
            }
        } else {
            log.error("Файл не найден: {}", fileToOpen.getAbsolutePath());
        }

        if (fileData != null) {
            caseEventRepository.updateFileDataById(fileData, eventId);
        }
    }

    private String getFileNameFromLink(String link) {
        String[] splittedLink = link.split("/");
        return splittedLink[splittedLink.length-1];
    }


}

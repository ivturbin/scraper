package ru.develop.turbin.scraper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.develop.turbin.scraper.dao.CaseEventRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileDownloader {
    private final CaseEventRepository caseEventRepository;
    private final WebDriver driver;

    public void download(String fileLink, Long eventId) {
        driver.get(fileLink);
        log.info("Файл загружен {}", fileLink);

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        byte[] fileData = null;

        String downloadsDirectory = getDownloadsDirectory();
        String fileName = getFileNameFromLink(fileLink);
        File fileToOpen = new File(downloadsDirectory, fileName);

//        if (fileToOpen.exists()) {
//            try {
//                fileData = Files.readAllBytes(Paths.get(downloadsDirectory + fileName));
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        } else {
//            log.error("Файл не найден: {}", fileToOpen.getAbsolutePath());
//        }

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

    private String getDownloadsDirectory() {
        String userHome = System.getProperty("user.home");
        return userHome + File.separator + "Downloads" + File.separator;
    }

    private String getFileNameFromLink(String link) {
        String[] splittedLink = link.split("/");
        return splittedLink[splittedLink.length-1];
    }


}

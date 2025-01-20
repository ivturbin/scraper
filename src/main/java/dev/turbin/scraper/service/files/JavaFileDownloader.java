package dev.turbin.scraper.service.files;

import dev.turbin.scraper.dao.CaseEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "configuration.download", name = "method", havingValue = "java")
@Slf4j
public class JavaFileDownloader implements FileDownloader {
    private final CaseEventRepository caseEventRepository;
    private final WebDriver driver;

    @Override
    public void download(String fileLink, Long eventId) {

        byte[] fileData;
        String[] splittedLink = fileLink.split("/");
        String fileName = splittedLink[splittedLink.length - 1];

        try {
            //todo капчу кажется удалось обойти, скачивается html и похоже, что в value у него лежит pdf

            // Извлекаем заголовки из браузера
            HttpURLConnection connection = (HttpURLConnection) new URL(fileLink).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36");
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
            connection.setRequestProperty("Referer", driver.getCurrentUrl());
            connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");

            // Добавляем cookies
            Set<Cookie> cookies = driver.manage().getCookies();
            String cookieHeader = cookies.stream()
                    .map(cookie -> cookie.getName() + "=" + cookie.getValue())
                    .collect(Collectors.joining("; "));
            connection.setRequestProperty("Cookie", cookieHeader);

            // Читаем файл
            // todo стрим нужно закрывать?
            InputStream inputStream = connection.getInputStream();

            fileData = IOUtils.toByteArray(inputStream);

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

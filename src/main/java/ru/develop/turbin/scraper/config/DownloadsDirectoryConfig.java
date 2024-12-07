package ru.develop.turbin.scraper.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
@Slf4j
public class DownloadsDirectoryConfig {

    @Bean("downloadsDirectory")
    public static String downloadsDirectory() {
        String downloadsDirectory = System.getProperty("user.home") + File.separator + "Downloads" + File.separator;
        log.info("downloadsDirectory: {}", downloadsDirectory);
        return downloadsDirectory;
    }
}
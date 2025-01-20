package dev.turbin.scraper.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
@Slf4j
public class DownloadsDirectoryConfig {

    @Bean("downloadsDirectory")
    public String downloadsDirectory() {
        String downloadsDirectory = System.getProperty("user.home") + File.separator + "kad" + File.separator + "tmp";
        log.info("downloadsDirectory: {}", downloadsDirectory);
        return downloadsDirectory;
    }
}

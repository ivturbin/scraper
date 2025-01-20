package dev.turbin.scraper.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
@Slf4j
public class DownloadsDirectoryConfig {

    @Bean("downloadsDirectory")
    @ConditionalOnProperty(prefix = "configuration.download", name = "directory", havingValue = "home")
    public String downloadToHome() {
        return withLogging(System.getProperty("user.home") + File.separator + "kad" + File.separator + "tmp");
    }

    @Bean("downloadsDirectory")
    @ConditionalOnMissingBean(name = "downloadsDirectory")
    @ConditionalOnProperty(prefix = "configuration.download", name = "directory")
    public String downloadsDirectoryFromProperties() {
        return withLogging(System.getProperty("configuration.download.directory"));
    }

    @ConditionalOnMissingBean(name = "downloadsDirectory")
    @Bean("downloadsDirectory")
    public String defaultDownloadsDirectory() {
        return withLogging(System.getProperty("user.dir") + File.separator + "tmp");
    }

    private String withLogging(String directory) {
        log.info("downloadsDirectory: {}", directory);
        return directory;
    }
}

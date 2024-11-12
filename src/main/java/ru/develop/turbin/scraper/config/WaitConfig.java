package ru.develop.turbin.scraper.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class WaitConfig {

    @Bean
    public WebDriverWait driverWait(WebDriver driver,
                                    @Value("${configuration.selenium_awaiting_timeout:20000}") Long timeout) {

        return new WebDriverWait(driver, Duration.ofMillis(timeout));
    }
}

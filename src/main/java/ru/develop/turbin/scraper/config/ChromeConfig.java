package ru.develop.turbin.scraper.config;

import com.google.common.collect.ImmutableMap;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "configuration", name = "browser", havingValue = "chrome")
public class ChromeConfig {

    @Bean
    public ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-gpu");                                  // Disable GPU acceleration (if needed)
        options.addArguments("--start-maximized");
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);

        options.setExperimentalOption("prefs", ImmutableMap.of(
                "plugins.always_open_pdf_externally", true
        ));
        return options;
    }

    @Bean
    public WebDriverManager webDriverManager() {
        WebDriverManager webDriverManager = WebDriverManager.chromedriver();
        webDriverManager.setup();
        return webDriverManager;
    }

    @Bean
    public WebDriver chromeDriver(ChromeOptions options) {
        return new ChromeDriver(options);
    }

}

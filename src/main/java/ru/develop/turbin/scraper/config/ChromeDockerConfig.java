package ru.develop.turbin.scraper.config;

import com.google.common.collect.ImmutableMap;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "configuration", name = "browser", havingValue = "chrome-docker")
@Slf4j
public class ChromeDockerConfig {

    @Bean
    public ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();

        options.addArguments("--disable-blink-features=AutomationControlled");

        options.addArguments("--disable-gpu");
        options.addArguments("--start-maximized");
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);

        options.setExperimentalOption("prefs", ImmutableMap.of(
                "plugins.always_open_pdf_externally", true
        ));

        return options;
    }

    @Bean(destroyMethod = "quit")
    public WebDriverManager webDriverManager(ChromeOptions options) {
        return WebDriverManager
                .chromedriver()
                .clearDriverCache()
                .capabilities(options)
                .browserInDocker()
                .dockerDefaultArgs("--disable-gpu,--no-sandbox")
                .enableVnc()
                .avoidShutdownHook() //Убирает шатдаун хук WebDriverManager, чтобы корректно работал спринговый ApplicationShutdownHook
                .disableTracing(); //Убирает шатдаун хук трейсинга, чтобы корректно работал спринговый ApplicationShutdownHook
    }

    @Bean
    public WebDriver chromeDriver(WebDriverManager webDriverManager) {
        return webDriverManager.create();
    }
}

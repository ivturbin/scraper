package dev.turbin.scraper.config;

import com.google.common.collect.ImmutableMap;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(prefix = "configuration", name = "browser", havingValue = "chrome-docker")
@RequiredArgsConstructor
@Slf4j
public class ChromeDockerConfig {

    private final String downloadsDirectory;
    private final String dockerDownloadsDirectory = "/tmp/downloads";

    @Bean
    public ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();

        options.addArguments("--disable-blink-features=AutomationControlled");

        options.addArguments("--disable-gpu");
        options.addArguments("--start-maximized");
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);

        Map<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("download.default_directory", dockerDownloadsDirectory);
        chromePrefs.put("plugins.always_open_pdf_externally", true);
        options.setExperimentalOption("prefs", chromePrefs);

        return options;
    }

    @Bean(destroyMethod = "quit")
    public WebDriverManager webDriverManager(ChromeOptions options) {
        return WebDriverManager
                .chromedriver()
                .clearDriverCache()
                .capabilities(options)
                .browserInDocker()
                .dockerVolumes(downloadsDirectory + ":" + dockerDownloadsDirectory) //Монтировать директорию загрузок к докер-контейнеру
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

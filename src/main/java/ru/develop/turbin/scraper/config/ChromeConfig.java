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

import java.util.List;

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
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation")); //Чтобы не отображалась панель "Chrome is being controlled by automated test software"
        return options;
    }

    @Bean
    public WebDriverManager webDriverManager() {
        WebDriverManager webDriverManager = WebDriverManager.chromedriver();
        webDriverManager.avoidShutdownHook();
        webDriverManager.setup();
        return webDriverManager;
    }

    @Bean
    public WebDriver chromeDriver(ChromeOptions options, WebDriverManager webDriverManager) { //Чтобы сначала создался бин менеджера
        return new ChromeDriver(options);
    }

}

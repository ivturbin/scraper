package ru.develop.turbin.scraper.config;

import com.google.common.collect.ImmutableMap;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class SeleniumConfig {

    @Bean
//    @ConditionalOnProperty("configuration.download_directory")
//    public ChromeOptions chromeOptions(@Value("${configuration.download_directory}") String downloadPath) {
    public ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
 //       options.addArguments("download.default_directory=" + downloadPath);
//        options.addArguments("--download.prompt_for_download=false");             // Optional: Disable the download prompt
//        options.addArguments("--download.directory_upgrade=true");                // Optional: Allow Chrome to move files from the default download folder to the specified folder
//        options.addArguments("safebrowsing.enabled=true");                      // Optional: Enable safe browsing
//        //options.addArguments("--disable-infobars");                               // Хром контролируется ...
        //options.addArguments("--disable-extensions");                           // Отключить расширения
        //options.addArguments("--disable-plugins");                           // Отключить расширения
        options.addArguments("--disable-gpu");                                  // Disable GPU acceleration (if needed)
        options.addArguments("--start-maximized");
//        options.addArguments("--no-sandbox");                                   // Disable sandbox (Linux environments)
//        options.addArguments("--disable-popup-blocking");                       // Disable popup blocking
//        options.addArguments("pdfjs.disabled=true");

//        options.setExperimentalOption("prefs", ImmutableMap.of(
//                "plugins.always_open_pdf_externally", true,
//                "download.default_directory", downloadPath
//        ));
//
        options.setExperimentalOption("prefs", ImmutableMap.of(
                "plugins.always_open_pdf_externally", true
        ));

//        options.addArguments("--disable-plugins", "--disable-extensions", "--disable-pdfjs");
//        // Set up ChromeOptions to handle file download
//        Map<String, Object> prefs = new HashMap<>();
//        prefs.put("profile.default_content_settings.popups", 0);
//        prefs.put("download.default_directory", downloadPath);
//        options.setExperimentalOption("prefs", prefs);
//        options.setExperimentalOption("prefs", ImmutableMap.of(
//                "download.default_directory", downloadPath
//        ));

        return options;
    }

    @Bean
    public WebDriver chromeDriver() {
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver(chromeOptions());
    }

    @Bean
    public WebDriverWait driverWait(WebDriver driver,
                                        @Value("${configuration.selenium_awaiting_timeout:20000}") Long timeout) {
        return new WebDriverWait(driver, Duration.ofMillis(timeout));
    }
}

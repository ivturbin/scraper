package dev.turbin.scraper.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//todo скрейпинг не работает в файрфокс. Скорее всего нужен конфиг наподобие --disable-blink-features=AutomationControlled
@Configuration
@ConditionalOnProperty(prefix = "configuration", name = "browser", havingValue = "firefox")
public class FirefoxConfig {

    @Bean
    public WebDriver firefoxDriver(FirefoxOptions options) {
        WebDriverManager.firefoxdriver().setup();

        return new FirefoxDriver(options);
    }

    @Bean
    public FirefoxOptions firefoxOptions(String downloadsDirectory) {
        FirefoxOptions options = new FirefoxOptions();

       // options.addArguments("--disable-blink-features=AutomationControlled");
        //       options.addArguments("download.default_directory=" + downloadPath);
//        options.addArguments("--download.prompt_for_download=false");             // Optional: Disable the download prompt
//        options.addArguments("--download.directory_upgrade=true");                // Optional: Allow Chrome to move files from the default download folder to the specified folder
//        options.addArguments("safebrowsing.enabled=true");                      // Optional: Enable safe browsing
//        //options.addArguments("--disable-infobars");                               // Хром контролируется ...
        //options.addArguments("--disable-extensions");                           // Отключить расширения
        //options.addArguments("--disable-plugins");                           // Отключить расширения
       // options.addArguments("--disable-gpu");                                  // Disable GPU acceleration (if needed)
       // options.addArguments("--start-maximized");
        //options.setPageLoadStrategy(PageLoadStrategy.EAGER);

//        options.addArguments("--disable-popup-blocking");                       // Disable popup blocking
//        options.addArguments("pdfjs.disabled=true");

//        options.setExperimentalOption("prefs", ImmutableMap.of(
//                "plugins.always_open_pdf_externally", true,
//                "download.default_directory", downloadPath
//        ));


       // options.setHeadless(true);
//        options.setExperimentalOption("prefs", ImmutableMap.of(
//                "plugins.always_open_pdf_externally", true
//        ));

//        options.addArguments("--disable-plugins", "--disable-extensions", "--disable-pdfjs");
//        // Set up ChromeOptions to handle file download
//        Map<String, Object> prefs = new HashMap<>();
//        prefs.put("profile.default_content_settings.popups", 0);
//        prefs.put("download.default_directory", downloadPath);
//        options.setExperimentalOption("prefs", prefs);
//        options.setExperimentalOption("prefs", ImmutableMap.of(
//                "download.default_directory", downloadPath
//        ));

        options.addPreference("dom.webdriver.enabled", false);
        options.addPreference("useAutomationExtension", false);

        FirefoxProfile profile = new FirefoxProfile();



        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.download.manager.showWhenStarting", false);
        profile.setPreference("browser.download.dir", downloadsDirectory);
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/pdf");
        profile.setPreference("pdfjs.disabled", true);

        options.setProfile(profile);

        return options;
    }
}

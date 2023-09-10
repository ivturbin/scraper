package ru.develop.turbin.scraper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.develop.turbin.scraper.dao.HealthCheckRepository;
import ru.develop.turbin.scraper.service.HealthCheckService;

@SpringBootTest
class ScraperApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void webDriverCheck() {
        WebDriver driver = new ChromeDriver();
        driver.get("https://google.com");
        driver.quit();
    }

}

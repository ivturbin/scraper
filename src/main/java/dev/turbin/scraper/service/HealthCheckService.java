package dev.turbin.scraper.service;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import dev.turbin.scraper.dao.HealthCheckRepository;

@Service
public class HealthCheckService {

    @Autowired(required = false)
    private HealthCheckRepository healthCheckRepository;

    @Autowired(required = false)
    private WebDriver webDriver;

    public String getStatus() {

        StringBuilder response = new StringBuilder();
        try {
            int databaseResponse = healthCheckRepository.check();
            if (databaseResponse == 1) {
                response.append("БД: ок\n");
            }
            if (databaseResponse == 0) {
                response.append("БД: ошибка обращения\n");
            }
        } catch (Exception e){
            response.append("БД: ").append(e.getLocalizedMessage()).append("\n");
        }

        if (webDriver != null) {
            try {
                webDriver.get("https://google.com");
                response.append("Selenium: ок\n");
            } catch (Exception e) {
                response.append("Selenium: ").append(e.getLocalizedMessage()).append("\n");
            }
        } else {
            response.append("Selenium: ошибка инициализации\n");
        }

        return response.toString().stripTrailing();
    }

}

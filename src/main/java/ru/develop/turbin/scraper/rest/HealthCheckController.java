package ru.develop.turbin.scraper.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.develop.turbin.scraper.service.HealthCheckService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Проверка состояния приложения")
@Slf4j
public class HealthCheckController {
    private final HealthCheckService healthCheckService;

    @GetMapping("/status")
    public String checkStatus() {
        return healthCheckService.getStatus();
    }
}

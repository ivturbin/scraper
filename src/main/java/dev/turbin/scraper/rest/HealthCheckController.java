package dev.turbin.scraper.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import dev.turbin.scraper.service.HealthCheckService;

import java.net.SocketException;
import java.util.List;

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

    @GetMapping("/vnc-url")
    public List<String> vnc() throws SocketException {
        return healthCheckService.getVncUrl();
    }
}

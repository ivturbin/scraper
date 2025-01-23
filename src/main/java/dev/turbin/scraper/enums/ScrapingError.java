package dev.turbin.scraper.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScrapingError {
    CASE_NOT_FOUND("Дело не найдено в источнике. ", "Дело {} не найдено в источнике"),
    ELEMENT_NOT_FOUND("Веб-элемент не найден на странице. ", "Ошибка поиска элемента на странице, дело {}: {}"),
    COMMON_ERROR("Ошибка. ", "Ошибка, дело {}: {}"),
    THREAD_SLEEP_ERROR("Ошибка Thread.sleep(). ", "Ошибка Thread.sleep(), дело {}");
    private final String message;
    private final String logMessage;
}

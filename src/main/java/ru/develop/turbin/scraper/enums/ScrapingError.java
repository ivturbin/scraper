package ru.develop.turbin.scraper.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScrapingError {
    CASE_NOT_FOUND("Не найдено в источнике", "Дело {} не найдено в источнике"),
    ELEMENT_NOT_FOUND("Веб-элемент не найден на странице: ", "Ошибка поиска элемента на странице, дело {}: {}");
    private final String message;
    private final String logMessage;
}

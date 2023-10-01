package ru.develop.turbin.scraper.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventScrapingLogEntity {
    private Long eventId;
    private Long scrapingTaskId;
    private String code;
    private String result;
}

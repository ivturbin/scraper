package ru.develop.turbin.scraper.entity;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ScrapingTaskEntity {
private Long scrapingTaskId;
private String taskType;
private String taskStatus;
private String taskDetails;
private OffsetDateTime createDttm;
private OffsetDateTime endDttm;
private int passed;
private int failed;
}

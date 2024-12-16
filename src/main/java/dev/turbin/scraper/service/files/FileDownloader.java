package dev.turbin.scraper.service.files;

public interface FileDownloader {
    void download(String fileLink, Long eventId);
}

package dev.turbin.scraper.service.files;

import dev.turbin.scraper.exception.FileDownloadException;

public interface FileDownloader {
    void download(String fileLink, Long eventId) throws FileDownloadException;
}

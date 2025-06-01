package dev.turbin.scraper.exception;

public class FileDownloadException extends RuntimeException {
    public FileDownloadException(String message, String url) {
        super(message + " " + url);
    }
}

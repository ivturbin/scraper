package ru.develop.turbin.scraper;

import java.io.File;

public class Utils {

    public static String getDownloadsDirectory() {
        String userHome = System.getProperty("user.home");
        return userHome + File.separator + "Downloads" + File.separator;
    }
}

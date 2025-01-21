package dev.turbin.scraper.service;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import dev.turbin.scraper.dao.HealthCheckRepository;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Service
public class HealthCheckService {

    @Autowired(required = false)
    private HealthCheckRepository healthCheckRepository;

    @Autowired(required = false)
    private WebDriver webDriver;

    @Autowired(required = false)
    private WebDriverManager webDriverManager;

    @Autowired
    Environment env;

    public String getStatus() {

        StringBuilder response = new StringBuilder();
        try {
            int databaseResponse = healthCheckRepository.check();
            if (databaseResponse == 1) {
                response.append("БД: ок\n");
            }
            if (databaseResponse == 0) {
                response.append("БД: ошибка обращения\n");
            }
        } catch (Exception e){
            response.append("БД: ").append(e.getLocalizedMessage()).append("\n");
        }

        if (webDriver != null) {
            try {
                webDriver.get("https://google.com");
                response.append("Selenium: ок\n");
            } catch (Exception e) {
                response.append("Selenium: ").append(e.getLocalizedMessage()).append("\n");
            }
        } else {
            response.append("Selenium: ошибка инициализации\n");
        }

        return response.toString().stripTrailing();
    }

    public List<String> getVncUrl() throws SocketException {

        List<String> urls = new ArrayList<>();
        URL dockerNoVncUrl = webDriverManager.getDockerNoVncUrl();
        Enumeration e = NetworkInterface.getNetworkInterfaces();
        while(e.hasMoreElements())
        {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();
            while (ee.hasMoreElements())
            {
                InetAddress i = (InetAddress) ee.nextElement();
                if (i instanceof Inet4Address) {
                    urls.add(i.getHostAddress() + ":" + dockerNoVncUrl.getPort());
                }
            }
        }
        return urls;
    }
}

package ru.develop.turbin.scraper.service.scraping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import ru.develop.turbin.scraper.entity.ScrapingTaskEntity;
import ru.develop.turbin.scraper.model.CaseHeader;
import ru.develop.turbin.scraper.model.CaseItem;
import ru.develop.turbin.scraper.model.ParsedInfoModel;
import ru.develop.turbin.scraper.service.ErrorHandler;
import ru.develop.turbin.scraper.service.ParsedInfoProcessor;
import ru.develop.turbin.scraper.service.parsing.HeaderParser;
import ru.develop.turbin.scraper.service.parsing.ItemParser;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty("configuration.main_url")
@Slf4j
public class CaseScraperService {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final HeaderParser headerParser;
    private final ItemParser itemParser;
    private final ParsedInfoProcessor parsedInfoProcessor;
    private final ErrorHandler errorHandler;

    @Value("${configuration.main_url}")
    private String url;

    public void scrapeCase(String caseNumber, ScrapingTaskEntity scrapingTaskEntity) {

        driver.manage().deleteAllCookies();
        try {
            driver.get(url);
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

            WebElement caseNumberTextBox = driver.findElement(By.xpath("//input[@placeholder='например, А50-5568/08']"));
            caseNumberTextBox.sendKeys(caseNumber);
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(5000));

            WebElement searchButton = driver.findElement(By.xpath("//button[@alt='Найти']"));
            searchButton.click();

            //WebElement caseRef = driver.findElement(By.xpath("//a[@class='num_case']"));
            WebElement caseRef = null;
            try {
                caseRef = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@class='num_case']")));
            } catch (TimeoutException e) {
                WebElement noResults = driver.findElement(By.xpath("//div[@class='b-noResults']"));
                log.error("Дело {} не найдено в источнике", caseNumber);
                errorHandler.skipCase(caseNumber);
                return;
            }

            log.info("Дело {} найдено в источнике", caseNumber);

            String caseLink = caseRef.getAttribute("href");
            driver.get(caseLink);

            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//i[@class='b-sicon']")));

            List<WebElement> buttons = driver.findElements(By.xpath("//i[@class='b-sicon']"));
            buttons.forEach(WebElement::click);

            String headerExpandedClassName = "b-chrono-item-header js-chrono-item-header page-break b-chrono-item-header-expanded";
            String headerNotExpandedClassName = "b-chrono-item-header js-chrono-item-header page-break";

            //wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='b-chrono-item-header js-chrono-item-header page-break b-chrono-item-header-expanded']")));

            wait.until(driver -> driver
                    .findElements(By.xpath("//div[@class='" + headerNotExpandedClassName + "']"))
                    .isEmpty());

            List<WebElement> headerWebElements = (List<WebElement>) ((JavascriptExecutor) driver)
                    .executeScript("return document.getElementsByClassName('" + headerExpandedClassName + "');");

            //TODO писать ошибку в БД
            if (headerWebElements.isEmpty()) {
                throw new RuntimeException(String.format("Не обнаружено элементов header-expanded для дела %s", caseNumber));
            }

            String itemsContainerClassName = "b-chrono-items-container js-chrono-items-container";
            List<WebElement> itemContainers = (List<WebElement>) ((JavascriptExecutor) driver)
                    .executeScript("return document.getElementsByClassName('" + itemsContainerClassName + "');");

            //TODO писать ошибку в БД
            if (itemContainers.isEmpty()) {
                throw new RuntimeException(String.format("Не обнаружено элементов items-container для дела %s", caseNumber));
            }

            ParsedInfoModel parsedInfoModel = new ParsedInfoModel();
            parsedInfoModel.setCaseNumber(caseNumber);
            parsedInfoModel.setCaseLink(caseLink);

            Map<CaseHeader, List<CaseItem>> parsedEvents = new HashMap<>();

            for (int i = 0; i < headerWebElements.size(); i++) {
                Document headersDocument = Jsoup.parse(headerWebElements.get(i).getAttribute(("outerHTML")));

                //Elements header = document.getElementsByClass("b-chrono-item-header js-chrono-item-header page-break");
                //Elements items = document.getElementsByClass("b-chrono-item js-chrono-item b-chrono-cols page-break g-ec");

                CaseHeader caseHeader = headerParser.parseHeader(headersDocument.children());

                Document itemsContainerDocument = Jsoup.parse(itemContainers.get(i).getAttribute(("outerHTML")));

                //TODO обработать нпе
                List<CaseItem> caseItems = itemParser.parseItems(itemsContainerDocument.body().children().first().children().first().children());
                parsedEvents.put(caseHeader, caseItems);
                parsedInfoModel.setParsedEventsByHeader(parsedEvents);
            }

            parsedInfoProcessor.process(parsedInfoModel);


        } catch (NoSuchElementException e) {
            log.error("Ошибка поиска элемента на странице, дело {}: {}", caseNumber, e.getLocalizedMessage());
        } catch (RuntimeException e) {
            log.error("Ошибка, дело {}: {}", caseNumber, e.getLocalizedMessage());
        }
    }
}

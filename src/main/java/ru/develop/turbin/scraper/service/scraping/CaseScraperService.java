package ru.develop.turbin.scraper.service.scraping;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import ru.develop.turbin.scraper.entity.ScrapingTaskEntity;
import ru.develop.turbin.scraper.enums.ScrapingError;
import ru.develop.turbin.scraper.model.CaseHeader;
import ru.develop.turbin.scraper.model.CaseItem;
import ru.develop.turbin.scraper.model.ParsedInfoModel;
import ru.develop.turbin.scraper.service.ScrapingResultHandler;
import ru.develop.turbin.scraper.service.ParsedInfoProcessor;
import ru.develop.turbin.scraper.service.parsing.HeaderParser;
import ru.develop.turbin.scraper.service.parsing.ItemParser;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
    private final ScrapingResultHandler scrapingResultHandler;
    private JavascriptExecutor javascriptExecutor;
    private Random rand;

    @Value("${configuration.main_url}")
    private String url;

    @PostConstruct
    public void init() {
        javascriptExecutor = (JavascriptExecutor) driver;
        rand = new Random();
    }

    public void scrapeCase(String caseNumber, ScrapingTaskEntity scrapingTaskEntity) {

        try {
            driver.get(url);
            Thread.sleep(rand.nextInt(1500) + 500);

            WebElement caseNumberTextBox = driver.findElement(By.xpath("//input[@placeholder='например, А50-5568/08']"));
            caseNumberTextBox.sendKeys(caseNumber);
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(5000));

            WebElement searchButton = driver.findElement(By.xpath("//button[@alt='Найти']"));
            searchButton.click();

            Thread.sleep(rand.nextInt(2000) + 1000);

            WebElement caseRef;
            try {
                caseRef = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@class='num_case']")));
            } catch (TimeoutException e) {
                driver.findElement(By.xpath("//div[@class='b-noResults']"));
                log.error(ScrapingError.CASE_NOT_FOUND.getLogMessage(), caseNumber);
                scrapingResultHandler.skipCaseScraping(scrapingTaskEntity, caseNumber, ScrapingError.CASE_NOT_FOUND.getMessage());
                return;
            }

            log.info("Дело {} найдено в источнике", caseNumber);

            String caseLink = caseRef.getAttribute("href");
            driver.get(caseLink);

            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//i[@class='b-sicon']")));

            List<WebElement> buttons = driver.findElements(By.xpath("//i[@class='b-sicon']"));

            for (int i = buttons.size() - 1; i >= 0; i--) {
                WebElement button = buttons.get(i);
                javascriptExecutor.executeScript("arguments[0].scrollIntoView()", button);
                wait.until(ExpectedConditions.elementToBeClickable(button));
                Thread.sleep(rand.nextInt(2000) + 1000);
                button.click();
            }

            String headerExpandedClassName = "b-chrono-item-header js-chrono-item-header page-break b-chrono-item-header-expanded";
            String headerNotExpandedClassName = "b-chrono-item-header js-chrono-item-header page-break";

            wait.until(driver -> driver
                    .findElements(By.xpath("//div[@class='" + headerNotExpandedClassName + "']"))
                    .isEmpty());

            List<WebElement> headerWebElements = (List<WebElement>) javascriptExecutor
                    .executeScript("return document.getElementsByClassName('" + headerExpandedClassName + "');");

            //TODO писать ошибку в БД
            if (headerWebElements.isEmpty()) {
                throw new RuntimeException(String.format("Не обнаружено элементов header-expanded для дела %s", caseNumber));
            }

            String itemsContainerClassName = "b-chrono-items-container js-chrono-items-container";
            List<WebElement> itemContainers = (List<WebElement>) javascriptExecutor
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

                CaseHeader caseHeader = headerParser.parseHeader(headersDocument.children());

                Document itemsContainerDocument = Jsoup.parse(itemContainers.get(i).getAttribute(("outerHTML")));

                Element itemsContainerElement = itemsContainerDocument.body().children().first();
                if (itemsContainerElement != null) {
                    Element itemsWrapperElement = itemsContainerElement.children().first();
                    if (itemsWrapperElement != null) {
                        List<CaseItem> caseItems = itemParser.parseItems(itemsWrapperElement.children());
                        parsedEvents.put(caseHeader, caseItems);
                        parsedInfoModel.setParsedEventsByHeader(parsedEvents);
                    }
                }
            }

            Long caseId = parsedInfoProcessor.process(parsedInfoModel);

            scrapingResultHandler.completeCaseScraping(scrapingTaskEntity, caseId);
        } catch (NoSuchElementException e) {
            log.error(ScrapingError.ELEMENT_NOT_FOUND.getLogMessage(), caseNumber, e.getLocalizedMessage());
            scrapingResultHandler.skipCaseScraping(scrapingTaskEntity, caseNumber, ScrapingError.ELEMENT_NOT_FOUND.getMessage() + e.getLocalizedMessage());
        } catch (RuntimeException e) {
            log.error(ScrapingError.COMMON_ERROR.getLogMessage(), caseNumber, e.getLocalizedMessage());
            scrapingResultHandler.skipCaseScraping(scrapingTaskEntity, caseNumber, ScrapingError.COMMON_ERROR.getMessage() + e.getLocalizedMessage());
        } catch (InterruptedException e) {
            log.error(ScrapingError.THREAD_ERROR.getLogMessage(), caseNumber);
            scrapingResultHandler.skipCaseScraping(scrapingTaskEntity, caseNumber, ScrapingError.THREAD_ERROR.getMessage() + e.getLocalizedMessage());
        }
    }

    @PreDestroy
    public void preDestroy() {
        driver.quit();
        log.info("Драйвер остановлен");
    }
}

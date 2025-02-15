package dev.turbin.scraper.service.scraping;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import dev.turbin.scraper.entity.ScrapingTaskEntity;
import dev.turbin.scraper.enums.ScrapingError;
import dev.turbin.scraper.model.ParsedCaseModel;
import dev.turbin.scraper.service.ScrapingResultHandler;
import dev.turbin.scraper.service.ParsedInfoProcessor;
import dev.turbin.scraper.service.parsing.CaseParser;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty("configuration.main_url")
@Slf4j
public class Scraper {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final CaseParser caseParser;
    private final ParsedInfoProcessor parsedInfoProcessor;
    private final ScrapingResultHandler scrapingResultHandler;

    private Random rand;
    private JavascriptExecutor javascriptExecutor;

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

            List<WebElement> promoPopupCloseButton = driver.findElements(
                    By.xpath("//*[@id=\"js\"]/div[13]/div[2]/div/div/div/div/a[1]"));
            if (!promoPopupCloseButton.isEmpty()) {
                Thread.sleep(rand.nextInt(1500) + 500);
                promoPopupCloseButton.get(0).click();
            }

            Thread.sleep(rand.nextInt(1500) + 500);

            WebElement caseNumberTextBox = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@placeholder='например, А50-5568/08']")));
            caseNumberTextBox.sendKeys(caseNumber);

            WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@alt='Найти']")));
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

            List<WebElement> headerWebElementList = (List<WebElement>) javascriptExecutor
                    .executeScript("return document.getElementsByClassName('" + headerExpandedClassName + "');");

            if (headerWebElementList.isEmpty()) {
                throw new RuntimeException(String.format("Не обнаружено элементов header-expanded для дела %s", caseNumber));
            }

            String itemsContainerClassName = "b-chrono-items-container js-chrono-items-container";
            List<WebElement> itemsContainerWebElementList = (List<WebElement>) javascriptExecutor
                    .executeScript("return document.getElementsByClassName('" + itemsContainerClassName + "');");

            if (itemsContainerWebElementList.isEmpty()) {
                throw new RuntimeException(String.format("Не обнаружено элементов items-container для дела %s", caseNumber));
            }

            ParsedCaseModel parsedCaseModel = caseParser.getParsedCaseModel(caseNumber, caseLink, headerWebElementList, itemsContainerWebElementList);
            Long caseId = parsedInfoProcessor.process(parsedCaseModel, scrapingTaskEntity);
            scrapingResultHandler.completeCaseScraping(scrapingTaskEntity, caseId, parsedCaseModel.getErrorBuilder());

        } catch (NoSuchElementException e) {
            log.error(ScrapingError.ELEMENT_NOT_FOUND.getLogMessage(), caseNumber, e.getLocalizedMessage());
            scrapingResultHandler.skipCaseScraping(scrapingTaskEntity, caseNumber, ScrapingError.ELEMENT_NOT_FOUND.getMessage() + e.getLocalizedMessage());
        } catch (RuntimeException e) {
            log.error(ScrapingError.COMMON_ERROR.getLogMessage(), caseNumber, e.getLocalizedMessage());
            scrapingResultHandler.skipCaseScraping(scrapingTaskEntity, caseNumber, ScrapingError.COMMON_ERROR.getMessage() + e.getLocalizedMessage());
        } catch (InterruptedException e) {
            log.error(ScrapingError.THREAD_SLEEP_ERROR.getLogMessage(), caseNumber);
            scrapingResultHandler.skipCaseScraping(scrapingTaskEntity, caseNumber, ScrapingError.THREAD_SLEEP_ERROR.getMessage() + e.getLocalizedMessage());
        }
    }

}

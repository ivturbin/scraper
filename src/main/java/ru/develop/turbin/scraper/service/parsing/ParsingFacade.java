package ru.develop.turbin.scraper.service.parsing;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;
import ru.develop.turbin.scraper.model.CaseHeader;
import ru.develop.turbin.scraper.model.CaseItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ParsingFacade {
    private final HeaderParser headerParser;
    private final ItemParser itemParser;

    public Map<CaseHeader, List<CaseItem>> getParsedItemsMap(List<WebElement> headerWebElements,
                                                             List<WebElement> itemContainers,
                                                             StringBuilder errorBuilder) {
        Map<CaseHeader, List<CaseItem>> parsedEvents = new HashMap<>();


        for (int i = 0; i < headerWebElements.size(); i++) {

            CaseHeader caseHeader;
            try {
                Document headersDocument = Jsoup.parse(headerWebElements.get(i).getAttribute(("outerHTML")));
                caseHeader = headerParser.parseHeader(headersDocument.children());
            } catch (Exception e) {
                errorBuilder.append(String.format("Ошибка парсинга хэдера [%d]. ", i))
                        .append(e.getLocalizedMessage());
                continue;
            }

            Document itemsContainerDocument;
            try {
                itemsContainerDocument = Jsoup.parse(itemContainers.get(i).getAttribute(("outerHTML")));
            } catch (Exception e) {
                errorBuilder.append(String.format("Не удалось спарсить контейнер ивентов для хэдера [%d]. ", i));
                continue;
            }

            Element itemsContainerElement = itemsContainerDocument.body().children().first();
            if (itemsContainerElement != null) {
                Element itemsWrapperElement = itemsContainerElement.children().first();
                if (itemsWrapperElement != null) {
                    List<CaseItem> caseItems = itemParser.parseItems(itemsWrapperElement.children(), errorBuilder);
                    parsedEvents.put(caseHeader, caseItems);
                }
            }
        }

        return parsedEvents;
    }
}

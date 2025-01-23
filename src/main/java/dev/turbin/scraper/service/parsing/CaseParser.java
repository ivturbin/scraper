package dev.turbin.scraper.service.parsing;

import dev.turbin.scraper.model.ParsedCaseModel;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;
import dev.turbin.scraper.model.CaseHeader;
import dev.turbin.scraper.model.CaseItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CaseParser {
    private final HeaderParser headerParser;
    private final ItemParser itemParser;

    public ParsedCaseModel getParsedCaseModel(String caseNumber, String caseLink,
                                              List<WebElement> headerWebElements,
                                              List<WebElement> itemContainers) {

        ParsedCaseModel parsedInfoModel = new ParsedCaseModel();
        parsedInfoModel.setCaseNumber(caseNumber);
        parsedInfoModel.setCaseLink(caseLink);

        Map<CaseHeader, List<CaseItem>> parsedEvents = new HashMap<>();


        for (int i = 0; i < headerWebElements.size(); i++) {

            CaseHeader caseHeader;
            try {
                Document headersDocument = Jsoup.parse(headerWebElements.get(i).getAttribute(("outerHTML")));
                caseHeader = headerParser.parseHeader(headersDocument.children());
            } catch (Exception e) {
                parsedInfoModel.getErrorBuilder().append(String.format("Ошибка парсинга хэдера [%d]. ", i))
                        .append(e.getLocalizedMessage());
                continue;
            }

            Document itemsContainerDocument;
            try {
                itemsContainerDocument = Jsoup.parse(itemContainers.get(i).getAttribute(("outerHTML")));
            } catch (Exception e) {
                parsedInfoModel.getErrorBuilder().append(String.format("Не удалось спарсить контейнер ивентов для хэдера [%d]. ", i));
                continue;
            }

            Element itemsContainerElement = itemsContainerDocument.body().children().first();
            if (itemsContainerElement != null) {
                Element itemsWrapperElement = itemsContainerElement.children().first();
                if (itemsWrapperElement != null) {
                    List<CaseItem> caseItems = itemParser.parseItems(itemsWrapperElement.children(), parsedInfoModel.getErrorBuilder());
                    parsedEvents.put(caseHeader, caseItems);
                }
            }
        }


        parsedInfoModel.setParsedEventsByHeader(parsedEvents);

        return parsedInfoModel;
    }
}

package dev.turbin.scraper.service.parsing;

import io.micrometer.common.util.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import dev.turbin.scraper.model.CaseItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ItemParser {

    public List<CaseItem> parseItems(Elements items, StringBuilder errorBuilder) {
//        String itemsClassName1 = "b-chrono-item js-chrono-item b-chrono-cols page-break g-ec";
//        String itemsClassName2 = "b-chrono-item js-chrono-item b-chrono-cols page-break g-ec even";

        List<CaseItem> caseItems = new ArrayList<>();

        items.forEach(element -> {
            try {
                CaseItem caseItem = new CaseItem();
                caseItem.setDataId(element.attr("data-id"));
                caseItem.setDataDate(element.attr("data-date"));

                Optional.ofNullable(element.select(".case-date").first())
                        .ifPresent(caseDateElement -> caseItem.setCaseDate(caseDateElement.text().strip()));

                //TODO обработать
                caseItem.setCaseType(element.select(".case-type").first().text().strip());

                Element rCol = element.select(".r-col").first();

                Element caseSubject = rCol.select("p").first();

                //TODO проверить
                caseSubject.select(".b-icons16 info")
                        .stream()
                        .findFirst()
                        .ifPresent(info ->
                                caseItem.setAdditionalComment(info
                                        .attr("data-appealeddocs")
                                        .strip()));

                caseSubject.select("span[title]")
                        .stream()
                        .findFirst()
                        .ifPresent(eventActor ->
                                caseItem.setEventActor(eventActor
                                        .text()
                                        .strip()));

                Element caseResult = rCol.select("h2").first();

                caseResult.select("a")
                        .stream()
                        .findFirst()
                        .ifPresent(fileLink -> caseItem.setFileLink(fileLink.attr("href")));

                caseResult.getElementsByClass("js-judges-rollover")
                        .stream()
                        .findFirst()
                        .ifPresent(eventDescription -> caseItem.setEventDescription(eventDescription.text().strip()));

                caseResult.getElementsByClass("js-judges-rolloverHtml g-hidden")
                        .stream()
                        .findFirst()
                        .ifPresent(fileDescription -> {
                            if (StringUtils.isNotBlank(fileDescription.text())) {
                                caseItem.setFileDescription(fileDescription.text().strip());
                            }
                        });

                //TODO проверить
                caseResult.getElementsByClass("g-hidden js-signers-rolloverHtml")
                        .stream()
                        .findFirst()
                        .ifPresent(signatureInfo -> caseItem.setSignatureInfo(signatureInfo.text().strip()));

                rCol.getElementsByClass("b-case-publish_info js-case-publish_info")
                        .stream()
                        .findFirst()
                        .ifPresent(fileDateOfPublication -> caseItem.setFileDateOfPublication(fileDateOfPublication.text())
                        );

                rCol.getElementsByClass("additional-info")
                        .stream()
                        .findFirst()
                        .ifPresent(additionalInfo -> caseItem.setAdditionalInfo(additionalInfo.text())
                        );

                element.getElementsByClass("g-valid_sign js-signers-rollover")
                        .stream()
                        .findFirst()
                        .ifPresent(sign ->
                                caseItem.setSigned(sign.text().strip().equals("[Подписано]"))
                        );

                caseItems.add(caseItem);
            } catch (Exception e) {
                errorBuilder.append("Ошибка парсинга item: ")
                        .append(e.getLocalizedMessage());
            }
        });

        return caseItems;
    }
}

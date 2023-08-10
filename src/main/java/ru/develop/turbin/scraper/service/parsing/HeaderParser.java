package ru.develop.turbin.scraper.service.parsing;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import ru.develop.turbin.scraper.model.CaseHeader;

@Service
public class HeaderParser {
    public CaseHeader parseHeader(Elements headerOuterHtml) {

        Element headerElement = headerOuterHtml.select("div").first();

        CaseHeader caseHeader = new CaseHeader();
        caseHeader.setDataId(headerElement.attr("data-id"));
        caseHeader.setDataCourt(headerElement.attr("data-court"));
        caseHeader.setInstance(headerOuterHtml.select(".l-col").select("strong").text().strip());
        caseHeader.setCourtName(headerOuterHtml.select(".instantion-name").select("a").text().strip());

        return caseHeader;
    }
}

package com.trainingdemo.core.xml;

import com.trainingdemo.core.models.types.RssItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XmlParser {

    public static List<RssItem> parse(InputStream inputStream) {

        List<RssItem> rssItems = new ArrayList<>();
        try {
            NodeList list = getParsedRssItems(inputStream);
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                Element element = (Element) node;
                RssItem rssItem = new RssItem(getField(element,"title"), getField(element,"description"), getField(element,"pubDate"));
                rssItems.add(rssItem);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return rssItems;
    }

    private static NodeList getParsedRssItems(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder db = getDocumentBuilder();
        Document doc = db.parse(inputStream);
        doc.getDocumentElement().normalize();
        return doc.getElementsByTagName("item");
    }

    private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        return dbf.newDocumentBuilder();
    }

    private static String getField(Element element, String fieldName) {
        return element.getElementsByTagName(fieldName).item(0).getTextContent();
    }
}

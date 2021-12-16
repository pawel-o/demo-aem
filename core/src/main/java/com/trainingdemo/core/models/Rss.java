package com.trainingdemo.core.models;

import com.adobe.cq.export.json.ComponentExporter;
import com.trainingdemo.core.services.RssReader;
import com.trainingdemo.core.models.types.RssItem;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

@Model(
        adaptables = { SlingHttpServletRequest.class },
        adapters = {ComponentExporter.class},
        resourceType = Rss.TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(name = "jackson", extensions = "json", options = {
        @ExporterOption(name = "MapperFeature.SORT_PROPERTIES_ALPHABETICALLY", value = "true"),
        @ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", value="false")
})
public class Rss implements ComponentExporter {


    public static final String TYPE = "trainingdemo/components/rss";

    @Inject
    RssReader rssReader;

    @Inject
    private SlingHttpServletRequest request;

    @Inject
    private Resource resource;

    public List<RssItem> getFeeds() {
        Locale locale = request.getLocale();
        Integer limit = resource.getValueMap().get("limit", 5);
        List<RssItem> feeds = rssReader.getFeeds();
        if(feeds.isEmpty()) {
            return getFallbackFeeds().subList(0,limit);
        } else {
            return feeds.subList(0, limit);
        }
    }

    public List<RssItem> getFallbackFeeds() {
        List<RssItem> fallbackFeeds = new ArrayList<>();
        Resource feedsResource = resource.getChild("fallbackFeeds");
        if (feedsResource != null) {
            Iterator<Resource> iterator = feedsResource.listChildren();
            while (iterator.hasNext()) {
                Resource next = iterator.next();
                ValueMap valueMap = next.getValueMap();
                fallbackFeeds.add(new RssItem(valueMap.get("title", ""),
                        valueMap.get("description", ""),
                        getFormmatedDate("publishDate")
                        ));
            }
        }
        return fallbackFeeds;
    }

    public String getFormmatedDate(String propertyName) {
        Calendar date = resource.getValueMap().get(propertyName, Calendar.class);
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
            Date time = date.getTime();
            return sdf.format(time);
        }
        return "";
    }

    @Override
    public String getExportedType() {
       return TYPE;
    }
}

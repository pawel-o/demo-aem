package com.trainingdemo.core.services;

import com.trainingdemo.core.models.types.RssItem;
import com.trainingdemo.core.xml.XmlParser;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component(immediate = true, service = {Runnable.class, RssReader.class} )
public class RssReaderImpl implements RssReader,Runnable {

    @Reference
    private Scheduler scheduler;

    List<RssItem> rssFeeds = new ArrayList<>();

    private static final Logger log = LoggerFactory.getLogger(RssReaderImpl.class);

    @Activate
    protected void activate() {
        addScheduler();
        this.rssFeeds = getRssFeeds();
    }

    @Deactivate
    protected void deactivate() {
    }

    /**
     * This method adds the scheduler
     */
    private void addScheduler() {
        ScheduleOptions scheduleOptions = scheduler.EXPR("0 0 0/1 1/1 * ? *");
        scheduleOptions.name("Rss Feed Scheduler");
        scheduleOptions.canRunConcurrently(false);
        scheduler.schedule(this, scheduleOptions);
        log.info("Scheduler added");
    }


    private List<RssItem> getRssFeeds() {
        String url = "http://www.nba.com/bucks/rss.xml";
        try (CloseableHttpClient client = getHttpClient()) {
            HttpUriRequest request = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(request);
                 InputStream stream = response.getEntity().getContent()) {
                return XmlParser.parse(stream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private CloseableHttpClient getHttpClient() {
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5 * 1000).build();
        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    @Override
    public void run() {
        log.info("RSS Feeds scheduler");
        rssFeeds = getRssFeeds();
    }

    public List<RssItem> getFeeds(){
        return rssFeeds;
    }
}
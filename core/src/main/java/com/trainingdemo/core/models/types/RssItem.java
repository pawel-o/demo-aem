package com.trainingdemo.core.models.types;

public class RssItem {
    private String title;
    private String description;
    private String publishDate;

    public RssItem(String title, String description, String pubDate) {
        this.title = title;
        this.description = description;
        this.publishDate = pubDate;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPublishDate() {
        return publishDate;
    }
}

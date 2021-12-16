package com.trainingdemo.core.services;

import com.trainingdemo.core.models.types.RssItem;

import java.util.List;

public interface RssReader {

    public List<RssItem> getFeeds();
}

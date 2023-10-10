package com.moonstoneid.web3feed.publisher.rss;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.moonstoneid.web3feed.publisher.config.AppProperties;
import com.moonstoneid.web3feed.publisher.model.Entry;
import com.moonstoneid.web3feed.publisher.service.EntryService;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RssController {

    private final String baseUrl;

    private final EntryService entryService;

    public RssController(AppProperties appProperties, EntryService entryService) {
        this.baseUrl = appProperties.getBaseUrl();
        this.entryService = entryService;
    }

    @RequestMapping(value = "/rss", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_XML_VALUE)
    public String getRssFeed() throws FeedException {
        List<Entry> entries = entryService.getAllEntries();
        SyndFeed feed = createRssFeed(entries);
        return new SyndFeedOutput().outputString(feed);
    }

    private SyndFeed createRssFeed(List<Entry> entries) {
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");
        feed.setTitle("A sample web3 publisher");
        feed.setDescription("A sample web3 feed");
        feed.setLink(baseUrl);
        feed.setEntries(createRssEntries(entries));
        return feed;
    }

    private List<SyndEntry> createRssEntries(List<Entry> entries) {
        List<SyndEntry> rssEntries = new ArrayList<>();
        for (Entry entry : entries) {
            rssEntries.add(createRssEntry(entry));
        }
        return rssEntries;
    }

    private SyndEntry createRssEntry(Entry entry) {
        SyndEntry rssEntry = new SyndEntryImpl();
        rssEntry.setTitle(entry.getTitle());
        rssEntry.setLink(createEntryLink(entry.getId()));
        SyndContentImpl description = new SyndContentImpl();
        description.setType("text/plain");
        description.setValue(entry.getDescription());
        rssEntry.setDescription(description);
        rssEntry.setPublishedDate(new Date(entry.getDate().toInstant().toEpochMilli()));
        return rssEntry;
    }

    private String createEntryLink(Integer id) {
        return baseUrl + "/feed/entry/" + id;
    }

}

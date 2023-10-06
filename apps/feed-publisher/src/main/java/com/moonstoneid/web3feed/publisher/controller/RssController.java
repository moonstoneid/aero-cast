package com.moonstoneid.web3feed.publisher.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.moonstoneid.web3feed.publisher.model.Entry;
import com.moonstoneid.web3feed.publisher.service.EntryService;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RssController extends BaseController {

    private final EntryService entryService;

    public RssController(EntryService entryService) {
        this.entryService = entryService;
    }

    @RequestMapping(value = "/rss", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_XML_VALUE)
    public String getRss(HttpServletRequest request) throws FeedException {
        String url = getBaseUrl(request);
        List<Entry> entries = entryService.getAllEntries();
        SyndFeed feed = createFeed(url, entries);
        return new SyndFeedOutput().outputString(feed);
    }

    private SyndFeed createFeed(String url, List<Entry> entries) {
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");
        feed.setTitle("A sample web3 publisher");
        feed.setDescription("A sample web3 feed");
        feed.setLink("https://example.org");
        feed.setEntries(toRssEntries(url, entries));
        return feed;
    }

    private static List<SyndEntry> toRssEntries(String url, List<Entry> entries) {
        List<SyndEntry> rssEntries = new ArrayList<>();
        for (Entry entry : entries) {
            SyndEntry rssEntry = new SyndEntryImpl();
            rssEntry.setTitle(entry.getTitle());
            rssEntry.setLink(url + "/feed/entry/" + entry.getId());
            SyndContentImpl description = new SyndContentImpl();
            description.setType("text/plain");
            description.setValue(entry.getDescription());
            rssEntry.setDescription(description);
            rssEntry.setPublishedDate(new Date(entry.getDate().toInstant().toEpochMilli()));
            rssEntries.add(rssEntry);
        }
        return rssEntries;
    }

}
